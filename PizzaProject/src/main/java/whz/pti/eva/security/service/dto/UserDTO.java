package whz.pti.eva.security.service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{
		private Long id;
		@NotEmpty(message = "Username should not be empty")
		private String username;
		private String email;
		@NotEmpty(message = "Password should not be empty")
		private String passwordHash;
		

	
	
}

