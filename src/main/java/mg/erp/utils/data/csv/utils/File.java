package mg.erp.utils.data.csv.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class File {
    String name;
    String path;
    HashMap<String,Class<?>> types = new HashMap<>();

    public File() {
    }

    public File(String name, String path, HashMap<String, Class<?>> types) {
        this.name = name;
        this.path = path;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, Class<?>> getTypes() {
        return types;
    }

    public void setTypes(HashMap<String, Class<?>> types) {
        this.types = types;
    }

    public String upload(MultipartFile file, String dir){
        try{
            Path uploadDir = Paths.get(dir).toAbsolutePath();
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
