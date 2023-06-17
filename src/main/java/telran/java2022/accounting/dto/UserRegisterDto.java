package telran.java2022.accounting.dto;

import lombok.Getter;
import telran.java2022.accounting.model.UserId;

@Getter
public class UserRegisterDto {
	UserId user;
	String password;
	String firstName;
	String lastName;
}
