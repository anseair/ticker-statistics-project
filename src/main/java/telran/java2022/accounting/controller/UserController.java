package telran.java2022.accounting.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dto.UserDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
@Component
public class UserController {
	
	final UserService service;
    
	@CrossOrigin
	@PostMapping("/register")
	public UserDto register(@RequestBody UserRegisterDto userRegisterDto) {
		return service.register(userRegisterDto);
	}
	
	@CrossOrigin
	@PostMapping("/login")
	public UserDto login(Principal principal) {
		return service.login(principal.getName());
	}
	
	@CrossOrigin
	@DeleteMapping("/delete/{login}")
	public UserDto deleteUser(@PathVariable String login) {
		return service.deleteUser(login);
	}
	
	@CrossOrigin
	@PutMapping("/user/{login}")
	public UserDto updateUser(@PathVariable String login, @RequestBody UserUpdateDto userUpdateDto) {
		return service.updateUser(login, userUpdateDto);
	}
	
	@CrossOrigin
	@PutMapping("/changeRole/{login}/role/{role}")
	public UserDto addRole(@PathVariable String login, @PathVariable String role) {
		return service.changeRoles(login, role, true);
	}
	
	@CrossOrigin
	@DeleteMapping("/changeRole/{login}/role/{role}")
	public UserDto deleteRole(@PathVariable String login, @PathVariable String role) {
		return service.changeRoles(login, role, false);
	}
	
	@CrossOrigin
	@PutMapping("/changePassword/user/{login}")
	public void changePassword(@PathVariable String login, @RequestHeader("X-Password") String newPassword) {
		service.changePassword(login, newPassword);
	}
	
	@CrossOrigin
	@PostMapping("/resetPassword")
	public String processResetPasswordToken(@RequestParam String email, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		service.updateResetPasswordToken(email, request);
		return "forgot_password_form";
	}


//	@CrossOrigin
//	@PostMapping("/resetPassword/")
//	public String processResetPassword(HttpServletRequest request) {
//		String token = request.getParameter("token");
//		String password = request.getParameter("password");
//		User user = service.get(token);
//		if (user != null) {
//			service.changePassword(user.getUser().getEmail(), password);
//		}
//		return "reset_oassword_form";
//	}
	

	
	

}
