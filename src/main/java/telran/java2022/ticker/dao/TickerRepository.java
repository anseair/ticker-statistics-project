package telran.java2022.ticker.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;

public interface TickerRepository extends CrudRepository<Ticker, TickerId>{

	Stream<Ticker> findTickerByDateName(String name);
	
	Stream<Ticker> findTickerByDateNameAndDateDateBetween(String name, LocalDate dateFrom, LocalDate dateTo);

	Stream<Ticker> findTickerByDateDateBetweenOrderByDateDate(LocalDate dateFrom, LocalDate dateTo);

	Stream<Ticker> findQueryByDateNameAndDateDateBetweenOrderByDateDate(String name, LocalDate dateFrom, LocalDate dateTo);

}
