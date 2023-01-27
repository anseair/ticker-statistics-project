package telran.java2022.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.SandP;
import telran.java2022.model.SandPDate;

public interface SandPRepository extends CrudRepository<SandP, SandPDate> {

	Stream<SandP> findByDateDateBetween(LocalDate dateFrom, LocalDate dateTo);
}
