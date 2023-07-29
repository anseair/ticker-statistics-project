package telran.java2022.ticker.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTickerDescriptionDto {
//	TickerDescription2 tickerDescriptions;
	
	String ticker;
	String name;
	String description;
	LocalDate startDate;
	LocalDate endDate;
	String exchangeCode;
}
