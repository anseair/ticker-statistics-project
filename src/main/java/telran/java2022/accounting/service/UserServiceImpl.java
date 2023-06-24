package telran.java2022.accounting.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dao.UserRepository;
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
	final JavaMailSender mailSender;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (repository.existsById(userRegisterDto.getUser())) {
			throw new UserExistException();
		}
		String patternlogin= "(?=.+[a-zA-Z0-9])\\w{2,}";
		boolean checkLogin = userRegisterDto.getUser().getLogin().matches(patternlogin);
//		if (checkLogin == false) {
		System.out.println(checkLogin);
//		}

		String patternEmail = "^\\w(\\w|\\.|-|_)*@\\w(\\w|\\.|-)*\\.[A-Za-z]{2,6}";
		boolean checkEmail = userRegisterDto.getUser().getEmail().matches(patternEmail);
//		if (checkEmail == false) {
		System.out.println(checkEmail);
//		}

		String patternPassword = "[a-zA-Z0-9](?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])\\w{8,16}";
		boolean checkPassword = userRegisterDto.getPassword().matches(patternPassword);
//		if (checkPassword == false) {
		System.out.println(checkPassword);
//		}
	
		String patternName = "([a-zA-Z])+(-)*\\w{1,}";
		boolean checkFirstName = userRegisterDto.getFirstName().matches(patternName);
//		if (checkFirstName == false) {
		System.out.println(checkFirstName);
//		}

		boolean checkLastName = userRegisterDto.getLastName().matches(patternName);
//		if (checkLastName == false) {
		System.out.println(checkLastName);
//		}

		String password = passwordEncoder.encode(userRegisterDto.getPassword());
		
		User user = null;
		if((checkLogin == true) && (checkEmail == true) && (checkPassword == true) && (checkFirstName == true) && (checkLastName == true)) {
			user = new User(userRegisterDto.getUser(), password,
					userRegisterDto.getFirstName(), userRegisterDto.getLastName());
			repository.save(user);
		} else {
			if (checkLogin == false) {
				System.out.println("Login is not valid. Please enter a valid login.");
			} if (checkEmail == false) {
				System.out.println("Email is not valid. Please enter a valid email address.");
			} if (checkPassword == false) {
				System.out.println("Password must be 8-16 characters long, contain one upper case letter, one lower case letter, one digit");
			} if (checkFirstName == false) {
				System.out.println("First name is not valid. Please enter a valid first name.");
			} if (checkLastName == false) {
				System.out.println("Last name is not valid. Please enter a valid last name.");
			}
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
		String patternName = "([a-zA-Z])+(-)*\\w{1,}";
		if (updateDto.getFirstName() != null) {
			boolean checkFirstName = updateDto.getFirstName().matches(patternName);
			if (checkFirstName == false) {
				System.out.println("First name is not valid. Please enter a valid first name.");
			}
			user.setFirstName(updateDto.getFirstName());
		}
		if (updateDto.getLastName() != null) {
			boolean checkLastName = updateDto.getLastName().matches(patternName);
			if (checkLastName == false) {
				System.out.println("Last name is not valid. Please enter a valid last name.");
			}
			user.setLastName(updateDto.getLastName());
		}
		repository.save(user);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public UserDto changeRoles(String login, String role, boolean isAddRole) {
		User user = null;
		System.out.println(login);
		if (repository.existsByUserLogin(login)) {
			System.out.println("+++++++");
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
			System.out.println(user);
		} else if (repository.existsByUserEmail(login)) {
			System.out.println("+++++++");
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
			System.out.println(user);

		} else {
			System.out.println("--------");

			throw new UserNotFoundException();
		}
		if (isAddRole) {
			user.addRole(role);
		} else {
			user.removeRole(role);
		}
		System.out.println("!!!!!!!!");
		repository.save(user);
		return modelMapper.map(user, UserDto.class);
	}
	
	@Override
	public void changePassword(String login, String newPassword) {
		String patternPassword = "(?=.+[a-z])(?=.+[A-Z])(?=.+[0-9])\\w{8,16}";
		boolean checkPassword = newPassword.matches(patternPassword);
		System.out.println(checkPassword);
		
		
		User user = null;
		if (repository.existsByUserLogin(login)) {
			user = repository.findByUserLogin(login).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(login)) {
			user = repository.findByUserEmail(login).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		String password = passwordEncoder.encode(newPassword);

		if (checkPassword == false) {
			System.out.println("Password must be 8-16 characters long, contain one upper case letter, one lower case letter, one digit");
		}
		user.setPassword(password);
		user.setResetPasswordToken(null);
		repository.save(user);
		
	}	
	
	@Override
	public void run(String... args) throws Exception {
		UserId admin = new UserId("admin", "admin@gmail.com");
		if(!repository.existsById(admin)) {
			String password = passwordEncoder.encode("adminADMIN7");
			User user = new User(admin, password, "admin", "admin");
			user.addRole("administrator");
			user.addRole("moderator");
			repository.save(user);
		}
		
	}

	@Override
	public void updateResetPasswordToken(String token, String email) {
		User user = repository.findByUserEmail(email).orElseThrow(() -> new UserNotFoundException());
		if (user != null) {
			user.setResetPasswordToken(token);
			repository.save(user);
		} else {
			throw new UserNotFoundException();
		}
		
//		String resetPasswordLink = getSiteURL(request) + "/resetPassword/?=token" + token;
//		sendMail(email, resetPasswordLink);
	}
	
	@Override
	public void sendMail(String email, String resetPasswordLink) throws UnsupportedEncodingException, MessagingException {
//		SimpleMailMessage message = new SimpleMailMessage();
		Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.put("mail.man.com", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.trust","smtp.gmail.com");
        properties.put("mail.username", "golovchenkoanastasiya7@gmail.com");
		Session session = Session.getInstance(properties, 
        	    new javax.mail.Authenticator(){
        	        protected PasswordAuthentication getPasswordAuthentication() {
        	            return new PasswordAuthentication(
        	                "golovchenkoanastasiya7@gmail.com", "7Skazochnik2025");
        	        }
        	});
        session.setDebug(false);
		Transport transport = session.getTransport("smtp");
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("support@gmail.com", "Support");
		helper.setTo(email);
		String subject = "Here's the link to reset your password";
		String text = "<p>Hello,</p> " 
				+ "<p>Hello, you have requested to reset your password.</p>" 
				+ "<p>Click to the link below to change your password:</p>" 
				+ "<p><b><a href=\"" + resetPasswordLink + "\">Change my password</a><b></p>"
				+ "<p>Ignore this email  if you  do remember your  password, or  you have not made the request.</p>";
		helper.setSubject(subject);
		helper.setText(text, true);
//		FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
//		helper.addAttachment("Purchase Order", file);
//		mailSender.send(message);
		 transport.connect("smtp.gmail.com" , 587, "golovchenkoanastasiya7@gmail.com", "nfwotffdlstqehqh");
         transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
         transport.close();
	}
	
	@Override
	public User get(String resetPasswordToken) {
		return repository.findByResetPasswordToken(resetPasswordToken).orElseThrow(() -> new UserNotFoundException());
	}

	
}
