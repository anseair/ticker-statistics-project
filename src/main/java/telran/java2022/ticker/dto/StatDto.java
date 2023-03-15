package telran.java2022.ticker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {

	private StatIdDto ticker;
	double minPercent;
	double maxPercent;
	double avgPercent;
	double minRevenue;
	double maxRevenue;
	double avgRevenue;

}
