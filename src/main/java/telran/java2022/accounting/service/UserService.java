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
	
	void updateResetPasswordToken(String email, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException;
	
	User get(String resetPasswordToken);
}
