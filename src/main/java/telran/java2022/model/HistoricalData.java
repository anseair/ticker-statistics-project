package telran.java2022.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVRecord;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

@EqualsAndHashCode(of = {"id"})
public class HistoricalData implements Serializable {

	private static final long serialVersionUID = -9134665312805093000L;
	String id;
	LocalDate date;
	Double close_last;
	String volume;
	Double open;
	Double high;
	Double low;

	public HistoricalData(LocalDate date, Double close_last, String volume, Double open, Double high, Double low) {
		this.date = date;
		this.close_last = close_last;
		this.volume = volume;
		this.open = open;
		this.high = high;
		this.low = low;
	}
	
	public static HistoricalData fromCsv(CSVRecord csvRecord) {
		return new HistoricalData(LocalDate.parse(csvRecord.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
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
