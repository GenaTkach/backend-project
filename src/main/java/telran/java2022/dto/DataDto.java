package telran.java2022.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Этот DTO представляет данные, получаемые в виде листа для одной акции
// Объект класс DataDto - это репрезентация акции за конкретный день
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    String close;
    String date;
    String symbol;
}
