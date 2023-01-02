package telran.java2022.dao;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;

public interface StockRepository extends CrudRepository<Stock, LabelDate> {
    
    Stream<Stock> findStocksByIdSymbolAndIdDateBetween(String symbol, String from, String to);
    
    // Этот метод нужно доделать, чтобы рпавильно работал
    // Нужна предварительная сортировка по close
    Stock findTopByIdSymbolOrderByClose(String symbol);
}
