package telran.java2022.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;
import telran.java2022.API.StockAPI;
import telran.java2022.dao.StockRepository;
import telran.java2022.dto.DataDto;
import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.StockDtoAPI;
import telran.java2022.dto.exceptions.StockNotFoundException;
import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;

@RequiredArgsConstructor
@Component
public class StockServiceImpl implements StockService {

    // Подключаем репозиторий, чтобы был доступ к базе данных
    final StockRepository repository;

    // Подключаем меппер
    final ModelMapper modelMapper;


    @Override
    public StockDto findStockByDate(String symbol, DateDto date) {
	LabelDate labelDate = new LabelDate(symbol, date.getDate());
	Stock stock = repository.findById(labelDate)
		.orElseThrow(() -> new StockNotFoundException(symbol, date.getDate()));
	return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public Iterable<StockDto> findStocksByPeriod(String symbol, DatePeriodDto datePeriodDto) {
	return repository
		.findStocksByIdSymbolAndIdDateBetween(symbol, datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
		.map(s -> modelMapper.map(s, StockDto.class))
		.collect(Collectors.toList());
    }

    /**
     * Метод для загрузки данных по акции за какой-то период времени. Возвращает
     * кол-во загруженных дневных статистик. Возможно стоит поменять и доработать
     * этот метод Этот метод для работы со Stock.
     */
    @Override
    public Integer downloadDataFromAPIForStockByPeriod(String label, DatePeriodDto datePeriodDto) {
	ResponseEntity<StockDtoAPI> responseEntity = StockAPI.request(label, datePeriodDto.getDateFrom(),
		datePeriodDto.getDateTo());
	List<DataDto> list = responseEntity.getBody()
		.getData();
	for (int i = 0; i < list.size(); i++) {
	    DataDto dataDto = modelMapper.map(list.get(i), DataDto.class);

	    String[] splittedTime = dataDto.getDate()
		    .split("T");
	    LabelDate id = new LabelDate(dataDto.getSymbol(), splittedTime[0]);

	    String close = dataDto.getClose();
	    Stock stock = new Stock(id, close);
	    repository.save(stock);
	}
	return list.size();
    }
    
    /**
     * Метод, который скачивает CSV файл на локальный компьютер и после парсит его в Java объеты
     * В конце записи добавляются в базу данных
     */
    @Override
    public Boolean downloadCSVandParseToDB(String symbol) {
	
	// period1 и period2 всегда такие, потому что покрывают полностью весь период для любой акции
	final String period1 = "1";
	final String period2 = "16725312000";
	final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/download/" + symbol;
	
	// Создание файла и сохранение на локальный диск
	File file = new File("/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/" + symbol);
	
	// Создание URL адреса, который будет запращивать CSV файл с сайта 
	UriComponents builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
		.queryParam("period1", period1)
		.queryParam("period2", period2)
		.build();

	try {
	    FileUtils.copyURLToFile(new URL(builder.toString()), file);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}
	
	// Парсинг и создание List<Stock>
	List<Stock> beans = new ArrayList<>();
	try {
	    beans = new CsvToBeanBuilder<Stock>(new FileReader(file)).withType(Stock.class)
		    .build()
		    .parse();
	} catch (IllegalStateException | FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}

	// Вручную добавляем символ к каждой акции перед сохранинем в БД
	beans.forEach(b -> b.getId()
		.setSymbol(symbol));
	// Проверка на существование уже в БД таких акций, чтобы не грузить сервис и не сохранять снова
	if (!(repository.existsById(beans.get(0)
		.getId()) && repository.existsById(
			beans.get(beans.size() - 1)
				.getId()))) {
	    repository.saveAll(beans);
	}
	return true;
    }

    @Override
    public StockDto findTopByIdSymbolOrderByClose(String symbol) {
	Stock stock = repository.findTopByIdSymbolOrderByClose(symbol);
	System.out.println(stock);
	return modelMapper.map(stock, StockDto.class); 
    }

}
