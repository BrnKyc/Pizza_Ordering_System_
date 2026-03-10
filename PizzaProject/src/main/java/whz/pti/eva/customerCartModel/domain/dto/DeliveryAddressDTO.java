package whz.pti.eva.customerCartModel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDTO {
	private String street;
    private String houseNumber;
    private String town;
    private String postalCode;
}
