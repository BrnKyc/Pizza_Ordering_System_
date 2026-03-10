package whz.pti.eva.customerCartModel.domain.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;
import whz.pti.eva.common.BaseEntity;
import whz.pti.eva.customerCartModel.domain.PizzaSize;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderedItem extends BaseEntity<Long>{

	@ManyToOne
	private Pizza pizza;
	private String name;
	private int quantity;
	private String userId;
	
	@Enumerated(EnumType.STRING)
	private PizzaSize pizzasize;
	
	private BigDecimal price;
}
