package telran.java2022;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StaticProjectApplication implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(StaticProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
