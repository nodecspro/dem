package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreateRequestFormController implements Initializable {
	@FXML
	private Button createRequestButton;
	@FXML
	private TextField fullNameClientTextField;
	@FXML
	private TextField phoneNumberClientTextField;
	@FXML
	private ComboBox<String> equipmentsTypeComboBox;
	@FXML
	private TextField equipmentsModelTextField;
	@FXML
	private TextArea problemTextArea;

	private MainFormController mainFormController;

	public void setMainFormController(MainFormController mainFormController) {
		this.mainFormController = mainFormController;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		equipmentsTypeComboBox.setItems(DatabaseHandler.getEquipmentTypes());

		// Ограничение для fullNameClientTextField
		fullNameClientTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[а-яА-Яa-zA-Z\\s]*")) {
					fullNameClientTextField.setText(newValue.replaceAll("[^а-яА-ЯёЁa-zA-Z\\s]", ""));
				}
				if (fullNameClientTextField.getText().length() > 100) {
					fullNameClientTextField.setText(fullNameClientTextField.getText().substring(0, 100));
				}
			}
		});

		// Ограничение для phoneNumberClientTextField
		phoneNumberClientTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[0-9+\\-()\\s]*")) {
					phoneNumberClientTextField.setText(newValue.replaceAll("[^0-9+\\-()\\s]", ""));
				}
				if (phoneNumberClientTextField.getText().length() > 11) {
					phoneNumberClientTextField.setText(phoneNumberClientTextField.getText().substring(0, 11));
				}
			}
		});

		// Ограничение для problemTextArea
		problemTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (problemTextArea.getText().length() > 256) {
					problemTextArea.setText(problemTextArea.getText().substring(0, 256));
				}
			}
		});

		// Ограничение для equipmentsModelTextField
		equipmentsModelTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("[а-яА-ЯёЁa-zA-Z0-9]*")) {
					equipmentsModelTextField.setText(newValue.replaceAll("[^а-яА-ЯёЁa-zA-Z0-9]", ""));
				}
				if (equipmentsModelTextField.getText().length() > 100) {
					equipmentsModelTextField.setText(equipmentsModelTextField.getText().substring(0, 100));
				}
			}
		});
	}

	@FXML
	public void createRequestButtonOnClick(MouseEvent event) {
		String clientName = fullNameClientTextField.getText();
		String clientPhone = phoneNumberClientTextField.getText();
		String equipmentType = equipmentsTypeComboBox.getValue();
		String equipmentModel = equipmentsModelTextField.getText();
		String problemDescription = problemTextArea.getText();

		if (clientName.isEmpty() || clientPhone.isEmpty() || equipmentType == null || equipmentModel.isEmpty()
				|| problemDescription.isEmpty()) {
			showAlert(AlertType.ERROR, "Ошибка!", "Пожалуйста, заполните все поля!");
			return;
		}

		try {
			int clientId = addClientToDatabase(clientName, clientPhone);
			int modelId = addEquipmentModelToDatabase(equipmentModel, equipmentType);
			addRequestToDatabase(modelId, problemDescription, clientId);
			showAlert(AlertType.INFORMATION, "Успех!", "Заявка успешно добавлена!");

			// Закрытие окна создания заявки
			Stage stage = (Stage) createRequestButton.getScene().getWindow();
			stage.close();

			mainFormController.loadRequests();

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "Ошибка!", "Не удалось добавить заявку в базу данных.");
		}
	}

	private int addClientToDatabase(String clientName, String clientPhone) throws SQLException {
		String query = "INSERT INTO clients (client_name, phone_number) VALUES (?, ?)";
		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement statement = connection.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, clientName);
			statement.setString(2, clientPhone);
			statement.executeUpdate();

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException("Не удалось получить ID клиента.");
				}
			}
		}
	}

	private int addEquipmentModelToDatabase(String modelName, String equipmentType) throws SQLException {
		String getEquipmentTypeIdQuery = "SELECT equipment_type_id FROM equipment_types WHERE type_name = ?";
		String insertModelQuery = "INSERT INTO equipment_models (model_name, equipment_type_id) VALUES (?, ?)";

		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement getTypeIdStatement = connection.prepareStatement(getEquipmentTypeIdQuery);
				PreparedStatement insertModelStatement = connection.prepareStatement(insertModelQuery,
						Statement.RETURN_GENERATED_KEYS)) {

			getTypeIdStatement.setString(1, equipmentType);
			try (ResultSet resultSet = getTypeIdStatement.executeQuery()) {
				if (resultSet.next()) {
					int equipmentTypeId = resultSet.getInt("equipment_type_id");

					insertModelStatement.setString(1, modelName);
					insertModelStatement.setInt(2, equipmentTypeId);
					insertModelStatement.executeUpdate();

					try (ResultSet generatedKeys = insertModelStatement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							return generatedKeys.getInt(1);
						} else {
							throw new SQLException("Не удалось получить ID модели оборудования.");
						}
					}
				} else {
					throw new SQLException("Не удалось найти ID типа оборудования.");
				}
			}
		}
	}

	private void addRequestToDatabase(int modelId, String problemDescription, int clientId) throws SQLException {
		String query = "INSERT INTO requests (model_id, problem_description, client_id, status, create_at, date_added) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, modelId);
			statement.setString(2, problemDescription);
			statement.setInt(3, clientId);
			statement.setString(4, "Новая заявка");
			statement.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));
			statement.setDate(6, new java.sql.Date(System.currentTimeMillis()));

			statement.executeUpdate();
		}
	}

	private void showAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}