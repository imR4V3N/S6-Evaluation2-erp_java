package mg.erp.entities.rh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FichePaye {
    String name;
    String employee;
    String employee_name;
    String start_date;
    String end_date;
    String salary_structure;
    String company;
    String designation;
    String departement;
    double net_pay;
    double gross_pay;
    YearMonth yearMonth;
    String payroll_frequency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getSalary_structure() {
        return salary_structure;
    }

    public void setSalary_structure(String salary_structure) {
        this.salary_structure = salary_structure;
    }

    public double getNet_pay() {
        return net_pay;
    }

    public void setNet_pay(double net_pay) {
        this.net_pay = net_pay;
    }

    public YearMonth getYearMonth() {return yearMonth;}

    public void setYearMonth(YearMonth yearMonth) {this.yearMonth = yearMonth;}

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public double getGross_pay() {
        return gross_pay;
    }

    public void setGross_pay(double gross_pay) {
        this.gross_pay = gross_pay;
    }

    public String getPayroll_frequency() {
        return payroll_frequency;
    }

    public void setPayroll_frequency(String payroll_frequency) {
        this.payroll_frequency = payroll_frequency;
    }

    public List<FichePaye> getFiches(String response) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response).path("data");

        return mapToFiche(data);
    }

    private List<FichePaye> mapToFiche(JsonNode data) throws Exception {
        List<FichePaye> fichePayes = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                FichePaye fichePaye = new FichePaye();

                fichePaye.setName(node.get("name").asText());
                fichePaye.setEmployee(node.get("employee").asText());
                fichePaye.setEmployee_name(node.get("employee_name").asText());
                fichePaye.setDepartement(node.get("department").asText());
                fichePaye.setDesignation(node.get("designation").asText());
                fichePaye.setCompany(node.get("company").asText());
                fichePaye.setPayroll_frequency(node.get("payroll_frequency").asText());
                fichePaye.setStart_date(node.get("start_date").asText());
                fichePaye.setEnd_date(node.get("end_date").asText());
                fichePaye.setSalary_structure(node.get("salary_structure").asText());
                fichePaye.setNet_pay(node.get("net_pay").asDouble());
                fichePaye.setNet_pay(node.get("gross_pay").asDouble());

                fichePayes.add(fichePaye);
            }
        }

        return fichePayes;
    }

    public List<FichePaye> regrouperFichesParMois(List<FichePaye> slips) {
        return slips.stream()
                .collect(Collectors.groupingBy(
                        slip -> {
                            LocalDate endDate = LocalDate.parse(slip.getEnd_date());
                            return YearMonth.of(endDate.getYear(), endDate.getMonth());
                        }
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    YearMonth mois = entry.getKey();
                    List<FichePaye> fichesDuMois = entry.getValue();

                    FichePaye ficheMensuelle = new FichePaye();
                    ficheMensuelle.setYearMonth(mois);

                    // On suppose que l'employé est le même pour toutes les fiches du mois
                    ficheMensuelle.setEmployee(fichesDuMois.get(0).getEmployee());
                    ficheMensuelle.setEmployee_name(fichesDuMois.get(0).getEmployee_name());
                    ficheMensuelle.setDepartement(fichesDuMois.get(0).getDepartement());
                    ficheMensuelle.setCompany(fichesDuMois.get(0).getCompany());
                    ficheMensuelle.setDesignation(fichesDuMois.get(0).getDesignation());
                    ficheMensuelle.setSalary_structure(fichesDuMois.get(0).getSalary_structure());

                    ficheMensuelle.setNet_pay(
                            fichesDuMois.stream()
                                    .mapToDouble(FichePaye::getNet_pay)
                                    .sum()
                    );
                    ficheMensuelle.setGross_pay(
                            fichesDuMois.stream()
                                    .mapToDouble(FichePaye::getGross_pay)
                                    .sum()
                    );

                    return ficheMensuelle;
                })
                .sorted(Comparator.comparing(FichePaye::getYearMonth))
                .collect(Collectors.toList());
    }

    public void genererPdfFichePaie(FichePaye slip, HttpServletResponse response) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        String fileName = "Fiche_Paie_" + slip.getEmployee() + ".pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

        // Titre centré
        Paragraph titre = new Paragraph("FICHE DE PAIE", titleFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20f);
        document.add(titre);

        // Ligne séparation
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.GRAY);
        document.add(new Chunk(ls));

        // Tableau pour les infos employé et entreprise
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(20f);
        infoTable.setSpacingAfter(20f);
        infoTable.setWidths(new float[]{1f, 2f});

        // Ajouter les lignes
        infoTable.addCell(createLabelCell("Employé :", labelFont));
        infoTable.addCell(createValueCell(slip.getEmployee() + " - " + slip.getEmployee_name(), valueFont));

        infoTable.addCell(createLabelCell("Entreprise :", labelFont));
        infoTable.addCell(createValueCell(slip.getCompany(), valueFont));

        infoTable.addCell(createLabelCell("Département :", labelFont));
        infoTable.addCell(createValueCell(slip.getDepartement(), valueFont));

        infoTable.addCell(createLabelCell("Poste :", labelFont));
        infoTable.addCell(createValueCell(slip.getDesignation(), valueFont));

        infoTable.addCell(createLabelCell("Période :", labelFont));
        infoTable.addCell(createValueCell(slip.getYearMonth().toString(), valueFont));

        infoTable.addCell(createLabelCell("Structure salariale :", labelFont));
        infoTable.addCell(createValueCell(slip.getSalary_structure(), valueFont));

        document.add(infoTable);

        // Tableau salaire
        PdfPTable salaireTable = new PdfPTable(2);
        salaireTable.setWidthPercentage(50);
        salaireTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        salaireTable.setSpacingBefore(10f);
        salaireTable.setWidths(new float[]{2f, 1f});

        // En-tête du tableau salaire
        PdfPCell cell1 = new PdfPCell(new Phrase("Description", headerFont));
        cell1.setBackgroundColor(new BaseColor(0, 70, 140)); // Bleu foncé
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setPadding(8);
        salaireTable.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Montant ($)", headerFont));
        cell2.setBackgroundColor(new BaseColor(0, 70, 140));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setPadding(8);
        salaireTable.addCell(cell2);

        // Données salaire
        PdfPCell descCell = new PdfPCell(new Phrase("Salaire Net", valueFont));
        descCell.setPadding(8);
        salaireTable.addCell(descCell);

        PdfPCell montantCell = new PdfPCell(new Phrase(String.format("%.2f", slip.getNet_pay()), valueFont));
        montantCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        montantCell.setPadding(8);
        salaireTable.addCell(montantCell);

        // Données salaire
        PdfPCell descCell2 = new PdfPCell(new Phrase("Salaire Brut", valueFont));
        descCell2.setPadding(8);
        salaireTable.addCell(descCell2);

        PdfPCell montantCell2 = new PdfPCell(new Phrase(String.format("%.2f", slip.getGross_pay()), valueFont));
        montantCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        montantCell2.setPadding(8);
        salaireTable.addCell(montantCell2);

        document.add(salaireTable);

        // Espace avant footer
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Pied de page
        Paragraph footer = new Paragraph("Voici le fiche de paied'un employee, une preuve officielle de rémunération.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        // Envoi dans la réponse HTTP
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType("application/pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
    // Méthode pour créer une cellule label
    private PdfPCell createLabelCell(String text, Font labelFont) {
        PdfPCell cell = new PdfPCell(new Phrase(text, labelFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
    // Méthode pour créer une cellule valeur
    private PdfPCell createValueCell(String text, Font valueFont) {
        PdfPCell cell = new PdfPCell(new Phrase(text, valueFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
}
