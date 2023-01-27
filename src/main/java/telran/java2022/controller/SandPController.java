package telran.java2022.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.SandPDto;
import telran.java2022.model.SandPDate;
import telran.java2022.service.SandPService;

@RequiredArgsConstructor
@RestController
public class SandPController {

	final SandPService sandPService;
	
	@PostMapping("/sandp")
	public SandPDto add(@RequestBody SandPDto sandpDto) {
		return sandPService.add(sandpDto);
		
	}
	@GetMapping("/sandp/{date}")
	public SandPDto findByDate(@PathVariable String date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return sandPService.findByDate(new SandPDate("S&P", localDate));
		
	}
	
	@DeleteMapping("/sandp/{date}")
	public SandPDto remove(@PathVariable String date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return sandPService.remove(new SandPDate("S&P",  localDate));
	}
	
	@PutMapping("/sandp/{date}")
	public SandPDto update(@PathVariable String date, @RequestBody double priceClose) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return sandPService.update(new SandPDate("S&P", localDate), priceClose);
	}
	
	@PostMapping("/sandp/max/period")
	public SandPDto findMaxPriceByDatePeriod(@RequestBody DatePeriodDto datePeriodDto) {
		return sandPService.findMaxPriceByDatePeriod(datePeriodDto);
	}
	
	@PostMapping("/sandp/min/period")
	public SandPDto findMinPriceByDatePeriod(@RequestBody DatePeriodDto datePeriodDto) {
		return sandPService.findMinPriceByDatePeriod(datePeriodDto);
	}
	
}
