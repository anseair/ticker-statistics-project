package telran.java2022.service;

import telran.java2022.dto.SandPDto;
import telran.java2022.model.SandPDate;

public interface SandPService {

	SandPDto add(SandPDto sanpDto);
	
	SandPDto remove(SandPDate date);

	SandPDto findByDate(SandPDate date);
	
	SandPDto update (SandPDate date, double priceClose);

}
