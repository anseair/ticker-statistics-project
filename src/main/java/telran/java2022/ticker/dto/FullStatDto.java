package telran.java2022.ticker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FullStatDto {

	LocalDate minDateStart;
	LocalDate minDateEnd;
	double minPriceStart;
	double minPriceEnd;
	double minPercent;
	double minRevenue;
	
	
	LocalDate maxDateStart;
	LocalDate maxDateEnd;
	double maxPriceStart;
	double maxPriceEnd;
	double maxPercent;
	double maxRevenue;
	
	double avgPercent;
	double avgRevenue;
}
