package mg.erp.utils.data.csv.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateUtils {
    public static boolean estDateValide(String dateStr, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String formatterDate(String dateStr, String formatEntree) throws Exception {
//        if (!estDateValide(dateStr, formatEntree)) {
//            throw new Exception("Le format de la date est ivalide!");
//        }
        DateTimeFormatter formatterEntree = DateTimeFormatter.ofPattern(formatEntree);
        LocalDate date = LocalDate.parse(dateStr, formatterEntree);
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE); // Format yyyy-MM-dd
    }
}
