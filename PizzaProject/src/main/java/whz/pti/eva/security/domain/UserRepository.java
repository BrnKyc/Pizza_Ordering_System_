package whz.pti.eva.security.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import whz.pti.eva.security.service.dto.UserDTO;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findOneByEmail(String email);
	
	Optional<User> findByUsername(String username);
	
	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	List<User> findAllByOrderByUsernameAsc();
	
	void save(UserDTO userDto);
	
	// Find active users only
    @Query("SELECT u FROM User u WHERE u.disabled = false")
    List<User> findActiveUsers();
}

