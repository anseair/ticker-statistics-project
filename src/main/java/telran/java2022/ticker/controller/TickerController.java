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
	public TickerDto deleteByDate(@PathVariable String date, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return service.deleteByDate(new TickerId(name,  localDate));
	}
	
	@DeleteMapping("/{name}")
	public int deleteAllTickersByName(@PathVariable String name) {
		return service.deleteAllTickersByName(name);
	}
	
	@PutMapping("/{name}/{date}")
	public TickerDto update(@PathVariable String date, @RequestBody double priceClose, @PathVariable String name) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return service.update(new TickerId(name, localDate), priceClose);
	}
	
	@PostMapping("/max")
	public TickerDto findMaxPriceByDatePeriod(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.findMaxPriceByDatePeriod(namesAndDatesDto.getDateBetween(), namesAndDatesDto.getNames()[0]);
	}
	
	@PostMapping("/min")
	public TickerDto findMinPriceByDatePeriod(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.findMinPriceByDatePeriod(namesAndDatesDto.getDateBetween(), namesAndDatesDto.getNames()[0]);
	}
	
	@GetMapping("/statistic/{name}/{periodDays}/{sum}/{depositPeriodDays}")
	public FullStatDto getStatistic(@PathVariable String name, @PathVariable long periodDays, @PathVariable double sum, @PathVariable long depositPeriodDays) {
		return service.getStatistic(name, periodDays, sum, depositPeriodDays);
	}
	
	@PostMapping("/statistic")
	public FullStatDto getStatistic(@RequestBody NamesAndDatesForStatDto namesAndDatesForStatDto) {
		return service.getStatistic(namesAndDatesForStatDto.getNames()[0], 
				namesAndDatesForStatDto.getDateBetween(), 
				namesAndDatesForStatDto.getDepositSum(),
				namesAndDatesForStatDto.getDepositPeriodDays());
	}
	
	@GetMapping("/correlation/{name1}/{name2}/{termDays}")
	public String getCorrelation(@PathVariable String name1, @PathVariable String name2, @PathVariable int termDays) {
		return service.getCorrelation(name1, name2, termDays);
	}
	
	@PostMapping("/correlation")
	public String getCorrelation(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.getCorrelation(namesAndDatesDto.getNames()[0], namesAndDatesDto.getNames()[1], namesAndDatesDto.getDateBetween());
	}
	
	@PostMapping("/statistic/investmentPortfolio")
	public FullStatDto investmentPortfolio(@RequestBody NamesAndDatesForStatDto namesAndDatesForStatDto) {
		return service.investmentPortfolio(namesAndDatesForStatDto.getNames(), 
				namesAndDatesForStatDto.getDateBetween(), 
				namesAndDatesForStatDto.getDepositSum(), 
				namesAndDatesForStatDto.getDepositPeriodDays());
	}
	
	@PostMapping("/download")
	public int downloadDataByTickerName(@RequestBody NamesAndDatesDto namesAndDatesDto) {
		return service.downloadDataByTickerName(namesAndDatesDto.getNames(), namesAndDatesDto.getDateBetween());
	}
	
	@GetMapping("/tickers")
	public List<String> findAllTickerNames(){
		return service.findAllNames();
	}
	
	@PostMapping("/update/all")
	public int updateAllTickers() {
		return service.updateAllTickers();
	}
	
}
