package renamefield.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.FieldDeclaration;

import renamefield.projectPath.SaveData;

public class HistoryCount {
	static List<SaveData> allDataSave = new ArrayList<>();// 保存所有数据
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();// 所有的field
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();
	static List<String> beforeField = new ArrayList<>();
	static List<String> afterField = new ArrayList<>();
	static List<String> afterType = new ArrayList<>();
	static List<String> projectJavaList = new ArrayList<>();
	public static int namePartsPtr = -1;
	static String commitBefore = "";
	static String javaPathBefore = "";
	static Map<String, Integer> historyAdd = new HashMap<>();// key:token value:
	static Map<String, Integer> historyRemove = new HashMap<>();// token-token
	static Map<String, String> historyReplace = new HashMap<>();// remove 
	static int historyCount = 0;
	static int rmCount = 0;
    static int totalCommit=0;
    static int countAll=0;
	public static void main(String[] args) throws InterruptedException {
		String saveCSV = "../dataset/Commit.csv";
		String csvPath = "../dataset/test.csv";
		readFile(csvPath);
		String dataBeforePath = "";
		String tempCommitString="";
		for (int i = commitId.size() - 1; i >= 0; i--) {
			if(tempCommitString.equals(commitId.get(i))) {
				totalCommit+=1;
			}else {
				countAll+=totalCommit;
				totalCommit=0;
				tempCommitString=commitId.get(i);
			}
		}
	}
	
	public static void readFile(String scr) {

		File csv = new File(scr); // CSV文件路径
		if (!csv.exists()) {
			System.out.println("csv not find");
		} else {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(csv));
				String line = null;
				int index = 0;
				try {
					br.readLine();
					while ((line = br.readLine()) != null) {
						String item[] = line.split(",");
						projectName.add(item[0]);
						commitId.add(item[1]);
						beforeField.add(item[2]);
						afterField.add(item[3]);
						afterType.add(item[4]);
						classPath.add(item[5]);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
