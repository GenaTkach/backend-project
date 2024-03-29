package telran.java2022.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
import telran.java2022.dto.CorrelationDto;
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
    public StockDto findStockByDate(String symbol, String date) {
	LocalDate localDate = LocalDate.parse(date);
	LabelDate labelDate = new LabelDate(symbol, localDate);
	Stock stock = repository.findById(labelDate)
		.orElseThrow(() -> new StockNotFoundException(symbol, localDate));
	return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public Iterable<StockDto> findStocksByPeriod(String symbol, String dateFrom, String dateTo) {
	LocalDate localDateFrom = LocalDate.parse(dateFrom);
	LocalDate localDateTo = LocalDate.parse(dateTo);
	return repository.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, localDateFrom, localDateTo)
		.map(s -> modelMapper.map(s, StockDto.class))
		.collect(Collectors.toList());
    }

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
	File file = new File("/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/CSV/" + symbol);

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

	// Проверка на корректность дат, если дата заходит за рамку toDate ->
	// -> берем дату последней акции в базе данных и присваем toDate
	if (toDate.isAfter(LocalDate.now())) {
	    Stock lastDayForThisLabel = repository.findTopByIdSymbolOrderByIdDateDesc(symbol);
	    toDate = lastDayForThisLabel.getId()
		    .getDate();
	}
	final Integer localFinalPeriodInYears = periodInYears;

	// Stream 1 -> to List stocksFromDate
	List<Stock> stocksFromDate = repository
		.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, fromDate, toDate)
		.collect(Collectors.toList());

	// Stream 2 -> to List stocksToDates
	List<Stock> stocksToDate = repository
		.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, fromDate.plusYears(periodInYears),
			toDate.plusYears(periodInYears))
		.collect(Collectors.toList());

	// Лист для создания StockProfit
	// -> результирующих объектов, получаемых из 1 и 2 stream
	List<StockProfit> listOfStockProfits = new ArrayList<>();

	// Заполнение ранее созданного листа listOfStockProfits
	for (int i = 0; i < stocksFromDate.size(); i++) {
	    /*
	     * Главная проверка Если dateFrom + periodInYears было раньше конечной рамки
	     * toDate То выполняй функцию getResultingStockProfitFromTwoArrays(), которая
	     * возвращает StockProfit. Если i хоть раз не заходит в if(), то цикл
	     * прекращается. Тем самым не бегая просто так.
	     * 
	     */
	    if (stocksFromDate.get(i)
		    .getId()
		    .getDate()
		    .plusYears(periodInYears)
		    .isBefore(toDate)) {
		listOfStockProfits.add(getStockProfit(i, stocksFromDate, stocksToDate, periodInYears));
	    } else {
		break;
	    }
	}

	// Sorting array by profit field for fiding MIN and MAX
	listOfStockProfits.sort((s1, s2) -> s1.getProfit(localFinalPeriodInYears)
		.compareTo(s2.getProfit(localFinalPeriodInYears)));

	// Statistics
	// Minimum StockProfit
	StockProfitDto stockProfitDtoMin = modelMapper.map(listOfStockProfits.get(0), StockProfitDto.class);
	stockProfitDtoMin.setYearProfit(Precision.round(listOfStockProfits.get(0)
		.getProfit(localFinalPeriodInYears), 2));
	stockProfitDtoMin.setIncome(Precision.round(listOfStockProfits.get(0)
		.getIncome(), 3));

	// Maximum StockProfit
	StockProfitDto stockProfitDtoMax = modelMapper.map(listOfStockProfits.get(listOfStockProfits.size() - 1),
		StockProfitDto.class);
	stockProfitDtoMax.setYearProfit(Precision.round(listOfStockProfits.get(listOfStockProfits.size() - 1)
		.getProfit(localFinalPeriodInYears), 2));
	stockProfitDtoMax.setIncome(Precision.round(listOfStockProfits.get(listOfStockProfits.size() - 1)
		.getIncome(), 3));

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
		.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, fromDate.plusYears(periodInYears),
			toDate.plusYears(periodInYears))
		.collect(Collectors.toList());

	// Stream 1 -> to List stocksFromDate
	List<Stock> stocksFromDate = repository
		.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, fromDate, toDate)
		.limit(stocksToDate.size())
		.collect(Collectors.toList());

	List<StockProfit> listOfStockProfits = new ArrayList<>();

	// Заполнение ранее созданного листа listOfStockProfits
	for (int i = 0; i < stocksFromDate.size(); i++) {
	    /*
	     * Главная проверка Если dateFrom + periodInYears было раньше конечной рамки
	     * toDate То выполняй функцию getResultingStockProfitFromTwoArrays(), которая
	     * возвращает StockProfit. Если i хоть раз не заходит в if(), то цикл
	     * прекращается. Тем самым не бегая просто так.
	     * 
	     */
	    if (stocksFromDate.get(i)
		    .getId()
		    .getDate()
		    .plusYears(periodInYears)
		    .isBefore(toDate)) {
		listOfStockProfits.add(getStockProfit(i, stocksFromDate, stocksToDate, periodInYears));
	    } else {
		break;
	    }
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
    public CorrelationDto correlation(String fromDate, String toDate, String firstSymbol, String secondSymbol) {

	List<String> stockNames = new ArrayList<>();
	stockNames.add(firstSymbol);
	stockNames.add(secondSymbol);

	Double[] stocksX = findClosePricesByPeriod(firstSymbol, fromDate, toDate);
	Double[] stocksY = findClosePricesByPeriod(secondSymbol, fromDate, toDate);

	List<Double[]> stockDayPrices = new ArrayList<>();
	stockDayPrices.add(stocksX);
	stockDayPrices.add(stocksY);

	List<String> stockDays = getDays(firstSymbol, fromDate, toDate);

//	// Удалить эти выводы потом
//	System.out.println(primitiveFirstArray.length);
//	System.out.println(primitiveSecondArray.length);

	Double correlationRatio = Precision.round(
		new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(stocksX), ArrayUtils.toPrimitive(stocksY)),
		2);
	String interpretation = checkCorrelationRatio(correlationRatio);
	CorrelationDto correlationDto = new CorrelationDto(stockNames, correlationRatio, interpretation, stockDayPrices,
		stockDays);
	return correlationDto;
    }

    private StockProfit getStockProfit(int index, List<Stock> stocksFromDate, List<Stock> stocksToDate,
	    Integer periodInYears) {
	// j и k созданы для случаев, когда даты dateFrom и dateTo из 2 листов
	// (stocksFromDate и stocksToDate) не равны.
	// В таких ситуациях мы алгоритмом подстраиваем dateTo с помощью j и k
	int j = index;
	int k = index;

	StockProfit stockProfit = new StockProfit();

	stockProfit.setSymbol(stocksFromDate.get(index)
		.getId()
		.getSymbol());
	stockProfit.setDateFrom(stocksFromDate.get(index)
		.getId()
		.getDate());

	LocalDate stockFromDate = stocksFromDate.get(index)
		.getId()
		.getDate()
		.plusYears(periodInYears);
	LocalDate stockToDate = stocksToDate.get(index)
		.getId()
		.getDate();

	// Если даты stocksFromDate и stocksToDate равны
	if (stockFromDate.compareTo(stockToDate) == 0) {
	    stockProfit.setDateTo(stockToDate);
	}
	// Если дата stocksFromDate раньше stocksToDate
	else if (stockFromDate.isBefore(stockToDate)) {
	    while (stockFromDate.compareTo(stocksToDate.get(k)
		    .getId()
		    .getDate()) < 0) {
		k--;
		j = k;
	    }
	    stockProfit.setDateTo(stocksToDate.get(k)
		    .getId()
		    .getDate());
	}
	// Если дата stocksFromDate позже stocksToDate
	else {
	    while ((stockFromDate.compareTo(stocksToDate.get(j)
		    .getId()
		    .getDate()) > 0)) {
		j++;
	    }
	    stockProfit.setDateTo(stocksToDate.get(j)
		    .getId()
		    .getDate());
	}

	stockProfit.setCloseStart(stocksFromDate.get(index)
		.getClose());
	stockProfit.setCloseEnd(stocksToDate.get(j)
		.getClose());
	return stockProfit;
    }

    // Приватный метод для подсчета корреляции
    private Double[] findClosePricesByPeriod(String symbol, String fromDate, String toDate) {
	LocalDate from = LocalDate.parse(fromDate);
	LocalDate to = LocalDate.parse(toDate);
	return repository.findStocksByIdSymbolAndIdDateBetweenOrderByIdDate(symbol, from, to)
		.map(s -> s.getClose())
		.toArray(Double[]::new);
    }

    // Приватный метод для получения всех дат для корреляции
    private List<String> getDays(String firstSymbol, String fromDate, String toDate) {
	return repository
		.findStockByIdSymbolAndIdDateBetweenOrderByIdDate(firstSymbol, LocalDate.parse(fromDate),
			LocalDate.parse(toDate))
		.map(d -> d.getId()
			.getDate()
			.toString())
		.collect(Collectors.toList());
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

    @Override
    public List<String> findAllSymbolNames() {
	return repository.findAllBy();
    }
}
