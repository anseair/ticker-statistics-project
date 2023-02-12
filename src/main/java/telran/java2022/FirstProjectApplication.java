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

		List<Ticker> newDatas = TickerCsvParsing.parsingWithApache("tesla_5years.csv", "tesla", "yyyy-MM-dd", 4);
		List<Ticker> newDatasM = TickerCsvParsing.parsingWithApache("microsoft_5years.csv", "microsoft", "yyyy-MM-dd",4);
		List<Ticker> newDatasS = TickerCsvParsing.parsingWithApache("sandp_5years.csv", "sandp", "yyyy-MM-dd",4);
		List<Ticker> newDatasG = TickerCsvParsing.parsingWithApache("gold_5years.csv", "gold", "yyyy-MM-dd",4);


		System.out.println("amount of data tesla in csv: " + newDatas.size());
		System.out.println("amount of data microsoft in csv: " + newDatasM.size());
		System.out.println("amount of data sandp in csv: " + newDatasS.size());
		System.out.println("amount of data gold in csv: " + newDatasG.size());


		List<Ticker> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("amount data before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			oldDatas.addAll(newDatasM);
			oldDatas.addAll(newDatasS);
			oldDatas.addAll(newDatasG);

			repository.saveAll(oldDatas);

			System.out.println("total data in db: " + oldDatas.size());

		} else {
			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);
			
			notExistsDatas = newDatasM.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);
			
			notExistsDatas = newDatasS.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);
			
			notExistsDatas = newDatasG.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);

			int res = notExistsDatas.size() + oldDatas.size();
			System.out.println("amount new data  added in db: " + notExistsDatas.size());
			System.out.println("total data in db : " + res);
		}

	}
	


}
