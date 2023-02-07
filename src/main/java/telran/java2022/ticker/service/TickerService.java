package telran.java2022.ticker.service;

import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.dto.TickerStatDto;
import telran.java2022.ticker.model.TickerDate;

public interface TickerService {
	TickerDto add(TickerDto  tickerDto);

	TickerDto  remove(TickerDate date);

	TickerDto findByDate(TickerDate date);

	TickerDto  update(TickerDate date, double priceClose);

	TickerStatDto getStat(long periodDays, double sum, long termDays, String name);
}
