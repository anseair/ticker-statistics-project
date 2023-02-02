package telran.java2022.gold.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.gold.model.Gold;
import telran.java2022.gold.model.GoldDate;

public interface GoldRepository extends CrudRepository<Gold, GoldDate> {

	Stream<Gold> findByDateDateBetweenOrderByDate(LocalDate dateFrom, LocalDate dateTo);
}
