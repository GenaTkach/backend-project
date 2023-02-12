package telran.java2022.schedulerService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.StockRepository;
import telran.java2022.model.Stock;


@RequiredArgsConstructor
@Service
public class SchedulerService {
	final StockRepository repository;
	final ModelMapper modelMapper;

	// @Scheduled(cron = "0/5 * * * * *")
	// 3am
	@Scheduled(cron = "0 0 3 * * *")
	public void doWork() throws InterruptedException {
		List<String> stocks = repository.findAllBy();
		if (stocks.size() > 0) {
			stocks.forEach(s -> dounoutData(s));
		}
	}

	public void dounoutData(String symbol) {
		LocalDate from = repository.findTopByIdSymbolOrderByIdDateDesc(symbol).getId().getDate().plusDays(1);
		Long dateTimeStampTo = System.currentTimeMillis() / 1000;

		Calendar calendar = new GregorianCalendar(from.getYear(), from.getMonthValue() - 1, from.getDayOfMonth());
		Long dateTimeStampFrom = calendar.getTimeInMillis() / 1000;

		downloadCSVandParseToDB(symbol, dateTimeStampFrom, dateTimeStampTo);
	}

	public Boolean downloadCSVandParseToDB(String symbol, long period1, long period2) {
		final String BASE_URL = "https://query1.finance.yahoo.com/v7/finance/download/" + symbol;
		File file = new File("/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/SCV" + symbol);
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).queryParam("period1", period1)
				.queryParam("period2", period2).build();
		try {
			FileUtils.copyURLToFile(new URL(builder.toString()), file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		List<Stock> beans = new ArrayList<>();
		try {
			beans = new CsvToBeanBuilder<Stock>(new FileReader(file)).withType(Stock.class).build().parse();
		} catch (IllegalStateException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		beans.forEach(b -> System.out.println(b));
		beans.forEach(b -> b.getId().setSymbol(symbol));
		if (!(repository.existsById(beans.get(0).getId())
				&& repository.existsById(beans.get(beans.size() - 1).getId()))) {
			repository.saveAll(beans);
		}
		return true;
	}
}
