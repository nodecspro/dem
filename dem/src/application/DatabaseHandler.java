package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHandler {
	private static final String URL = "jdbc:mysql://localhost:3306/24m115";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	private static final String REQUESTS_QUERY = "SELECT r.requestID, r.startDate, r.computerTechType, r.computerTechModel, "
			+ "r.problemDescription, r.requestStatus, r.completionDate, r.repairParts, "
			+ "r.masterID, r.clientID, u1.fio AS masterName, u2.fio AS clientName, u2.phone AS clientPhone "
			+ "FROM Requests r " + "LEFT JOIN Masters m ON r.masterID = m.masterID "
			+ "LEFT JOIN Users u1 ON m.userID = u1.userID " + "LEFT JOIN Clients c ON r.clientID = c.clientID "
			+ "LEFT JOIN Users u2 ON c.userID = u2.userID";
	private static final String EQUIPMENT_TYPES_QUERY = "SELECT DISTINCT computerTechType FROM Requests";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	public static List<Request> getRequests() {
		List<Request> requests = new ArrayList<>();
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(REQUESTS_QUERY);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				requests.add(mapToRequest(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}

	private static Request mapToRequest(ResultSet resultSet) throws SQLException {
		int requestId = resultSet.getInt("requestID");
		Date startDate = resultSet.getDate("startDate");
		String computerTechType = resultSet.getString("computerTechType");
		String computerTechModel = resultSet.getString("computerTechModel");
		String problemDescription = resultSet.getString("problemDescription");
		String requestStatus = resultSet.getString("requestStatus");
		Date completionDate = resultSet.getDate("completionDate");
		String repairParts = resultSet.getString("repairParts");
		Integer masterId = resultSet.getObject("masterID", Integer.class);
		int clientId = resultSet.getInt("clientID");
		String masterName = resultSet.getString("masterName");
		String clientName = resultSet.getString("clientName");
		String clientPhone = resultSet.getString("clientPhone");

		return new Request(requestId, startDate, computerTechType, computerTechModel, problemDescription, clientId,
				masterId, requestStatus, repairParts, completionDate, masterName, clientName, clientPhone);
	}

	public static ObservableList<String> getEquipmentTypes() {
		ObservableList<String> equipmentTypes = FXCollections.observableArrayList();
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(EQUIPMENT_TYPES_QUERY);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				equipmentTypes.add(resultSet.getString("computerTechType"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return equipmentTypes;
	}
}