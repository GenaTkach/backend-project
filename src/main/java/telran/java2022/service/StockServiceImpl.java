package telran.java2022.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.Precision;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.StockRepository;
import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockAverageProfitDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.StockProfitDto;
import telran.java2022.dto.exceptions.StockNotFoundException;
import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;
import telran.java2022.model.StockProfit;

@RequiredArgsConstructor
@Component
public class StockServiceImpl implements StockService {

    // Подключаем репозиторий, чтобы был доступ к базе данных
    final StockRepository repository;

    // Подключаем меппер
    final ModelMapper modelMapper;

    @Override
    public StockDto findStockByDate(String symbol, DateDto date) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	LabelDate labelDate = new LabelDate(symbol, LocalDate.parse(date.getDate(), formatter));
	Stock stock = repository.findById(labelDate)
		.orElseThrow(() -> new StockNotFoundException(symbol, LocalDate.parse(date.getDate(), formatter)));
	return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public Iterable<StockDto> findStocksByPeriod(String symbol, DatePeriodDto datePeriodDto) {
	return repository
		.findStocksByIdSymbolAndIdDateBetween(symbol, datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
		.map(s -> modelMapper.map(s, StockDto.class))
		.collect(Collectors.toList());
    }

    @Override
    public Integer downloadDataFromAPIForStockByPeriod(String label, DatePeriodDto datePeriodDto) {
	// TODO Auto-generated method stub
	return null;
    }
    /**
     * Метод для загрузки данных по акции за какой-то период времени. Возвращает
     * кол-во загруженных дневных статистик. Возможно стоит поменять и доработать
     * этот метод Этот метод для работы со Stock.
     */
//    @Override
//    public Integer downloadDataFromAPIForStockByPeriod(String label, DatePeriodDto datePeriodDto) {
//	ResponseEntity<StockDtoAPI> responseEntity = StockAPI.request(label, datePeriodDto.getDateFrom(),
//		datePeriodDto.getDateTo());
//	List<DataDto> list = responseEntity.getBody()
//		.getData();
//	for (int i = 0; i < list.size(); i++) {
//	    DataDto dataDto = modelMapper.map(list.get(i), DataDto.class);
//
//	    String[] splittedTime = dataDto.getDate()
//		    .split("T");
//	    LabelDate id = new LabelDate(dataDto.getSymbol(), splittedTime[0]);
//
//	    String close = dataDto.getClose();
//	    Stock stock = new Stock(id, Double.valueOf(close));
//	    repository.save(stock);
//	}
//	return list.size();
//    }

    /**
     * Метод, который скачивает CSV файл на локальный компьютер и после парсит его в
     * Java объеты В конце записи добавляются в базу данных
     */
    @Override
    public Boolean downloadCSVandParseToDB(String symbol) {

	// period1 и period2 всегда такие, потому что покрывают полностью весь период
	// для любой акции
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
	// Вручную добавляем символ к каждой акции перед сохранением в БД
	beans.forEach(b -> b.getId()
		.setSymbol(symbol));
	// Проверка на существование уже в БД таких акций, чтобы не грузить сервис и не
	// сохранять снова
	if (!(repository.existsById(beans.get(0)
		.getId()) && repository.existsById(
			beans.get(beans.size() - 1)
				.getId()))) {
	    repository.saveAll(beans);
	}
	return true;
    }

    /**
     * Метод нахождения максимального значения close
     */
    @Override
    public StockDto findTopByIdSymbolOrderByClose(String symbol) {
	Stock stock = repository.findTopByIdSymbolOrderByClose(symbol);
	return modelMapper.map(stock, StockDto.class);
    }

    /**
     * Метод нахождения минимального значения close
     */
    @Override
    public StockDto findTopByIdSymbolOrderByCloseDesc(String symbol) {
	Stock stock = repository.findTopByIdSymbolOrderByCloseDesc(symbol);
	return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public Iterable<StockProfitDto> getMinAndMaxYearProfit(String symbol, String from, String to,
	    Integer periodInYears) {

	List<StockProfitDto> returnedListOfStatistics = new ArrayList<>();

	LocalDate fromDate = LocalDate.parse(from);
	LocalDate toDate = LocalDate.parse(to);
	
	// Проверка на корректность дат
	if (toDate.isAfter(LocalDate.now())) {
	    Stock lastDayForThisLabel = repository.findTopByIdSymbolOrderByIdDateDesc(symbol);
	    toDate = lastDayForThisLabel.getId()
		    .getDate();
	}
	final Integer localFinalPeriodInYears = periodInYears;
//	periodInYears *= 365;

	// Stream 2 -> to List stocksToDates
	List<Stock> stocksToDate = repository
		.findStocksByIdSymbolAndIdDateBetween(symbol, fromDate.plusYears(periodInYears),
			toDate.plusYears(periodInYears))
		.collect(Collectors.toList());

	// Stream 1 -> to List stocksFromDate
	List<Stock> stocksFromDate = repository.findStocksByIdSymbolAndIdDateBetween(symbol, fromDate, toDate)
		.limit(stocksToDate.size())
		.collect(Collectors.toList());

	List<StockProfit> listOfStockProfits = new ArrayList<>();

	// Stream 3
	for (int i = 0; i < stocksFromDate.size(); i++) {
	    StockProfit stockProfit = new StockProfit();
	    stockProfit.setSymbol(stocksFromDate.get(i)
		    .getId()
		    .getSymbol());
	    stockProfit.setDateFrom(stocksFromDate.get(i)
		    .getId()
		    .getDate());
	    stockProfit.setDateTo(stocksToDate.get(i)
		    .getId()
		    .getDate());
	    stockProfit.setCloseStart(stocksFromDate.get(i)
		    .getClose());
	    stockProfit.setCloseEnd(stocksToDate.get(i)
		    .getClose());
	    listOfStockProfits.add(stockProfit);
	}

	// Sorting array by profit field for fiding MIN and MAX
	listOfStockProfits.sort((s1, s2) -> s1.getProfit(localFinalPeriodInYears)
		.compareTo(s2.getProfit(localFinalPeriodInYears)));

	// Statistics
	// Minimum StockProfit
	StockProfitDto stockProfitDtoMin = modelMapper.map(listOfStockProfits.get(0), StockProfitDto.class);
	stockProfitDtoMin.setYearProfit(Precision.round(listOfStockProfits.get(0)
		.getProfit(localFinalPeriodInYears), 2));

	// Maximum StockProfit
	StockProfitDto stockProfitDtoMax = modelMapper.map(listOfStockProfits.get(listOfStockProfits.size() - 1),
		StockProfitDto.class);
	stockProfitDtoMax.setYearProfit(Precision.round(listOfStockProfits.get(listOfStockProfits.size() - 1)
		.getProfit(localFinalPeriodInYears), 2));

	// Adding MIN and MAX StockProfits to returning list
	returnedListOfStatistics.add(stockProfitDtoMin);
	returnedListOfStatistics.add(stockProfitDtoMax);
	return returnedListOfStatistics;
    }

    @Override
    public StockAverageProfitDto getAverageProfit(String symbol, String from, String to, Integer periodInYears) {
	LocalDate fromDate = LocalDate.parse(from);
	LocalDate toDate = LocalDate.parse(to);
	if (toDate.isAfter(LocalDate.now())) {
	    Stock lastDayForThisLabel = repository.findTopByIdSymbolOrderByIdDateDesc(symbol);
	    toDate = lastDayForThisLabel.getId()
		    .getDate();
	}
	final Integer innerPeriodInYears = periodInYears;

	// Stream 2 -> to List stocksToDates
	List<Stock> stocksToDate = repository
		.findStocksByIdSymbolAndIdDateBetween(symbol, fromDate.plusYears(periodInYears),
			toDate.plusYears(periodInYears))
		.collect(Collectors.toList());

	// Stream 1 -> to List stocksFromDate
	List<Stock> stocksFromDate = repository.findStocksByIdSymbolAndIdDateBetween(symbol, fromDate, toDate)
		.limit(stocksToDate.size())
		.collect(Collectors.toList());

	List<StockProfit> listOfStockProfits = new ArrayList<>();

	// Stream 3
	for (int i = 0; i < stocksFromDate.size(); i++) {
	    StockProfit stockProfit = new StockProfit();
	    stockProfit.setSymbol(stocksFromDate.get(i)
		    .getId()
		    .getSymbol());
	    stockProfit.setDateFrom(stocksFromDate.get(i)
		    .getId()
		    .getDate());
	    stockProfit.setDateTo(stocksToDate.get(i)
		    .getId()
		    .getDate());
	    stockProfit.setCloseStart(stocksFromDate.get(i)
		    .getClose());
	    stockProfit.setCloseEnd(stocksToDate.get(i)
		    .getClose());
	    listOfStockProfits.add(stockProfit);
	}

	// Finding average profit
	double sumProfits = listOfStockProfits.stream()
		.map(e -> e.getProfit(innerPeriodInYears))
		.reduce(0.0, (total, n) -> total + n);
	double avgProfit = Precision.round(sumProfits / listOfStockProfits.size(), 2);
	StockAverageProfitDto stockAverageProfitDto = new StockAverageProfitDto(symbol, innerPeriodInYears, fromDate,
		toDate, avgProfit);
	return stockAverageProfitDto;
    }

    @Override
    public String correlation(String fromDate, String toDate, String firstSymbol, String secondSymbol) {
	Double[] stocksX = findClosePricesByPeriod(firstSymbol, fromDate, toDate);
	Double[] stocksY = findClosePricesByPeriod(secondSymbol, fromDate, toDate);

	double[] primitiveFirstArray = ArrayUtils.toPrimitive(stocksX);
	double[] primitiveSecondArray = ArrayUtils.toPrimitive(stocksY);

	// Удалить эти выводы потом
//	System.out.println(primitiveFirstArray.length);
//	System.out.println(primitiveSecondArray.length);
	
	double correlationRatio = Precision
		.round(new PearsonsCorrelation().correlation(primitiveFirstArray, primitiveSecondArray), 2);
	String correlationInterpretation = checkCorrelationRatio(correlationRatio);
	return firstSymbol + " - " + secondSymbol + "\n" +  correlationRatio + " : " + correlationInterpretation;
    }

    // Приватный метод для подсчета корреляции
    private Double[] findClosePricesByPeriod(String symbol, String fromDate, String toDate) {
	LocalDate from = LocalDate.parse(fromDate);
	LocalDate to = LocalDate.parse(toDate);
	return repository.findStocksByIdSymbolAndIdDateBetween(symbol, from, to)
		.map(s -> s.getClose())
		.toArray(Double[]::new);
    }

    // Приватный метод для интерпритации корреляционного коэффициента
    private String checkCorrelationRatio(double correlationRatio) {
	if (correlationRatio <= 0.3) {
	    return "Neglible correlation";
	}
	if (0.3 < correlationRatio && correlationRatio <= 0.5) {
	    return "Weak correlation";
	}
	if (0.5 < correlationRatio && correlationRatio <= 0.7) {
	    return "Moderate correlation";
	}
	if (0.7 < correlationRatio && correlationRatio <= 0.9) {
	    return "Strong correlation";
	} else {
	    return "Very strong correlation";
	}
    }
}
