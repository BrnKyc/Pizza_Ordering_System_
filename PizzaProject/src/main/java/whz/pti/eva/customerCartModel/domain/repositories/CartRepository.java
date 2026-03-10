package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Customer;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	List<Cart> findByCustomer(Customer customer);
	Optional<Cart> findByUserId(String userId);
	Optional<Cart> findById(Long id);
}
