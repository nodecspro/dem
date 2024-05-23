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
	private Button loginbutton;

	@FXML
	private TextField logintextfield;

	@FXML
	private PasswordField passwordtextfield;

	@FXML
	private Button regbutton;

	@FXML
	void loginbuttonclick(MouseEvent event) {
		String login = logintextfield.getText();
		String password = passwordtextfield.getText();

		if (isValidLogin(login, password)) {
			String userRole = authenticateUser(login, password);
			if (userRole != null) {
				showAlert("Вход выполнен успешно", "Добро пожаловать, " + login + "!");
				openMainForm(userRole); // открываем главную форму и передаем роль пользователя
			} else {
				showAlert("Ошибка входа", "Неверный логин или пароль");
			}
		} else {
			showAlert("Ошибка входа", "Неверный логин или пароль");
		}
	}

	private boolean isValidLogin(String login, String password) {
		return login != null && !login.isEmpty() && password != null && !password.isEmpty();
	}

	private String authenticateUser(String login, String password) {
		String query = "SELECT role FROM user WHERE username=? AND password=?";
		try (Connection connection = DatabaseHandler.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, login);
			statement.setString(2, password);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("role");
				} else {
					return null;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Ошибка базы данных", "Не удалось подключиться к базе данных");
			return null;
		}
	}

	private void openMainForm(String userRole) {
		closeCurrentStage();
		Stage mainStage = loadMainStage(userRole);
		if (mainStage != null) {
			mainStage.show();
			mainStage.setTitle("Главное окно");
		}
	}

	private void closeCurrentStage() {
		Stage stage = (Stage) loginbutton.getScene().getWindow();
		stage.close();
	}

	private Stage loadMainStage(String userRole) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
			Parent root = loader.load();
			Stage mainStage = new Stage();
			mainStage.setScene(new Scene(root));

			// Получение контроллера и установка роли пользователя
			MainFormController mainController = loader.getController();
			mainController.setUserRole(userRole);

			return mainStage;
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Ошибка загрузки", "Не удалось загрузить главный интерфейс");
			return null;
		}
	}

	@FXML
	void regbuttonclick(MouseEvent event) {
		String login = logintextfield.getText();
		String password = passwordtextfield.getText();

		if (!isValidInput(login, password)) {
			return;
		}

		try (Connection connection = DatabaseHandler.getConnection()) {
			if (isUserExists(connection, login)) {
				showAlert("Ошибка регистрации", "Пользователь с таким логином уже существует");
				return;
			}

			if (registerUser(connection, login, password)) {
				showAlert("Успешная регистрация", "Пользователь " + login + " успешно зарегистрирован");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Ошибка базы данных", "Не удалось подключиться к базе данных");
		}
	}

	private boolean isValidInput(String login, String password) {
		if (login.isEmpty() || password.isEmpty()) {
			showAlert("Ошибка регистрации", "Логин и пароль не могут быть пустыми");
			return false;
		}

		if (!login.matches("[a-zA-Z0-9]+") || !password.matches("[a-zA-Z0-9]+")) {
			showAlert("Ошибка регистрации", "Логин и пароль должны содержать только английские буквы и цифры");
			return false;
		}

		if (login.length() < 3 || login.length() > 16) {
			showAlert("Ошибка регистрации", "Логин должен содержать от 3 до 16 символов");
			return false;
		}

		if (password.length() < 6 || password.length() > 32) {
			showAlert("Ошибка регистрации", "Пароль должен содержать от 6 до 32 символов");
			return false;
		}

		return true;
	}

	private boolean isUserExists(Connection connection, String login) throws SQLException {
		try (PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM user WHERE username=?")) {
			checkStatement.setString(1, login);
			try (ResultSet resultSet = checkStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private boolean registerUser(Connection connection, String login, String password) throws SQLException {
		try (PreparedStatement insertStatement = connection
				.prepareStatement("INSERT INTO user (username, password) VALUES (?, ?)")) {
			insertStatement.setString(1, login);
			insertStatement.setString(2, password);
			return insertStatement.executeUpdate() > 0;
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