package telran.java2022.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.DataRepository;
import telran.java2022.dto.DataDto;
import telran.java2022.dto.DateDto;
import telran.java2022.exceptoins.DateExistsException;
import telran.java2022.model.HistoricalData;

@RequiredArgsConstructor
@Service
public class DataServiceImpl implements DataService {
	final DataRepository dataRepository;
	final ModelMapper modelMapper;

	@Override
	public boolean add(HistoricalData historicalData) {
		if (dataRepository.existsById(historicalData.getId())) {
			throw new DateExistsException();
		}
		HistoricalData data = new HistoricalData();
		dataRepository.save(data);
		return true;
	}

	@Override
	public DataDto findByDate(DateDto date) {
		HistoricalData data = dataRepository.findByDate(date.getDate());
		return modelMapper.map(data, DataDto.class);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		List<HistoricalData> datas = CsvFileToDatabaseConfiguration.csvToDatabase();
//		dataRepository.saveAll(datas);
//	}

}
