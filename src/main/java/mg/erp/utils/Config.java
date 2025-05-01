package mg.erp.utils;

import org.springframework.core.env.ConfigurableEnvironment;

public class Config {
    public String getErpUrl (ConfigurableEnvironment environment){
        return environment.getProperty("erp.server.url");
    }
}
