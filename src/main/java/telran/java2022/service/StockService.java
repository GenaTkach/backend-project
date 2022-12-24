package telran.java2022.service;


import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockDto;

public interface StockService {
    StockDto findStockByDate(DateDto date);

    Iterable<StockDto> findStocksByPeriod(DatePeriodDto datePeriodDto);
}
