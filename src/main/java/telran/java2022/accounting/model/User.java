package telran.java2022.accounting.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Document(collection = "Users")
@EqualsAndHashCode(of = "user")
public class User {
	@Id
	@Setter
	private UserId user; 
	@Setter
	private String password;
	@Setter
	private String firstName;
	@Setter
	private String lastName;
	@Setter
	private Set<String> roles = new HashSet<>();
	
	public User() {
		roles.add("user");
	}
	
	public User(UserId user, String password, String firstName, String lastName) {
		this();
		this.user = user;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}

}
