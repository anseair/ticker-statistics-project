package telran.java2022.ticker.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.ticker.exceptions.TickerExistException;
import telran.java2022.ticker.exceptions.TickerNotFoundException;
import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.LastPriceDto;
import telran.java2022.ticker.dto.MaxStatDto;
import telran.java2022.ticker.dto.MinStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickersMinMaxDto;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TickerService {
	final TickerRepository repository;
	final ModelMapper modelMapper;

	@Override
	public TickerDto add(TickerDto tickerDto) {
		if (repository.existsById(modelMapper.map(tickerDto.getDate(), TickerId.class))) {
			throw new TickerExistException();
		}
		Ticker ticker = modelMapper.map(tickerDto, Ticker.class);
		repository.save(ticker);
		return modelMapper.map(ticker, TickerDto.class);
	}

	@Override
	public TickerDto deleteByDate(TickerId date) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new TickerNotFoundException());
		repository.deleteById(date);
		return modelMapper.map(ticker, TickerDto.class);
	}
	
	@Override
	public int deleteAllTickersByName(String name) {
//		return repository.deleteAllTickersByDateName(name);
		
		List<Ticker> ticker = repository.findTickerByDateName(name).collect(Collectors.toList());
		System.out.println(ticker.size());
		if (ticker.size() == 0) {
			return 0;
		}
		repository.deleteAll(ticker);
		return ticker.size();
	}
	
	@Override
	public TickerDto findByDate(TickerId date) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new TickerNotFoundException());
		return modelMapper.map(ticker, TickerDto.class);
	}

	@Override
	public TickerDto update(TickerId date, double priceClose) {
		Ticker ticker = repository.findById(date).orElseThrow(() -> new TickerNotFoundException());
		ticker.setPriceClose(priceClose);
		repository.save(ticker);
		return modelMapper.map(ticker, TickerDto.class);
	}
	
	/**
	 * Statistic with LocalDate from - to
	 */
	@Override
	public FullStatDto statistic(String name, DateBetweenDto dateBetweenDto, double sum, long depositPeriodDays) {
		List<Double> allStats = new ArrayList<>();
		List<Ticker> allPeriod = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());		
		
