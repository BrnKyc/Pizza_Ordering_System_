package whz.pti.eva.customerCartModel.service;

import whz.pti.eva.customerCartModel.domain.dto.PayActionResponseDTO;

public interface SmmpService {
	PayActionResponseDTO doPayAction(String from, String to, String pcontent);
}
