package mg.erp;

import mg.erp.models.rh.TabEmployee;
import mg.erp.utils.Connexion;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        Connection conn = Connexion.getConnectionTest();
        TabEmployee[] employees = new TabEmployee().getAll(conn);
        for (TabEmployee employee : employees) {
            System.out.println("Name: " + employee.getName() + ", Employee Name: " + employee.getEmployeeName());
        }
    }
}
