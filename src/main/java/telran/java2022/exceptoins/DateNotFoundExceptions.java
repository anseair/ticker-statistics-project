package telran.java2022.exceptoins;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DateNotFoundExceptions extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2272482300726886821L;

}
