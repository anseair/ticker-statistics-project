package telran.java2022.ticker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class AlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3073895933329952506L;

	public AlreadyExistException() {
		super("Ticker already exist");
	}
	
}
