package telran.java2022.ticker.service;

import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.model.TickerId;

public interface TickerService {
	TickerDto add(TickerDto  tickerDto);

	TickerDto  remove(TickerId date);

	TickerDto findByDate(TickerId date);

	TickerDto  update(TickerId date, double priceClose);

	TickerDto findMaxPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);
	
	TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);

	FullStatDto getStatistic(long periodDays, double sum, long depositPeriodDays, String[] names);
	
	double getCorrelation(String name1, String name2, int termDays);
		
	String getCorrelation(String name1, String name2, DateBetweenDto dateBetweenDto);

	FullStatDto getStatistic(double sum, long depositPeriodDays, String[] names, DateBetweenDto dateBetweenDto);
	
	boolean removeByName(String name);
	
	int updateDataByTickerName(String tickerName);
	
	FullStatDto investmentPortfolio(String[] names, DateBetweenDto dateBetweenDto, double sum, long termDays);
}
