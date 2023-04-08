package telran.java2022.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistException extends RuntimeException{

	private static final long serialVersionUID = 4358435113079372555L;

	public UserExistException() {
		super("User already exists");
	}
}
