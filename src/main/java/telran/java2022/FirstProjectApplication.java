package telran.java2022;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;
import telran.java2022.sandp.dao.SandPRepository;
import telran.java2022.utils.CsvFileParsing;

@SpringBootApplication
@RequiredArgsConstructor
public class FirstProjectApplication {
	final SandPRepository dataRepository;

	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		dataRepository.saveAll(CsvFileParsing.parsingWithApache());
//
//	}

}
