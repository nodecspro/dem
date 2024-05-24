package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AuthFormController {

	@FXML
	private Button loginButton;

	@FXML
	private TextField loginTextField;

	@FXML
	private PasswordField passwordTextField;

	@FXML
	void loginButtonClick(MouseEvent event) {
		String login = loginTextField.getText().trim();
		String password = passwordTextField.getText().trim();

		if (login.isEmpty() || password.isEmpty()) {
			showAlert("Ошибка входа", "Пожалуйста, введите логин и пароль.");
			return;
		}

		String userID = authenticateUser(login, password);
		if (userID != null) {
			showAlert("Вход выполнен успешно", "Добро пожаловать, " + login + "!");
			openMainForm(userID);
		} else {
			showAlert("Ошибка входа", "Неверный логин или пароль.");
		}
	}

	private String authenticateUser(String login, String password) {
		String query = "SELECT userID FROM Users WHERE login=? AND password=?";
		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, login);
			statement.setString(2, password);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? resultSet.getString("userID") : null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Ошибка базы данных", "Не удалось подключиться к базе данных.");
			return null;
		}
	}

	private void openMainForm(String userID) {
		Stage stage = (Stage) loginButton.getScene().getWindow();
		stage.close();

		Stage mainStage = loadMainStage(userID);
		if (mainStage != null) {
			mainStage.show();
			mainStage.setTitle("Главное окно");
		}
	}

	private Stage loadMainStage(String userID) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
			Parent root = loader.load();
			MainFormController mainController = loader.getController();
			mainController.setUserID(userID);

			Stage mainStage = new Stage();
			mainStage.setScene(new Scene(root));
			return mainStage;
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Ошибка загрузки", "Не удалось загрузить главный интерфейс.");
			return null;
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}