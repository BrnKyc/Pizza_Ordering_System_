package whz.pti.eva.security.domain;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

	private User user;

	public CurrentUser(User user) {
		super(user.getUsername(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Long getId() {
		return user.getId();
	}

	public String getUsername() {
		return user.getUsername();
	}
	
	public Role getRole() {
		return user.getRole();
	}


	@Override
	public String toString() {
		return "CurrentUser{" + "user=" + user + "} " + super.toString();
	}

}
