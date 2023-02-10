package telran.java2022.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Document("stocks")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class Stock implements Serializable {
    private static final long serialVersionUID = 998443373406764689L;
    @Id
    @CsvRecurse
    LabelDate id;
    @CsvBindByName(column = "close")
    Double close;
}
