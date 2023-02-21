package telran.java2022.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockProfit {
    String symbol;
    LocalDate dateFrom;
    LocalDate dateTo;
    Double closeStart;
    Double closeEnd;

    public Double getProfit(int years) {
	return (Math.pow(closeEnd / closeStart, 1.0 / years) - 1) * 100;
    }
    
    public Double getIncome() {
	return closeEnd - closeStart;
    }
}
