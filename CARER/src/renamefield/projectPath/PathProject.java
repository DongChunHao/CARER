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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class PathProject {
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> preNewList = new ArrayList<>();
	static List<String> preOldList = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();

	static List<String> projectList = new ArrayList<String>();
	static List<RenameData> renameDatas = new ArrayList<>();
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();

	public static void main(String[] args) {
		String filePathString = "";
		String csvPath = "D:\\BIT_Report\\真实data\\StaticFinalFilterData.csv";
		String saveCSV = "D:\\BIT_Report\\真实data\\test_NotStaticTest.csv";
		readFile(csvPath);
		// filePath:D:\AllProject\dataset\SonarSource@sonarqube\sonar-home\src\test\java\org\sonar\home\cache\FileCacheTest.java
		for (int n = 0; n < commitId.size(); n++) {
			String projectname = "D:\\AllProject\\dataset\\" + projectName.get(n);
			String projectNameString = projectName.get(n);
			String cmd = "cmd /c D: && cd " + projectname + " " + "&& git reset --hard" + " " + commitId.get(n);
			Runtime run = Runtime.getRuntime();
			try {
				Process process = run.exec(cmd);
//				Thread.sleep(2000);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!projectList.isEmpty()) {
				projectList.clear();
			}
			File filename = new File(projectname);
			func(filename);
			for (int i = 0; i < projectList.size(); i++) {
				if (projectList.get(i).endsWith(classPath.get(n))) {
					filePathString = projectList.get(i) + ".java";
					File fileAfter = new File(filePathString);
					if (fileAfter.exists()) {
						CompilationUnit cu = getCompilationUnit(filePathString);
						if (!fieldDeclarations.isEmpty()) {
							fieldDeclarations.clear();
						}
						getype(cu, fieldDeclarations);
						for (int e = 0; e < fieldDeclarations.size(); e++) {
							FieldDeclaration fd = fieldDeclarations.get(e);
							VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
							String fieldName1 = vdf.getName().getIdentifier();// 新的field
							if (fieldName1.equals(preNewList.get(n)) || fieldName1.equals(preOldList.get(n))) {
								System.out.println("filePath:" + filePathString);
								RenameData rData = new RenameData();
								rData.setProjectName(projectName.get(n));
								rData.setCommit(commitId.get(n));
								rData.setOldFieldName(preOldList.get(n));
								rData.setNewFieldName(preNewList.get(n));
								rData.setPath(filePathString);
								renameDatas.add(rData);
							}
						}
					}
				}

			}
		}
		try {
			Array2CSV(renameDatas, saveCSV);
		} catch (IOException e) {
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
//						projectName.add(item[0]);
//						commitId.add(item[1]);
//						preOldList.add(item[2]);// 获取：前边的Field名？？
//						preNewList.add(item[3]);
//						classPath.add(item[6].replace(".", "\\"));

						projectName.add(item[0]);
						commitId.add(item[1]);
						preOldList.add(item[2]);// 获取：前边的Field名？？
						preNewList.add(item[3]);
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
					projectList.add(f.toString().replace(".java", ""));
				}
			}
		}
	}

	public static void Array2CSV(List<RenameData> data, String path) throws IOException {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));

			for (RenameData dt : data) {
				String line = dt.getProjectName() + "," + dt.getCommit() + "," + dt.getOldFieldName() + ","
						+ dt.getNewFieldName() + "," + dt.getPath();
//					out.write(line + "\t\n");
				out.write(line + "\t\n");
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		ASTParser astParser = ASTParser.newParser(AST.JLS18);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(new String(input).toCharArray());
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		return unit;
	}

	private static void getype(ASTNode cuu, final List<FieldDeclaration> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldDeclaration node) {
				types.add(node);
				return true;
			}
		});
	}
}
