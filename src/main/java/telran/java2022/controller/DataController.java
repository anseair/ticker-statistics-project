package telran.java2022.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.DataDto;
import telran.java2022.service.DataService;

@RequiredArgsConstructor
@RestController
public class DataController {

	final DataService dataService;
	
	@PostMapping("/data/{date}")
	public DataDto add(@PathVariable String date) {
		return dataService.add(date);
		
	}
	@GetMapping("/data/{date}")
	public DataDto findByDate(@PathVariable String date) {
		return dataService.findByDate(date);
		
	}
	
	@DeleteMapping("/data/{date}")
	public DataDto remove(@PathVariable String date) {
		return dataService.remove(date);
	}
	
	
}
