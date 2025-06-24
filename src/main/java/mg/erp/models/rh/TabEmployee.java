package mg.erp.models.rh;

import mg.erp.entities.rh.Employee;
import mg.framework.dao.annotation.Column;
import mg.framework.dao.annotation.Table;
import mg.framework.dao.utils.Dao;

import java.sql.Connection;

@Table(name = "tabEmployee")
public class TabEmployee extends Dao {
    @Column(name = "name", isPK = true)
    String name;
    @Column(name = "employee_name")
    String employeeName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public TabEmployee[] getAll(Connection connection) {
        return this.read("ORDER BY name ASC", connection).toArray(TabEmployee[]::new);
    }
}
