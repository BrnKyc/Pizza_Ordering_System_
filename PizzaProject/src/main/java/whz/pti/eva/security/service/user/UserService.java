package whz.pti.eva.security.service.user;

import java.util.Collection;
import java.util.Optional;

import whz.pti.eva.security.domain.User;
import whz.pti.eva.security.domain.UserCreateForm;
import whz.pti.eva.security.service.dto.CustomerDTO;
import whz.pti.eva.security.service.dto.UserDTO;

public interface UserService {

	UserDTO getUserById(Long id);
	UserDTO findByUsername(String username);

	Optional<User> getUserByEmail(String email);
	
	Optional<User> getUserByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Collection<UserDTO> getAllUsers();

	User create(UserCreateForm form);
	
	void saveUser(CustomerDTO customerDto);
	
	void deleteUser(Long id);
	
	User update(UserCreateForm form, Long id);
	void deleteUserAndCustomer(Long id);
	void disableUser(Long id);
	void enableUser(Long id);

}

