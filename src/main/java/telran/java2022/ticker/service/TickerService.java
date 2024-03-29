package telran.java2022.ticker.service;

import java.util.List;

import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.LastPriceDto;
import telran.java2022.ticker.dto.TickerDescriptionDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickersMinMaxDto;
import telran.java2022.ticker.model.TickerId;

public interface TickerService {
	TickerDto add(TickerDto  tickerDto);
	
	TickerDto findByDate(TickerId date);

	TickerDto  deleteByDate(TickerId date);
	
	boolean deleteAllTickersByName(String name);

	TickerDto  update(TickerId date, double priceClose);
	
	FullStatDto statistic(String name, long periodDays, double sum, long depositPeriodDays);
	
	FullStatDto statistic(String name, DateBetweenDto dateBetweenDto, double sum, long depositPeriodDays);
	
	String correlation(String name1, String name2, int termDays);
		
	String correlation(String name1, String name2, DateBetweenDto dateBetweenDto);
	
	int downloadDataByTickerName(String[] names, DateBetweenDto dateBetweenDto);
	
	FullStatDto investmentPortfolio(String[] names, DateBetweenDto dateBetweenDto, double sum, long termDays);
	
	List<String> findAllNames();
	
	int updateAllTickers();
	
	List<TickerDto> findAllPricesByPeriod(DateBetweenDto dateBetweenDto, String name);
	
	TickersMinMaxDto findMinMaxPricesByDatePeriod(DateBetweenDto dateBetweenDto, String name);

	List<LastPriceDto> findLastPrice();
	
	TickerDescriptionDto addDescription(String name);
	
	List<TickerDescriptionDto> findDescriptions();
	
	
}
