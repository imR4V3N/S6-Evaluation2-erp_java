package mg.erp.utils.data.csv;

import mg.erp.entities.rh.Employee;
import mg.erp.entities.rh.SalaryStructureAssignment;
import mg.erp.utils.data.csv.entity.EmployeUtils;
import mg.erp.utils.data.csv.entity.FicheUtils;
import mg.erp.utils.data.csv.entity.SalaryUtils;
import mg.erp.utils.data.csv.utils.File;
import mg.erp.utils.toolkit.io.CsvFile;
import mg.erp.utils.toolkit.io.Utils;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Import {
    List<File> files = new ArrayList<File>();
    public List<File> getFiles() {
        return files;
    }
    public void setFiles(List<File> files) {
        this.files = files;
    }
    public void addFile(File file) {
        this.files.add(file);
    }

    public void importCsv(String separator, String sid, String baseUrl, ResponseEntity<String> response) throws Exception {
        CsvFile utils = new CsvFile();
        EmployeUtils empUtils = new EmployeUtils();
        FicheUtils ficheUtils = new FicheUtils();
        SalaryUtils salaryUtils = new SalaryUtils();
        List<Employee> employees = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            try {
                processFile(file, i, separator, utils, empUtils, ficheUtils, salaryUtils, employees);
            } catch (Exception e) {
                throw new Exception("Erreur lors de l'importation du fichier \"" + file.getName() + "\" " +  e.getMessage());
            }
        }
        try {
//            empUtils.saveEmployee(sid, baseUrl);
            salaryUtils.saveComponent(sid, baseUrl);
            salaryUtils.saveStructure(sid, baseUrl);
            ficheUtils.saveAssignement(sid, baseUrl, response);
            ficheUtils.saveSlip(sid, baseUrl);
        } catch (Exception e) {
            throw new Exception("Erreur lors de l'insertion des donnees -> " + e.getMessage());
        }
    }

    private void processFile(File file, int index, String separator, CsvFile utils,
                             EmployeUtils empUtils, FicheUtils ficheUtils, SalaryUtils salaryUtils,
                             List<Employee> employees) throws Exception {

        List<HashMap<String, Object>> data = utils.importCsv(file.getPath(), separator, file.getTypes());

        if (index == 0) {
            employees = empUtils.read(data);
        } else if (index == 1) {
            salaryUtils.read(data);
        } else if (index == 2) {
            List<SalaryStructureAssignment> ass = ficheUtils.readAssignement(data, employees);
            ficheUtils.readSlip(ass);
        }
    }
}
