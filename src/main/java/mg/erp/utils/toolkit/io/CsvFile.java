package mg.erp.utils.toolkit.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public class CsvFile {
    public List<HashMap<String,Object>> importCsv(String FileName, String separateur, HashMap<String,Class<?>> typeColonnesMap){
        List<HashMap<String,Object>> result = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(FileName));
            String ligne;
            int nbLine = 0;
            List<String> entete = new ArrayList<>();
            while ((ligne = csvReader.readLine()) != null) {
                ligne = reformatLine(ligne);
                String [] valeur = ligne.split(separateur);
                if (nbLine>0){
                    HashMap<String,Object> ligneData = new HashMap<>();
                    for (int i = 0; i < valeur.length; i++) {
                        Class<?> valeurType = typeColonnesMap.get(entete.get(i));
                        ligneData.put(entete.get(i), this.castValue(valeur[i],valeurType));
                    }
                    result.add(ligneData);
                }
                else {
                    for (String v:valeur){
                        entete.add(v);
                    }
                }
                nbLine++;
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    public void exportCsv(List<?> objects, String file_name, char separator) {
        java.io.File file = new java.io.File(file_name+".csv");

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER,
                                                                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                                                                CSVWriter.DEFAULT_LINE_END);

            Field[] fields = Reflect.getColumns(objects.get(0));

            List<String> headers = new ArrayList<>();
            for (Field field : fields) {
                headers.add(field.getName());
            }

            writer.writeNext(headers.toArray(new String[]{}));

            for (Object object : objects) {
                List<String> values = new ArrayList<>();
                for (Field field : fields) {
                    Method method = object.getClass().getDeclaredMethod("get"+Utils.toUpperCase(field.getName()));
                    Object value = method.invoke(object);
                    values.add(value.toString());
                }
                writer.writeNext(values.toArray(new String[]{}));
                writer.flush();
            }

            writer.close();
        } catch (RuntimeException | IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void exportCsvServlet(List<?> objects, String file_name, HttpServletResponse response, String separator) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        Class<?> clazz = objects.get(0).getClass();

        String fileName = objects.isEmpty() ? file_name+".csv" : clazz.getSimpleName()+".csv";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        manageCsv(objects, out, separator);

        out.close();
    }

    private void manageCsv(List<?> objects, PrintWriter out, String separator) throws ServletException, IOException {

        if (objects == null || objects.isEmpty()) {
            return;
        }

        Field[] fields = Reflect.getColumns(objects.get(0));

        StringBuilder header = new StringBuilder();

        for (Field field : fields) {
            header.append(field.getName()).append(separator);
        }
        header.deleteCharAt(header.length() - 1);
        out.println(header.toString());

        for (Object obj : objects) {
            StringBuilder row = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    row.append(value != null ? value.toString() : "").append(separator);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            row.deleteCharAt(row.length() - 1);
            out.println(row.toString());
        }
        out.flush();
    }

    private String reformatLine(String line) {
        char[] chars = line.toCharArray();
        String value = "";
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            boolean isIn = false;
            if (found){
                if (chars[i] == ','){
                    chars[i] = '.';
                }
            }
            if (chars[i] == '"' && !found && !isIn) {
                found = true;
                isIn = true;
            }
            if (chars[i] == '"' && found && !isIn) {
                found = false;
                isIn = true;
            }
            value += chars[i];
        }
        return value;
    }

    private String reformatDate (String date){
        date = date.replace("/","-");
        return date;
    }
    private String reformatDouble (String valeur){
        valeur = valeur.replace("\"","");
        return valeur;
    }

    private Object castValue(String value,Class<?> clazz) throws Exception {
        Object result = null;
        try {
            if(clazz == String.class){
                result = value;
            }
            if (clazz == int.class || clazz == Integer.class) {
                result = Integer.parseInt(value);
            }
            if (clazz == double.class || clazz == Double.class) {
                value = reformatDouble(value);
                result = Double.parseDouble(value);
            }
            if (clazz == boolean.class || clazz == Boolean.class) {
                result = Boolean.valueOf(value);
            }
            if(clazz == Date.class){
                value = reformatDate(value);
                result = Date.valueOf(value);
            }
            if(clazz == Timestamp.class){
                value = reformatDate(value);
                result = Timestamp.valueOf(value);
            }
        }catch (Exception e){
            throw new Exception("Impossible de convertir la valeur : '" + value + "' en " + clazz.getSimpleName());
        }
        return result;
    }
}
