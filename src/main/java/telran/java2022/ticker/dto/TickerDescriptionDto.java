package telran.java2022.ticker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TickerDescriptionDto {
		String ticker;
		String name;
		String description;
		LocalDate startDate;
		String exchangeCode;
}
