package telran.java2022.ticker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LastPriceDto {
	TickerDateDto date;
	double priceClose;
	double change;
	double changePersent;
	double minPrice;
	double maxPrice;
}
