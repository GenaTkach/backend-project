package telran.java2022.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8028703475221309569L;
    
    public StockNotFoundException(String symbol, String date) {
	super(symbol + " stock for date - " + date + " doesn't exists");
    }
}
