package telran.java2022.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.Setter;


// LabelDateDto аналог entity LabelDate
@Data
@Setter
public class LabelDateDto {
    String symbol;
    LocalDate date;
}
