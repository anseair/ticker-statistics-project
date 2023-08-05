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
	double change5Days;
	double changePersent5Days;
	double changeMonth;
	double changePersentMonth;
	double change6Months;
	double changePersent6Months;
	double change1Year;
	double changePersent1Year;
	double change5Years;
	double changePersent5Years;
	double changeAllTime;
	double changePersentAllTime;
	double minPrice;
	double maxPrice;
}
