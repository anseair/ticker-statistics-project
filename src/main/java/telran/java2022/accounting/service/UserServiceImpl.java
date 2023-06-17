package telran.java2022.accounting.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.plaf.synth.SynthScrollPaneUI;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dao.UserRepository;
import telran.java2022.accounting.dto.RolesChangeDto;
import telran.java2022.accounting.dto.UserDto;
import telran.java2022.accounting.dto.UserRegisterDto;
import telran.java2022.accounting.dto.UserUpdateDto;
import telran.java2022.accounting.exceptions.UserExistException;
import telran.java2022.accounting.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.User;
import telran.java2022.accounting.model.UserId;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, CommandLineRunner {
	final UserRepository repository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (repository.existsById(userRegisterDto.getUser())) {
			throw new UserExistException();
		}
		String patternlogin= "(?=.+[a-zA-Z])\\w{5,}";
		boolean checkLogin = userRegisterDto.getUser().getLogin().matches(patternlogin);
//		System.out.println("Login is not valid. Please enter a valid login.");

		String patternEmail = "^\\w(\\w|\\.|-|_)*@\\w(\\w|\\.|-)*\\.[A-Za-z]{2,6}";
		boolean checkEmail = userRegisterDto.getUser().getEmail().matches(patternEmail);
//		System.out.println("Email is not valid. Please enter a valid email address.");

		String patternPassword = "(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])\\w{8,16}";
		boolean checkPassword = userRegisterDto.getPassword().matches(patternPassword);
//		System.out.println("Password must be 8-16 characters long, contain one upper case letter, one lower case letter, one digit");
	
		String patternName = "([a-zA-Z])+(-)*\\w{1,}";
		boolean checkFirstName = userRegisterDto.getFirstName().matches(patternName);
//		System.out.println("First name is not valid. Please enter a valid first name.");

		boolean checkLastName = userRegisterDto.getLastName().matches(patternName);
//		System.out.println("Last name is not valid. Please enter a valid last name.");

		String password = passwordEncoder.encode(userRegisterDto.getPassword());
		
		User user = null;
		if((checkLogin == true) && (checkEmail == true) && (checkPassword == true) && (checkFirstName == true) && (checkLastName == true)) {
			user = new User(userRegisterDto.getUser(), password,
					userRegisterDto.getFirstName(), userRegisterDto.getLastName());
			repository.save(user);
		}
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto login(String login) {
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
				user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto deleteUser(String login) {
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		repository.delete(user);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto updateUser(String login, UserUpdateDto updateDto) {
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		if (updateDto.getFirstName() != null) {
			user.setFirstName(updateDto.getFirstName());
		}
		if (updateDto.getLastName() != null) {
			user.setLastName(updateDto.getLastName());
		}
		repository.save(user);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public RolesChangeDto changeRoles(String login, String role, boolean isAddRole) {
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		if (isAddRole) {
			user.addRole(role);
		} else {
			user.removeRole(role);
		}
		repository.save(user);
		return modelMapper.map(user, RolesChangeDto.class);
	}
	
	@Override
	public void changePassword(String login, String newPassword) {
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		String password = passwordEncoder.encode(newPassword);
		user.setPassword(password);
		repository.save(user);
	}	
	
	@Override
	public void run(String... args) throws Exception {
		UserId admin = new UserId("admin", "admin@gmail.com");
		if(!repository.existsById(admin)) {
			String password = passwordEncoder.encode("admin");
			User user = new User(admin, password, "admin", "admin");
			user.addRole("administrator");
			user.addRole("moderator");
			repository.save(user);
		}
		
	}

}
