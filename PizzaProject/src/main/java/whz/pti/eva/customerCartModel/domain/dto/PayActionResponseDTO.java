package whz.pti.eva.customerCartModel.domain.dto;

public class PayActionResponseDTO {

	private boolean payment;
	private String description = "";

	public PayActionResponseDTO() {
	}

	public PayActionResponseDTO payment(boolean payment) {
		this.payment = payment;
		return this;
	}
	
	public boolean getPayment() {
		return payment;
	}
	public String getDescription() {
		return description;
	}

	public PayActionResponseDTO description(String description) {
		this.description = description;
		return this;
	}

}
