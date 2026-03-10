package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.Item;
import whz.pti.eva.customerCartModel.domain.entities.Pizza;

@Repository
public interface ItemRepository extends JpaRepository<Pizza, Integer>{
	List<Pizza> findByName(String name);

	void save(Item newItem);
}