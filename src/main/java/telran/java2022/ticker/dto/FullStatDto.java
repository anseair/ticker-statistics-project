package telran.java2022.ticker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FullStatDto {

	String tickerName;
	long depositPeriodDays;
	
	private MinStatDto minStat;
	private MaxStatDto maxStat;
	
	double avgPercent;
	double avgRevenue;
}
