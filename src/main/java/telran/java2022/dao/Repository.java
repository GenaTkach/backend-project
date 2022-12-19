package telran.java2022.dao;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.model.Stock;

public interface Repository extends CrudRepository<Stock, String> {

}
