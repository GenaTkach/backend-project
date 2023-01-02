package telran.java2022.modelWithMap;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * @author Gena
 * Это entity если будем использовать Map
 */
@Getter
@Document("stocksAPI-NoMAP")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class StockMap implements Serializable {
    private static final long serialVersionUID = -6553258061224677715L;
    
    @Id
    String symbol;
    List<DayClose> data;
}
