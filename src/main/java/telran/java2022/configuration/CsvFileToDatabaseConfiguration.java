package telran.java2022.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.DataRepository;
import telran.java2022.model.HistoricalData;

@Configuration
@RequiredArgsConstructor
public class CsvFileToDatabaseConfiguration {

//	final DataRepository dataRepository;
//	
//	@Bean
//	public  static List<HistoricalData> csvToDatabase() {
//		List<HistoricalData> datas = new ArrayList<>();
//
//		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalDataMax.csv"));
//				CSVParser csvParser = new CSVParser(br,
//						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
//			List<CSVRecord> csvRecords = csvParser.getRecords();
//			datas = csvRecords.stream().map(HistoricalData::fromCsv).collect(Collectors.toList());
////
//////			for (CSVRecord csvRecord: csvRecords) {
//////			HistoricalData data = new HistoricalData(LocalDate.parse(csvRecord.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
//////						Double.parseDouble(csvRecord.get(1)),
//////						csvRecord.get(2),
//////						Double.parseDouble(csvRecord.get(3)),
//////						Double.parseDouble(csvRecord.get(4)), 
//////						Double.parseDouble(csvRecord.get(5)));
//////			datas.add(data);
//////			}
////
//			System.out.println();
//			System.out.println(datas.size());
//			System.out.println(datas.get(0));
//			System.out.println(datas.get(1));
//			System.out.println(datas.get(2));
//			System.out.println();
//////			if (dataRepository.existsById(csvParser.)) {
//////				throw new DateExistsException(userRegisterDto.getLogin());
//////			}
//			dataRepository.saveAll(datas);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return datas;
//
////		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalDataMax.csv"))) {
////		br.readLine();
////
////		String line = "";
////		while ((line = br.readLine()) != null) {
////			String[] col = line.split(",");
////			HistoricalData data = new HistoricalData(LocalDate.parse(col[0], DateTimeFormatter.ofPattern("MM/dd/yyyy")),
////					Double.parseDouble(col[1]), col[2], Double.parseDouble(col[3]), Double.parseDouble(col[4]),
////					Double.parseDouble(col[5]));
////			datas.add(data);
////		}
////		System.out.println();
////		System.out.println(datas.size());
////		System.out.println(datas.get(0));
////		System.out.println(datas.get(1));
////		System.out.println(datas.get(2));
////
////		System.out.println();
////		
////		dataRepository.saveAll(datas);
////	} catch (IOException e) {
////		// TODO Auto-generated catch block
////		e.printStackTrace();
////	}
////		return datas;
//	}
}
