package telran.java2022.ticker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import lombok.RequiredArgsConstructor;
import telran.java2022.ticker.exceptions.TickerExistException;
import telran.java2022.ticker.exceptions.TickerNotFoundException;
import telran.java2022.ticker.dao.TickerDescriptionRepository;
import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.LastPriceDto;
import telran.java2022.ticker.dto.MaxStatDto;
import telran.java2022.ticker.dto.MinStatDto;
import telran.java2022.ticker.dto.ResponseTickerDescriptionDto;
import telran.java2022.ticker.dto.ResponseTickerDto;
import telran.java2022.ticker.dto.Ticker2;
import telran.java2022.ticker.dto.TickerDescriptionDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickersMinMaxDto;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerDescription;
import telran.java2022.ticker.model.TickerId;
import yahoofinance.Stock;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TickerService {
	final TickerRepository repository;
	final TickerDescriptionRepository descriptionrepository;
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
	public boolean deleteAllTickersByName(String name) {
		List<Ticker> tickers = repository.findAllTickersByDateName(name).collect(Collectors.toList());
		System.out.println(tickers.size());
		if (tickers.size() == 0) {
			return false;
		} 
		repository.deleteAllTickersByDateName(name);
		tickers = repository.findAllTickersByDateName(name).collect(Collectors.toList());
		System.out.println(tickers.size());
		return true;
		
//		List<Ticker> tickers = repository.findAllTickersByDateName(name).collect(Collectors.toList());
//		System.out.println(tickers.size());
//		tickers.forEach(t -> System.out.println(t));
//		if (tickers.size() == 0) {
//			return 0;
//		} else {
//			repository.deleteAll(tickers);
//		}
//		return tickers.size();
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
	
	@Override
	public 	List<String> findAllNames(){
		LocalDate date = LocalDate.of(2023, 1,3);
		return repository.findByDateDateOrderByDateName(date)
				.map(t->t.getDate().getName())
				.collect(Collectors.toList());
	};

	/**
	 * Downloading new data by financialmodelingprep.com
	 */
	@Override
	public int downloadDataByTickerName(String[] names, DateBetweenDto dateBetweenDto) {
		
//		Calendar from = GregorianCalendar.from(ZonedDateTime.of(dateBetweenDto.getDateFrom().atTime(0, 0), ZoneId.systemDefault()));
//		Calendar to = GregorianCalendar.from(ZonedDateTime.of(dateBetweenDto.getDateTo().atTime(0,0), ZoneId.systemDefault()));
//	
//		Stock tickerRequest = null;
		
//		try {
//			tickerRequest = YahooFinance.get(names[0]);
//			System.out.println(tickerRequest.getSymbol());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		List<HistoricalQuote> googleHistQuotes = new ArrayList<>();
//		try {
//			googleHistQuotes = tickerRequest.getHistory(from, to, Interval.DAILY);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		googleHistQuotes.forEach(t-> System.out.println(t));
//		List<Ticker> requesTickers = new ArrayList<>();
//		googleHistQuotes.stream()
//			.forEach(e -> {
//				LocalDate date = e.getDate().toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
//				if (e.getClose() != null) {
//					double price = e.getClose().doubleValue();
//					Ticker ticker = new Ticker(new TickerId(names[0], date), price);
//					requesTickers.add(ticker);
//
//				}				
//			});
//		
//		requesTickers.forEach(t -> System.out.println(t));
//		System.out.println("==================");		
//		return 0;
		
		
		String TOKEN = "d7fcbc6c1eee75524db81e0b62dd0c21";
		String baseUrl = "https://financialmodelingprep.com/api/v3/historical-price-full";
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/"+names[0])
																.queryParam("from", dateBetweenDto.getDateFrom())
																.queryParam("to", dateBetweenDto.getDateTo().minusDays(1))
																.queryParam("apikey", TOKEN);
//		System.out.println(builder.build().toUri());
		RestTemplate restTemplate = new RestTemplate();
		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, builder.build().toUri());
		ResponseEntity<ResponseTickerDto> responseEntity = restTemplate.exchange(requestEntity, ResponseTickerDto.class);
		List<Ticker2> tickers2 = responseEntity.getBody().getHistorical();
		List<Ticker> requestTickers = new ArrayList<>();
		tickers2.stream()
			.forEach(t -> {
				LocalDate date = t.getDate().atStartOfDay().plusHours(4).atZone(ZoneId.systemDefault()).toLocalDate();
				if (t.getClose() != 0) {
					double price = Math.round(t.getClose() * 100.0)/100.0;
					Ticker ticker = new Ticker(new TickerId(names[0], date), price);
					requestTickers.add(ticker);
				}				
			});		
//		System.out.println("================");
//		System.out.println(requestTickers.size());
//		requestTickers.forEach(t -> System.out.println(t));
		
		List<Ticker> baseTickers = repository
				.findQueryByDateNameAndDateDateBetweenOrderByDateDate(names[0], dateBetweenDto.getDateFrom().minusDays(1), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());
		
//		System.out.println("==============");
//		System.out.println(baseTickers.size());
//		baseTickers.forEach(t -> System.out.println(t));
		
		List<Ticker> newData = requestTickers.stream()
				.filter(ticker -> !baseTickers.stream().anyMatch(ticker::equals))
				.collect(Collectors.toList());
		
//		System.out.println("================");
//		System.out.println(newData.size());
//		newData.forEach(t -> System.out.println(t));
//		System.out.println("================");

		repository.saveAll(newData);
		return newData.size();	
	}
	
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
		List<Ticker> allPeriod = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name, dateBetweenDto.getDateFrom().minusDays(1), dateBetweenDto.getDateTo())
				.collect(Collectors.toList());
