package mg.erp.utils;

import java.time.LocalDate;
import java.time.Period;

public class Date {
    public static int calculerAge(String dateNaissanceStr) {
        if (dateNaissanceStr == null || dateNaissanceStr.isEmpty()) return -1;
        LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr);
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}
