package telran.java2022.ticker.controller;

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
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.model.TickerId;
import telran.java2022.ticker.service.TickerService;

@RequiredArgsConstructor
@RestController
public class TickerController {
	final TickerService tickerService;
	
	@PostMapping("/ticker")
	public TickerDto add(@RequestBody TickerDto tickerDto) {
		return tickerService.add(tickerDto);
	}
	
	@GetMapping("/{name}/{date}")
	public TickerDto findByDate(@PathVariable String date, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return tickerService.findByDate(new TickerId(name.toLowerCase(), localDate));
	}
	
	@DeleteMapping("/{name}/{date}")
	public TickerDto remove(@PathVariable String date, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return tickerService.remove(new TickerId(name.toLowerCase(),  localDate));
	}
	
	@PutMapping("/{name}/{date}")
	public TickerDto update(@PathVariable String date, @RequestBody double priceClose, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return tickerService.update(new TickerId(name.toLowerCase(), localDate), priceClose);
	}
	
	@PostMapping("/{name}/max/period")
	public TickerDto findMaxPriceByDatePeriod(@RequestBody DateBetweenDto dateBetweenDto,  @PathVariable String name) {
		return tickerService.findMaxPriceByDatePeriod(dateBetweenDto, name.toLowerCase());
	}
	
	@PostMapping("/{name}/min/period")
	public TickerDto findMinPriceByDatePeriod(@RequestBody DateBetweenDto dateBetweenDto, @PathVariable String name) {
		return tickerService.findMinPriceByDatePeriod(dateBetweenDto, name.toLowerCase());
	}
	
	@GetMapping("/{name}/{periodDays}/{sum}/{termDays}")
	public FullStatDto getStat(@PathVariable long periodDays, @PathVariable double sum, @PathVariable long termDays, @PathVariable String name) {
		return tickerService.getStatistic(periodDays, sum, termDays, name.toLowerCase());
	}
	
	@GetMapping("/stat/{name}/{sum}/{termDays}")
	public FullStatDto getStat(@PathVariable double sum, @PathVariable long termDays, @PathVariable String name, @RequestBody DateBetweenDto dateBetweenDto) {
		return tickerService.getStatistic(sum, termDays, name, dateBetweenDto);
	}
	
	@GetMapping("/{name1}/{name2}/{termDays}")
	public double getCorrelation(@PathVariable String name1, @PathVariable String name2, @PathVariable int termDays) {
		return tickerService.getCorrelation(name1, name2, termDays);
	}
	
	@GetMapping("/correlation/{name1}/{name2}")
	public String getCorrelation(@PathVariable String name1, @PathVariable String name2, @RequestBody DateBetweenDto dateBetweenDto) {
		return tickerService.getCorrelation(name1, name2, dateBetweenDto);
	}
	
	@DeleteMapping("/{name}")
	public boolean removeByName(@PathVariable String name) {
		return tickerService.removeByName(name);
	}
	
}
