package whz.pti.eva.customerCartModel.boundary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import whz.pti.eva.customerCartModel.domain.entities.OrderedItem;
import whz.pti.eva.customerCartModel.service.OrderService;

@Controller

public class OrderedItemController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orderItem/add")
    public ResponseEntity<OrderedItem> addOrderedItem(@RequestBody OrderedItem orderedItem) {
        orderService.saveOrderedItem(orderedItem);
        return ResponseEntity.ok(orderedItem);
    }

    @DeleteMapping("/orderItem/delete")
    public ResponseEntity<Void> deleteOrderedItem(@RequestParam Long id) {
        try {
            OrderedItem orderedItem = orderService.getOrderById(id).getOrderedItem().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ordered item not found with id: " + id));
            orderService.removeFromOrdered(orderedItem);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

