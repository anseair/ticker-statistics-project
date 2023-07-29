package telran.java2022.ticker.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.ticker.model.TickerDescription;

public interface TickerDescriptionRepository extends CrudRepository<TickerDescription, String>{

	List<TickerDescription> findAllByOrderByTicker();
}
