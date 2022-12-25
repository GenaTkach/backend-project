package telran.java2022.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// StockDto предназначен для вывода дневных данных для конкретной акции
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {
    LabelDateDto id;
    String close;
}