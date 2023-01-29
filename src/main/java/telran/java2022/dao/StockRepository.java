package telran.java2022.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;

public interface StockRepository extends CrudRepository<Stock, LabelDate> {
    
    Stream<Stock> findStocksByIdSymbolAndIdDateBetween(String symbol, LocalDate from, LocalDate to);
    
    Stock findTopByIdSymbolOrderByClose(String symbol);
    
    Stock findTopByIdSymbolOrderByCloseDesc(String symbol);
    
    Stock findTopByIdSymbolOrderByIdDateDesc(String symbol);

}
