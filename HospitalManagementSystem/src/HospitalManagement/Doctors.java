package HospitalManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctors {
    private Connection connection;
    private Scanner scanner;

    public Doctors(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addDoctors(){
        System.out.print("Enter Doctor name : ");
        String name = scanner.next();
        System.out.print("Enter your Specialization: ");
        String specialization = scanner.next();

        try{
            String query = "INSERT INTO doctors(name,specialization) values (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, specialization);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Doctor successfully added");
            }else{
                System.out.println("Failed to add Doctor");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void viewDoctors(){
        String query = "select * from doctors";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Doctors: ");
            System.out.println("+--------------+--------------+------+----------+");
            System.out.println("| Doctor id   | Name          | Specialization  |");
            System.out.println("+--------------+--------------+------+----------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("%-14s|%-15s|%-17s|\n", id, name, specialization);//%-14s: Left-aligns the text within a 14-character field.//%14s: Right-aligns the text within a 14-character field.
                System.out.println("+--------------+--------------+------+----------+");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getDoctorsById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
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

    // Method to delete a doctor by ID
    public void deleteDoctorById(int id) {
        String query = "DELETE FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Doctor successfully deleted");
            } else {
                System.out.println("No Doctor found with the given ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
