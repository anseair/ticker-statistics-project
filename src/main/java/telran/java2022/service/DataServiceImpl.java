package telran.java2022.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		if (dataRepository.existsById(historicalData.getDate())) {
			throw new DateExistsException();
		}
		HistoricalData data = new HistoricalData(historicalData.getDate(), historicalData.getClose_last(),
				historicalData.getVolume(), historicalData.getOpen(), historicalData.getHigh(),
				historicalData.getLow());
		List<HistoricalData> datas = new ArrayList<>();
		datas.add(data);
		dataRepository.saveAll(datas);
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
		
//		List<HistoricalData> datas = CsvFileParsing.parsingWithApache();
//
////		List<String> dates = CsvFileParsing.parsingWithApache().stream().map(HistoricalData::getDate)
////				.collect(Collectors.toList());
//		if (!dataRepository.existsById(datas.parallelStream().map(HistoricalData::getDate).findAny().get())) {
//			dataRepository.saveAll(CsvFileParsing.parsingWithApache());
//		}
//		System.out.println("Document already exists with _id: ");
		
		dataRepository.deleteAll();
		dataRepository.saveAll(CsvFileParsing.parsingWithApache());
	}

}
