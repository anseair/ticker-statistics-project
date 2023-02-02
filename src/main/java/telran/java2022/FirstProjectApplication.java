package telran.java2022;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;
import telran.java2022.sandp.dao.SandPRepository;

@SpringBootApplication
@RequiredArgsConstructor
public class FirstProjectApplication {
	final SandPRepository dataRepository;

	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}



}
