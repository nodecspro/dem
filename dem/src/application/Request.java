package application;

import java.util.Date;

public class Request {
	private int requestId;
	private Date startDate;
	private String computerTechType;
	private String computerTechModel;
	private String problemDescription;
	private int clientId;
	private Integer masterId;
	private String requestStatus;
	private String repairParts;
	private Date completionDate;
	
	private String masterName;
	private String clientName;
	private String clientPhone;

	public Request() {
		super();
	}

	public Request(int requestId, Date startDate, String computerTechType, String computerTechModel,
			String problemDescription, int clientId, Integer masterId, String requestStatus, String repairParts,
			Date completionDate, String masterName, String clientName, String clientPhone) {
		this.requestId = requestId;
		this.startDate = startDate;
		this.computerTechType = computerTechType;
		this.computerTechModel = computerTechModel;
		this.problemDescription = problemDescription;
		this.clientId = clientId;
		this.masterId = masterId;
		this.requestStatus = requestStatus;
		this.repairParts = repairParts;
		this.completionDate = completionDate;
		this.masterName = masterName;
		this.clientName = clientName;
		this.clientPhone = clientPhone;
	}

	// Getters and setters for all fields

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getComputerTechType() {
		return computerTechType;
	}

	public void setComputerTechType(String computerTechType) {
		this.computerTechType = computerTechType;
	}

	public String getComputerTechModel() {
		return computerTechModel;
	}

	public void setComputerTechModel(String computerTechModel) {
		this.computerTechModel = computerTechModel;
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

	public Integer getMasterId() {
		return masterId;
	}

	public void setMasterId(Integer masterId) {
		this.masterId = masterId;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getRepairParts() {
		return repairParts;
	}

	public void setRepairParts(String repairParts) {
		this.repairParts = repairParts;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	@Override
	public String toString() {
		return "Request{" + "requestId=" + requestId + ", startDate=" + startDate + ", computerTechType='"
				+ computerTechType + '\'' + ", computerTechModel='" + computerTechModel + '\''
				+ ", problemDescription='" + problemDescription + '\'' + ", clientId=" + clientId + ", masterId="
				+ masterId + ", requestStatus='" + requestStatus + '\'' + ", repairParts='" + repairParts + '\''
				+ ", completionDate=" + completionDate + ", masterName='" + masterName + '\'' + ", clientName='"
				+ clientName + '\'' + ", clientPhone='" + clientPhone + '\'' + '}';
	}
}