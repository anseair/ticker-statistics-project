package telran.java2022.dto;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataDto {
	String id;
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate date;
	Double close_last;
	String volume;
	Double open;
	Double high;
	Double low;
}
