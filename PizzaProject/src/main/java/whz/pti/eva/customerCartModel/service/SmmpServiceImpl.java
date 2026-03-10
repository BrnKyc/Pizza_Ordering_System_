package whz.pti.eva.customerCartModel.service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import whz.pti.eva.customerCartModel.domain.dto.AccountResponseDTO;
import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;
import whz.pti.eva.customerCartModel.domain.dto.TransferDTO;


@Service
public class SmmpServiceImpl implements SmmpService {

//    @Autowired
//    private MessageSource messageSource;

	private static final Logger log = LoggerFactory.getLogger(SmmpServiceImpl.class);

	@Value("${my.smmp.url}")
	String myUrl;

	@Value("${my.smmp.plainCreds}")
	String plainCreds;

	@Override
	public PayActionResponseDTO doPayAction(String from, String to, String pcontent) {
		PayActionResponseDTO payActionResponse = new PayActionResponseDTO().payment(false)
				.description("unbekanntes Problem. Transfer nicht erfolgreich");
//                new PayActionResponseDTO().payment(false).description(messageSource.getMessage("unbekanntes Problem. Transfer nicht erfolgreich", null,  LocaleContextHolder.getLocale())); //my.action.unknownProblem

		String[] tokens = pcontent.split("\\s+");
		if (tokens.length == 0)
			return payActionResponse.description("falsche Syntax");
		if (tokens.length >= 4)
			return payActionResponse.description("falsche Syntax - Eingabe zu lang / zuviele Worte");
//        if (tokens.length == 0) return payActionResponse.description(messageSource.getMessage("falsche Syntax", null,  LocaleContextHolder.getLocale())); //my.action.badSyntax
//        if (tokens.length >= 4) return payActionResponse.description(messageSource.getMessage("falsche Syntax - Eingabe zu lang / zuviele Worte", null,  LocaleContextHolder.getLocale())); //my.action.badSyntaxInpuToLong

		String token1 = tokens[0];
		String token2 = tokens.length > 1 ? tokens[1] : "";
		String token3 = tokens.length > 2 ? tokens[2] : "";

		if (!isCommunicationValid(token1, token2, token3, from)) {
			return payActionResponse.description("die Eingabe ist nicht korrekt");
//        return payActionResponse.description(messageSource.getMessage("die Eingabe ist nicht korrekt", null,  LocaleContextHolder.getLocale())); //my.action.cannotbedone
		}

		return smmpAccountCommunication(token1, token2, token3, from, payActionResponse);
	}

	private boolean isCommunicationValid(String token1, String token2, String token3, String from) {
		return isBodylessCommunication(token1, token2, token3) || isTransferCommunication(token1, token2, token3, from);
	}

	private boolean isBodylessCommunication(String token1, String token2, String token3) {
		return List.of("get", "delete", "open", "suspend").contains(token1) && token2.isEmpty() && token3.isEmpty();
	}

	private boolean isTransferCommunication(String token1, String token2, String token3, String from) {
		try {
			new BigDecimal(token3);
		} catch (NumberFormatException e) {
			return false;
		}
		return token1.equals("transfer") && !token2.equals(from);
	}

	private PayActionResponseDTO smmpAccountCommunication(String token1, String token2, String token3, String from,
			PayActionResponseDTO payActionResponse) {

		String uriReturn;
		ResponseEntity<?> response = null;
		byte[] plainCredsBytes = plainCreds.getBytes();
		String base64CredsBytes = Base64.getEncoder().encodeToString(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		String headerName = "Authorization";
		String headerValue = "Basic " + base64Creds;
		RestClient restClient = RestClient.create();

		try {
			switch (token1) {
			case "get":
				uriReturn = myUrl + from + "/account";
				response = restClient.get().uri(uriReturn).header(headerName, headerValue).retrieve()
						.toEntity(AccountResponseDTO.class);
				break;
			case "delete":
				uriReturn = myUrl + from + "/deleted";
				response = restClient.delete().uri(uriReturn).header(headerName, headerValue).retrieve()
						.toEntity(AccountResponseDTO.class);
				break;
			case "open":
				uriReturn = myUrl + from + "/opened";
				response = restClient.put().uri(uriReturn).header(headerName, headerValue).retrieve()
						.toEntity(AccountResponseDTO.class);
				break;
			case "suspend":
				uriReturn = myUrl + from + "/suspended";
				String requestPut = "suspended";
				response = restClient.put().uri(uriReturn).body(requestPut).contentType(MediaType.APPLICATION_JSON)
						.header(headerName, headerValue).retrieve().toEntity(AccountResponseDTO.class);
				break;
			case "transfer":			
				uriReturn = myUrl + from + "/payment";
				TransferDTO transferDTO = new TransferDTO(token2, new BigDecimal(token3));
				response = restClient.post().uri(uriReturn).contentType(MediaType.APPLICATION_JSON)
						.header(headerName, headerValue).body(transferDTO).retrieve()
						.toEntity(AccountResponseDTO.class);
				break;

			default:
				return payActionResponse.description("falsche Syntax - Befehl unbekannt !");
//            return payActionResponse.description(messageSource.getMessage("falsche Syntax - Befehl unbekannt !", null,  LocaleContextHolder.getLocale())); //my.action.badSyntaxUnkownCommand
			}

		} catch (ResourceAccessException e) {
			log.info(" ResourceException   " + e);
			response = new ResponseEntity<Object>(
					new AccountResponseDTO(
							token1 + " " + "ist nicht erfolgreich gewesen :: vlt. smmp-Dienst nicht erreichbar"),
					HttpStatus.OK);
//            response = new ResponseEntity<Object>(new AccountResponseDTO(token1 +" " + messageSource.getMessage("get ist nicht erfolgreich gewesen :: vlt. Smmf nicht erreichbar", null,  LocaleContextHolder.getLocale())), HttpStatus.OK); //my.action.getNotSuccessful
		} catch (Exception e) {
			response = new ResponseEntity<Object>(
					new AccountResponseDTO(token1 + " "
							+ " ist nicht erfolgreich gewesen :: vlt. Empfaenger unbekannt oder Konto deaktiviert"),
					HttpStatus.OK);
			AccountResponseDTO accountResponse = (AccountResponseDTO) response.getBody();
			payActionResponse.description(accountResponse.code());

			return payActionResponse.payment(false);
		}

		AccountResponseDTO accountResponse = (AccountResponseDTO) response.getBody();
		payActionResponse.description(accountResponse.code());

		return payActionResponse.payment(true);
	}

}
