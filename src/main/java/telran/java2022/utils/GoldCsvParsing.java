package telran.java2022.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import telran.java2022.gold.model.Gold;
import telran.java2022.gold.model.GoldDate;
import telran.java2022.sandp.model.SandP;
import telran.java2022.sandp.model.SandPDate;

public class GoldCsvParsing {

	
	public static List<Gold> parsingWithApache(String fileName, String name, String pattern, int numberOfClose) {
		List<Gold> res = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName));
				CSVParser csvParser = new CSVParser(br,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			List<CSVRecord> csvRecords = csvParser.getRecords();
			res = csvRecords.stream().map(g -> fillData(g, name, pattern, numberOfClose)).collect(Collectors.toList());

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

	private static Gold fillData(CSVRecord csvRecord, String name, String pattern, int numberOfClose) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate date = LocalDate.parse(csvRecord.get(0), formatter);
	    double price = Double.parseDouble(csvRecord.get(numberOfClose));
	    Gold res = new Gold(new GoldDate(name, date), price);
		return res;
	}
	
	
	
	
	
	
	public static List<SandP> parsingWithoutApache() {
		List<SandP> res = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalData1months.csv"))) {
			br.readLine();
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] col = line.split(",");
				res.add(fillSandP(col));
				line = br.readLine();
			}
//			
//			System.out.println();
//			System.out.println(res.size());
//			System.out.println(res.get(0));
//			System.out.println(res.get(1));
//			System.out.println(res.get(2));
//			System.out.println();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private static SandP fillSandP(String[] arr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate date = LocalDate.parse(arr[0], formatter);
	    double price = Double.parseDouble(arr[1]);
	    SandP res = new SandP(new SandPDate("S&P", date), price);
		return res;
	}
}
