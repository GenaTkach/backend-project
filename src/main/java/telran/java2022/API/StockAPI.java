package telran.java2022.API;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import telran.java2022.dto.StockDtoAPI;

// Это класс для получения ResponseEntity<StockDtoAPI> с листом данных по какой-то акции
@Component
public class StockAPI {
    static RestTemplate restTemplate = new RestTemplate();
    static final String API_ACCESS_KEY = "3191c1d38175d8eb09017743b5f720e7";
    static String baseUrl = "http://api.marketstack.com/v1/eod";

    public static ResponseEntity<StockDtoAPI> request(String label, String dateFrom, String dateTo) {
	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
		.queryParam("access_key", API_ACCESS_KEY)
		.queryParam("symbols", label)
		.queryParam("date_from", dateFrom)
		.queryParam("date_to", dateTo);

	RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, builder.build()
		.toUri());
	ResponseEntity<StockDtoAPI> responseEntity = restTemplate.exchange(requestEntity, StockDtoAPI.class);
	return responseEntity;
    }
}
