package telran.java2022.ticker.dao;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.ticker.model.TickerIdStat;
import telran.java2022.ticker.model.TickerStat;

public interface TickerStatRepository extends CrudRepository<TickerStat, TickerIdStat> {

}
