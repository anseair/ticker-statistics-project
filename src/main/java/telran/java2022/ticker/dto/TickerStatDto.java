package telran.java2022.ticker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TickerStatDto {

	double minPercent;
	double maxPercent;
	double minRevenue;
	double maxRevenue;

}