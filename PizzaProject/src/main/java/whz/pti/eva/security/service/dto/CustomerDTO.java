package whz.pti.eva.security.service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
	private Long id;
	@NotEmpty(message = "Username should not be empty")
    private String username;
	@NotEmpty(message = "Password should not be empty")
    private String passwordHash;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private DeliveryAddress currentDeliveryAddress;
    @NotEmpty(message = "Email should not be empty")
    private String email;
}