package telran.java2022.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.HistoricalData;

public interface DataRepository extends CrudRepository<HistoricalData, String> {

	HistoricalData findByDate(LocalDate date);
}
