import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import parsers.Employee;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        final String inputFileName = "data.xml";
        final String outputFileName = "output_data.json";
        List<Employee> list = parseXML(inputFileName);
        listToJson(list);
        String json = listToJson(list);
        stringToFile(outputFileName, json);
        // для визуального контроля
        printJSONFile(outputFileName);
    }

    public static void stringToFile(String outputFileName, String jsonString) {
        File file = new File(outputFileName);
        try (Writer writer = new FileWriter(file, false)) {
            writer.write(jsonString.toString());
        } catch (IOException ex) {
            System.out.printf("Ошибка сохранения файла %s:", ex.getMessage());
        }
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nodeList.item(i);
                Employee employee = new Employee();
                employee.id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                employee.firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                employee.lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                employee.country = element.getElementsByTagName("country").item(0).getTextContent();
                employee.age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                list.add(employee);
            }
        }
        return list;
    }

    private static void printJSONFile(String jsonFileName) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonFileName));
            JSONArray jsonObjects = (JSONArray) obj;
            jsonObjects.forEach(System.out::println);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }
}
