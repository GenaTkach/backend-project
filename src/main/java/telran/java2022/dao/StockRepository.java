package telran.java2022.dao;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.Stock;

public interface StockRepository extends CrudRepository<Stock, String> {
    Stream<Stock> findStocksByDateBetween(String from, String to);
}
