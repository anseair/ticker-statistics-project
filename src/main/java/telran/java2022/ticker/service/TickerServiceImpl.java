package telran.java2022.ticker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.ticker.exceptions.AlreadyExistException;
import telran.java2022.ticker.exceptions.NotFoundException;
import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.dao.TickerStatRepository;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickerStatDto;
import telran.java2022.ticker.dto.TickerStatIdDto;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;
import telran.java2022.ticker.model.TickerIdStat;
import telran.java2022.ticker.model.TickerStat;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TickerService {
	final TickerRepository repository;
	final TickerStatRepository statRepository;
	final ModelMapper modelMapper;

	@Override
	public TickerDto add(TickerDto tickerDto) {
		if (repository.existsById(modelMapper.map(tickerDto.getDate(), TickerId.class))) {
			throw new AlreadyExistException();
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
				.max((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	@Override
	public TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name) {
		Ticker s = repository.findByDateDateBetween(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name))
				.min((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	@Override
	public TickerStatDto getStat(long periodDays, double sum, long termDays, String name) {
		LocalDate dateStart = LocalDate.now().minusDays(periodDays + termDays);
		List<Double> stats = new ArrayList<>();
		LocalDate dateEnd = LocalDate.now().minusDays(termDays);
		List<Ticker> tickerPeriods = repository.findByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name)).collect(Collectors.toList());

//		tickerPeriods.forEach(e -> System.out.println(e));
//		System.out.println(tickerPeriods.size());

		int end = tickerPeriods.indexOf(new Ticker(new TickerId(name, dateEnd), 0.0));
		while (end < 0) {
			end = tickerPeriods.indexOf(new Ticker(new TickerId(name, dateEnd.minusDays(1)), 0.0));
		}
		LocalDate dateEndOfPeriod = null;
		TickerId tickerIdEnd = new TickerId(name, LocalDate.now());
		Ticker tickerEnd = new Ticker(tickerIdEnd, 0.0);
		for (int start = 0; start < end; start++) {

//			System.out.println("======================");
//			System.out.println("index dateStart in list = " + start);
//			System.out.println("TickerStart: " + tickerPeriods.get(start));

			dateEndOfPeriod = tickerPeriods.get(start).getDate().getDate().plusDays(termDays);
			tickerIdEnd.setDate(dateEndOfPeriod);
			tickerEnd.setDate(tickerIdEnd);
			int indexEnd = tickerPeriods.indexOf(tickerEnd);

			while (indexEnd < 0) {
				dateEndOfPeriod = dateEndOfPeriod.minusDays(1);
				tickerIdEnd.setDate(dateEndOfPeriod);
				tickerEnd.setDate(tickerIdEnd);
				indexEnd = tickerPeriods.indexOf(tickerEnd);
			}

//			System.out.println("TickerpEnd: " + tickerEnd);
//			System.out.println("index dateEnd in list = " + indexEnd);

			Double apy = (tickerPeriods.get(indexEnd).getPriceClose() - tickerPeriods.get(start).getPriceClose())
					/ tickerPeriods.get(start).getPriceClose() * 100 * (365.0 / termDays);

//			System.out.println("==============");
//			System.out.println("apy: " + apy);

			stats.add(apy);

//			System.out.println("stats: " + stats);
		}
		double minPercent = stats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = stats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double avgPercent = stats.stream().max((s1, s2) -> Double.compare(s1, s2)).get()/2;
		double minRevenue = sum * (minPercent / 100) + sum;
		double maxRevenue = sum * (maxPercent / 100) + sum;
		double avgRevenue = sum * (maxPercent / 100) + sum/2;


//		List<TickerStat> tickerStats = StreamSupport.stream(statRepository.findAll().spliterator(), false)
//				.collect(Collectors.toList());
//
//		if (statRepository.existsById(new TickerIdStat(name, termDays, periodDays))) {
//			throw new AlreadyExistException();
//		}
//		tickerStats.add(new TickerStat(new TickerIdStat(name, termDays, periodDays), minPercent, maxPercent, minRevenue, maxRevenue));
//		statRepository.saveAll(tickerStats);
//		
//		System.out.println("TickerStats: " + tickerStats);
//		System.out.println(tickerStats.size());

		return new TickerStatDto(new TickerStatIdDto(name, termDays, periodDays), minPercent, maxPercent, avgPercent, minRevenue, maxRevenue, avgRevenue);
	}
	
	@Override
	public double getCorrelation(String name1, String name2, int termDays) {
		LocalDate dateStart = LocalDate.now().minusDays(termDays);
		List<Double> tickersFirst = repository.findByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name1))
				.map(t->t.getPriceClose())
				.collect(Collectors.toList());
		List<Double> tickersSecond = repository.findByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name2))
				.map(t->t.getPriceClose())
				.collect(Collectors.toList());
		double avrX = tickersFirst.stream()
				.reduce(0.0, (x,y) -> x + y);
		avrX = avrX / tickersFirst.size();
		double avrY = tickersSecond.stream()
				.reduce(0.0, (x,y) -> x + y);
		avrY = avrY / tickersSecond.size();
		double avrXY = tickersFirst.stream()
				.map(t-> t * tickersSecond.get(tickersFirst.indexOf(t)))
				.reduce(0.0, (x, y) -> x + y);
		avrXY = avrXY / tickersFirst.size();
		double tX = tickersFirst.stream()
				.map(t->t*t)
				.reduce(0.0, (x,y)->x+y);
		tX = tX / tickersFirst.size();
		tX = tX - avrX * avrX;
		tX = Math.sqrt(tX);
		double tY = tickersSecond.stream()
				.map(t->t*t)
				.reduce(0.0, (x,y)->x+y);
		tY = tY / tickersSecond.size();
		tY = tY - avrY * avrY;
		tY = Math.sqrt(tY);
		double res = (avrXY - (avrX * avrY)) / (tX * tY);
		return res;
	}

}
