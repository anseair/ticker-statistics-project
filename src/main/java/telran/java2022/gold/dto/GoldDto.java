package telran.java2022.gold.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoldDto {

	private GoldDateDto date;
	private double priceClose;
}