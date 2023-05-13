package telran.java2022.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CorrelationDto {
    List<String> stockNames;
    Double correlationRatio;
    String interpretation;
    List<Double[]> stockDayPrices;
    List<String> stockDays;
    
}
