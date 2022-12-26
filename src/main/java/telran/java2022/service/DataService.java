package telran.java2022.service;


import telran.java2022.dto.DataDto;
import telran.java2022.model.HistoricalData;

public interface DataService {

	DataDto add(String date);
	
	DataDto remove(String date);

	DataDto findByDate(String date);
}
