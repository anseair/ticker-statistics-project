package telran.java2022.ticker.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.ticker.dto.AllNamesDto;
import telran.java2022.ticker.dto.DateBetweenDto;
import telran.java2022.ticker.dto.FullStatDto;
import telran.java2022.ticker.dto.NamesAndDatesDto;
import telran.java2022.ticker.dto.NamesAndDatesForStatDto;
import telran.java2022.ticker.dto.TickerDto;
import telran.java2022.ticker.model.TickerId;
import telran.java2022.ticker.service.TickerService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/financials")
public class TickerController {
	final TickerService service;
	
	@PostMapping("/ticker")
	public TickerDto add(@RequestBody TickerDto tickerDto) {
		return service.add(tickerDto);
	}
	
	@GetMapping("/ticker/{name}/{date}")
	public TickerDto findByDate(@PathVariable String date, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return service.findByDate(new TickerId(name, localDate));
	}
	
	@DeleteMapping("/{name}/{date}")
	public TickerDto remove(@PathVariable String date, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return service.remove(new TickerId(name,  localDate));
	}
	
	@PutMapping("/{name}/{date}")
	public TickerDto update(@PathVariable String date, @RequestBody double priceClose, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return service.update(new TickerId(name, localDate), priceClose);
	}
	
	@PostMapping("/max/{name}")
	public TickerDto findMaxPriceByDatePeriod(@RequestBody DateBetweenDto dateBetweenDto,  @PathVariable String name) {
		return service.findMaxPriceByDatePeriod(dateBetweenDto, name);
	}
	
	@PostMapping("/min/{name}")
	public TickerDto findMinPriceByDatePeriod(@RequestBody DateBetweenDto dateBetweenDto, @PathVariable String name) {
		return service.findMinPriceByDatePeriod(dateBetweenDto, name);
	}
	
	@GetMapping("/statistic/{name}/{periodDays}/{sum}/{termDays}")
	public FullStatDto getStat(@PathVariable long periodDays, @PathVariable double sum, @PathVariable long termDays, @PathVariable String name) {
		return service.getStatistic(periodDays, sum, termDays, name);
	}
	
	@GetMapping("/statistic")
	public FullStatDto getStat(@RequestBody NamesAndDatesForStatDto namesAndDatesForStatDto) {
		return service.getStatistic(namesAndDatesForStatDto);
	}
	
	@GetMapping("/correlation/{name1}/{name2}/{termDays}")
	public double getCorrelation(@PathVariable String name1, @PathVariable String name2, @PathVariable int termDays) {
		return service.getCorrelation(name1, name2, termDays);
	}
	
	@GetMapping("/correlation")
	public String getCorrelation(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.getCorrelation(namesAndDatesDto.getNames()[0], namesAndDatesDto.getNames()[1], namesAndDatesDto.getDateBetween());
	}
	
	@DeleteMapping("/{name}")
	public boolean removeByName(@PathVariable String name) {
		return service.removeByName(name);
	}
	
	@PostMapping("/download")
	public int downloadDataByTickerName(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.downloadDataByTickerName(namesAndDatesDto.getNames(), namesAndDatesDto.getDateBetween());
	}
	
	@GetMapping("/statistic/investmentPortfolio")
	public FullStatDto investmentPortfolio(@RequestBody NamesAndDatesForStatDto namesAndDatesForStatDto) {
		return service.investmentPortfolio(namesAndDatesForStatDto.getNames(), namesAndDatesForStatDto.getDateBetween(), namesAndDatesForStatDto.getSum(), namesAndDatesForStatDto.getDepositPeriodDays());
	}
	
	@GetMapping("/tickers")
	public List<String> findAllTickerNames(){
		return service.findAllNames();
	}
	
	
}
