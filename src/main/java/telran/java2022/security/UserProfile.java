package telran.java2022.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserProfile extends User {


	private static final long serialVersionUID = -6152345071555058220L;

	public UserProfile(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

}
