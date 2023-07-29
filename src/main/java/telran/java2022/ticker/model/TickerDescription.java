package telran.java2022.ticker.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "Ticker descriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "ticker")
public class TickerDescription {
	
	@Id
	String ticker;
	String name;
	String description;
	LocalDate startDate;
	LocalDate endDate;
	String exchangeCode;
}
