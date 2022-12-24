package telran.java2022.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.DataDto;
import telran.java2022.dto.DateDto;
import telran.java2022.service.DataService;

@RequiredArgsConstructor
@RestController
public class DataController {

	final DataService dataService;
	
	@PostMapping("/data/{date}")
	public DataDto findByDate(@PathVariable String date) {
		return dataService.findByDate(date);
		
	}
}
