package telran.java2022;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.inference.TTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;
import telran.java2022.utils.TickerCsvParsing;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@SpringBootApplication
public class FirstProjectApplication implements CommandLineRunner{

	@Autowired
	TickerRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
//		Stock intel = YahooFinance.get("INTC");		 
//		System.out.println("name: "+ intel.getName());
//		System.out.println("price : " + intel.getQuote().getPrice());
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.DATE, -30); 
		 
		Stock tesla = YahooFinance.get("TSLA");
		String name = tesla.getSymbol();
		List<HistoricalQuote> googleHistQuotes = tesla.getHistory(from, to, Interval.DAILY);
		System.out.println(googleHistQuotes);
		System.out.println(googleHistQuotes.size());
		
		googleHistQuotes.stream()
				.forEach(t-> System.out.println("date: " + t.getDate().toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate() 
				 + "; price: " + t.getClose()));
		List<Ticker> tickers = new ArrayList<>();	
		
//		List<Ticker> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
//				.collect(Collectors.toList());
//		if (oldDatas.isEmpty()) {
//			oldDatas.addAll(res);
//			repository.saveAll(oldDatas);
//		} else {
//			List<Ticker> notExistsDatas = res.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
//					.collect(Collectors.toList());
//			repository.saveAll(notExistsDatas);
//		}
		
		for (int i = 0; i < googleHistQuotes.size(); i++) {
			LocalDate date = googleHistQuotes.get(i).getDate().toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
			double price = googleHistQuotes.get(i).getClose().doubleValue();
			Ticker ticker = new Ticker(new TickerId(name, date), price);
			tickers.add(ticker);
		}
		System.out.println(tickers);
		System.out.println(tickers.size());
		repository.saveAll(tickers);
	

		
		
//======RUN ONLY FOR DOWNLOAD NEW DATA=============================================
		
//		List<Ticker> newDatas = new ArrayList<>();
//		newDatas.addAll(TickerCsvParsing.parsingWithApache("tsla.csv", "tesla", "yyyy-MM-dd", 4));
//		newDatas.addAll(TickerCsvParsing.parsingWithApache("msft.csv", "microsoft", "yyyy-MM-dd",4));
//		newDatas.addAll(TickerCsvParsing.parsingWithApache("spx.csv", "sandp", "yyyy-MM-dd",4));
//		newDatas.addAll(TickerCsvParsing.parsingWithApache("gold.csv", "gold", "yyyy-MM-dd",4));
//		newDatas.addAll(TickerCsvParsing.parsingWithApache("apple.csv", "apple", "yyyy-MM-dd",4));
//		
//		List<Ticker> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
//				.collect(Collectors.toList());
//		
//		if (oldDatas.isEmpty()) {
//			oldDatas.addAll(newDatas);
//			repository.saveAll(oldDatas);
//		} else {
//			List<Ticker> notExistsDatas = new ArrayList<>();
//			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
//					.collect(Collectors.toList());
//			repository.saveAll(notExistsDatas);
//		}
	}

}
