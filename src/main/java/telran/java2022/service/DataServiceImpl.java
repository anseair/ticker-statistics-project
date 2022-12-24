package telran.java2022.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.DataRepository;
import telran.java2022.dto.DataDto;
import telran.java2022.dto.DateDto;
import telran.java2022.exceptoins.DateExistsException;
import telran.java2022.exceptoins.DateNotFoundExceptions;
import telran.java2022.model.HistoricalData;
import telran.java2022.utils.CsvFileParsing;

@RequiredArgsConstructor
@Service
public class DataServiceImpl implements DataService, CommandLineRunner {
	final DataRepository dataRepository;
	final ModelMapper modelMapper;

	@Override
	public boolean add(HistoricalData historicalData) {
//		if (dataRepository.existsById(historicalData.getDate())) {
//			throw new DateExistsException();
//		}
//		HistoricalData data = new HistoricalData();
//		dataRepository.save(data);
		return true;
	}

	@Override
	public DataDto findByDate(String date) {
//		LocalDateTime dateTime = LocalDateTime.of(date.getDate().getYear(),date.getDate().getMonth(), date.getDate().getDayOfMonth(), 00, 00);
		HistoricalData data = dataRepository.findById(date).orElseThrow(() -> new DateNotFoundExceptions());
		return modelMapper.map(data, DataDto.class);
	}

	@Override
	public void run(String... args) throws Exception {
		dataRepository.saveAll(CsvFileParsing.parsingWithApache());
	}

}
