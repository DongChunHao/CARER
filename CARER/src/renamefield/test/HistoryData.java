package renamefield.test;

import java.util.Map;

public class HistoryData {
	private String beforeField;
	private String afterField;
	private boolean isChanged;
	private int conventions;
    private Map<String, Integer> historyAdd;
    private Map<String, Integer> historyRemove;
    private Map<String, String> historyReplace;
    private Map<String, String> historyField;   
    
	public Map<String, String> getHistoryField() {
		return historyField;
	}
	public void setHistoryField(Map<String, String> historyField) {
		this.historyField = historyField;
	}
	public int getConventions() {
		return conventions;
	}
	public void setConventions(int conventions) {
		this.conventions = conventions;
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
	public boolean isChanged() {
		return isChanged;
	}
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	public Map<String, Integer> getHistoryAdd() {
		return historyAdd;
	}
	public void setHistoryAdd(Map<String, Integer> historyAdd) {
		this.historyAdd = historyAdd;
	}
	public Map<String, Integer> getHistoryRemove() {
		return historyRemove;
	}
	public void setHistoryRemove(Map<String, Integer> historyRemove) {
		this.historyRemove = historyRemove;
	}
	public Map<String, String> getHistoryReplace() {
		return historyReplace;
	}
	public void setHistoryReplace(Map<String, String> historyReplace) {
		this.historyReplace = historyReplace;
	}
    
}
