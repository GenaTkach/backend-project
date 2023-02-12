package telran.java2022.service;

import java.util.List;

import telran.java2022.dto.CorrelationDto;
import telran.java2022.dto.StockAverageProfitDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.StockProfitDto;

public interface StockService {
    StockDto findStockByDate(String symbol, String date);

    Iterable<StockDto> findStocksByPeriod(String symbol, String dateFrom, String dateTo);

    Boolean downloadCSVandParseToDB(String symbol);

    // Maximum
    StockDto findTopByIdSymbolOrderByClose(String symbol);

    // Minimum
    StockDto findTopByIdSymbolOrderByCloseDesc(String symbol);

    Iterable<StockProfitDto> getMinAndMaxYearProfit(String symbol, String fromDate, String toDate,
	    Integer periodInYears);

    StockAverageProfitDto getAverageProfit(String symbol, String fromDate, String toDate, Integer periodInYears);

    // Correlation
    CorrelationDto correlation(String fromDate, String toDate, String firstSymbol, String secondSymbol);

    List<String> findAllSymbolNames();
}
