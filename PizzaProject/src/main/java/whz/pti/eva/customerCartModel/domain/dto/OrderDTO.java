package whz.pti.eva.customerCartModel.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
}