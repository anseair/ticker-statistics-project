package telran.java2022.ticker.service;

import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickerStatDto;
import telran.java2022.ticker.model.TickerId;

public interface TickerService {
	TickerDto add(TickerDto  tickerDto);

	TickerDto  remove(TickerId date);

	TickerDto findByDate(TickerId date);

	TickerDto  update(TickerId date, double priceClose);

	TickerDto findMaxPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);
	
	TickerDto findMinPriceByDatePeriod(DateBetweenDto dateBetweenDto, String name);

	TickerStatDto getStat(long periodDays, double sum, long termDays, String name);
	
	double getCorrelation(String name1, String name2, int termDays);
	
	double getCorrelationWithApache(String name1, String name2, int termDays);
}
