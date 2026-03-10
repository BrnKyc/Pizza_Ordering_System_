package whz.pti.eva.customerCartModel.domain.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import lombok.*;
import whz.pti.eva.common.BaseEntity;
import whz.pti.eva.customerCartModel.domain.PizzaSize;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class Pizza extends BaseEntity<Long>{
	private String name;
	private BigDecimal priceLarge;
	private BigDecimal priceMedium;
	private BigDecimal priceSmall;
	
	public BigDecimal getPriceForSize(PizzaSize size) {
	    switch (size) {
	        case Small: return priceSmall;
	        case Medium: return priceMedium;
	        case Large: return priceLarge;
	        default: return BigDecimal.valueOf(0.0);
	    }
	}
}
