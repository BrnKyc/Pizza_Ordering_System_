package whz.pti.eva.customerCartModel.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import whz.pti.eva.customerCartModel.domain.entities.DeliveryAddress;
import whz.pti.eva.security.domain.User;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Integer>{

	Optional<DeliveryAddress> findById(Long addressId);


}
