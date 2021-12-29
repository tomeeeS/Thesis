package tmit.bme.telkicar.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ServiceHelper {

	private String frontEndDateFormat = "MM/dd/yyyy";
	private String dataBaseDateFormat = "yyyy-MM-dd";

	public DateTimeFormatter getFrontEndDateTimeFormatter() {
		return  DateTimeFormatter.ofPattern(frontEndDateFormat);
	}

	public LocalDate getDateFromFrontEndPattern(String dateString) {
		try	{
			if (dateString != null && !dateString.isEmpty()){
				return LocalDate.parse(dateString, getFrontEndDateTimeFormatter());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public DateTimeFormatter getDataBaseDateTimeFormatter() {
		return  DateTimeFormatter.ofPattern(dataBaseDateFormat);
	}

}
