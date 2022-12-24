package telran.java2022.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import telran.java2022.model.HistoricalData;

public class CsvFileParsing {

	public static List<HistoricalData> parsingWithApache() {
		List<HistoricalData> datas = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalData1month.csv"));
				CSVParser csvParser = new CSVParser(br,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			List<CSVRecord> csvRecords = csvParser.getRecords();
			datas = csvRecords.stream().map(CsvFileParsing::fillData).collect(Collectors.toList());

//
////			for (CSVRecord csvRecord: csvRecords) {
////			HistoricalData data = new HistoricalData(LocalDate.parse(csvRecord.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
////						Double.parseDouble(csvRecord.get(1)),
////						csvRecord.get(2),
////						Double.parseDouble(csvRecord.get(3)),
////						Double.parseDouble(csvRecord.get(4)), 
////						Double.parseDouble(csvRecord.get(5)));
////			datas.add(data);
////			}
//

			System.out.println();
			System.out.println(datas.size());
			System.out.println(datas.get(0));
			System.out.println(datas.get(1));
			System.out.println(datas.get(2));
			System.out.println();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datas;
	}

	private static HistoricalData fillData(CSVRecord csvRecord) {
//		String[] dateArr = csvRecord.get(0).split("/");
//		LocalDateTime date = LocalDateTime.of(
//				Integer.parseInt(dateArr[2]), 
//				Integer.parseInt(dateArr[0]),
//				Integer.parseInt(dateArr[1]), 05, 00);
		return new HistoricalData(csvRecord.get(0).replace("/", "-"), 
				Double.parseDouble(csvRecord.get(1)),
				csvRecord.get(2),
				Double.parseDouble(csvRecord.get(3)),
				Double.parseDouble(csvRecord.get(4)), 
				Double.parseDouble(csvRecord.get(5)));
	}
	
	
	public static List<HistoricalData> parsingWithoutApache() {
		List<HistoricalData> datas = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalData1month.csv"))) {
			br.readLine();

			String line = "";
			while ((line = br.readLine()) != null) {
				String[] col = line.split(",");
				col[0] = col[0].replace("/", "-");
//				String[] dateArr = col[0].split("/");
//				LocalDateTime date = LocalDateTime.of(
//									Integer.parseInt(dateArr[2]), 
//									Integer.parseInt(dateArr[0]),
//									Integer.parseInt(dateArr[1]), 05, 00);
				HistoricalData data = new HistoricalData(
									col[0].replace("/", "-"), 
									Double.parseDouble(col[1]), 
									col[2],
									Double.parseDouble(col[3]), 
									Double.parseDouble(col[4]), 
									Double.parseDouble(col[5]));
				datas.add(data);
			}

			System.out.println();
			System.out.println(datas.size());
			System.out.println(datas.get(0));
			System.out.println(datas.get(1));
			System.out.println(datas.get(2));
			System.out.println();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return datas;
	}
}
