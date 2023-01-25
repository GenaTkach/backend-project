package telran.java2022.service;

import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockAverageProfitDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.StockProfitDto;

public interface StockService {
    StockDto findStockByDate(String symbol, DateDto date);

    Iterable<StockDto> findStocksByPeriod(String symbol, DatePeriodDto datePeriodDto);

    Integer downloadDataFromAPIForStockByPeriod(String label, DatePeriodDto datePeriodDto);

    Boolean downloadCSVandParseToDB(String symbol);

    // Maximum
    StockDto findTopByIdSymbolOrderByClose(String symbol);

    // Minimum
    StockDto findTopByIdSymbolOrderByCloseDesc(String symbol);

    Iterable<StockProfitDto> getMinAndMaxYearProfit(String symbol, String fromDate, String toDate, Integer periodInYears);
    
    StockAverageProfitDto getAverageProfit(String symbol, String fromDate, String toDate, Integer periodInYears);
}
