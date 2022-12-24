package telran.java2022.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.csv.CSVRecord;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"date"})
public class HistoricalData{

	@Id
	String date;
//	LocalDateTime date;
	Double close_last;
	String volume;
	Double open;
	Double high;
	Double low;

	
	public HistoricalData(String date, Double close_last, String volume, Double open, Double high, Double low) {
		this.date = date;
		this.close_last = close_last;
		this.volume = volume;
		this.open = open;
		this.high = high;
		this.low = low;
	}
	
	public static HistoricalData fromCsv(CSVRecord csvRecord) {
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

	@Override
	public String toString() {
		return "HistoricalData [date=" + date + ", close_last=" + close_last + ", volume=" + volume + ", open=" + open
				+ ", high=" + high + ", low=" + low + "]";
	}


	
	

}
