package telran.java2022.sandp.exceptoins;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ResponseStatus(HttpStatus.CONFLICT)
public class DateExistsException  extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2643181933891861796L;



}
