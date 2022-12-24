package telran.java2022.service;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.StockRepository;
import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.exceptions.StockNotFoundException;
import telran.java2022.model.Stock;

@RequiredArgsConstructor
@Component
public class StockServiceImpl implements StockService {

    // Подключаем репозиторий, чтобы был доступ к базе данных
    final StockRepository repository;
    // Подключаем Model Mapper
    final ModelMapper modelMapper;

    @Override
    public StockDto findStockByDate(DateDto date) {
	Stock stock = repository.findById(date.getDate())
		.orElseThrow(() -> new StockNotFoundException(date.getDate()));
	return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public Iterable<StockDto> findStocksByPeriod(DatePeriodDto datePeriodDto) {
	return repository.findStocksByDateBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
		.map(s -> modelMapper.map(s, StockDto.class))
		.collect(Collectors.toList());
    }
}