//		System.out.println(allPeriod.size());
		List<TickerDto> tickerDtos = allPeriod.stream().map(t -> modelMapper.map(t, TickerDto.class)).collect(Collectors.toList());
		return tickerDtos;
	}
	
	@Override
	public TickersMinMaxDto findMinMaxPricesByDatePeriod(DateBetweenDto dateBetweenDto, String name) {
		Ticker tickerMin = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
					.min((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		Ticker tickerMax = repository.findQueryByDateNameAndDateDateBetweenOrderByDateDate(name, dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
					.max((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		TickerDto tickerMinDto = modelMapper.map(tickerMin, TickerDto.class);
		TickerDto tickerMaxDto = modelMapper.map(tickerMax, TickerDto.class);
		return new TickersMinMaxDto(tickerMinDto, tickerMaxDto);
	}

	@Override
	public List<LastPriceDto> findLastPrice() {
		List<LastPriceDto> res = new ArrayList<>();
		List<String> names = findAllNames();
		names.forEach(n -> res.add(createTodayInfo(n)));
		return res;
	}

	private LastPriceDto createTodayInfo(String name) {
		List<Ticker> tickers = repository.findTop2ByDateNameOrderByDateDateDesc(name).collect(Collectors.toList());
		Ticker lastTicker = tickers.get(0);
		Ticker prevLastTicker = tickers.get(1);
		double change = Math.round((lastTicker.getPriceClose() - prevLastTicker.getPriceClose())*100.0)/100.0;
		double changePersent = Math.round(((lastTicker.getPriceClose() - prevLastTicker.getPriceClose())/lastTicker.getPriceClose()*100) *100.0)/100.0;
		LocalDate dateTo = lastTicker.getDate().getDate();
		LocalDate dateFrom = dateTo.minusWeeks(52);
		DateBetweenDto dateBetween = new DateBetweenDto(dateFrom, dateTo);
		double minPrice = findMinMaxPricesByDatePeriod(dateBetween, name).getMin().getPriceClose();
		double maxPrice = findMinMaxPricesByDatePeriod(dateBetween, name).getMax().getPriceClose();
		TickerDto lastTickerDto = modelMapper.map(lastTicker, TickerDto.class);
		LastPriceDto res = new LastPriceDto(lastTickerDto.getDate(), lastTicker.getPriceClose(), change, changePersent, minPrice, maxPrice);
		return res;
	}
	
	/**
	 * Downloading descriptions by api.tiingo.com
	 */
	@Override
	public TickerDescriptionDto addDescription(String name) {
		TickerDescription description = null;
		List<TickerDescription> baseDescriptions = descriptionrepository.findAllByOrderByTicker();
		List<String> baseNames = baseDescriptions.stream()
				.map(n -> n.getName()).collect(Collectors.toList());
		if (!baseNames.contains(name)) {
			description = createDescription(name);
			descriptionrepository.save(description);
		}
		return modelMapper.map(description, TickerDescriptionDto.class);
	}
	
	private TickerDescription createDescription(String name) {
		String TOKEN = "4789f04c9f54ae2ce69af8db744bb7fc4883de69";
		String baseUrl = "https://api.tiingo.com/tiingo/daily";
		UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/"+name)
				.queryParam("token", TOKEN);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, TOKEN);
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		
		RequestEntity<String> requestEntity2 = new RequestEntity<>(headers, HttpMethod.GET, builder2.build().toUri());
		ResponseEntity<ResponseTickerDescriptionDto> responseEntity2 = restTemplate.exchange(requestEntity2, ResponseTickerDescriptionDto.class);
		TickerDescription tickerDescription = new TickerDescription(responseEntity2.getBody().getTicker(), responseEntity2.getBody().getName(),
					responseEntity2.getBody().getDescription(), responseEntity2.getBody().getStartDate(), 
					responseEntity2.getBody().getEndDate(), responseEntity2.getBody().getExchangeCode());
		return tickerDescription;
	}
	
	@Override
	public List<TickerDescriptionDto> findDescriptions(){
		List<TickerDescriptionDto> tickerDescriptionsDto = descriptionrepository.findAllByOrderByTicker().stream().map(t -> modelMapper.map(t, TickerDescriptionDto.class)).collect(Collectors.toList());
		return tickerDescriptionsDto;
	}
}
