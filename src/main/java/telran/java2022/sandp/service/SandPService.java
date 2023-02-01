package telran.java2022.sandp.service;

import telran.java2022.sandp.dto.DatePeriodDto;
import telran.java2022.sandp.dto.SandPDto;
import telran.java2022.sandp.dto.SandPStatDto;
import telran.java2022.sandp.model.SandPDate;

public interface SandPService {

	SandPDto add(SandPDto sanpDto);
	
	SandPDto remove(SandPDate date);

	SandPDto findByDate(SandPDate date);
	
	SandPDto update (SandPDate date, double priceClose);
	
	SandPDto findMaxPriceByDatePeriod(DatePeriodDto datePeriodDto);
	
	SandPDto findMinPriceByDatePeriod(DatePeriodDto datePeriodDto);
	
	SandPStatDto getStat(long periodDays, double sum, long termDays);

}
