package telran.java2022.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.SandPRepository;
import telran.java2022.dto.SandPDto;
import telran.java2022.exceptoins.DateExistsException;
import telran.java2022.exceptoins.NotFoundException;
import telran.java2022.model.SandP;
import telran.java2022.model.SandPDate;
import telran.java2022.utils.CsvFileParsing;

@RequiredArgsConstructor
@Service
public class SandPServiceImpl implements SandPService, CommandLineRunner {
	final SandPRepository repository;
	final ModelMapper modelMapper;

	@Override
	public SandPDto add(SandPDto sandpDto) {
		if(repository.existsById(modelMapper.map(sandpDto.getDate(), SandPDate.class))) {
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

		List<SandP> newDatas = CsvFileParsing.parsingWithApache();

		System.out.println("new datas: " + newDatas.size());

		List<SandP> oldDatas = StreamSupport.stream(repository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		System.out.println("before add in db: " + oldDatas.size());

		if (oldDatas.isEmpty()) {
			oldDatas.addAll(newDatas);
			repository.saveAll(oldDatas);

			System.out.println("after add in db: " + oldDatas.size());

		} else {
//			if (!newDatas.containsAll(oldDatas)) {
				notExistsDatas = newDatas.stream().filter(arr -> !oldDatas.stream().anyMatch(arr::equals))
						.collect(Collectors.toList());
//				oldDatas.addAll(0, notExistsDatas);
				repository.saveAll(notExistsDatas);

				System.out.println("new datas in db: " + notExistsDatas.size());
				System.out.println("after add in db: " + oldDatas.size());

//			}
		}

	}

}
