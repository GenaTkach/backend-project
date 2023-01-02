package telran.java2022.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telran.java2022.modelWithMap.DayClose;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockDtoMap {

    String symbol;
    List<DayClose> data;
}
