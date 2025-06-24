package mg.erp.utils;

import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

public class Config {
    public String getErpUrl (ConfigurableEnvironment environment){
        return environment.getProperty("erp.server.url");
    }

    public static Map<String, String> databaseConfig(ConfigurableEnvironment environment) {
        return Map.of(
            "url", environment.getProperty("spring.datasource.url"),
            "driver", environment.getProperty("spring.datasource.driver"),
            "user", environment.getProperty("spring.datasource.user"),
            "pwd", environment.getProperty("spring.datasource.password")
        );
    }
}
