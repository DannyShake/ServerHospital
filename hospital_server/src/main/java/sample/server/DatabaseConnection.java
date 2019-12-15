package sample.server;


import org.openjfx.entity.Doctor;
import org.openjfx.entity.Patient;
import org.openjfx.entity.Procedure;
import org.openjfx.entity.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseConnection {
    private Connection basicConnection;

    private static final String URL = "jdbc:mysql://localhost:3306/hospital?autoReconnect=true&useSSL=false&useUnicode=true&serverTimezone=UTC";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static final String INSERT_USERS = "INSERT INTO users (login, password) VALUES(?,?)";
    public static final String INSERT_PATIENTS = "INSERT INTO patients (name, lastname, gender, age) VALUES(?,?,?,?)";
    public static final String SELECT_USER = "SELECT * FROM users WHERE login = ? AND password = ?";
    public static final String CHECK_LOGIN = "SELECT * FROM users WHERE login = ?";
    public static final String SELECT_ALL_PATIENTS = "SELECT * FROM patients";
    public static final String SELECT_ALL_DOCTORS = "SELECT * FROM doctors";
    public static final String INSERT_DOCTOR = "INSERT INTO doctors (name, lastname, specialization) VALUES(?,?,?)";
    public static final String DELETE_DOCTOR_BY_ID = "DELETE FROM doctors WHERE idDoctor=?";
    public static final String SELECT_ALL_PROCEDURES = "SELECT * FROM procedures";
    public static final String DELETE_PROCEDURE_BY_ID = "DELETE FROM procedures WHERE idProcedur=?";
    public static final String INSERT_PROCEDURE = "INSERT INTO procedures (name, cost) VALUES(?,?)";
    public static final String INSERT_ROOM = "INSERT INTO room (patientName, ProcedureName) VALUES(?,?)";
    public static final String INSERT_RECORD = "INSERT INTO medicalrecords (record) VALUES(?)";
    public static final String SELECT_PROCEDURE_BY_ID = "SELECT * FROM procedures WHERE idProcedur = ?";
    public static final String SELECT_ALL_ROOMS = "SELECT * FROM room";

    public Connection getBasicConnection() {
        try {
            basicConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return basicConnection;
    }

    public String getUser(String login, String password) throws SQLException {
        try (Connection c = getBasicConnection()) {
            PreparedStatement ps = c.prepareStatement(SELECT_USER);
            ps.setString(1, login);
            ps.setString(2, password);

            ResultSet result = ps.executeQuery();
            while (result.next()) {
                return result.getString("login");
            }
        } catch (Exception all) {
            all.printStackTrace();
        }
        return null;
    }


    public Map<String, Object> singUpUser(String login, String password, String name, String lastName, String gender, int age) throws SQLException {
        Connection connection = getBasicConnection();
        Map<String, Object> result = new HashMap<>();
        if (!loginCheck(login)) {
            try (connection) {
                connection.setAutoCommit(false); //отключаю автоматический коммит транзакции

                saveUserToDB(login, password, connection);
                savePatientToDB(name, lastName, gender, age, connection);

                connection.commit(); //коммитаем изменения, если всё прошло без ошибок
            } catch (Exception all) {
                try {
                    connection.rollback(); //откатываемся до состояния перед транзакцией
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
                all.printStackTrace();
            }
            result.put("result", true);
            return result;
        } else {
            result.put("result", false);
            return result;
        }
    }

    public boolean loginCheck(String login) throws SQLException {
        try (Connection c = getBasicConnection();) {
            ResultSet result;
            PreparedStatement ps = c.prepareStatement(CHECK_LOGIN);
            ps.setString(1, login);
            result = ps.executeQuery();
            AtomicInteger counter = new AtomicInteger(0);
            try {
                while (result.next())
                    counter.incrementAndGet();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return counter.get() >= 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void saveUserToDB(String login, String password, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_USERS);
        ps.setString(1, login);
        ps.setString(2, password);
        ps.executeUpdate();
    }

    private void savePatientToDB(String name, String lastName, String gender, int age, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_PATIENTS);
        ps.setString(1, name);
        ps.setString(2, lastName);
        ps.setString(3, gender);
        ps.setInt(4, age);
        ps.executeUpdate();
    }

    public Map<String, Object> getPatients() throws SQLException {
        Map<String, Object> result = new HashMap<>();
        try (Connection c = getBasicConnection()) {
            PreparedStatement ps = c.prepareStatement(SELECT_ALL_PATIENTS);
            ResultSet resSet = ps.executeQuery();

            List<Patient> patientArrayList = new ArrayList<>();
            while (resSet.next()) {
                Patient patient = new Patient();
                patient.setIdPatient(resSet.getInt("idPatient"));
                patient.setName((resSet.getString("name")) + " " + (resSet.getString("lastname")));
                patient.setGender(resSet.getString("gender"));
                patient.setAge(resSet.getString("age"));
                patientArrayList.add(patient);
            }
            result.put("patients", patientArrayList);
            result.put("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("result", false);
        }

        return result;
    }

    public Map<String, Object> getDoctors() throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection c = getBasicConnection()) {
            PreparedStatement ps = c.prepareStatement(SELECT_ALL_DOCTORS);
            ResultSet resultSet = ps.executeQuery();

            List<Doctor> doctorArrayList = new ArrayList<>();
            while (resultSet.next()) {
                Doctor doctor = new Doctor();
                doctor.setIdDoctor(resultSet.getInt("idDoctor"));
                doctor.setName((resultSet.getString("name")) + " " + (resultSet.getString("lastname")));
                doctor.setSpecialization(resultSet.getString("specialization"));
                doctorArrayList.add(doctor);
            }

            result.put("doctors", doctorArrayList);
            result.put("result", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("result", false);
        }
        return result;
    }

    public Map<String, Object> addDoctor(String name, String lastName, String specialization) throws SQLException {
        try (Connection c = getBasicConnection()) {
            Map<String, Object> result = new HashMap<>();
            PreparedStatement ps = c.prepareStatement(INSERT_DOCTOR);
            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setString(3, specialization);
            ps.executeUpdate();
            result.put("result", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("result", false);
        }
    }

    public Map<String, Object> delDoctor(String idDoctor) throws SQLException {
        try (Connection c = getBasicConnection()) {
            Map<String, Object> result = new HashMap<>();
            PreparedStatement ps = c.prepareStatement(DELETE_DOCTOR_BY_ID);
            ps.setInt(1, Integer.parseInt(idDoctor));
            ps.executeUpdate();
            result.put("result", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("result", false);
        }
    }

    public Map<String, Object> getProcedures() throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection c = getBasicConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(SELECT_ALL_PROCEDURES);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Procedure> procedureArrayList = new ArrayList<>();

            while (resultSet.next()) {
                Procedure p = new Procedure();
                p.setIdProcedur(resultSet.getInt("idProcedur"));
                p.setName(resultSet.getString("name"));
                p.setCost(resultSet.getInt("cost"));
                procedureArrayList.add(p);
            }

            result.put("procedures", procedureArrayList);
            result.put("result", true);
        } catch (Exception all) {
            all.printStackTrace();
            result.put("result", false);
        }
        return result;
    }

    public Map<String, Object> delProcedure(String idProcedure) throws SQLException {
        try (Connection c = getBasicConnection();) {
            Map<String, Object> result = new HashMap<>();
            PreparedStatement ps = c.prepareStatement(DELETE_PROCEDURE_BY_ID);
            ps.setInt(1, Integer.parseInt(idProcedure));
            ps.executeUpdate();
            result.put("result", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("result", false);
        }
    }

    public Map<String, Object> addProcedure(String name, int cost) throws SQLException {
        try (Connection c = getBasicConnection()) {
            Map<String, Object> result = new HashMap<>();
            PreparedStatement ps = c.prepareStatement(INSERT_PROCEDURE);
            ps.setString(1, name);
            ps.setInt(2, cost);
            ps.executeUpdate();
            
            result.put("result", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("result", false);
        }
    }

    public Map<String, Object> addRecord(String name, int number) {
        
        try (Connection c = getBasicConnection();) {
            Map<String, Object> result = new HashMap<>();
            try {
                c.setAutoCommit(false); //disable auto commit

                PreparedStatement ps = getBasicConnection().prepareStatement(SELECT_PROCEDURE_BY_ID);
                ps.setInt(1, number);
                ResultSet resultSet = ps.executeQuery();

                String procedure;
                String Record;

                while (resultSet.next()) {
                    procedure = resultSet.getString("name");
                    Record = name + " " + resultSet.getString("name");

                    PreparedStatement insertRoomPS = getBasicConnection().prepareStatement(INSERT_ROOM);
                    insertRoomPS.setString(1, name);
                    insertRoomPS.setString(2, procedure);
                    insertRoomPS.executeUpdate();

                    PreparedStatement insertRecordPS = getBasicConnection().prepareStatement(INSERT_RECORD);
                    insertRecordPS.setString(1, Record);
                    insertRecordPS.executeUpdate();
                }
                c.commit(); //commit changes, if all passed
            } catch (Exception all) {
                try {
                    c.rollback(); 
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
                all.printStackTrace();
            }
            result.put("result", true);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of("result", false);
        }
    }

    public Map<String, Object> getRooms() throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection c = getBasicConnection()) {
            PreparedStatement ps = c.prepareStatement(SELECT_ALL_ROOMS);
            ResultSet resultSet = ps.executeQuery();

            final ArrayList<Room> roomArrayList = new ArrayList<>();
            while (resultSet.next()) {
                Room room = new Room();
                room.setRoomNumber(resultSet.getInt("idRoom"));
                room.setPatientName(resultSet.getString("patientName"));
                room.setProcedureName(resultSet.getString("procedureName"));
                roomArrayList.add(room);
            }
            result.put("result", true);
            result.put("rooms", roomArrayList);
        } catch (Exception all) {
            all.printStackTrace();
            result.put("result", false);
        }
        return result;
    }
}
