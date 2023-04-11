package telran.java2022.accounting.service;


import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, CommandLineRunner {
	final UserRepository repository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (repository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistException();
		}
		String password = passwordEncoder.encode(userRegisterDto.getPassword());
		User user = new User(userRegisterDto.getLogin(), password,
				userRegisterDto.getFirstName(), userRegisterDto.getLastName());
		repository.save(user);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto login(String login) {
		User user = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto deleteUser(String login) {
		User user = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		repository.deleteById(login);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto updateUser(String login, UserUpdateDto updateDto) {
		User user = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
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
		User user = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
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
		User user = repository.findById(login).orElseThrow(() -> new UserNotFoundException());
		String password = passwordEncoder.encode(newPassword);
		user.setPassword(password);
		repository.save(user);
	}
	
	@Override
	public void run(String... args) throws Exception {
		if(!repository.existsById("admin")) {
			String password = passwordEncoder.encode("admin");
			User user = new User("admin", password , "", "");
			user.addRole("MODERATOR");
			user.addRole("ADMINISTRATOR");
			repository.save(user);
		}
		
	}
	
	
	

}
