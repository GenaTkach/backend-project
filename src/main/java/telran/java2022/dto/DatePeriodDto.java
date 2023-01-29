package telran.java2022.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatePeriodDto {
    LocalDate dateFrom;
    LocalDate dateTo;
}
