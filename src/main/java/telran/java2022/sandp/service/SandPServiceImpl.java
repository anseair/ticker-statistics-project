package telran.java2022.sandp.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.sandp.dao.SandPRepository;
import telran.java2022.sandp.dto.DatePeriodDto;
import telran.java2022.sandp.dto.SandPDto;
import telran.java2022.sandp.dto.SandPStatDto;
import telran.java2022.sandp.exceptoins.DateExistsException;
import telran.java2022.sandp.exceptoins.NotFoundException;
import telran.java2022.sandp.model.SandP;
import telran.java2022.sandp.model.SandPDate;
import telran.java2022.utils.SandPCsvParsing;

@RequiredArgsConstructor
@Service
public class SandPServiceImpl implements SandPService, CommandLineRunner {
	final SandPRepository repository;
	final ModelMapper modelMapper;

	@Override
	public SandPDto add(SandPDto sandpDto) {
		if (repository.existsById(modelMapper.map(sandpDto.getDate(), SandPDate.class))) {
			throw new DateExistsException();
		}
		SandP sandp = modelMapper.map(sandpDto, SandP.class);
		repository.save(sandp);
		return modelMapper.map(sandp, SandPDto.class);
	}

	@Override
	public SandPDto remove(SandPDate date) {
		SandP sandp = repository.findById(date).orElseThrow(() -> new NotFoundException());
		repository.deleteById(date);
		return modelMapper.map(sandp, SandPDto.class);
	}

	@Override
	public SandPDto findByDate(SandPDate date) {
		SandP sandp = repository.findById(date).orElseThrow(() -> new NotFoundException());
		return modelMapper.map(sandp, SandPDto.class);
	}

	@Override
	public SandPDto update(SandPDate date, double priceClose) {
		SandP sandp = repository.findById(date).orElseThrow(() -> new NotFoundException());
		sandp.setPriceClose(priceClose);
		repository.save(sandp);
		return modelMapper.map(sandp, SandPDto.class);
	}

