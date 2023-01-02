package telran.java2022.configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import telran.java2022.dao.StockRepository;
import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;

@Configuration
@RequiredArgsConstructor
public class ServiceConfiguration {

    final StockRepository repository;

    @Bean
    public ModelMapper getModelMapper() {
	ModelMapper modelMapper = new ModelMapper();
	modelMapper.getConfiguration()
		.setFieldMatchingEnabled(true)
		.setFieldAccessLevel(AccessLevel.PRIVATE)
		.setMatchingStrategy(MatchingStrategies.STRICT);
	return modelMapper;
    }

    // Парсер через локальный файл
    // Для Stock
    public void CSVMaptoObject() throws FileNotFoundException {
	// Cоздаем объект, который парсит из CSV -> Stock
	CsvToBean<Stock> csvToBean = new CsvToBean<>();
	// Локальный путь к CSV файлу
	String pathToCsvFile = "/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/AAPL.csv";
	String[] getSymbol = pathToCsvFile.split("/");
	String symbol = getSymbol[getSymbol.length - 1].split(".csv")[0];
	CSVReader csvReader = new CSVReader(new FileReader(pathToCsvFile));

	// Парсинг в лист Stocks
	List<Stock> list = csvToBean.parse(setMapStrategy(), csvReader);
	for (Stock stock : list) {

	    ModelMapper modelMapper = getModelMapper();
	    // После парсинга создаем из каждой строки листа -> Java объект Stock
	    LabelDate id = modelMapper.map(stock, LabelDate.class);
	    id.setSymbol(symbol);
	    System.out.println(id);
	    Stock stockPerDay = modelMapper.map(stock, Stock.class);
	    System.out.println(stockPerDay);
	    stockPerDay.setId(id);
	    // Делаем проверку и отсекаем первую строку (заголовки)
	    if (!stockPerDay.getId()
		    .getDate()
		    .equalsIgnoreCase("date")) {
		repository.save(stockPerDay);
	    }
	}
    }

    // Устанавливаем парсинг-стратегию
    private ColumnPositionMappingStrategy<Stock> setMapStrategy() {
	ColumnPositionMappingStrategy<Stock> strategy = new ColumnPositionMappingStrategy<>();
	strategy.setType(Stock.class);
	String[] columns = { "Date", "Open", "High", "Low", "Close", "Adj Close", "Volume" };
	strategy.setColumnMapping(columns);
	return strategy;
    }

}