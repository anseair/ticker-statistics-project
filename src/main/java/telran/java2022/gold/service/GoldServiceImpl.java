package telran.java2022.gold.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.gold.dao.GoldRepository;
import telran.java2022.gold.dto.GoldDto;
import telran.java2022.gold.dto.GoldStatDto;
import telran.java2022.gold.model.Gold;
import telran.java2022.gold.model.GoldDate;
import telran.java2022.sandp.exceptoins.DateExistsException;
import telran.java2022.sandp.exceptoins.NotFoundException;
import telran.java2022.utils.GoldCsvParsing;

@RequiredArgsConstructor
@Service
public class GoldServiceImpl implements GoldService, CommandLineRunner {
	final GoldRepository repository;
	final ModelMapper modelMapper;

	
	@Override
	public GoldDto add(GoldDto goldDto) {
		if (repository.existsById(modelMapper.map(goldDto.getDate(), GoldDate.class))) {
			throw new DateExistsException();
		}
		Gold gold = modelMapper.map(goldDto, Gold.class);
		repository.save(gold);
		return modelMapper.map(gold, GoldDto.class);
	}

	@Override
	public GoldDto remove(GoldDate date) {
		Gold gold = repository.findById(date).orElseThrow(() -> new NotFoundException());
		repository.deleteById(date);
		return modelMapper.map(gold, GoldDto.class);
	}

	@Override
	public GoldDto findByDate(GoldDate date) {
		Gold gold = repository.findById(date).orElseThrow(() -> new NotFoundException());
		return modelMapper.map(gold, GoldDto.class);
	}

	@Override
	public GoldDto update(GoldDate date, double priceClose) {
		Gold gold = repository.findById(date).orElseThrow(() -> new NotFoundException());
		gold.setPriceClose(priceClose);
		repository.save(gold);
		return modelMapper.map(gold, GoldDto.class);
	}
	
	
	@Override
	public GoldStatDto getStat(long periodDays, double sum, long termDays) {
		LocalDate dateStart = LocalDate.now().minusDays(periodDays + termDays);
		List<Double> goldPStats = new ArrayList<>();
		LocalDate dateEnd = LocalDate.now().minusDays(termDays);
		List<Gold> goldPeriods = repository.findByDateDateBetweenOrderByDate(dateStart, LocalDate.now()).collect(Collectors.toList());
		goldPeriods.forEach(e -> System.out.println(e));
		int end = goldPeriods.indexOf(new Gold(new GoldDate("gold", dateEnd), 0.0));

		for (int start = 0; start < end; start++) {
			
			System.out.println("======================");
			System.out.println("index dateStart in list = " + start);
			System.out.println("sandpStart: " + goldPeriods.get(start)); 

			LocalDate dateEnd1 = goldPeriods.get(start).getDate().getDate().plusDays(termDays);
			Gold goldEnd = null;
			while (!repository.existsById(new GoldDate("gold", dateEnd1))) {
				dateEnd1 = dateEnd1.minusDays(1);
			}
			goldEnd = repository.findById(new GoldDate("gold", dateEnd1)).get();
			int endIndex = goldPeriods.indexOf(goldEnd);

			System.out.println("SandpEnd: " + goldEnd);
			System.out.println("index dateEnd in list = " + endIndex);

			Double apy = ((sum / goldPeriods.get(start).getPriceClose() * goldPeriods.get(endIndex).getPriceClose()) - sum) / sum * 100;
			
			System.out.println("==============");
			System.out.println("apy: " + apy);
			goldPStats.add(apy);
			System.out.println("sandpStats: " + goldPStats);
		}
		double minPercent = goldPStats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = goldPStats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double minRevenue = sum + (sum * minPercent / 100);
		double maxRevenue = sum + (sum * maxPercent / 100);
		return new GoldStatDto(minPercent, maxPercent, minRevenue, maxRevenue);
	}

	@Override
	public void run(String... args) throws Exception {

		List<Gold> notExistsDatas = new ArrayList<>();

		List<Gold> newDatas = GoldCsvParsing.parsingWithApache("gold_5years.csv", "gold", "yyyy-MM-dd", 4);

		System.out.println("amount of data gold in csv: " + newDatas.size());

		List<Gold> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("amount data before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			repository.saveAll(oldDatas);

			System.out.println("total data in db: " + oldDatas.size());

		} else {
//			if (!newDatas.containsAll(oldDatas)) {
			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
//				oldDatas.addAll(0, notExistsDatas);
			repository.saveAll(notExistsDatas);

			int res = notExistsDatas.size() + oldDatas.size();
			System.out.println("amount new data added in db: " + notExistsDatas.size());
			System.out.println("total data in db : " + res);

//			}
		}

	}


	
	

}
