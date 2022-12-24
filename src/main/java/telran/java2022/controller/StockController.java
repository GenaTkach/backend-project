package telran.java2022.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java2022.dto.DateDto;
import telran.java2022.dto.DatePeriodDto;
import telran.java2022.dto.StockDto;
import telran.java2022.service.StockService;

@RestController
@RequiredArgsConstructor
public class StockController{
    
    final StockService service;
    
    @GetMapping("/stock/date")
    public StockDto findStockByDate(@RequestBody DateDto date) {
	return service.findStockByDate(date);
    }
    
    @GetMapping("/stock/stocks")
    public Iterable<StockDto> findStocksByPeriod(@RequestBody DatePeriodDto datePeriodDto) {
	return service.findStocksByPeriod(datePeriodDto);
    }
}
