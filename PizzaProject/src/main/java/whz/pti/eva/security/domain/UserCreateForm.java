package whz.pti.eva.security.domain;

import jakarta.validation.constraints.NotEmpty;

public class UserCreateForm {

	@NotEmpty
	private String username = "";

	@NotEmpty
	private String email = "";

	@NotEmpty
	private String password = "";

	@NotEmpty()
	private String passwordRepeated = "";
	
//    @NotNull
	private Role role = Role.CUSTOMER;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return password;
	}



	public String getPasswordRepeated() {
		return passwordRepeated;
	}

	public void setPasswordRepeated(String passwordRepeated) {
		this.passwordRepeated = passwordRepeated;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserCreateForm{" + "email='" + email.replaceFirst("@.+", "@***") + '\'' + ", password=***" + '\''
				+ ", passwordRepeated=***" + '\'' + ", role=" + role + ", username=" + username + '}';

	}

}
