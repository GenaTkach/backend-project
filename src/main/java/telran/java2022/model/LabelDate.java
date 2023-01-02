package telran.java2022.model;

import java.io.Serializable;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Класс для составного ключа
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelDate implements Serializable {
    private static final long serialVersionUID = -8248238238853250121L;
    public String symbol;
    @CsvBindByName(column = "Date")
    public String date;
}
