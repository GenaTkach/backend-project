package telran.java2022.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// StockDto предназначен для вывода дневных данных для конкретной акции
@Getter
@Setter
@NoArgsConstructor
public class StockDto {
    LabelDateDto id;
    Double close;
}
