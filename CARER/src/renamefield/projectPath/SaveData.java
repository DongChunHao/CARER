package renamefield.projectPath;

public class SaveData {
	private String commitId;
	private String oldRe;
	private String newRe;
	private String type;
	private String classPath;
	private String projectNameString;
	private String rmName;
	
    public String getProjectNameString() {
		return projectNameString;
	}
	public void setProjectNameString(String projectNameString) {
		this.projectNameString = projectNameString;
	}
	
	public String getCommitId() {
		return commitId;
	}
	public String getRmName() {
		return rmName;
	}
	public void setRmName(String rmName) {
		this.rmName = rmName;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	public String getOldRe() {
		return oldRe;
	}
	public void setOldRe(String oldRe) {
		this.oldRe = oldRe;
	}
	public String getNewRe() {
		return newRe;
	}
	public void setNewRe(String newRe) {
		this.newRe = newRe;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
}
