package mg.erp.entities.rh;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mg.erp.utils.Date;

public class Employee {
    String name;
    String employee_name;
    String first_name;
    String middle_name;
    String last_name;
    String gender;
    String date_of_birth;
    String date_of_joining;
    String status;
    String employment_type;
    String department;
    String designation;
    String employee_number;
    String grade;
    String branch;
    String job_applicant;
    String company;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getDate_of_joining() {
        return date_of_joining;
    }

    public void setDate_of_joining(String date_of_joining) {
        this.date_of_joining = date_of_joining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployment_type() {
        return employment_type;
    }

    public void setEmployment_type(String employment_type) {
        this.employment_type = employment_type;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmployee_number() {
        return employee_number;
    }

    public void setEmployee_number(String employee_number) {
        this.employee_number = employee_number;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getJob_applicant() {
        return job_applicant;
    }

    public void setJob_applicant(String job_applicant) {
        this.job_applicant = job_applicant;
    }

    public String getCompany() {return company;}

    public void setCompany(String company) {this.company = company;}

    public List<Employee> getEmployees(String response) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(response).path("data");

        return mapToEmployee(data);
    }

    private List<Employee> mapToEmployee(JsonNode data) throws Exception {
        List<Employee> employees = new ArrayList<>();

        if (data.isArray()) {
            for (JsonNode node : data) {
                Employee employee = new Employee();
                employee.setName(node.path("name").asText());
                employee.setEmployee_name(node.path("employee_name").asText());
                employee.setFirst_name(node.path("first_name").asText());
                employee.setMiddle_name(node.path("middle_name").asText());
                employee.setLast_name(node.path("last_name").asText());
                employee.setGender(node.path("gender").asText());
                employee.setDate_of_birth(node.path("date_of_birth").asText());
                employee.setDate_of_joining(node.path("date_of_joining").asText());
                employee.setStatus(node.path("status").asText());
                employee.setEmployment_type(node.path("employment_type").asText());
                employee.setDepartment(node.path("department").asText());
                employee.setDesignation(node.path("designation").asText());
                employee.setEmployee_number(node.path("employee_number").asText());
                employee.setGrade(node.path("grade").asText());
                employee.setBranch(node.path("branch").asText());
                employee.setJob_applicant(node.path("job_applicant").asText());
                employee.setCompany(node.path("company").asText());

                employees.add(employee);
            }
        }

        return employees;
    }


    public List<Employee> filtre(List<Employee> employees, String ageMinStr, String ageMaxStr,
                                 String nom, String genre, String poste) throws Exception {
        Integer ageMin = (ageMinStr != null && !ageMinStr.isEmpty()) ? Integer.parseInt(ageMinStr) : null;
        Integer ageMax = (ageMaxStr != null && !ageMaxStr.isEmpty()) ? Integer.parseInt(ageMaxStr) : null;

        List<Employee> filtres = employees.stream().filter(e -> {
            boolean match = true;

            if (nom != null && !nom.isEmpty()) {
                match &= e.getEmployee_name() != null && e.getEmployee_name().toLowerCase().contains(nom.toLowerCase());
            }

            if (genre != null && !genre.isEmpty()) {
                match &= genre.equalsIgnoreCase(e.getGender());
            }

            if (poste != null && !poste.isEmpty()) {
                match &= poste.equalsIgnoreCase(e.getDesignation());
            }

            if ((ageMin != null || ageMax != null) && e.getDate_of_birth() != null && !e.getDate_of_birth().isEmpty()) {
                int age = Date.calculerAge(e.getDate_of_birth());
                if (ageMin != null) match &= age >= ageMin;
                if (ageMax != null) match &= age <= ageMax;
            }

            return match;
        }).collect(Collectors.toList());

        return filtres;
    }
}
