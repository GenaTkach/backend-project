package telran.java2022.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import telran.java2022.dao.Repository;
import telran.java2022.model.Stock;

@RequiredArgsConstructor
@Component
public class Service {

    // Подключаем репозиторий, чтобы был доступ к базе данных
    final Repository repository;

    @Bean
    public void CSVMaptoObject() throws FileNotFoundException {
	// Cоздаем объект, который парсит из CSV -> Stock
	CsvToBean<Stock> csvToBean = new CsvToBean<>();
	// Локальный путь к CSV файлу
	String pathToCsvFile = "/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/HistoricalPrices.csv";
	CSVReader csvReader = new CSVReader(new FileReader(pathToCsvFile));
	
	// Парсинг в лист Stocks
	List<Stock> list = csvToBean.parse(setMapStrategy(), csvReader);
	for (Stock stock : list) {
	    // После парсинга создаем из каждой строки листа -> Java объект Stock
	    Stock stockPerDay = stock;
	    //Делаем проверку и отсекаем первую строку (заголовки)
	    if (!stockPerDay.getDate().equalsIgnoreCase("date")) {
		repository.save(stockPerDay);
	    }
	}
    }
    
    // Устанавливаем парсинг-стратегию
    private ColumnPositionMappingStrategy<Stock> setMapStrategy() {
	ColumnPositionMappingStrategy<Stock> strategy = new ColumnPositionMappingStrategy<>();
	strategy.setType(Stock.class);
	String[] columns = { "Date", "Open", "High", "Low", "Close" };
	strategy.setColumnMapping(columns);
	return strategy;
    }
}
