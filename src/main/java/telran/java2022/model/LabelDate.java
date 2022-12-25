package telran.java2022.model;

import java.io.Serializable;

import lombok.Data;

// Класс для составного ключа
@Data
public class LabelDate implements Serializable {
    private static final long serialVersionUID = -8248238238853250121L;
    final String symbol;
    final String date;
}
