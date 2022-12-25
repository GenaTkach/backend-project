package telran.java2022.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockDto;
import telran.java2022.service.StockService;

@RestController
@RequiredArgsConstructor
public class StockController {

    final StockService service;

    @GetMapping("/stock/{symbol}/date")
    public StockDto findStockByDate(@PathVariable String symbol, @RequestBody DateDto date) {
	return service.findStockByDate(symbol, date);
    }

    @GetMapping("/stock/{symbol}/stocks")
    public Iterable<StockDto> findStocksByPeriod(@PathVariable String symbol,
	    @RequestBody DatePeriodDto datePeriodDto) {
	return service.findStocksByPeriod(symbol, datePeriodDto);
    }

    @GetMapping("stock/download/{label}")
    public Integer downloadDataForStockByPeriod(@PathVariable String label, @RequestBody DatePeriodDto datePeriodDto) {
	return service.downloadDataForStockByPeriod(label, datePeriodDto);
    }
}