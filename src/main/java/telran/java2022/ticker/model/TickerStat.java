package telran.java2022.ticker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection  = "TickerStat")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "tickerIdStat")
public class TickerStat {

	@Id
	private TickerIdStat tickerIdStat;
	private double minPercent;
	private double maxPercent;
	private double minRevenue;
	private double maxRevenue;
}
