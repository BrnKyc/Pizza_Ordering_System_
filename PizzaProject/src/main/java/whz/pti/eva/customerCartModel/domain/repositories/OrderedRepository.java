package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.Cart;
import whz.pti.eva.customerCartModel.domain.entities.Customer;
import whz.pti.eva.customerCartModel.domain.entities.Ordered;

@Repository
public interface OrderedRepository extends JpaRepository<Ordered, Long>{
	List<Ordered> findAllByUserId(String userId);
	Optional<Ordered> findByUserId(String userId);
	Optional<Ordered> findById(Long id);
}
