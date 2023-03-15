package telran.java2022.ticker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.ticker.exceptions.AlreadyExistException;
import telran.java2022.ticker.exceptions.NotFoundException;
import telran.java2022.ticker.dao.TickerRepository;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.StatDto;
import telran.java2022.ticker.dto.StatIdDto;
import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;

@RequiredArgsConstructor
@Service
public class TickerServiceImpl implements TickerService {
	final TickerRepository repository;
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
		Ticker s = repository.findTickerByDateDateBetween(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name))
				.max((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	@Override
	public TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name) {
		Ticker s = repository.findTickerByDateDateBetween(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name))
				.min((s1, s2) -> Double.compare(s1.getPriceClose(), s2.getPriceClose())).orElse(null);
		return modelMapper.map(s, TickerDto.class);
	}

	/**
	 * Statistic with days
	 */
	@Override
	public StatDto getStatistic(long periodDays, double sum, long termDays, String name) {
		LocalDate dateStart = LocalDate.now().minusDays(periodDays);
		List<Double> allStats = new ArrayList<>();
		List<Ticker> allPeriod = repository.findTickerByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name)).collect(Collectors.toList());
		
		System.out.println(allPeriod.get(0));
		System.out.println(allPeriod.get(allPeriod.size()-1));
		
		LocalDate dateEnd = LocalDate.now().minusDays(termDays);
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
//			System.out.println("TickerStart: " + tickerPeriods.get(start));

			dateEndOfPeriod = allPeriod.get(start).getDate().getDate().plusDays(termDays);
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

			Double apy = (allPeriod.get(indexEnd).getPriceClose() - allPeriod.get(start).getPriceClose())
					/ allPeriod.get(start).getPriceClose() * (365.0 / termDays) * 100;

//			System.out.println("==============");
//			System.out.println("apy: " + apy);

			allStats.add(apy);

//			System.out.println("stats: " + stats);
		}
		double minPercent = allStats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = allStats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double avgPercent = allStats.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		double minRevenue = sum * (minPercent / 100) + sum;
		double maxRevenue = sum * (maxPercent / 100) + sum;
		double avgRevenue = sum * (avgPercent / 100) + sum;

		return new StatDto(new StatIdDto(name, termDays, periodDays), minPercent, maxPercent, avgPercent, minRevenue, maxRevenue, avgRevenue);
	}
	
	/**
	 * Statistic with LocalDate
	 */
	@Override
	public FullStatDto getStatistic(double sum, long termDays, String name, DateBetweenDto dateBetweenDto) {
		List<Double> allStats = new ArrayList<>();
		List<Ticker> allPeriod = repository.findTickerByDateDateBetweenOrderByDateDate(dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
				.filter(t -> t.getDate().getName().equals(name)).collect(Collectors.toList());

		System.out.println(allPeriod.get(0));
		System.out.println(allPeriod.get(allPeriod.size()-1));
		
		List<Ticker> datesOfEnds = new ArrayList<>();
		LocalDate dateEnd = dateBetweenDto.getDateTo().minusDays(termDays);
		int end = allPeriod.indexOf(new Ticker(new TickerId(name, dateEnd), 0.0));
		while (end < 0) {
			end = allPeriod.indexOf(new Ticker(new TickerId(name, dateEnd.minusDays(1)), 0.0));
		}
		LocalDate dateEndOfPeriod = null;
		TickerId tickerIdEnd = new TickerId(name, LocalDate.now());
		Ticker tickerEnd = new Ticker(tickerIdEnd, 0.0);
		for (int start = 0; start < end; start++) {
			dateEndOfPeriod = allPeriod.get(start).getDate().getDate().plusDays(termDays);
			tickerIdEnd.setDate(dateEndOfPeriod);
			tickerEnd.setDate(tickerIdEnd);
			int indexEnd = allPeriod.indexOf(tickerEnd);
			while (indexEnd < 0) {
				dateEndOfPeriod = dateEndOfPeriod.minusDays(1);
				tickerIdEnd.setDate(dateEndOfPeriod);
				tickerEnd.setDate(tickerIdEnd);
				indexEnd = allPeriod.indexOf(tickerEnd);
			}
			Double apy = (allPeriod.get(indexEnd).getPriceClose() - allPeriod.get(start).getPriceClose())
					/ allPeriod.get(start).getPriceClose() * (365.0 / termDays) * 100;
			allStats.add(apy);
			datesOfEnds.add(allPeriod.get(indexEnd));
		}
		double minPercent = allStats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = allStats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double avgPercent = allStats.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		double minRevenue = sum * (minPercent / 100) + sum;
		double maxRevenue = sum * (maxPercent / 100) + sum;
		double avgRevenue = sum * (avgPercent / 100) + sum;
		int indexMin = allStats.indexOf(minPercent);
		int indexMax = allStats.indexOf(maxPercent);
		return new FullStatDto(
				allPeriod.get(indexMin).getDate().getDate(), 
				datesOfEnds.get(indexMin).getDate().getDate(), 
				allPeriod.get(indexMin).getPriceClose(),
				datesOfEnds.get(indexMin).getPriceClose(), 
				minPercent, 
				minRevenue, 
				allPeriod.get(indexMax).getDate().getDate(), 
				datesOfEnds.get(indexMax).getDate().getDate(), 
				allPeriod.get(indexMax).getPriceClose(),
				datesOfEnds.get(indexMax).getPriceClose(), 
				maxPercent, 
				maxRevenue,
				avgPercent,
				avgRevenue);
		}
	
	/**
	 * Correlation with days
	 */
	@Override
	public double getCorrelation(String name1, String name2, int termDays) {
		LocalDate dateStart = LocalDate.now().minusDays(termDays);
		double[] tickersFirst = repository.findTickerByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name1))
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double[] tickersSecond = repository.findTickerByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name2))
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double pearsonsCorrelation = new PearsonsCorrelation().correlation(tickersFirst, tickersSecond);		
		return pearsonsCorrelation;
	}
	
	/**
	 * Correlation with LocalDate from - to
	 */
	@Override
	public String getCorrelation(String name1, String name2,  DateBetweenDto dateBetweenDto) {
		double[] tickersFirst = repository.findTickerByDateDateBetweenOrderByDateDate(dateBetweenDto.getDateFrom(), LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name1))
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double[] tickersSecond = repository.findTickerByDateDateBetweenOrderByDateDate(dateBetweenDto.getDateFrom(), LocalDate.now())
				.filter(t -> t.getDate().getName().equals(name2))
				.map(t->t.getPriceClose())
				.mapToDouble(Double::doubleValue)
				.toArray();
		double correlation = new PearsonsCorrelation().correlation(tickersFirst,tickersSecond);		
		String result = resultCorrelation(correlation);
		return correlation + ": " + result;
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
	 * Experimental correlation method with query request in repository
	 */
