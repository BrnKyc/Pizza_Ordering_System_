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
@ToString(callSuper = true)
public class Item extends BaseEntity<Long> {
	
	private int quantity;
	
	@ManyToOne
	private Pizza pizza;
	
	@Enumerated(EnumType.STRING)
	private PizzaSize pizzasize;
	
	private BigDecimal price;

	
	public Item(int quantity, Pizza pizza, PizzaSize size, BigDecimal price) {
        this.quantity = quantity;
        this.pizza = pizza;
        this.pizzasize = size;
        this.price = price;
    }
}