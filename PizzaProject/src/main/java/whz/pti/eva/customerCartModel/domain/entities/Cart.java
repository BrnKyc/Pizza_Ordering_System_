package whz.pti.eva.customerCartModel.domain.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import whz.pti.eva.common.BaseEntity;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(callSuper = true)
public class Cart extends BaseEntity<Integer> {
	
	private int quantity;
	
	private String userId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();
	
	@OneToOne
	Customer customer;

	public int getTotalQuantity() {
        return items.stream().mapToInt(Item::getQuantity).sum();
    }

	public BigDecimal getTotalPrice() {
        return items.stream()
                    .map(item -> item.getPizza().getPriceForSize(item.getPizzasize())
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    
}
