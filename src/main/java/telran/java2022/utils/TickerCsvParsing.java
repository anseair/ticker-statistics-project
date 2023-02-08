package telran.java2022.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import telran.java2022.ticker.model.Ticker;
import telran.java2022.ticker.model.TickerId;

public class TickerCsvParsing {

	
	public static List<Ticker> parsingWithApache(String fileName, String name, String pattern, int numberOfClose) {
		List<Ticker> res = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName));
				CSVParser csvParser = new CSVParser(br,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			List<CSVRecord> csvRecords = csvParser.getRecords();
			res = csvRecords.stream().map(t -> fillData(t, name, pattern, numberOfClose)).collect(Collectors.toList());

			System.out.println();
			System.out.println(res.get(0));
			System.out.println(res.get(1));
			System.out.println(res.get(2));
			System.out.println();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private static Ticker fillData(CSVRecord csvRecord, String name, String pattern, int numberOfClose) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate date = LocalDate.parse(csvRecord.get(0), formatter);
	    double price = Double.parseDouble(csvRecord.get(numberOfClose));
	    Ticker res = new Ticker(new TickerId(name, date), price);
		return res;
	}
	
	
//	public static List<Ticker> parsingWithoutApache(String fileName, String name, String pattern, int numberOfClose) {
//		List<Ticker> res = new ArrayList<>();
//		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//			br.readLine();
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				String[] col = line.split(",");
//				res.add(fillSandP(col, name, pattern, numberOfClose));
//				line = br.readLine();
//			}
////			
////			System.out.println();
////			System.out.println(res.get(0));
////			System.out.println(res.get(1));
////			System.out.println(res.get(2));
////			System.out.println();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//	private static Ticker fillSandP(String[] arr, String name, String pattern, int numberOfClose) {
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//		LocalDate date = LocalDate.parse(arr[0], formatter);
//	    double price = Double.parseDouble(arr[numberOfClose]);
//	    Ticker res = new Ticker(new TickerId(name, date), price);
//		return res;
//	}
}
