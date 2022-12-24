package telran.java2022.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "date")
@Document("stocks")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class Stock implements Serializable {
    private static final long serialVersionUID = 998443373406764689L;
    @Id
    String date;
    String close;
    
    // ID
    // LabelDate id;
}
