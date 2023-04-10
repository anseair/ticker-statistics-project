package telran.java2022.ticker.service;

import java.util.List;

import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.model.TickerId;

public interface TickerService {
	TickerDto add(TickerDto  tickerDto);
	
	TickerDto findByDate(TickerId date);

	TickerDto  deleteByDate(TickerId date);
	
	int deleteAllTickersByName(String name);

	TickerDto  update(TickerId date, double priceClose);

	TickerDto findMaxPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);
	
	TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);

	FullStatDto getStatistic(String name, long periodDays, double sum, long depositPeriodDays);
	
	FullStatDto getStatistic(String name, DateBetweenDto dateBetweenDto, double sum, long depositPeriodDays);
	
	String getCorrelation(String name1, String name2, int termDays);
		
	String getCorrelation(String name1, String name2, DateBetweenDto dateBetweenDto);
	
	int downloadDataByTickerName(String[] names, DateBetweenDto dateBetweenDto);
	
	FullStatDto investmentPortfolio(String[] names, DateBetweenDto dateBetweenDto, double sum, long termDays);
	
	List<String> findAllNames();
}
