package mg.erp.entities.rh;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import mg.erp.utils.Config;
import mg.erp.utils.Date;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalarySummary {
    private String idEmployee;
    private String nomEmployee;
    private String month;
    private double totalPayNet;
    private double totalPayBrut;
    private double totalPayDeduction;
    private Map<String, Double> componentTotals = new HashMap<>();
    private Map<String, Double> componentEarnings;
    private Map<String, Double> componentDeductions;

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

    public double getTotalPayDeduction() {
        return totalPayDeduction;
    }

    public void setTotalPayDeduction(double totalPayDeduction) {
        this.totalPayDeduction = totalPayDeduction;
    }

    public Map<String, Double> getComponentEarnings() {
        return componentEarnings;
    }

    public void setComponentEarnings(Map<String, Double> componentEarnings) {
        this.componentEarnings = componentEarnings;
    }

    public Map<String, Double> getComponentDeductions() {
        return componentDeductions;
    }

    public void setComponentDeductions(Map<String, Double> componentDeductions) {
        this.componentDeductions = componentDeductions;
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
                                            double totalDeduit = 0;

                                            Map<String, Double> componentEarnings = new HashMap<>();
                                            Map<String, Double> componentDeductions = new HashMap<>();

                                            for (FichePaye fiche : ficheList) {
                                                String comp = fiche.getSalary_structure().getName();
                                                double net = fiche.getNet_pay();
                                                double brut = fiche.getGross_pay();
                                                double deduit = fiche.getTotal_deduction();

                                                totals.put(comp, totals.getOrDefault(comp, 0.0) + net);
                                                totalNet += net;
                                                totalBrut += brut;
                                                totalDeduit += deduit;

                                                // Earnings
                                                fiche.getSalary_structure().getEarings().forEach(compo -> {
                                                    String label = compo.getSalary_component();
                                                    double amount = compo.getAmount();
                                                    componentEarnings.put(label,
                                                            componentEarnings.getOrDefault(label, 0.0) + amount);
                                                });

                                                // Deductions
                                                fiche.getSalary_structure().getDeductions().forEach(compo -> {
                                                    String label = compo.getSalary_component();
                                                    double amount = compo.getAmount();
                                                    componentDeductions.put(label,
                                                            componentDeductions.getOrDefault(label, 0.0) + amount);
                                                });
                                            }

                                            summary.setComponentTotals(totals);
                                            summary.setTotalPayNet(totalNet);
                                            summary.setTotalPayBrut(totalBrut);
                                            summary.setTotalPayDeduction(totalDeduit);
                                            summary.setComponentEarnings(componentEarnings);
                                            summary.setComponentDeductions(componentDeductions);

                                            return summary;
                                        })
                                        .collect(Collectors.toList())
                        )
                ));
    }

    public Map<YearMonth, SalarySummary> regrouperFichesParMoisAnnee(List<FichePaye> slips, int anneeFiltre) {
        return slips.stream()
                .filter(slip -> {
                    LocalDate endDate = LocalDate.parse(slip.getEnd_date());
                    return endDate.getYear() == anneeFiltre;
                })
                .collect(Collectors.groupingBy(
                        slip -> YearMonth.from(LocalDate.parse(slip.getEnd_date())),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                ficheList -> {
                                    SalarySummary summary = new SalarySummary();
                                    YearMonth mois = YearMonth.from(LocalDate.parse(ficheList.get(0).getEnd_date()));
                                    summary.setMonth(mois.toString());

                                    Map<String, Double> totals = new HashMap<>();
                                    double totalNet = 0;
                                    double totalBrut = 0;
                                    double totalDeduit = 0;

                                    Map<String, Double> componentEarnings = new HashMap<>();
                                    Map<String, Double> componentDeductions = new HashMap<>();

                                    for (FichePaye fiche : ficheList) {
                                        String comp = fiche.getSalary_structure().getName();
                                        totalNet += fiche.getNet_pay();
                                        totalBrut += fiche.getGross_pay();
                                        totalDeduit += fiche.getTotal_deduction();
                                        totals.put(comp, totals.getOrDefault(comp, 0.0) + fiche.getNet_pay());

                                        // Earnings
                                        fiche.getSalary_structure().getEarings().forEach(compo -> {
                                            String label = compo.getSalary_component();
                                            double amount = compo.getAmount();
                                            componentEarnings.put(label,
                                                    componentEarnings.getOrDefault(label, 0.0) + amount);
                                        });

                                        // Deductions
                                        fiche.getSalary_structure().getDeductions().forEach(compo -> {
                                            String label = compo.getSalary_component();
                                            double amount = compo.getAmount();
                                            componentDeductions.put(label,
                                                    componentDeductions.getOrDefault(label, 0.0) + amount);
                                        });
                                    }

                                    summary.setComponentTotals(totals);
                                    summary.setTotalPayNet(totalNet);
                                    summary.setTotalPayBrut(totalBrut);
                                    summary.setTotalPayDeduction(totalDeduit);
                                    summary.setComponentEarnings(componentEarnings);
                                    summary.setComponentDeductions(componentDeductions);

                                    return summary;
                                }
                        )
                ));
    }


//    Statistiques
    public List<FichePaye> fetchFichePayes(String url, HttpEntity<String> entity, FichePaye fichePaye, ConfigurableEnvironment configurableEnvironment) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return fichePaye.getFiches(response.getBody(), new Config().getErpUrl(configurableEnvironment), entity);
    }

    public void initializeStatistics(HttpServletRequest request, List<SalarySummary> summaries, String annee, Gson gson) {
        double[] brut = new double[12];
        double[] net = new double[12];
        double[] deduction = new double[12];
        double totalPayBrut = 0.0;
        double totalPayNet = 0.0;
        double totalPayDeduction = 0.0;

        Map<String, double[]> earningsMap = new LinkedHashMap<>();
        Map<String, double[]> deductionMap = new LinkedHashMap<>();

        for (SalarySummary summary : summaries) {
            int monthIndex = getMonthIndex(summary);

            brut[monthIndex] += summary.getTotalPayBrut();
            net[monthIndex] += summary.getTotalPayNet();
            deduction[monthIndex] += summary.getTotalPayDeduction();

            populateComponentMap(summary.getComponentEarnings(), earningsMap, monthIndex);
            populateComponentMap(summary.getComponentDeductions(), deductionMap, monthIndex);

//            Total des paiements
            totalPayBrut += summary.getTotalPayBrut();
            totalPayNet += summary.getTotalPayNet();
            totalPayDeduction += summary.getTotalPayDeduction();
        }

        request.setAttribute("dates", gson.toJson(Date.dates(annee)));
        request.setAttribute("brut", gson.toJson(brut));
        request.setAttribute("net", gson.toJson(net));
        request.setAttribute("deduction", gson.toJson(deduction));
        request.setAttribute("earningsMap", earningsMap);
        request.setAttribute("deductionMap", deductionMap);

        request.setAttribute("totalPayBrut", totalPayBrut);
        request.setAttribute("totalPayNet", totalPayNet);
        request.setAttribute("totalPayDeduction", totalPayDeduction);
    }

    private int getMonthIndex(SalarySummary summary) {
        return Integer.parseInt(summary.getMonth().substring(summary.getMonth().length() - 2)) - 1;
    }

    private void populateComponentMap(Map<String, Double> components, Map<String, double[]> componentMap, int monthIndex) {
        if (components != null) {
            for (Map.Entry<String, Double> entry : components.entrySet()) {
                componentMap.putIfAbsent(entry.getKey(), new double[12]);
                componentMap.get(entry.getKey())[monthIndex] += entry.getValue();
            }
        }
    }
}
