package telran.java2022;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import telran.java2022.dao.StockRepository;
import telran.java2022.dto.LabelDateDto;
import telran.java2022.dto.StockDto;
import telran.java2022.model.LabelDate;
import telran.java2022.model.Stock;
import telran.java2022.service.StockServiceImpl;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FinancialDataApplicationTests {

    @Mock
    private StockRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl service;

    @Test
    public void testFindTopByIdSymbolOrderByClose() {
	String symbol = "AAPL";
	Stock stock = new Stock();
	LocalDate date = LocalDate.of(2020, 01, 01);
	LabelDate ld = new LabelDate(symbol, date);
	stock.setId(ld);
	stock.setClose(100.0);

	StockDto expectedResult = new StockDto();
	LabelDateDto lddto = new LabelDateDto();
	lddto.setSymbol(symbol);
	lddto.setDate(date);
	expectedResult.setId(lddto);
	expectedResult.setClose(100.0);

	when(repository.findTopByIdSymbolOrderByClose(eq(symbol))).thenReturn(stock);
	when(modelMapper.map(eq(stock), eq(StockDto.class))).thenReturn(expectedResult);

	StockDto result = service.findTopByIdSymbolOrderByClose(symbol);

	assertEquals(expectedResult, result);
    }

}
