package telran.java2022.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.opencsv.bean.CsvCustomBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import telran.java2022.configuration.LocalDateConverter;

// Класс для составного ключа
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LabelDate implements Serializable {
    private static final long serialVersionUID = -8248238238853250121L;
    public String symbol;
//    @CsvBindByName(column = "Date")
    @CsvCustomBindByName(converter = LocalDateConverter.class)
    public LocalDate date;
    
    
    // Метод для реализации метода getYearProfit в ServiceImpl
    public void plusOneDay() {
	date = date.plusDays(1);
    }
}
