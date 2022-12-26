package telran.java2022.service;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.DataRepository;
import telran.java2022.dto.DataDto;
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
	public DataDto add(String date) {
		if (dataRepository.existsById(date)) {
			throw new DateExistsException();
		}
		List<HistoricalData> csv = CsvFileParsing.parsingWithApache();
		HistoricalData data = csv.stream().filter(d -> d.getDate().equals(date)).findAny().get();
		dataRepository.save(data);
		return modelMapper.map(data, DataDto.class);
	}

	@Override
	public DataDto remove(String date) {
		HistoricalData data = dataRepository.findById(date).orElseThrow(() -> new DateNotFoundExceptions());
		dataRepository.delete(data);
		return modelMapper.map(data, DataDto.class);
	}

	@Override
	public DataDto findByDate(String date) {
//		LocalDateTime dateTime = LocalDateTime.of(date.getDate().getYear(),date.getDate().getMonth(), date.getDate().getDayOfMonth(), 00, 00);
		HistoricalData data = dataRepository.findById(date).orElseThrow(() -> new DateNotFoundExceptions());
		return modelMapper.map(data, DataDto.class);
	}

	
//	===================== VARIANT 1: deleteAll ========================= 
//	@Override
//	public void run(String... args) throws Exception {
//		dataRepository.deleteAll();
//		dataRepository.saveAll(CsvFileParsing.parsingWithApache());
//	}

	
	
//	===================== VARIANT 2: add in set ============================
//	@Override
//	public void run(String... args) throws Exception {
//
//		List<HistoricalData> newDatas = CsvFileParsing.parsingWithApache();
//
//		System.out.println("new datas: " + newDatas.size());
//
//		Set<HistoricalData> oldDatas = StreamSupport.stream(dataRepository.findAll().spliterator(), false)
//				.collect(Collectors.toSet());
//
//		System.out.println("before add in db: " + oldDatas.size());
//
//		if (oldDatas.isEmpty()) {
//			oldDatas.addAll(newDatas);
//			dataRepository.saveAll(oldDatas);
//	
//			System.out.println("after add in db: " + oldDatas.size());
//
//		} else {
//			if (!newDatas.containsAll(oldDatas)) {
//				oldDatas.addAll(newDatas);
//				dataRepository.saveAll(oldDatas);
//	
//				System.out.println("after add in db: " + oldDatas.size());
//			}
//		}
//	}

	
	
	
//	===================== VARIANT 3: add in list by stream ======================
	@Override
	public void run(String... args) throws Exception {

		List<HistoricalData> notExistsDatas = new ArrayList<>();

		List<HistoricalData> newDatas = CsvFileParsing.parsingWithApache();

		System.out.println("new datas: " + newDatas.size());

		List<HistoricalData> oldDatas = StreamSupport.stream(dataRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			dataRepository.saveAll(oldDatas);

			System.out.println("after add in db: " + oldDatas.size());

		} else {
//			if (!newDatas.containsAll(oldDatas)) {
				notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
						.collect(Collectors.toList());
//				oldDatas.addAll(0, notExistsDatas);
				dataRepository.saveAll(notExistsDatas);

				System.out.println("new datas in db: " + notExistsDatas.size());
//			}
		}

	}

}
