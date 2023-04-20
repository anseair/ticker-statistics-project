package telran.java2022;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import telran.java2022.accounting.dao.UserRepository;
import telran.java2022.ticker.dao.TickerRepository;

@SpringBootApplication
public class StaticProjectApplication implements CommandLineRunner{

	@Autowired
	TickerRepository repository;
	
	UserRepository userRepository;
	
	PasswordEncoder encoder;
	
	public static void main(String[] args) {
		SpringApplication.run(StaticProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}
