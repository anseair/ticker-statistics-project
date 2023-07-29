package telran.java2022.ticker.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTickerDto {
	String symbol;
	List<Ticker2> historical;
}
