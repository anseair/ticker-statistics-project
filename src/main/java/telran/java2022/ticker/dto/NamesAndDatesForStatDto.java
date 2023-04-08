package telran.java2022.ticker.dto;

import lombok.Getter;

@Getter
public class NamesAndDatesForStatDto  {
	
	String[] names;
	DateBetweenDto dateBetween;
	long depositPeriodDays;
	int sum;
}
