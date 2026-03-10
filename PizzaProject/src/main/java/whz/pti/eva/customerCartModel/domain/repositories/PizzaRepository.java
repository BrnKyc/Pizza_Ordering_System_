package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.Pizza;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long>{
	List<Pizza> findByName(String name);
}
