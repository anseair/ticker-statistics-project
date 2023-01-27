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

import telran.java2022.model.SandP;
import telran.java2022.model.SandPDate;

public class CsvFileParsing {

	
	public static List<SandP> parsingWithApache() {
		List<SandP> res = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("HistoricalData6months.csv"));
				CSVParser csvParser = new CSVParser(br,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase())) {
			List<CSVRecord> csvRecords = csvParser.getRecords();

			
			res = csvRecords.stream().map(CsvFileParsing::fillData).collect(Collectors.toList());

			System.out.println();
			System.out.println(res.size());
			System.out.println(res.get(0));
			System.out.println(res.get(1));
			System.out.println(res.get(2));
			System.out.println();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
		
	}

	private static SandP fillData(CSVRecord csvRecord) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate date = LocalDate.parse(csvRecord.get(0), formatter);
		
	    double price = Double.parseDouble(csvRecord.get(1));
	    SandP res = new SandP(new SandPDate("S&P", date), price);
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
