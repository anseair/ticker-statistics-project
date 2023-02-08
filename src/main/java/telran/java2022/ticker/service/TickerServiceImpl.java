package telran.java2022.ticker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.sandp.exceptoins.DateExistsException;
import telran.java2022.sandp.exceptoins.NotFoundException;
import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickerStatDto;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;
import telran.java2022.utils.TickerCsvParsing;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TickerService, CommandLineRunner {
	final TickerRepository repository;
	final ModelMapper modelMapper;

	@Override
	public TickerDto add(TickerDto tickerDto) {
		if (repository.existsById(modelMapper.map(tickerDto.getDate(), TickerId.class))) {
			throw new DateExistsException();
		}
		Ticker ticker = modelMapper.map(tickerDto, Ticker.class);
		repository.save(ticker);
		return modelMapper.map(ticker, TickerDto.class);
	}

	@Override
	public TickerDto remove(TickerId date) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new NotFoundException());
		repository.deleteById(date);
		return modelMapper.map(ticker, TickerDto.class);
	}

	@Override
	public TickerDto findByDate(TickerId date) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new NotFoundException());
		return modelMapper.map(ticker, TickerDto.class);
	}

	@Override
	public TickerDto update(TickerId date, double priceClose) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new NotFoundException());
		ticker.setPriceClose(priceClose);
		repository.save(ticker);
		return modelMapper.map(ticker, TickerDto.class);
	}
	
	@Override
	public TickerDto findMaxPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name) {
		Ticker s = repository.findByDateDateBetween(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name))
				.max((s1,s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose()))
				.orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	@Override
	public TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name) {
		Ticker s = repository.findByDateDateBetween(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name))
				.min((s1,s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose()))
				.orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	@Override
	public TickerStatDto getStat(long periodDays, double sum, long termDays, String name) {
		LocalDate dateStart = LocalDate.now().minusDays(periodDays + termDays);
		List<Double> tickerPStats = new ArrayList<>();
		LocalDate dateEnd = LocalDate.now().minusDays(termDays);
		List<Ticker> tickerPeriods = repository.findByDateDateBetweenOrderByDate(dateStart, LocalDate.now()).filter(t -> t.getDate().getName().equals(name))
				.collect(Collectors.toList());
		
		tickerPeriods.forEach(e -> System.out.println(e));
		
		int end = tickerPeriods.indexOf(new Ticker(new TickerId(name, dateEnd), 0.0));
		while(end < 0) {
			end = tickerPeriods.indexOf(new Ticker(new TickerId(name, dateEnd.minusDays(1)), 0.0));
		}
		for (int start = 0; start < end; start++) {

			System.out.println("======================");
			System.out.println("index dateStart in list = " + start);
			System.out.println("sandpStart: " + tickerPeriods.get(start));

			LocalDate dateEndOfPeriod = tickerPeriods.get(start).getDate().getDate().plusDays(termDays);
			while (!repository.existsById(new TickerId(name, dateEndOfPeriod))) {
				dateEndOfPeriod = dateEndOfPeriod.minusDays(1);
			}
			Ticker tickerEnd = repository.findById(new TickerId(name, dateEndOfPeriod)).get();
			
			int endIndex = tickerPeriods.indexOf(tickerEnd);

			System.out.println("SandpEnd: " + tickerEnd);
			System.out.println("index dateEnd in list = " + endIndex);

			Double apy = (tickerEnd.getPriceClose()/tickerPeriods.get(start).getPriceClose()-1) / (termDays/365.0);

			System.out.println("==============");
			System.out.println("apy: " + apy);
			
			tickerPStats.add(apy);
			
			System.out.println("sandpStats: " + tickerPStats);
		}
		double minPercent = tickerPStats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = tickerPStats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double minRevenue = sum * (minPercent * ((termDays * 1.0 / 365) / 100.0) + 1);
		double maxRevenue = sum * (minPercent * ((termDays * 1.0 / 365) / 100.0) + 1);
		return new TickerStatDto(minPercent, maxPercent, minRevenue, maxRevenue);
	}

	@Override
	public void run(String... args) throws Exception {

		List<Ticker> notExistsDatas = new ArrayList<>();

		List<Ticker> newDatas = TickerCsvParsing.parsingWithApache("tesla_5years.csv", "tesla", "yyyy-MM-dd", 4);
		List<Ticker> newDatasM = TickerCsvParsing.parsingWithApache("microsoft_5years.csv", "microsoft", "yyyy-MM-dd", 4);

		System.out.println("amount of data tesla in csv: " + newDatas.size());		
		System.out.println("amount of data microsoft in csv: " + newDatasM.size());

		List<Ticker> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("amount data before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			oldDatas.addAll(newDatasM);
			repository.saveAll(oldDatas);

			System.out.println("total data in db: " + oldDatas.size());

		} else {
			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);
			notExistsDatas = newDatasM.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
			repository.saveAll(notExistsDatas);

			int res = notExistsDatas.size() + oldDatas.size();
			System.out.println("amount new data  added in db: " + notExistsDatas.size());
			System.out.println("total data in db : " + res);
		}


	}



}
