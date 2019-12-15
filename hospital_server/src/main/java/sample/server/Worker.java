package sample.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Runnable {
	private Socket clientSocket;
	private ObjectOutputStream objectOutputStream = null;
	private ObjectInputStream objectInputStream = null;


	public Worker(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {

		DatabaseConnection dbHandler = new DatabaseConnection();

		try {
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

			while (true) {
				final Map<String, String> command = (Map<String, String>) objectInputStream.readObject();
				final String type = command.get("type");

				switch (type) {
					case "loginUser":
						System.out.println("начинаем авторизацию");
						final Map<String, Object> res = login(command);
						objectOutputStream.writeObject(res);
						System.out.println("авторизация прошла успешно");
						break;
					case "SignUpUser":
						final Map<String, Object> signUpRes = signUp(command);
						objectOutputStream.writeObject(signUpRes);
						break;
					case "AddRecord":
						final Map<String, Object> addRecordRes = addRecord(command);
						objectOutputStream.writeObject(addRecordRes);
						break;
					case "RoomList":
						final Map<String, Object> getRoomsRes = dbHandler.getRooms();
						objectOutputStream.writeObject(getRoomsRes);
						break;
					case "PatientList":
						final Map<String, Object> patientListRes = dbHandler.getPatients();
						objectOutputStream.writeObject(patientListRes);
						break;
					case "ProcedurList":
						final Map<String, Object> proceduresRes = dbHandler.getProcedures();
						objectOutputStream.writeObject(proceduresRes);
						break;
					case "AddProcedures":
						final Map<String, Object> addProcedureRes = addProcedure(command);
						objectOutputStream.writeObject(addProcedureRes);
						break;
					case "delProcedure":
						final Map<String, Object> deleteProcedureRes = delProcedure(command);
						objectOutputStream.writeObject(deleteProcedureRes);
						break;
					case "DoctorList":
						final Map<String, Object> docRes = dbHandler.getDoctors();
						objectOutputStream.writeObject(docRes);
						break;
					case "AddDoctor":
						final Map<String, Object> addDoctorRes = addDoctors(command);
						objectOutputStream.writeObject(addDoctorRes);
						break;
					case "delDoctor":
						final Map<String, Object> deleteDoctorRes = delDoctors(command);
						objectOutputStream.writeObject(deleteDoctorRes);
						break;
					default:
						Map<String, String> result = new HashMap<>();
						result.put("type", "error");
						result.put("reason", "Неизвестный тип комманды : " + type);
						objectOutputStream.writeObject(result);
						break;
				}
			}
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Object> addRecord(Map<String, String> command) {
		final String name = command.get("name");
		final int number = Integer.parseInt(command.get("number"));

		DatabaseConnection databaseConnection
				= new DatabaseConnection();
		return databaseConnection.addRecord(name, number);
	}

	private Map<String, Object> addProcedure(Map<String, String> command) throws SQLException {
		final String name = command.get("name");
		final int cost = Integer.parseInt(command.get("cost"));

		DatabaseConnection databaseConnection = new DatabaseConnection();
		return databaseConnection.addProcedure(name, cost);
	}

	private Map<String, Object> delProcedure(Map<String, String> command) throws SQLException {
		final String idProcedure = command.get("idProcedure");

		DatabaseConnection databaseConnection = new DatabaseConnection();
		return databaseConnection.delProcedure(idProcedure);
	}

	private Map<String, Object> delDoctors(Map<String, String> command) throws SQLException {

		final String idDoctor = command.get("idDoctor");

		DatabaseConnection databaseConnection = new DatabaseConnection();
		return databaseConnection.delDoctor(idDoctor);
	}

	private Map<String, Object> addDoctors(Map<String, String> command) throws SQLException {
		final String name = command.get("name");
		final String lastName = command.get("lastname");
		final String specialization = command.get("specialization");

		DatabaseConnection databaseConnection = new DatabaseConnection();
		return databaseConnection.addDoctor(name, lastName, specialization);
	}

	private Map<String, Object> signUp(Map<String, String> command) throws SQLException {
		final String login = command.get("login");
		final String password = command.get("password");
		final String name = command.get("name");
		final String lastName = command.get("lastname");
		final String gender = command.get("gender");
		final String ageString = command.get("age");
		final int age = Integer.parseInt(ageString);

		DatabaseConnection databaseConnection;
		databaseConnection = new DatabaseConnection();
		return databaseConnection.singUpUser(login, password, name, lastName, gender, age);
	}

	private Map<String, Object> login(final Map<String, String> command) throws SQLException {
		DatabaseConnection databaseConnection;
		final String login = command.get("login");
		final String password = command.get("password");

		databaseConnection = new DatabaseConnection();

		String userLogin = databaseConnection.getUser(login, password);
		Map<String, Object> result = new HashMap<>();
		if (userLogin != null) {
			result.put("result", true);
			result.put("login", userLogin);
			System.out.println("логин " + userLogin);
		} else {
			result.put("result", false);
		}
		return result;
	}
}