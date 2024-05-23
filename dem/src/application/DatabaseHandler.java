package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHandler {
	private static final String URL = "jdbc:mysql://localhost:3306/dem";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	public static List<Request> getRequests() throws SQLException {
		List<Request> requests = new ArrayList<>();

		String query = "SELECT r.request_id, r.date_added, r.model_id, r.problem_description, r.client_id, r.worker_id, r.status, r.comments, r.create_at, r.completion_time, "
				+ "m.model_name, e.type_name, c.client_name, c.phone_number, w.worker_name " + "FROM requests r "
				+ "JOIN equipment_models m ON r.model_id = m.model_id "
				+ "JOIN equipment_types e ON m.equipment_type_id = e.equipment_type_id "
				+ "JOIN clients c ON r.client_id = c.client_id " + "LEFT JOIN workers w ON r.worker_id = w.worker_id";

		try (Connection connection = getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				int requestId = resultSet.getInt("request_id");
				Date dateAdded = resultSet.getDate("date_added");
				int modelId = resultSet.getInt("model_id");
				String problemDescription = resultSet.getString("problem_description");
				int clientId = resultSet.getInt("client_id");
				Integer workerId = resultSet.getObject("worker_id", Integer.class); // worker_id может быть null
				String status = resultSet.getString("status");
				String comments = resultSet.getString("comments");
				Date createdAt = resultSet.getTimestamp("create_at");
				String completionTime = resultSet.getString("completion_time");

				String modelName = resultSet.getString("model_name");
				String typeName = resultSet.getString("type_name");
				String clientName = resultSet.getString("client_name");
				String phoneNumber = resultSet.getString("phone_number");
				String workerName = resultSet.getString("worker_name");

				requests.add(new Request(requestId, dateAdded, modelId, problemDescription, clientId, workerId, status,
						comments, createdAt, completionTime, modelName, typeName, clientName, phoneNumber, workerName));
			}
		}

		return requests;
	}

	public static ObservableList<String> getEquipmentTypes() {
		ObservableList<String> equipmentTypes = FXCollections.observableArrayList();
		String query = "SELECT type_name FROM equipment_types";

		try (Connection connection = getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				equipmentTypes.add(resultSet.getString("type_name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return equipmentTypes;
	}
}