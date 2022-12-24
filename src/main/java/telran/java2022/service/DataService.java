package telran.java2022.service;

import java.time.LocalDate;

import telran.java2022.dto.DataDto;
import telran.java2022.dto.DateDto;
import telran.java2022.model.HistoricalData;

public interface DataService {

	boolean add(HistoricalData historicalData);
	
	DataDto findByDate(String date);
}
