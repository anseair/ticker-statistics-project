package telran.java2022.sandp.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.gold.model.Gold;
import telran.java2022.gold.model.GoldDate;
import telran.java2022.sandp.model.SandP;
import telran.java2022.sandp.model.SandPDate;

public interface SandPRepository extends CrudRepository<SandP, SandPDate> {

	Stream<SandP> findByDateDateBetweenOrderByDate(LocalDate dateFrom, LocalDate dateTo);
}
