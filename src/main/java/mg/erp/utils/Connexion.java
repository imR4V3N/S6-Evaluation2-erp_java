package mg.erp.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class Connexion {

    public static Connection getConnection(ConfigurableEnvironment configurableEnvironment) throws Exception {
        Map<String,String> config = Config.databaseConfig(configurableEnvironment);
        Connection c = null;
        try {
            Class.forName(config.get("driver"));
            c = DriverManager.getConnection(config.get("url"),config.get("user"), config.get("password"));
            System.out.println("Connection established to " + config.get("url"));
        }
        catch(Exception e){
            throw new Exception("Connection error : " + e.getMessage());
        }
        return c;
    }

    public static Connection getConnectionTest() throws Exception {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/_7802180657c0640e", "root", "admin");
            System.out.println("Connection established to jdbc:mysql://localhost:3306/_7802180657c0640e");
        }
        catch(Exception e){
            throw new Exception("Connection error : " + e.getMessage());
        }
        return c;
    }

}
