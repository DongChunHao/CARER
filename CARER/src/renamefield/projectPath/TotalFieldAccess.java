package renamefield.projectPath;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TotalFieldAccess {
	static List<SaveData> allDataSave = new ArrayList<>();// 保存所有数据
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();// 所有的field
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();// 项目名
	static List<String> beforeField = new ArrayList<>();
	static List<String> afterField = new ArrayList<>();

	static List<String> saveClassName = new ArrayList<>();
	static List<String> saveUpFieldName = new ArrayList<>();
	static List<String> saveAfterJavadoc = new ArrayList<>();
	static List<String> saveafterModifier = new ArrayList<>();
	static List<String> saveafterType = new ArrayList<>();
	static List<String> savebeforeName = new ArrayList<>();
	static List<String> saveExpression = new ArrayList<>();
	static List<String> saveafterName = new ArrayList<>();
	static List<String> saveDownFieldName = new ArrayList<>();
	static List<String> saveSameType = new ArrayList<>();

	static String reachFile = "";
	static List<TypeDeclaration> typeDeclarations = new ArrayList<TypeDeclaration>();
	static List<TypeDeclaration> methodDeclarations = new ArrayList<TypeDeclaration>();
	static List<String> projectJavaList = new ArrayList<>();
	static List<String> projectJavaListBefore = new ArrayList<>();
	static List<FieldAccess> fieldAccess = new ArrayList<FieldAccess>();

	public static void main(String[] args) {
		int sums = 0;
		String saveCSV = "D:\\BIT_Report\\testRule\\FieldAccessCount.csv";
		String csvPath = "D:\\BIT_Report\\testRule\\test.csv";
		readFile(csvPath);
		for (int i = 0; i < commitId.size(); i++) {
			boolean flag = false;
			String projectname = "D:\\AllProject\\dataset\\" + projectName.get(i);
//			  System.out.println("project:"+projectname);
			String cmd = "cmd /c D: && cd " + projectname + " " + "&& git reset --hard" + " " + commitId.get(i);
			Runtime run = Runtime.getRuntime();
			try {
				Process process = run.exec(cmd);
//		            process.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File fileAfter = new File(classPath.get(i));// 读取文件
			if (fileAfter.exists()) {
				CompilationUnit cu = getCompilationUnit(classPath.get(i));
				if (!fieldAccess.isEmpty()) {
					fieldAccess.clear();
				}
				getFieldAccess(cu, fieldAccess);
				for (int j = 0; j < fieldAccess.size(); j++) {
					if (fieldAccess.get(j).toString().contains(afterField.get(i))) {
						sums += 1;
						SaveData datasData0 = new SaveData();
						datasData0.setProjectNameString(projectName.get(i));
						datasData0.setCommitId(commitId.get(i));
						datasData0.setOldRe(beforeField.get(i));
						datasData0.setNewRe(afterField.get(i));
						datasData0.setType(saveafterType.get(i));
						datasData0.setClassPath(classPath.get(i));
						allDataSave.add(datasData0);
					}
				}
			}
		}
		try {
			Array2CSV(allDataSave, saveCSV);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("sum:" + sums);
	}

	public static void Array2CSV(List<SaveData> data, String path) throws IOException {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));

			for (SaveData dt : data) {
				String line = dt.getProjectNameString() + "," + dt.getCommitId() + "," + dt.getOldRe() + ","
						+ dt.getNewRe() + "," + dt.getType() + "," + dt.getClassPath();
				out.write(line + "\t\n");

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
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
						beforeField.add(item[2]);// 获取：前边的Field名？？
						afterField.add(item[3]);
						saveafterType.add(item[4]);
						classPath.add(item[5].replace(".", "\\"));
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

	// 获取所有文件路径
	private static void func(File file) {
		File[] fs = file.listFiles();
		if (fs == null || fs.length == 0) {
			return;
		}
		for (File f : fs) {
			if (f.isDirectory()) {
				func(f);
			}
			if (f.isFile()) {
				if (f.getName().endsWith(".java")) {// 判断名字
					projectJavaList.add(f.toString().replace(".java", ""));
				}
			}
		}
	}

	// 返回解析.java文件
	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ASTParser astParser = ASTParser.newParser(AST.JLS17);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(new String(input).toCharArray());
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		return unit;
	}

	private static void getFieldAccess(ASTNode cuu, final List<FieldAccess> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldAccess node) {
				types.add(node);
				return true;
			}
		});
	}
}