//		allPeriod.forEach(t ->System.out.println(t));
//		System.out.println(allPeriod.size());
		
		List<Ticker> datesOfEnds = new ArrayList<>();
		LocalDate dateEnd = dateBetweenDto.getDateTo().minusDays(depositPeriodDays);;
		int end = allPeriod.indexOf(new Ticker(new TickerId(name, dateEnd), 0.0));
		while (end < 0) {
			end = allPeriod.indexOf(new Ticker(new TickerId(name, dateEnd.minusDays(1)), 0.0));
		}
		LocalDate dateEndOfPeriod = null;
		TickerId tickerIdEnd = new TickerId(name, LocalDate.now());
		Ticker tickerEnd = new Ticker(tickerIdEnd, 0.0);
		for (int start = 0; start < end; start++) {		
			
//			System.out.println("======================");
//			System.out.println("index dateStart in list = " + start);
//			System.out.println("TickerStart: " + allPeriod.get(start));
			
			dateEndOfPeriod = allPeriod.get(start).getDate().getDate().plusDays(depositPeriodDays);
			tickerIdEnd.setDate(dateEndOfPeriod);
			tickerEnd.setDate(tickerIdEnd);
			int indexEnd = allPeriod.indexOf(tickerEnd);
			while (indexEnd < 0) {
				dateEndOfPeriod = dateEndOfPeriod.minusDays(1);
				tickerIdEnd.setDate(dateEndOfPeriod);
				tickerEnd.setDate(tickerIdEnd);
				indexEnd = allPeriod.indexOf(tickerEnd);
			}
			
//			System.out.println("index dateEnd in list = " + indexEnd);
//			System.out.println("TickerEnd: " + tickerEnd);
			
			Double apr = (allPeriod.get(indexEnd).getPriceClose() - allPeriod.get(start).getPriceClose())
					/ allPeriod.get(start).getPriceClose(); 
			Double apy  = 100 * (Math.pow(1+ apr, 365.0/depositPeriodDays) - 1); 

//			System.out.println("end: " + allPeriod.get(indexEnd).getDate().getDate() + " - " + allPeriod.get(indexEnd).getPriceClose());
//			System.out.println("start: " + allPeriod.get(start).getDate().getDate() + " - " +  allPeriod.get(start).getPriceClose());
//			System.out.println("apy: " + apy);
			
			allStats.add(apy);
			datesOfEnds.add(allPeriod.get(indexEnd));
			
//			System.out.println("stats: " + allStats);

		}
		
		double minPercent = allStats.stream().min((p1, p2) -> Double.compare(p1, p2)).orElse(null);
		double maxPercent = allStats.stream().max((p1, p2) -> Double.compare(p1, p2)).orElse(null);
		double minRevenue = sum * (minPercent / 100) + sum;
		double maxRevenue = sum * (maxPercent / 100) + sum;
		
		int indexMin = allStats.indexOf(minPercent);
		int indexMax = allStats.indexOf(maxPercent);
		
		double avgPercent = allStats.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		double avgRevenue = sum * (avgPercent/100) + sum;		
		String[] names = {name};
		return new FullStatDto(names, depositPeriodDays, sum,
				new MinStatDto(
						allPeriod.get(indexMin).getDate().getDate(),
						datesOfEnds.get(indexMin).getDate().getDate(),
						allPeriod.get(indexMin).getPriceClose(),
						datesOfEnds.get(indexMin).getPriceClose(),
						minPercent, minRevenue),
				new MaxStatDto(
						allPeriod.get(indexMax).getDate().getDate(),
						datesOfEnds.get(indexMax).getDate().getDate(),
						allPeriod.get(indexMax).getPriceClose(),
						datesOfEnds.get(indexMax).getPriceClose(),
						maxPercent, maxRevenue),
				avgPercent, avgRevenue);
		}
	
	/**
	 * Statistic with days
	 */
	@Override
	public FullStatDto statistic(String name, long periodDays, double sum, long depositPeriodDays) {
			DateBetweenDto dateBetweenDto = new DateBetweenDto(LocalDate.now().minusDays(periodDays), LocalDate.now());
			return statistic(name, dateBetweenDto, sum, depositPeriodDays);
	}
	
	/**
	 * Correlation with LocalDate from - to
	 */
	@Override
	public String correlation(String name1, String name2,  DateBetweenDto dateBetweenDto) {
		double[] tickersFirst = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name1, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double[] tickersSecond = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name2, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double correlation = new PearsonsCorrelation().correlation(tickersFirst,tickersSecond);		
		String result = resultCorrelation(correlation);
		return correlation + ": " + result;
	}
	
	/**
	 * Correlation with days
	 */
	@Override
	public String correlation(String name1, String name2, int termDays) {
		DateBetweenDto dateBetweenDto = new DateBetweenDto(LocalDate.now().minusDays(termDays), LocalDate.now());
		return correlation(name1, name2, dateBetweenDto);
	}
	
	private String resultCorrelation(double correlation) {		
		String res = "";
		if (correlation <= 1.00 && correlation > 0.90) {
			res = "very strong correlation";
		} else if (correlation <= 0.90 && correlation > 0.70) {
			res = "strong correlation";
		} else if (correlation <= 0.70 && correlation > 0.50) {
			res = "moderate correlation";
		} else if (correlation <= 0.50 && correlation > 0.30) {
			res = "weak correlation";
		} else if (correlation <= 0.30 && correlation > 0.00) {
			res = "negligible correlation";
		} else if (correlation >= -1.00 && correlation < -0.90) {
			res = "inverse very strong correlation";
		} else if (correlation >= -0.90 && correlation < -0.70) {
			res = "inverse strong correlation";
		} else if (correlation >= -0.70 && correlation < -0.50) {
			res = "inverse moderate correlation";
		} else if (correlation >= -0.50 && correlation < -0.30) {
			res = "inverse weak correlation";
		} else if (correlation >= -0.30 && correlation < 0.00) {
			res = "inverse negligible correlation";
		}
		return res; 
	}
	
	/**
	 * Downloading new data by name and date between
	 */
	@Override
	public int downloadDataByTickerName(String[] names, DateBetweenDto dateBetweenDto) {
		List<HistoricalQuote> googleHistQuotes = requestData(names[0], dateBetweenDto);
		List<Ticker> requesTickers = new ArrayList<>();
		googleHistQuotes.stream()
			.forEach(e -> {
				LocalDate date = e.getDate().toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
				if (e.getClose() != null) {
					double price = e.getClose().doubleValue();
					Ticker ticker = new Ticker(new TickerId(names[0], date), price);
					requesTickers.add(ticker);

				}				
			});
		
//		requesTickers.forEach(t -> System.out.println(t));
//		System.out.println("==================");
		
		List<Ticker> baseTickers = repository
				.findQueryByDateNameAndDateDateBetweenOrderByDateDate(names[0], dateBetweenDto.getDateFrom().minusDays(1), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());
		
//		baseTickers.forEach(t -> System.out.println(t));
//		System.out.println("====================");
		
		List<Ticker> newData = requesTickers.stream()
				.filter(ticker -> !baseTickers.stream().anyMatch(ticker::equals))
				.collect(Collectors.toList());
		
//		newData.forEach(t->System.out.println(t));
		
		repository.saveAll(newData);
		return newData.size();
	}

	private List<HistoricalQuote> requestData(String name, DateBetweenDto dateBetweenDto) {
		Calendar from = GregorianCalendar.from(ZonedDateTime.of(dateBetweenDto.getDateFrom().atTime(0, 0), ZoneId.systemDefault()));
		Calendar to = GregorianCalendar.from(ZonedDateTime.of(dateBetweenDto.getDateTo().atTime(0,0), ZoneId.systemDefault()));
//		from.add(Calendar.DATE, 1); 
		
//		System.out.println(from);
//		System.out.println(to);
		
		Stock tickerRequest = null;
		try {
			tickerRequest = YahooFinance.get(name);
			System.out.println(tickerRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<HistoricalQuote> googleHistQuotes = new ArrayList<>();
		try {
			googleHistQuotes = tickerRequest.getHistory(from, to, Interval.DAILY);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		googleHistQuotes.forEach(t-> System.out.println(t));
		return googleHistQuotes;
	}

	@Override
	public FullStatDto investmentPortfolio(String[] names, DateBetweenDto dateBetweenDto, double sum, long depositPeriodDays) {
		List<Double> allStats = new ArrayList<>();
		List<Ticker> datesOfEnds = new ArrayList<>();
		List<Ticker> allPeriod = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(
				names[0], dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());
		LocalDate lastStart = dateBetweenDto.getDateTo().minusDays(depositPeriodDays);
		int end = allPeriod.indexOf(new Ticker(new TickerId(names[0], lastStart), 0.0));
		while (end < 0) {
			end = allPeriod.indexOf(new Ticker(new TickerId(names[0], lastStart.minusDays(1)), 0.0));
		}
		LocalDate dateEndOfPeriod = null;
		TickerId tickerIdEnd = new TickerId(names[0], LocalDate.now());
		Ticker tickerEnd = new Ticker(tickerIdEnd, 0.0);
		allPeriod = makePortfolioPriceClose(allPeriod, names, dateBetweenDto);
		for (int start = 0; start < end; start++) {
			dateEndOfPeriod = allPeriod.get(start).getDate().getDate().plusDays(depositPeriodDays);
			tickerIdEnd.setDate(dateEndOfPeriod);
			tickerEnd.setDate(tickerIdEnd);
			int indexEnd = allPeriod.indexOf(tickerEnd);
			while (indexEnd < 0) {
				dateEndOfPeriod = dateEndOfPeriod.minusDays(1);
				tickerIdEnd.setDate(dateEndOfPeriod);
				tickerEnd.setDate(tickerIdEnd);
				indexEnd = allPeriod.indexOf(tickerEnd);
			}
			Double apr = (allPeriod.get(indexEnd).getPriceClose() - allPeriod.get(start).getPriceClose())
					/ allPeriod.get(start).getPriceClose(); 
			Double apy  = 100 * (Math.pow(1+ apr, 365.0/depositPeriodDays) - 1); 
			allStats.add(apy);
			datesOfEnds.add(allPeriod.get(indexEnd));
		}
		double minPercent = allStats.stream().min((p1, p2) -> Double.compare(p1, p2)).orElse(null);
		double maxPercent = allStats.stream().max((p1, p2) -> Double.compare(p1, p2)).orElse(null);
		double minRevenue = sum * (minPercent / 100) + sum;
		double maxRevenue = sum * (maxPercent / 100) + sum;
		double avgPercent = allStats.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		double avgRevenue = sum * (avgPercent / 100) + sum;
		int indexMin = allStats.indexOf(minPercent);
		int indexMax = allStats.indexOf(maxPercent);
		return new FullStatDto(names, depositPeriodDays, sum,
				new MinStatDto(
						allPeriod.get(indexMin).getDate().getDate(),
						datesOfEnds.get(indexMin).getDate().getDate(),
						allPeriod.get(indexMin).getPriceClose(),
						datesOfEnds.get(indexMin).getPriceClose(),
						minPercent, minRevenue),
				new MaxStatDto(
						allPeriod.get(indexMax).getDate().getDate(),
						datesOfEnds.get(indexMax).getDate().getDate(),
						allPeriod.get(indexMax).getPriceClose(),
						datesOfEnds.get(indexMax).getPriceClose(),
						maxPercent, maxRevenue),
				avgPercent, avgRevenue);
	}
	
	private List<Ticker> makePortfolioPriceClose(List<Ticker> allPeriod, String[] names, DateBetweenDto dateBetweenDto) {
		List<Ticker> res = allPeriod;
		for (int i = 1; i < names.length; i++) {
			List<Ticker> query = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(names[i], 
					dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
					.collect(Collectors.toList());
			for (int j = 0; j < res.size(); j++) {
				double price = res.get(j).getPriceClose() + query.get(j).getPriceClose();
				res.get(j).setPriceClose(price);
			}
		}
		return res;
	}
	
	@Override
	public 	List<String> findAllNames(){
//		List<String> names =  repository.findAllByOrderByDateName().map(t -> t.getDate().getName()).distinct().collect(Collectors.toList());
//		return names;
		LocalDate date = LocalDate.of(2020, 1, 7);
		return repository.findByDateDateOrderByDateName(date)
				.map(t->t.getDate().getName())
				.collect(Collectors.toList());
	};

	@Override
	public int updateAllTickers() {
		List<String> names = findAllNames();
		List<Integer> res = new ArrayList<>();
		names.stream()
			.forEach(n -> {
				String[] arr = {n};
				res.add(downloadDataByTickerName(arr, new DateBetweenDto(LocalDate.now().minusDays(10), LocalDate.now())));
			});
		return res.stream().mapToInt(i -> i).sum();
	}

	@Override
	public 	List<TickerDto> findAllPricesByPeriod(DateBetweenDto dateBetweenDto, String name){
		List<Ticker> allPeriod = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());
		List<TickerDto> tickerDtos = allPeriod.stream().map(t -> modelMapper.map(t, TickerDto.class)).collect(Collectors.toList());
		return tickerDtos;
	}
	
	@Override
	public TickersMinMaxDto findMinMaxPricesByDatePeriod(DateBetweenDto dateBetweenDto, String[] names) {
		Ticker tickerMin = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(names[0], dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
					.min((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		Ticker tickerMax = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(names[0], dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
					.max((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		TickerDto tickerMinDto = modelMapper.map(tickerMin, TickerDto.class);
		TickerDto tickerMaxDto = modelMapper.map(tickerMax, TickerDto.class);
		return new TickersMinMaxDto(tickerMinDto, tickerMaxDto);
	}

	@Override
	public LastPriceDto findLastPrice(String name) {
		Ticker lastTicker = repository.findTop2ByDateNameOrderByDateDateDesc(name).findFirst().orElseThrow(() -> new TickerNotFoundException());
		TickerDto lastTickerDto = modelMapper.map(lastTicker, TickerDto.class);
		Ticker penultimateTicker = repository.findTop2ByDateNameOrderByDateDateDesc(name)
				.skip(1)
				.findFirst()
				.orElseThrow(() -> new TickerNotFoundException());
		double change = lastTicker.getPriceClose() - penultimateTicker.getPriceClose();
		double changePersent = (lastTicker.getPriceClose() - penultimateTicker.getPriceClose())/lastTicker.getPriceClose()*100;
		return new LastPriceDto(lastTickerDto.getDate(), lastTicker.getPriceClose(), change, changePersent);
	}
	
	
	
}
