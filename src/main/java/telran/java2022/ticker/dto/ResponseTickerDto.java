package telran.java2022.ticker.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class ResponseTickerDto {
	List<Ticker2> tickers;
	
	@JsonCreator
    public  ResponseTickerDto(List<Ticker2> tickers) {
        this.tickers = tickers;
    }

}
