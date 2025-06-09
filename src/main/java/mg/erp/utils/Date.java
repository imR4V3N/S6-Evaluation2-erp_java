package mg.erp.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Date {
    public static int calculerAge(String dateNaissanceStr) {
        if (dateNaissanceStr == null || dateNaissanceStr.isEmpty()) return -1;
        LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr);
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public static List<String> dates(String annee) {
        List<String> dates = new ArrayList<>();
        dates.add("Janvier " + annee);
        dates.add("Février " + annee);
        dates.add("Mars " + annee);
        dates.add("Avril " + annee);
        dates.add("Mai " + annee);
        dates.add("Juin " + annee);
        dates.add("Juillet " + annee);
        dates.add("Août " + annee);
        dates.add("Septembre " + annee);
        dates.add("Octobre " + annee);
        dates.add("Novembre " + annee);
        dates.add("Décembre " + annee);
        return dates;
    }
}
