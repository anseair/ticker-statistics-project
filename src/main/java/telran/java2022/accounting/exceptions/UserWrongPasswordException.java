package telran.java2022.accounting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class UserWrongPasswordException extends RuntimeException{

	private static final long serialVersionUID = -6293594850330923396L;

	public UserWrongPasswordException () {
		super("Wrong password");

	}
}
