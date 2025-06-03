package mg.erp.utils.toolkit.io;

import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFile {
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public void exportJson(Object object, String path) throws Exception{
        try (FileWriter file = new FileWriter(path)){
            gson.toJson(object, file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public Object importJson(Class<?> clazz, String path) throws Exception{
        try (FileReader file = new FileReader(path)){
            return gson.fromJson(file, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
