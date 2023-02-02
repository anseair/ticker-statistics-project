package telran.java2022.gold.service;

import telran.java2022.gold.dto.GoldDto;
import telran.java2022.gold.dto.GoldStatDto;
import telran.java2022.gold.model.GoldDate;

public interface GoldService {

	GoldDto add(GoldDto goldDto);
	
	GoldDto remove(GoldDate date);

	GoldDto findByDate(GoldDate date);
	
	GoldDto update (GoldDate date, double priceClose);
	
	GoldStatDto getStat(long periodDays, double sum, long termDays);

}
