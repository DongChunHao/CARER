package all.rule.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

public class IDEATest {
	private static final String[] KNOWN_METHOD_NAME_PREFIXES = { "get", "to", "has", "is" };
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();// 项目名
	static List<String> beforeField = new ArrayList<>();
	static List<String> afterField = new ArrayList<>();
	static List<String> afterType = new ArrayList<>();
	static List<String> projectJavaList = new ArrayList<>();
	static int recBaseType = 0;
	static int recCustomType = 0;
	static int recOtherType = 0;
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();// 所有的field
	static int remmondCount = 0;
	static int rm = 0;
	static int currentTime = 0;
	static List<Long> saveTime = new ArrayList<>();

	public static void main(String[] args) throws InterruptedException {
		long stime1 = System.currentTimeMillis();
		int sum = 0;
		int count = 0;
		String csvPath = "../Dataset/dataset.csv";
		readFile(csvPath);

		for (int i = 0; i < commitId.size(); i++) {
			long stime = System.currentTimeMillis();
			String dataPath = "D:\\BIT_Report\\dataSource\\" + i + "_" + afterField.get(i) + "_" + commitId.get(i)
					+ ".java";
			File folder = new File(dataPath);
			func(folder);
			File fileAfter = new File(dataPath);
			if (fileAfter.exists()) {
				CompilationUnit cu = getCompilationUnit(dataPath);
				if (!fieldDeclarations.isEmpty()) {
					fieldDeclarations.clear();
				}
				getFieldDeclaration(cu, fieldDeclarations);

				for (int t = 0; t < fieldDeclarations.size(); t++) {
					FieldDeclaration fd = fieldDeclarations.get(t);
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
					String fieldName = vdf.getName().getIdentifier();
					if (vdf.getInitializer() != null) {
						Expression initExp = vdf.getInitializer();
						String newFieldString = getBaseNameFromExpression(initExp, false);
						if (newFieldString != null) {
							if (!newFieldString.equals(beforeField.get(i))) {
								remmondCount += 1;
								if (newFieldString.equals(afterField.get(i))) {
								} else {
								}
							}
						} else {
							getDataType(i);
						}
					}
				}
			} else {
				getDataType(i);

			}

			long endTime = System.currentTimeMillis();
//			System.out.println(endTime - stime);
			long tTime = endTime - stime;
			saveTime.add(tTime);
		}
		
		long etime = System.currentTimeMillis();
		saveData();
		System.out.println(etime - stime1);
	}

	public static void saveData() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\BIT_Report\\testRule\\IDEA_Time1.csv"));
            for (int j = 0; j < saveTime.size(); j++) {
                bw.write(saveTime.get(j).toString());
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}	
	
	public static void readFile(String scr) {

		File csv = new File(scr); // CSV文件路径
		if (!csv.exists()) {
//			System.out.println("csv not find");
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

	public static void getDataType(int i) {
		if (afterType.get(i).toLowerCase().equals(afterField.get(i).toLowerCase())) {
			rm += 1;
		} else if (afterType.get(i).toLowerCase().contains(afterField.get(i).toLowerCase())
				&& afterType.get(i).toLowerCase().equals(beforeField.get(i).toLowerCase())) {

			rm += 1;
		}
	}

	private static String getBaseNameFromExpression(Expression assignedExpression, boolean flag) {
		String name = null;
		if (assignedExpression instanceof CastExpression) {
			assignedExpression = ((CastExpression) assignedExpression).getExpression();
		}
		if (assignedExpression instanceof Name) {
			Name simpleNode = (Name) assignedExpression;
			IBinding binding = simpleNode.resolveBinding();
			if (binding instanceof IVariableBinding)
				return binding.getName();
			return ASTNodes.getSimpleNameIdentifier(simpleNode);
		} else if (assignedExpression instanceof MethodInvocation) {
			name = ((MethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof SuperMethodInvocation) {
			name = ((SuperMethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof FieldAccess) {
			return ((FieldAccess) assignedExpression).getName().getIdentifier();
		} else if (flag
				&& (assignedExpression instanceof StringLiteral || assignedExpression instanceof NumberLiteral)) {
			String string = assignedExpression instanceof StringLiteral
					? ((StringLiteral) assignedExpression).getLiteralValue()
					: ((NumberLiteral) assignedExpression).getToken();
			StringBuilder res = new StringBuilder();
			boolean needsUnderscore = false;
			for (char ch : string.toCharArray()) {
				if (Character.isJavaIdentifierPart(ch)) {
					if (res.length() == 0 && !Character.isJavaIdentifierStart(ch) || needsUnderscore) {
						res.append('_');
					}
					res.append(ch);
					needsUnderscore = false;
				} else {
					needsUnderscore = res.length() > 0;
				}
			}
			if (res.length() > 0) {
				return res.toString();
			}
		}
		if (name != null) {
			for (String curr : KNOWN_METHOD_NAME_PREFIXES) {
				if (name.startsWith(curr)) {
					if (name.equals(curr)) {
						return null; // don't suggest 'get' as variable name
					} else if (Character.isUpperCase(name.charAt(curr.length()))) {
						return name.substring(curr.length());// 去除get,is,to的方法名
					}
				}
			}
		}
		return name;
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
		ASTParser astParser = ASTParser.newParser(AST.JLS17);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(new String(input).toCharArray());
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		CompilationUnit unit = (CompilationUnit) (astParser.createAST(null));
		return unit;
	}

	public static void getFieldDeclaration(ASTNode cuu, final List<FieldDeclaration> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldDeclaration node) {
				types.add(node);
				return true;
			}
		});
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
					projectJavaList.add(f.toString().replace(".java", ""));
				}
			}
		}
	}
}
