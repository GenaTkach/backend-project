package telran.java2022.service;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;

import lombok.RequiredArgsConstructor;
import telran.java2022.dao.Repository;
import telran.java2022.model.Stock;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service implements CommandLineRunner {

    final Repository repository;

    // Подготовка CSVParser
    private File file = new File("/Users/Gena/Desktop/Desc/Coding/TELRAN PROJECT/S&P 500 Historical Data.csv");
    CSVParser parser;
    Charset charset = Charset.defaultCharset();
    CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    @Override
    public void run(String... args) throws Exception {
	parser = CSVParser.parse(file, charset, format);
	for (CSVRecord record : parser) {
	    System.out.println(record);
	    record.stream()
		    .forEach(s -> System.out.println(s));
	    
	    Stock stock = new Stock();
	    stock.setDate(record.get(0));
	    stock.setPrice(record.get(1));
	    stock.setOpen(record.get(2));
	    stock.setHigh(record.get(3));
	    stock.setLow(record.get(4));
	    stock.setChange(record.get(6));
	    if (!repository.existsById(stock.getDate())) {
		repository.save(stock);
	    }
	}
	parser.close();
    }
}
