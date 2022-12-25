package telran.java2022.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

    // Метод для загрузки данных по акции за какой-то период времени.
    // Возвращает кол-во загруженных дневных статистик. 
    // Возможно стоит поменять и доработать этот метод.
    @Override
    public Integer downloadDataForStockByPeriod(String label, DatePeriodDto datePeriodDto) {
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
}
