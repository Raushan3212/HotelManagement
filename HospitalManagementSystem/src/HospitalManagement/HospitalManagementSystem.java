package HospitalManagement;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "12345";

    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection, scanner);
            Doctors doctors = new Doctors(connection,scanner);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Delete Patients");
                System.out.println("4. Add Doctors");
                System.out.println("5. View Doctors");
                System.out.println("6. Delete Doctor");
                System.out.println("7. Book Appointment");
                System.out.println("8. View Appointments");
                System.out.println("9. Delete Appointments");
                System.out.println("10. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        // Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        // Delete Patients
                        System.out.println("Enter patientsId to delete");
                        int patientId = scanner.nextInt();
                        patient.deletePatientById(patientId);
                        System.out.println("Patients delete successfully");
                        break;
                    case 4:
                        // Add Doctors
                        doctors.addDoctors();
                        System.out.println();
                        break;
                    case 5:
                        // View Doctors
                        doctors.viewDoctors();
                        System.out.println();
                        break;
                    case 6:
                        // Delete Doctors
                        System.out.println("Enter doctorId to delete");
                        int doctorId = scanner.nextInt();
                        doctors.deleteDoctorById(doctorId);
                        System.out.println("Doctor delete successfully");
                        break;
                    case 7:
                        // Book Appointment
                        bookAppointment(patient, doctors, connection, scanner);
                        System.out.println();
                        break;
                    case 8:
                        // View Appointments
                        viewAppointments(connection);
                        System.out.println();
                        break;
                    case 9:
                        //Delete appointment
                        System.out.println("Enter appointment ID to delete: ");
                        int appointmentId = scanner.nextInt();
                        deleteAppointmentById(appointmentId, connection);
                        System.out.println();
                        break;
                    case 10:
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }

            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctors doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientsById(patientId) && doctor.getDoctorsById(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to Book Appointment!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date!!");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void viewAppointments(Connection connection){
        String query = "SELECT a.id, p.name AS name, d.name AS name, a.appointment_date " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("+------------+------------------+------------------+-------------------+");
            System.out.println("| Appointment ID | Patient Name    | Doctor Name      | Appointment Date  |");
            System.out.println("+------------+------------------+------------------+-------------------+");

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("id");
                String patientName = resultSet.getString("name");
                String doctorName = resultSet.getString("name");
                String appointmentDate = resultSet.getString("appointment_date");

                System.out.printf(" %-16s| %-18s| %-18s| %-19s|\n", appointmentId, patientName, doctorName, appointmentDate);
                System.out.println("+------------+------------------+------------------+-------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAppointmentById(int appointmentId, Connection connection) {
        String query = "DELETE FROM appointments WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appointment deleted successfully.");
            } else {
                System.out.println("No appointment found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}