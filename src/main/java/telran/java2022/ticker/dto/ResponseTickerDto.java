package telran.java2022.ticker.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTickerDto {
	String symbol;
	List<Ticker2> historical;
	
//	@JsonCreator
//    public  ResponseTickerDto(List<Ticker2> tickers) {
//        this.historical = tickers;
//    }

}
