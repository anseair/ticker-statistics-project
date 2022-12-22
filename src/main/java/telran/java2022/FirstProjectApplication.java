package telran.java2022;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.DataRepository;
import telran.java2022.exceptoins.DateExistsException;
import telran.java2022.model.HistoricalData;

@SpringBootApplication
@RequiredArgsConstructor
public class FirstProjectApplication implements CommandLineRunner{
	final DataRepository dataRepository;

	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		List<HistoricalData> datas = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalDataMax.csv"));
				CSVParser csvParser = new CSVParser(br,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			List<CSVRecord> csvRecords = csvParser.getRecords();
			datas = csvRecords.stream().map(HistoricalData::fromCsv).collect(Collectors.toList());

//			for (CSVRecord csvRecord: csvRecords) {
//			HistoricalData data = new HistoricalData(LocalDate.parse(csvRecord.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
//						Double.parseDouble(csvRecord.get(1)),
//						csvRecord.get(2),
//						Double.parseDouble(csvRecord.get(3)),
//						Double.parseDouble(csvRecord.get(4)), 
//						Double.parseDouble(csvRecord.get(5)));
//			datas.add(data);
//			}

			System.out.println();
			System.out.println(datas.size());
			System.out.println(datas.get(0));
			System.out.println(datas.get(1));
			System.out.println(datas.get(2));
			System.out.println();
//			if (dataRepository.findByDate(datas.get(0).getDate().compareTo(other))) {
//				throw new DateExistsException();
//			}
			dataRepository.saveAll(datas);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalDataMax.csv"))) {
//			br.readLine();
//
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				String[] col = line.split(",");
//				HistoricalData data = new HistoricalData(LocalDate.parse(col[0], DateTimeFormatter.ofPattern("MM/dd/yyyy")),
//						Double.parseDouble(col[1]), col[2], Double.parseDouble(col[3]), Double.parseDouble(col[4]),
//						Double.parseDouble(col[5]));
//				datas.add(data);
//			}
//			System.out.println();
//			System.out.println(datas.size());
//			System.out.println(datas.get(0));
//			System.out.println(datas.get(1));
//			System.out.println(datas.get(2));
//
//			System.out.println();
//			
//			dataRepository.saveAll(datas);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
