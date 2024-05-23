package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainFormController implements Initializable {

	@FXML
	private TableView<Request> requestTableView;
	@FXML
	private TableColumn<Request, Integer> requestIdColumn;
	@FXML
	private TableColumn<Request, Date> dateAddedColumn;
	@FXML
	private TableColumn<Request, String> typeColumn;
	@FXML
	private TableColumn<Request, String> modelColumn;
	@FXML
	private TableColumn<Request, String> problemColumn;
	@FXML
	private TableColumn<Request, String> clientFullName;
	@FXML
	private TableColumn<Request, String> ClientNumber;
	@FXML
	private TableColumn<Request, String> statusColumn;
	@FXML
	private Button deleteRequestButton;
	@FXML
	private Button editRequestButton;
	@FXML
	private Button addRequestButton;
	@FXML
	private ComboBox<String> RequestStatusComboBox;
	@FXML
	private TextArea problemTextArea;
	@FXML
	private ComboBox<String> workerComboBox;
	@FXML
	private ComboBox<String> searchParametrComboBox;
	@FXML
	private TextField parametrTextField;
	@FXML
	private Button searchRequestByParametr;
	@FXML
	private TextArea commentTextArea;
	@FXML
	private ComboBox<String> parameterStatisticComboBox;
	@FXML
	private TextArea statisticTextArea;
	@FXML
	private Button saveRequestButton;
	@FXML
	private Label commentLabel;
	@FXML
	private Label problemLabel;
	@FXML
	private Label statusRequestLabel;
	@FXML
	private Label workerLabel;

	// Мапа для хранения ID работников по имени
	private Map<String, Integer> workerIdMap = new HashMap<>();

	public void setUserRole(String userRole) {
		// Проверка роли пользователя и скрытие кнопок при необходимости
		if ("Пользователь".equals(userRole)) {
			editRequestButton.setVisible(false);
			deleteRequestButton.setVisible(false);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Установка значений для колонок
		requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
		dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
		modelColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
		problemColumn.setCellValueFactory(new PropertyValueFactory<>("problemDescription"));
		clientFullName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
		ClientNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

		try {
			// Загрузка данных из базы данных
			loadRequests();

			// Загрузка данных для ComboBox'ов
			loadWorkers();
			loadRequestStatuses();
			loadSearchParameters();
			loadparameterStatistic();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Добавление слушателя на выбор строки в таблице
		requestTableView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showRequestDetails(newValue));

		// Скрытие элементов управления по умолчанию
		hideEditControls();

		// Обработчик для параметра статистики
		parameterStatisticComboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedParameter = parameterStatisticComboBox.getValue();
				if (selectedParameter != null) {
					switch (selectedParameter) {
					case "Количество выполненных заявок":
						displayCompletedRequestsCount();
						break;
					case "Среднее время выполнения заявки":
						displayAverageCompletionTime();
						break;
					case "Статистика по типам неисправности":
						displayProblemTypeStatistics();
						break;
					}
				}
			}
		});
	}

	// Загрузка данных о работниках из базы данных
	private void loadWorkers() throws SQLException {
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT worker_id, worker_name FROM workers")) {

			ObservableList<String> workers = FXCollections.observableArrayList();
			while (resultSet.next()) {
				int workerId = resultSet.getInt("worker_id");
				String workerName = resultSet.getString("worker_name");
				workers.add(workerName);
				workerIdMap.put(workerName, workerId);
			}

			workerComboBox.getItems().clear();
			workerComboBox.setItems(workers);
		}
	}

	public void loadRequests() throws SQLException {
		List<Request> requestList = DatabaseHandler.getRequests();
		ObservableList<Request> observableRequestList = FXCollections.observableArrayList(requestList);
		requestTableView.setItems(observableRequestList);
	}

	// Загрузка данных о статусах заявок
	private void loadRequestStatuses() {
		ObservableList<String> statuses = FXCollections.observableArrayList("Новая заявка", "В процессе ремонта",
				"Завершена");
		RequestStatusComboBox.setItems(statuses);
	}

	// Загрузка данных для поиска
	private void loadSearchParameters() {
		ObservableList<String> searchParameters = FXCollections.observableArrayList("Поиск по ID", "Поиск по клиенту",
				"Поиск по телефону");
		searchParametrComboBox.setItems(searchParameters);
	}

	// Загрузка данных для статистики
	private void loadparameterStatistic() {
		ObservableList<String> searchParametersStatistic = FXCollections.observableArrayList(
				"Количество выполненных заявок", "Среднее время выполнения заявки",
				"Статистика по типам неисправности");
		parameterStatisticComboBox.setItems(searchParametersStatistic);
	}

	// Отображение деталей заявки
	private void showRequestDetails(Request request) {
		if (request != null) {
			problemTextArea.setText(request.getProblemDescription());
			RequestStatusComboBox.setValue(request.getStatus());
			commentTextArea.setText(request.getComments());
			workerComboBox.setValue(request.getWorkerName());
		} else {
			RequestStatusComboBox.setValue(null);
			workerComboBox.setValue(null);
			problemTextArea.setText("");
			commentTextArea.setText("");
		}
	}

	// Скрытие элементов управления для редактирования заявки
	private void hideEditControls() {
		problemTextArea.setVisible(false);
		RequestStatusComboBox.setVisible(false);
		commentTextArea.setVisible(false);
		workerComboBox.setVisible(false);
		saveRequestButton.setVisible(false);
		commentLabel.setVisible(false);
		problemLabel.setVisible(false);
		statusRequestLabel.setVisible(false);
		workerLabel.setVisible(false);
	}

	// Показ элементов управления для редактирования заявки
	private void showEditControls() {
		problemTextArea.setVisible(true);
		RequestStatusComboBox.setVisible(true);
		commentTextArea.setVisible(true);
		workerComboBox.setVisible(true);
		saveRequestButton.setVisible(true);
		commentLabel.setVisible(true);
		problemLabel.setVisible(true);
		statusRequestLabel.setVisible(true);
		workerLabel.setVisible(true);
	}

	// Удаление заявки из базы данных
	private void deleteRequestFromDatabase(int requestId) throws SQLException {
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement()) {
			String query = String.format("DELETE FROM requests WHERE request_id=%d", requestId);
			statement.executeUpdate(query);
		}
	}

	// Обработчик события нажатия на кнопку "Удалить заявку"
	@FXML
	public void deleteRequestButtonOnClick(MouseEvent event) {
		Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
		if (selectedRequest == null) {
			showAlert("Ошибка", "Заявка не выбрана.");
			return;
		}

		try {
			deleteRequestFromDatabase(selectedRequest.getRequestId());
			List<Request> requestList = DatabaseHandler.getRequests();
			ObservableList<Request> observableRequestList = FXCollections.observableArrayList(requestList);
			requestTableView.setItems(observableRequestList);
			showAlert("Успех", "Заявка успешно удалена.");
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Ошибка", "Ошибка при удалении заявки.");
		}
	}

	// Обработчик события нажатия на кнопку "Редактировать заявку"
	@FXML
	public void editRequestButtonOnClick(MouseEvent event) {
		boolean areControlsVisible = problemTextArea.isVisible();

		if (!areControlsVisible) {
			showEditControls();
			Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
			if (selectedRequest != null) {
				showRequestDetails(selectedRequest);
			}
		} else {
			hideEditControls();
		}
	}

	// Обновление заявки в базе данных
	private void updateRequestInDatabase(Request request) throws SQLException {
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement()) {

			String query;
			if ("Завершена".equals(request.getStatus())) {
				String selectQuery = String.format("SELECT create_at FROM requests WHERE request_id=%d",
						request.getRequestId());
				try (ResultSet resultSet = statement.executeQuery(selectQuery)) {
					if (resultSet.next()) {
						Timestamp createAt = resultSet.getTimestamp("create_at");
						Timestamp now = new Timestamp(System.currentTimeMillis());
						long milliseconds = now.getTime() - createAt.getTime();
						long hours = milliseconds / (1000 * 60 * 60);

						query = String.format(
								"UPDATE requests SET problem_description='%s', status='%s', comments='%s', worker_id=%d, completion_time='%d часов' WHERE request_id=%d",
								request.getProblemDescription(), request.getStatus(), request.getComments(),
								request.getWorkerId(), hours, request.getRequestId());
					} else {
						throw new SQLException("Request not found with id: " + request.getRequestId());
					}
				}
			} else {
				query = String.format(
						"UPDATE requests SET problem_description='%s', status='%s', comments='%s', worker_id=%d WHERE request_id=%d",
						request.getProblemDescription(), request.getStatus(), request.getComments(),
						request.getWorkerId(), request.getRequestId());
			}

			statement.executeUpdate(query);
		}
	}

	// Обработчик события нажатия на кнопку "Сохранить заявку"
	@FXML
	public void saveRequestButtonOnClick(MouseEvent event) {
		Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
		if (selectedRequest == null) {
			showAlert("Ошибка", "Заявка не выбрана.");
			return;
		}

		selectedRequest.setProblemDescription(problemTextArea.getText());
		selectedRequest.setStatus(RequestStatusComboBox.getValue());
		selectedRequest.setComments(commentTextArea.getText());
		String workerName = workerComboBox.getValue();
		Integer workerId = workerIdMap.get(workerName);

		if (workerId != null) {
			selectedRequest.setWorkerId(workerId);
		} else {
			showAlert("Ошибка", "Работник не найден.");
			return;
		}

		try {
			updateRequestInDatabase(selectedRequest);
			List<Request> requestList = DatabaseHandler.getRequests();
			ObservableList<Request> observableRequestList = FXCollections.observableArrayList(requestList);
			requestTableView.setItems(observableRequestList);
			showAlert("Успех", "Заявка успешно обновлена.");
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Ошибка", "Ошибка при обновлении заявки.");
		}
	}

	// Обработчик события нажатия на кнопку "Добавить заявку"
	@FXML
	public void addRequestButtonOnClick(MouseEvent event) {
		loadRequestForm();
	}

	private void loadRequestForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateRequestForm.fxml"));
			Parent root = loader.load();

			// Передача MainFormController в CreateRequestFormController
			CreateRequestFormController createRequestController = loader.getController();
			createRequestController.setMainFormController(this);

			Stage newStage = new Stage();
			newStage.setScene(new Scene(root));
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(requestTableView.getScene().getWindow());
			newStage.setTitle("Создание заявки");
			newStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Ошибка загрузки", "Не удалось загрузить интерфейс создания заявки");
		}
	}

	// Обработчик события нажатия на кнопку "Поиск по заявке"
	@FXML
	public void searchRequestByParametrOnClick(MouseEvent event) {
		String searchParameter = searchParametrComboBox.getValue();
		String searchText = parametrTextField.getText();

		try {
			List<Request> matchingRequests;
			if (searchText == null || searchText.isEmpty()) {
				matchingRequests = DatabaseHandler.getRequests();
			} else {
				matchingRequests = searchRequests(searchParameter, searchText);
			}
			ObservableList<Request> observableRequestList = FXCollections.observableArrayList(matchingRequests);
			requestTableView.setItems(observableRequestList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Поиск заявок по параметру
	private List<Request> searchRequests(String searchParameter, String searchText) throws SQLException {
		List<Request> matchingRequests = new ArrayList<>();

		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement()) {

			String query = "";
			List<Integer> clientIds = new ArrayList<>();
			switch (searchParameter) {
			case "Поиск по ID":
				query = "SELECT * FROM requests WHERE request_id=" + searchText;
				break;
			case "Поиск по клиенту":
				query = "SELECT client_id FROM clients WHERE client_name LIKE '%" + searchText + "%'";
				try (ResultSet resultSet = statement.executeQuery(query)) {
					while (resultSet.next()) {
						clientIds.add(resultSet.getInt("client_id"));
					}
				}
				if (!clientIds.isEmpty()) {
					String clientIdsString = clientIds.stream().map(String::valueOf).collect(Collectors.joining(","));
					query = "SELECT * FROM requests WHERE client_id IN (" + clientIdsString + ")";
				} else {
					return matchingRequests;
				}
				break;
			case "Поиск по телефону":
				query = "SELECT client_id FROM clients WHERE phone_number LIKE '%" + searchText + "%'";
				try (ResultSet resultSet = statement.executeQuery(query)) {
					while (resultSet.next()) {
						clientIds.add(resultSet.getInt("client_id"));
					}
				}
				if (!clientIds.isEmpty()) {
					String clientIdsString = clientIds.stream().map(String::valueOf).collect(Collectors.joining(","));
					query = "SELECT * FROM requests WHERE client_id IN (" + clientIdsString + ")";
				} else {
					return matchingRequests;
				}
				break;
			default:
				return matchingRequests;
			}

			try (ResultSet resultSet = statement.executeQuery(query)) {
				while (resultSet.next()) {
					Request request = new Request();
					request.setRequestId(resultSet.getInt("request_id"));
					request.setDateAdded(resultSet.getDate("date_added"));
					request.setModelId(resultSet.getInt("model_id"));
					request.setProblemDescription(resultSet.getString("problem_description"));
					int clientId = resultSet.getInt("client_id");
					try (Statement clientStatement = connection.createStatement();
							ResultSet clientResultSet = clientStatement.executeQuery(
									"SELECT client_name, phone_number FROM clients WHERE client_id=" + clientId)) {
						if (clientResultSet.next()) {
							request.setClientName(clientResultSet.getString("client_name"));
							request.setPhoneNumber(clientResultSet.getString("phone_number"));
						}
					}
					request.setWorkerId(resultSet.getInt("worker_id"));
					request.setStatus(resultSet.getString("status"));
					request.setComments(resultSet.getString("comments"));
					request.setCompletionTime(resultSet.getString("completion_time"));
					int modelId = resultSet.getInt("model_id");
					try (Statement modelStatement = connection.createStatement();
							ResultSet modelResultSet = modelStatement.executeQuery(
									"SELECT model_name, equipment_type_id FROM equipment_models WHERE model_id="
											+ modelId)) {
						if (modelResultSet.next()) {
							request.setModelName(modelResultSet.getString("model_name"));
							int equipmentTypeId = modelResultSet.getInt("equipment_type_id");
							try (Statement typeStatement = connection.createStatement();
									ResultSet typeResultSet = typeStatement.executeQuery(
											"SELECT type_name FROM equipment_types WHERE equipment_type_id="
													+ equipmentTypeId)) {
								if (typeResultSet.next()) {
									request.setTypeName(typeResultSet.getString("type_name"));
								}
							}
						}
					}
					matchingRequests.add(request);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return matchingRequests;
	}

	// Отображение количества выполненных заявок
	private void displayCompletedRequestsCount() {
		String query = "SELECT COUNT(*) AS count FROM requests WHERE status = 'Завершена'";
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				int count = resultSet.getInt("count");
				statisticTextArea.setText("Количество выполненных заявок = " + count);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Отображение среднего времени выполнения заявки
	private void displayAverageCompletionTime() {
		String query = "SELECT AVG(completion_time) AS average_time FROM requests WHERE status = 'Завершена'";
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				double averageTime = resultSet.getDouble("average_time");
				statisticTextArea.setText("Среднее время выполнения заявки = " + averageTime + " часов");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Отображение статистики по типам неисправности
	private void displayProblemTypeStatistics() {
		String query = "SELECT problem_description, COUNT(*) AS count FROM requests GROUP BY problem_description";
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			StringBuilder statistics = new StringBuilder();
			while (resultSet.next()) {
				String problemDescription = resultSet.getString("problem_description");
				int count = resultSet.getInt("count");
				statistics.append(problemDescription).append(": ").append(count).append("\n");
			}
			statisticTextArea.setText(statistics.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Вывод сообщения в диалоговом окне
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}