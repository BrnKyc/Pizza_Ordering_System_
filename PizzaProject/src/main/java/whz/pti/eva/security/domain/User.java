package whz.pti.eva.security.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import whz.pti.eva.common.BaseEntity;
import whz.pti.eva.customerCartModel.domain.entities.Customer;

@Entity
@Getter @Setter
@RequiredArgsConstructor
@Table(name = "secuser")
public class User extends BaseEntity<Long>{
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "passwordHash", nullable = false)
	private String passwordHash;

	@Column(name = "role", nullable = false)
	private Role role;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Customer customer; // Optional link to the Customer entity
	
	@Column(name = "disabled", nullable = false)
    private boolean disabled = false;
	
	
	@Override
	public String toString() {
		return "User{" + "id=" + getId() + "usernameo=" + username + ", email='" + email.replaceFirst("@.*", "@***")
				+ ", password='" + passwordHash.substring(0, 10) + ", role=" + role + '}';
	}
	
	

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public String getPassword() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