	@Override
	public SandPDto findMaxPriceByDatePeriod(DatePeriodDto datePeriodDto) {
		return repository.findByDateDateBetweenOrderByDate(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
				.map(e -> modelMapper.map(e, SandPDto.class)).collect(Collectors.toList()).stream()
				.max(Comparator.comparing(SandPDto::getPriceClose)).get();
	}

	@Override
	public SandPDto findMinPriceByDatePeriod(DatePeriodDto datePeriodDto) {
		return repository.findByDateDateBetweenOrderByDate(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
				.map(e -> modelMapper.map(e, SandPDto.class)).collect(Collectors.toList()).stream()
				.min(Comparator.comparing(SandPDto::getPriceClose)).get();
	}

	@Override
	public SandPStatDto getStat(long periodDays, double sum, long termDays) {
		LocalDate dateStart = LocalDate.now().minusDays(periodDays + termDays);
		List<Double> sandPStats = new ArrayList<>();
		LocalDate dateEnd = LocalDate.now().minusDays(termDays);
		List<SandP> sandpPeriods = repository.findByDateDateBetweenOrderByDate(dateStart, LocalDate.now())
				.collect(Collectors.toList());
		sandpPeriods.forEach(e -> System.out.println(e));
		int end = sandpPeriods.indexOf(new SandP(new SandPDate("S&P", dateEnd), 0.0));

		for (int start = 0; start < end; start++) {
			
			System.out.println("======================");
			System.out.println("index dateStart in list = " + start);
			System.out.println("sandpStart: " + sandpPeriods.get(start)); 

			LocalDate dateEnd1 = sandpPeriods.get(start).getDate().getDate().plusDays(termDays);
			SandP sandpEnd = null;
			while (!repository.existsById(new SandPDate("S&P", dateEnd1))) {
				dateEnd1 = dateEnd1.minusDays(1);
			}
			sandpEnd = repository.findById(new SandPDate("S&P", dateEnd1)).get();
			int endIndex = sandpPeriods.indexOf(sandpEnd);

			System.out.println("SandpEnd: " + sandpEnd);
			System.out.println("index dateEnd in list = " + endIndex);

			Double apy = ((sum / sandpPeriods.get(start).getPriceClose() * sandpPeriods.get(endIndex).getPriceClose()) - sum) / sum * 100;
			
			System.out.println("==============");
			System.out.println("apy: " + apy);
			sandPStats.add(apy);
			System.out.println("sandpStats: " + sandPStats);
		}

//		while (!dateStart.isAfter(LocalDate.now())) {
//			sandPStats.add(apy(dateStart, dateEnd, sum));
//		}

		double minPercent = sandPStats.stream().min((s1, s2) -> Double.compare(s1, s2)).get();
		double maxPercent = sandPStats.stream().max((s1, s2) -> Double.compare(s1, s2)).get();
		double minRevenue = sum + (sum * minPercent / 100);
		double maxRevenue = sum + (sum * maxPercent / 100);
		return new SandPStatDto(minPercent, maxPercent, minRevenue, maxRevenue);
	}

	
	
//	private double apy(LocalDate dateStart, LocalDate dateEnd, double sum) {
//		double res = 0;
//		SandP sandpStart = repository.findById(new SandPDate("S&P", dateStart))
//				.orElseThrow(() -> new NotFoundException());
//		if (sandpStart == null) {
//			dateStart = dateStart.plusDays(1);
//			sandpStart = repository.findById(new SandPDate("S&P", dateStart))
//					.orElseThrow(() -> new NotFoundException());
//		}
//		SandP sandpEnd = repository.findById(new SandPDate("S&P", dateEnd)).orElseThrow(() -> new NotFoundException());
//
//		if (sandpEnd == null) {
//			dateEnd = dateEnd.plusDays(1);
//			sandpEnd = repository.findById(new SandPDate("S&P", dateEnd)).orElseThrow(() -> new NotFoundException());
//		}
//
//		res = ((sum / sandpStart.getPriceClose() * sandpEnd.getPriceClose()) - sum / sum) * 100;
//		return res;
//
//	}

	
	
	
	
//	===================== VARIANT 1: add in set ============================
//	@Override
//	public void run(String... args) throws Exception {
//
//		List<SandP> newDatas = CsvFileParsing.parsingWithApache();
//
//		System.out.println("new datas: " + newDatas.size());
//
//		Set<SandP> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
//				.collect(Collectors.toSet());
//
//		System.out.println("before add in db: " + oldDatas.size());
//
//		if (oldDatas.isEmpty()) {
//			oldDatas.addAll(newDatas);
//			repository.saveAll(oldDatas);
//	
//			System.out.println("after add in db: " + oldDatas.size());
//
//		} else {
//			if (!newDatas.containsAll(oldDatas)) {
//				oldDatas.addAll(newDatas);
//				repository.saveAll(oldDatas);
//	
//				System.out.println("after add in db: " + oldDatas.size());
//			}
//		}
//	}

	
	
//	===================== VARIANT 2: add in list by stream ======================
	@Override
	public void run(String... args) throws Exception {

		List<SandP> notExistsDatas = new ArrayList<>();

		List<SandP> newDatas = SandPCsvParsing.parsingWithApache();

		System.out.println("amount of data in csv: " + newDatas.size());

		List<SandP> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("amount data before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			repository.saveAll(oldDatas);

			System.out.println("amount data after add in db: " + oldDatas.size());

		} else {
//			if (!newDatas.containsAll(oldDatas)) {
			notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
					.collect(Collectors.toList());
//				oldDatas.addAll(0, notExistsDatas);
			repository.saveAll(notExistsDatas);

			System.out.println("amount new data added in db: " + notExistsDatas.size());
			System.out.println("amount new data added in db: " + oldDatas.size());

//			}
		}

	}


}
