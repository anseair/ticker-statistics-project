package telran.java2022.accounting.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import telran.java2022.accounting.dto.UserDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.model.User;

public interface UserService {
	UserDto register(UserRegisterDto userRegisterDto);
	
	UserDto login(String login);
	
	UserDto deleteUser(String login);
	
	UserDto updateUser(String login, UserUpdateDto updateDto);
	
	UserDto changeRoles(String login, String role, boolean isAddRole);
	
	void changePassword(String login, String newPassword);
	
	void updateResetPasswordToken(String token, String email);
	
	User get(String resetPasswordToken);

	void sendMail(String email, String resetPasswordLink) throws UnsupportedEncodingException, MessagingException;
}
