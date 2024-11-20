package HospitalManagement;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(){
        System.out.print("Enter Patient name : ");
        String name = scanner.next();
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        System.out.print("Enter Patient gender: ");
        String gender = scanner.next();

        try{
            String query = "INSERT INTO patients(name,age,gender) values (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Patient successfully added");
            }else{
                System.out.println("Failed to add Patient");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void viewPatients(){
        String query = "select * from patients";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("+--------------+--------------+------+----------+");
            System.out.println("| Patient id   | Name         | Age  | Gender   |");
            System.out.println("+--------------+--------------+------+----------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("%-14s|%-14s|%-6s|%-10s|\n", id, name, age, gender);//%-14s: Left-aligns the text within a 14-character field.//%14s: Right-aligns the text within a 14-character field.
                System.out.println("+--------------+--------------+------+----------+");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getPatientsById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            // Use PreparedStatement to prevent SQL injection
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id); // Set the value for the first "?" placeholder

            ResultSet resultSet = preparedStatement.executeQuery(); // Execute the query

            // Check if any result is returned
            if (resultSet.next()) {
                return true; // A patient with the given ID exists
            } else {
                return false; // No patient with the given ID exists
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace in case of SQL exception
            return false; // Return false in case of an exception to indicate failure
        }
    }

    // Method to delete a Patient by ID
    public void deletePatientById(int id) {
        String query = "DELETE FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patients successfully deleted");
            } else {
                System.out.println("No Patients found with the given ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
