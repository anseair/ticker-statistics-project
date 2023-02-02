package telran.java2022.sandp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SandPDto {

	private SandPDateDto date;
	private double priceClose;
}