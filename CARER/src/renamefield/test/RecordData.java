package renamefield.test;

public class RecordData {
	private String beforeField;
	private String afterField;
	private String commitId;
    private String classPath;
    private String projectPath;
    
	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getBeforeField() {
		return beforeField;
	}

	public void setBeforeField(String beforeField) {
		this.beforeField = beforeField;
	}

	public String getAfterField() {
		return afterField;
	}

	public void setAfterField(String afterField) {
		this.afterField = afterField;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
}
