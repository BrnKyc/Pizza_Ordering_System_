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
public class Ordered extends BaseEntity<Long> {

	private int numberOfItems;
	private String userId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<OrderedItem> orderedItem = new ArrayList<>();

	public int getTotalQuantity() {
        return orderedItem.stream().mapToInt(OrderedItem::getQuantity).sum();
    }

	public BigDecimal getTotalPrice() {
        return orderedItem.stream()
                    .map(orderedItem -> orderedItem.getPizza().getPriceForSize(orderedItem.getPizzasize())
                            .multiply(BigDecimal.valueOf(orderedItem.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
