package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private TableColumn<Request, Date> startDateColumn;
	@FXML
	private TableColumn<Request, String> computerTechTypeColumn;
	@FXML
	private TableColumn<Request, String> computerTechModelColumn;
	@FXML
	private TableColumn<Request, String> problemColumn;
	@FXML
	private TableColumn<Request, String> clientNameColumn;
	@FXML
	private TableColumn<Request, String> clientPhoneColumn;
	@FXML
	private TableColumn<Request, String> statusColumn;
	@FXML
	private Button deleteRequestButton;
	@FXML
	private Button editRequestButton;
	@FXML
	private Button addRequestButton;
	@FXML
	private ComboBox<String> requestStatusComboBox;
	@FXML
	private TextArea problemTextArea;
	@FXML
	private ComboBox<String> masterComboBox;
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
	private Label masterLabel;

	// Мапа для хранения ID мастеров по имени
	private Map<String, Integer> masterIdMap = new HashMap<>();

	public Integer userid;

	public void setUserID(String userID) {
		try {
			userid = Integer.parseInt(userID);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			userid = null;
		}
	}

	public Integer getCurrentUserId() {
		return userid;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Установка значений для колонок
		requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		computerTechTypeColumn.setCellValueFactory(new PropertyValueFactory<>("computerTechType"));
		computerTechModelColumn.setCellValueFactory(new PropertyValueFactory<>("computerTechModel"));
		problemColumn.setCellValueFactory(new PropertyValueFactory<>("problemDescription"));
		clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
		clientPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));

		try {
			// Загрузка данных из базы данных
			loadRequests();

			// Загрузка данных для ComboBox'ов
			loadMasters();
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

	// Загрузка данных о мастерах из базы данных
	private void loadMasters() throws SQLException {
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(
						"SELECT Masters.masterID, Users.fio AS masterName FROM Masters JOIN Users ON Masters.userID = Users.userID")) {

			ObservableList<String> masters = FXCollections.observableArrayList();
			while (resultSet.next()) {
				int masterId = resultSet.getInt("masterID");
				String masterName = resultSet.getString("masterName");
				masters.add(masterName);
				masterIdMap.put(masterName, masterId);
			}

			masterComboBox.getItems().clear();
			masterComboBox.setItems(masters);
		}
	}

	// Загрузка данных о заявках
	public void loadRequests() throws SQLException {
		List<Request> requests = DatabaseHandler.getRequests();
		ObservableList<Request> observableRequestList = FXCollections.observableArrayList(requests);
		requestTableView.setItems(observableRequestList);
	}

	// Загрузка данных о статусах заявок
	private void loadRequestStatuses() {
		ObservableList<String> statuses = FXCollections.observableArrayList("Новая заявка", "В процессе ремонта",
				"Готова к выдаче");
		requestStatusComboBox.setItems(statuses);
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
			requestStatusComboBox.setValue(request.getRequestStatus());
			commentTextArea.setText(request.getRepairParts());
			masterComboBox.setValue(request.getMasterName());
		} else {
			requestStatusComboBox.setValue(null);
			masterComboBox.setValue(null);
			problemTextArea.setText("");
			commentTextArea.setText("");
		}
	}

	// Скрытие элементов управления для редактирования заявки
	private void hideEditControls() {
		problemTextArea.setVisible(false);
		requestStatusComboBox.setVisible(false);
		commentTextArea.setVisible(false);
		masterComboBox.setVisible(false);
		saveRequestButton.setVisible(false);
		commentLabel.setVisible(false);
		problemLabel.setVisible(false);
		statusRequestLabel.setVisible(false);
		masterLabel.setVisible(false);
	}

	// Показ элементов управления для редактирования заявки
	private void showEditControls() {
		problemTextArea.setVisible(true);
		requestStatusComboBox.setVisible(true);
		commentTextArea.setVisible(true);
		masterComboBox.setVisible(true);
		saveRequestButton.setVisible(true);
		commentLabel.setVisible(true);
		problemLabel.setVisible(true);
		statusRequestLabel.setVisible(true);
		masterLabel.setVisible(true);
	}

	// Удаление заявки из базы данных
	private void deleteRequestFromDatabase(int requestId) throws SQLException {
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement()) {
			String query = String.format("DELETE FROM Requests WHERE requestID=%d", requestId);
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
			loadRequests();
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
			String query = String.format(
					"UPDATE Requests SET problemDescription='%s', requestStatus='%s', repairParts='%s', masterID=%d WHERE requestID=%d",
					request.getProblemDescription(), request.getRequestStatus(), request.getRepairParts(),
					request.getMasterId(), request.getRequestId());
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
		selectedRequest.setRequestStatus(requestStatusComboBox.getValue());
		selectedRequest.setRepairParts(commentTextArea.getText());
		String masterName = masterComboBox.getValue();
		Integer masterId = masterIdMap.get(masterName);

		if (masterId != null) {
			selectedRequest.setMasterId(masterId);
		} else {
			showAlert("Ошибка", "Мастер не найден.");
			return;
		}

		try {
			updateRequestInDatabase(selectedRequest);
			loadRequests();
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
	public void searchRequestByParameterOnClick(MouseEvent event) {
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

	private List<Request> searchRequests(String searchParameter, String searchText) throws SQLException {
		List<Request> matchingRequests = new ArrayList<>();
		PreparedStatement stmt;
		ResultSet resultSet;

		try (Connection connection = DatabaseHandler.getConnection()) {
			switch (searchParameter) {
			case "Поиск по ID":
				stmt = connection.prepareStatement("SELECT r.*, u.fio AS clientName, u.phone AS clientPhone "
						+ "FROM Requests r " + "JOIN Clients c ON r.clientID = c.clientID "
						+ "JOIN Users u ON c.userID = u.userID " + "WHERE r.requestID = ?");
				stmt.setInt(1, Integer.parseInt(searchText));
				resultSet = stmt.executeQuery();
				matchingRequests = getRequestsFromResultSet(resultSet);
				resultSet.close();
				stmt.close();
				break;
			case "Поиск по клиенту":
			case "Поиск по телефону":
				String field = searchParameter.equals("Поиск по клиенту") ? "fio" : "phone";
				stmt = connection.prepareStatement("SELECT clientID FROM Clients "
						+ "JOIN Users ON Clients.userID = Users.userID " + "WHERE Users." + field + " LIKE ?");
				stmt.setString(1, "%" + searchText + "%");
				resultSet = stmt.executeQuery();
				List<Integer> clientIds = new ArrayList<>();
				while (resultSet.next()) {
					clientIds.add(resultSet.getInt("clientID"));
				}
				resultSet.close();
				stmt.close();
				if (!clientIds.isEmpty()) {
					String clientIdsString = clientIds.stream().map(String::valueOf).collect(Collectors.joining(","));
					stmt = connection.prepareStatement("SELECT r.*, u.fio AS clientName, u.phone AS clientPhone "
							+ "FROM Requests r " + "JOIN Clients c ON r.clientID = c.clientID "
							+ "JOIN Users u ON c.userID = u.userID " + "WHERE r.clientID IN (" + clientIdsString + ")");
					resultSet = stmt.executeQuery();
					matchingRequests = getRequestsFromResultSet(resultSet);
					resultSet.close();
					stmt.close();
				}
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return matchingRequests;
	}

	private List<Request> getRequestsFromResultSet(ResultSet resultSet) throws SQLException {
		List<Request> requests = new ArrayList<>();
		while (resultSet.next()) {
			Request request = new Request(resultSet.getInt("requestID"), resultSet.getDate("startDate"),
					resultSet.getString("computerTechType"), resultSet.getString("computerTechModel"),
					resultSet.getString("problemDescription"), resultSet.getInt("clientID"),
					resultSet.getObject("masterID", Integer.class), resultSet.getString("requestStatus"),
					resultSet.getString("repairParts"), resultSet.getDate("completionDate"), null,
					resultSet.getString("clientName"), resultSet.getString("clientPhone"));
			requests.add(request);
		}
		return requests;
	}

	// Отображение количества выполненных заявок
	private void displayCompletedRequestsCount() {
		String query = "SELECT COUNT(*) AS count FROM requests WHERE requestStatus = 'Готова к выдаче'";
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
		String query = "SELECT AVG(DATEDIFF(completionDate, startDate)) AS average_days FROM requests WHERE requestStatus = 'Готова к выдаче'";
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				Integer averageDays = resultSet.getInt("average_days");
				statisticTextArea.setText("Среднее время выполнения заявки = " + averageDays + " дней");
			}

		} catch (Exception e) {
			e.printStackTrace();
			statisticTextArea.setText("Ошибка при вычислении среднего времени выполнения заявок.");
		}
	}

	// Отображение статистики по типам неисправности
	private void displayProblemTypeStatistics() {
		String query = "SELECT problemDescription, COUNT(*) AS count FROM requests GROUP BY problemDescription";
		try (Connection connection = DatabaseHandler.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			StringBuilder statistics = new StringBuilder();
			while (resultSet.next()) {
				String problemDescription = resultSet.getString("problemDescription");
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