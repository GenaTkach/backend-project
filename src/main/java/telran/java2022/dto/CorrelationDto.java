package telran.java2022.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CorrelationDto {
    String firstSymbol;
    String secondSymbol;
    Double correlationRatio;
    String interpretation;

}
