package mg.erp.entities.rh;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalarySummary {
    private String idEmployee;
    private String nomEmployee;
    private String month;
    private Map<String, Double> componentTotals = new HashMap<>();
    private double totalPayNet;
    private double totalPayBrut;

    public String getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(String idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getNomEmployee() {
        return nomEmployee;
    }

    public void setNomEmployee(String nomEmployee) {
        this.nomEmployee = nomEmployee;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Map<String, Double> getComponentTotals() {
        return componentTotals;
    }

    public void setComponentTotals(Map<String, Double> componentTotals) {
        this.componentTotals = componentTotals;
    }

    public double getTotalPayNet() {
        return totalPayNet;
    }

    public void setTotalPayNet(double totalPayNet) {
        this.totalPayNet = totalPayNet;
    }

    public double getTotalPayBrut() {
        return totalPayBrut;
    }

    public void setTotalPayBrut(double totalPayBrut) {
        this.totalPayBrut = totalPayBrut;
    }

    public Map<String, List<SalarySummary>> regrouperFichesParMoisEtParEmploye(List<FichePaye> slips) {
        return slips.stream()
                .collect(Collectors.groupingBy(
                        slip -> YearMonth.from(LocalDate.parse(slip.getEnd_date())).toString(), // clé : yyyy-MM
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(FichePaye::getEmployee),
                                mapParEmploye -> mapParEmploye.entrySet().stream()
                                        .map(entry -> {
                                            String idEmp = entry.getKey();
                                            List<FichePaye> ficheList = entry.getValue();
                                            FichePaye premiere = ficheList.get(0);

                                            SalarySummary summary = new SalarySummary();
                                            summary.setIdEmployee(idEmp);
                                            summary.setNomEmployee(premiere.getEmployee_name());
                                            summary.setMonth(YearMonth.from(LocalDate.parse(premiere.getEnd_date())).toString());

                                            Map<String, Double> totals = new HashMap<>();
                                            double totalNet = 0;
                                            double totalBrut = 0;

                                            for (FichePaye fiche : ficheList) {
                                                String comp = fiche.getSalary_structure();
                                                double net = fiche.getNet_pay();
                                                double brut = fiche.getGross_pay();

                                                totals.put(comp, totals.getOrDefault(comp, 0.0) + net);
                                                totalNet += net;
                                                totalBrut += brut;
                                            }

                                            summary.setComponentTotals(totals);
                                            summary.setTotalPayNet(totalNet);
                                            summary.setTotalPayBrut(totalBrut);

                                            return summary;
                                        })
                                        .collect(Collectors.toList())
                        )
                ));
    }


}