//	@Override
//	public double correlation(String name1, String name2, DateBetweenDto dateBetweenDto) {
//		double[] tickersFirst = repository.findQueryByNameAndByDateBetweenOrderByDate(name1 , dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
//				.map(t->t.getPriceClose())
//				.mapToDouble(Double::doubleValue)
//				.toArray();
//		double[] tickersSecond = repository.findQueryByNameAndByDateBetweenOrderByDate(name2 , dateBetweenDto.getDateFrom(), dateBetweenDto.getDateTo())
//				.map(t->t.getPriceClose())
//				.mapToDouble(Double::doubleValue)
//				.toArray();
//		double correlation = new PearsonsCorrelation().correlation(tickersFirst, tickersSecond);
//		return correlation;
//	}
	
	/**
	 * Correlation with days with Apache
	 */
//	public double getCorrelationWithoutApache(String name1, String name2, int termDays) {
//		LocalDate dateStart = LocalDate.now().minusDays(termDays);
//		List<Double> tickersFirst = repository.findTickerByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
//				.filter(t -> t.getDate().getName().equals(name1))
//				.map(t->t.getPriceClose())
//				.collect(Collectors.toList());
//		List<Double> tickersSecond = repository.findTickerByDateDateBetweenOrderByDateDate(dateStart, LocalDate.now())
//				.filter(t -> t.getDate().getName().equals(name2))
//				.map(t->t.getPriceClose())
//				.collect(Collectors.toList());
//	
//		double sumX = tickersFirst.stream()
//				.reduce(0.0, (x,y) -> x + y);
//		double avgX = sumX / tickersFirst.size();
//
//		double sumY = tickersSecond.stream()
//				.reduce(0.0, (x,y) -> x + y);
//		double avgY = sumY / tickersSecond.size();
//		
//		double sumXY = tickersFirst.stream()
//				.map(t-> t * tickersSecond.get(tickersFirst.indexOf(t)))
//				.reduce(0.0, (x, y) -> x + y);
//		double avgXY = sumXY / tickersFirst.size();
//		
//		double sumXX = tickersFirst.stream()
//				.map(t->t*t)
//				.reduce(0.0, (x,y)->x+y);
//		double avgXX = sumXX / tickersFirst.size();
//		
//		double sumYY = tickersSecond.stream()
//				.map(t->t*t)
//				.reduce(0.0, (x,y)->x+y);
//		double avgYY = sumYY / tickersSecond.size();
//				
//		double varianceX = avgXX - avgX * avgX;
//		double varianceY = avgYY - avgY * avgY;
//		double covarianceXY = avgXY - avgX * avgY;
//		double correlation = covarianceXY / Math.sqrt(varianceX*varianceY);
//				
//		return correlation;
//	}

}
