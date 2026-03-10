
package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.service.dto.CustomerDTO;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

	Optional<Customer> findById(Long id);
	
	Optional<Customer> findByUser(User user);

	Optional<Customer> findByUser(Optional<User> user);
	
	
}
