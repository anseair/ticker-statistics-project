package telran.java2022.ticker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatIdDto {
	private String name;
	private long termDays;
	private long periodDays;

	
}
