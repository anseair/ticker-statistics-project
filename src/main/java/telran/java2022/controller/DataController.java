package telran.java2022.controller;

import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/data/date")
	public DataDto findByDate(@RequestBody DateDto date) {
		return dataService.findByDate(date);
		
	}
}
