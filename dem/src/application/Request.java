package application;

import java.util.Date;

public class Request {
	private int requestId;
	private Date dateAdded;
	private int modelId;
	private String problemDescription;
	private int clientId;
	private Integer workerId;
	private String status;
	private String comments;
	private Date createdAt;
	private String completionTime;
	private String modelName;
	private String typeName;
	private String clientName;
	private String phoneNumber;
	private String workerName;

	public Request() {
		super();
	}

	public Request(int requestId, Date dateAdded, int modelId, String problemDescription, int clientId,
			Integer workerId, String status, String comments, Date createdAt, String completionTime, String modelName,
			String typeName, String clientName, String phoneNumber, String workerName) {
		this.requestId = requestId;
		this.dateAdded = dateAdded;
		this.modelId = modelId;
		this.problemDescription = problemDescription;
		this.clientId = clientId;
		this.workerId = workerId;
		this.status = status;
		this.comments = comments;
		this.createdAt = createdAt;
		this.completionTime = completionTime;
		this.modelName = modelName;
		this.typeName = typeName;
		this.clientName = clientName;
		this.phoneNumber = phoneNumber;
		this.workerName = workerName;
	}

	// Getters and setters for all fields

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public Integer getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Integer workerId) {
		this.workerId = workerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	@Override
	public String toString() {
		return "Request{" + "requestId=" + requestId + ", dateAdded=" + dateAdded + ", modelId=" + modelId
				+ ", problemDescription='" + problemDescription + '\'' + ", clientId=" + clientId + ", workerId="
				+ workerId + ", status='" + status + '\'' + ", comments='" + comments + '\'' + ", createdAt="
				+ createdAt + ", completionTime='" + completionTime + '\'' + ", modelName='" + modelName + '\''
				+ ", typeName='" + typeName + '\'' + ", clientName='" + clientName + '\'' + ", phoneNumber='"
				+ phoneNumber + '\'' + ", workerName='" + workerName + '\'' + '}';
	}
}