package telran.java2022.ticker.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"name", "termDays", "periodDays"})
public class TickerIdStat {
	private String name;
	private long termDays;
	private long periodDays;
}
