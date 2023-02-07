//package telran.java2022.gold.controller;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import lombok.RequiredArgsConstructor;
//import telran.java2022.gold.dto.GoldDto;
//import telran.java2022.gold.dto.GoldStatDto;
//import telran.java2022.gold.model.GoldDate;
//import telran.java2022.gold.service.GoldService;
//
//@RequiredArgsConstructor
//@RestController
//public class GoldController {
//
//	final GoldService goldService;
//	
//	@PostMapping("/gold")
//	public GoldDto add(@RequestBody GoldDto goldDto) {
//		return goldService.add(goldDto);
//		
//	}
//	@GetMapping("/gold/{date}")
//	public GoldDto findByDate(@PathVariable String date) {
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
//		return goldService.findByDate(new GoldDate("Gold", localDate));
//		
//	}
//	
//	@DeleteMapping("/gold/{date}")
//	public GoldDto remove(@PathVariable String date) {
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
//		return goldService.remove(new GoldDate("Gold",  localDate));
//	}
//	
//	@PutMapping("/gold/{date}")
//	public GoldDto update(@PathVariable String date, @RequestBody double priceClose) {
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
//		return goldService.update(new GoldDate("Gold", localDate), priceClose);
//	}
//	
//	
//	@GetMapping("/gold/{periodYears}/{sum}/{termYears}")
//	public GoldStatDto getStat(@PathVariable long periodYears, @PathVariable double sum, @PathVariable long termYears ) {
//		return goldService.getStat(periodYears, sum, termYears);
//		
//	}
//}
