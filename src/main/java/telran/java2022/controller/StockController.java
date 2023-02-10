package telran.java2022.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.StockAverageProfitDto;
import telran.java2022.dto.StockDto;
import telran.java2022.dto.StockProfitDto;
import telran.java2022.service.StockService;

@RestController
@RequiredArgsConstructor
public class StockController {
    
    final StockService service;

    @GetMapping("/stock/date")
    public StockDto findStockByDate(@RequestParam String symbol, @RequestParam String date) {
	return service.findStockByDate(symbol, date);
    }

    @GetMapping("/stock/period")
    public Iterable<StockDto> findStocksByPeriod(@RequestParam String symbol,
	    @RequestParam String dateFrom, @RequestParam String dateTo) {
	return service.findStocksByPeriod(symbol, dateFrom, dateTo);
    }

    @GetMapping("stock/download/csv")
    public Boolean parseDataFromLocalCSV(@RequestParam String symbol) {
	return service.downloadCSVandParseToDB(symbol);
    }

    @GetMapping("stock/min")
    public StockDto findTopByIdSymbolOrderByClose(@RequestParam String symbol) {
	return service.findTopByIdSymbolOrderByClose(symbol);
    }

    @GetMapping("stock/max")
    public StockDto findTopByIdSymbolOrderByCloseDesc(@RequestParam String symbol) {
	return service.findTopByIdSymbolOrderByCloseDesc(symbol);
    }

    @GetMapping("stock/profit")
    public Iterable<StockProfitDto> getMinAndMaxYearProfit(@RequestParam String symbol, @RequestParam String fromDate,
	    @RequestParam String toDate, @RequestParam Integer periodInYears) {
	return service.getMinAndMaxYearProfit(symbol, fromDate, toDate, periodInYears);
    }

    @GetMapping("stock/avg/profit")
    StockAverageProfitDto getAverageProfit(@RequestParam String symbol, @RequestParam String fromDate,
	    @RequestParam String toDate, @RequestParam Integer periodInYears) {
	return service.getAverageProfit(symbol, fromDate, toDate, periodInYears);
    }

    @GetMapping("stock/correlation")
    String correlation(@RequestParam String fromDate, @RequestParam String toDate, @RequestParam String firstSymbol,
	    @RequestParam String secondSymbol) {
	return service.correlation(fromDate, toDate, firstSymbol, secondSymbol);
    }
    
    @GetMapping("stock/symbols")
    List<String> findAllSymbolNames() {
	return service.findAllSymbolNames();
    }

}
