package org.example.app.fileAccess;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.example.models.MySql.Department;
import org.example.models.MySql.Employee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.sql.Date;

public class MySqlCsv {

    public static List<Employee> getEmployeesFromCsv(String filePath) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(
                new CSVParserBuilder().withSeparator(',').build()).build()) {

            List<Employee> list = new LinkedList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String[] line;

            while ((line = reader.readNext()) != null) {
                list.add( new Employee(
                    Integer.parseInt(line[0]),
                    Date.valueOf(line[1]),
                    line[2],
                    line[3],
                    line[4],
                    Date.valueOf(line[5]),
                    line[6]
                ));
            }
            return list;

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    public static List<Department> getDepartmentsFromCsv(String filePath) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(
                new CSVParserBuilder().withSeparator(',').build()).build()) {

            List<Department> list = new LinkedList<>();
            String[] line;

            while ((line = reader.readNext()) != null) {
                list.add( new Department(
                    line[0],
                    line[1]
                ));
            }
            return list;

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }
}
