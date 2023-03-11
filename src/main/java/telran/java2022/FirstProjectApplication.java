package telran.java2022;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.utils.TickerCsvParsing;

@SpringBootApplication
public class FirstProjectApplication implements CommandLineRunner{

	@Autowired
	TickerRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		List<Ticker> notExistsDatas = new ArrayList<>();
		List<Ticker> newDatas = new ArrayList<>();
		newDatas.addAll(TickerCsvParsing.parsingWithApache("tsla.csv", "tesla", "yyyy-MM-dd", 4));
		newDatas.addAll(TickerCsvParsing.parsingWithApache("msft.csv", "microsoft", "yyyy-MM-dd",4));
		newDatas.addAll(TickerCsvParsing.parsingWithApache("spx.csv", "sandp", "yyyy-MM-dd",4));
		newDatas.addAll(TickerCsvParsing.parsingWithApache("gold.csv", "gold", "yyyy-MM-dd",4));
		
		System.out.println("amount of data in csv: " + newDatas.size());

		List<Ticker> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("amount data before add in db: " + oldDatas.size());
		
		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			repository.saveAll(oldDatas);
		} else {
			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);
		}
		System.out.println("amount data after add in db: " + oldDatas.size());


	}
	


}
