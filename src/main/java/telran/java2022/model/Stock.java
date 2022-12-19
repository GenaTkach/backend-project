package telran.java2022.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = "date")
@Document("stocks")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Stock {
    @Id
    String date;
    String price;
    String open;
    String high;
    String low;
    String vol;
    String change;
}
