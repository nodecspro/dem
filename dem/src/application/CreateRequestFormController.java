package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreateRequestFormController implements Initializable {
	@FXML
	private Button createRequestButton;
	@FXML
	private TextField equipmentsModelTextField;
	@FXML
	private TextArea problemTextArea;
	@FXML
	private TextField equipmentsTypeTextField;

	private MainFormController mainFormController;

	public void setMainFormController(MainFormController mainFormController) {
		this.mainFormController = mainFormController;
	}

	@FXML
	public void createRequestButtonOnClick(MouseEvent event) {
		String equipmentType = equipmentsTypeTextField.getText().trim();
		String equipmentModel = equipmentsModelTextField.getText().trim();
		String problemDescription = problemTextArea.getText().trim();

		// Validate input
		if (equipmentType.isEmpty() || equipmentModel.isEmpty() || problemDescription.isEmpty()) {
			showAlert(AlertType.ERROR, "Ошибка!", "Пожалуйста, заполните все поля!");
			return;
		}

		try {
			if (addRequestToDatabase(problemDescription, mainFormController.getCurrentUserId(), equipmentType,
					equipmentModel)) {
				showAlert(AlertType.INFORMATION, "Успех!", "Заявка успешно добавлена!");
				closeForm();
				mainFormController.loadRequests();
			} else {
				showAlert(AlertType.ERROR, "Ошибка!", "Не удалось добавить заявку в базу данных.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "Ошибка базы данных", "Не удалось подключиться к базе данных.");
		}
	}

	private boolean addRequestToDatabase(String problemDescription, int clientId, String equipmentType,
			String equipmentModel) throws SQLException {
		String query = "INSERT INTO requests (startDate, computerTechType, computerTechModel, problemDescription, clientID) VALUES (CURDATE(), ?, ?, ?, ?)";
		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, equipmentType);
			statement.setString(2, equipmentModel);
			statement.setString(3, problemDescription);
			statement.setInt(4, clientId);
			return statement.executeUpdate() > 0;
		}
	}

	private void closeForm() {
		((Stage) createRequestButton.getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupTextFieldLimit(equipmentsModelTextField, 100);
		setupTextFieldLimit(equipmentsTypeTextField, 100);
		problemTextArea.textProperty().addListener((obs, old, niu) -> {
			if (problemTextArea.getText().length() > 256) {
				problemTextArea.setText(problemTextArea.getText().substring(0, 256));
			}
		});
	}

	private void setupTextFieldLimit(TextField textField, int limit) {
		textField.textProperty().addListener((obs, old, niu) -> {
			if (niu.length() > limit) {
				textField.setText(niu.substring(0, limit));
			}
		});
	}

	private void showAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}