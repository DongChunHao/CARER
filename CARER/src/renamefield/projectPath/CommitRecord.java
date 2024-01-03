package renamefield.projectPath;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public class CommitRecord {
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
	public static int namePartsPtr = -1;

	public static void main(String[] args) {
		Map<String, String> dataMap = new HashMap<>();
		Map<String, String> tempMap = new HashMap<>();
		int remmondSums = 0;
		int correctSums = 0;
		String saveCSV = "D:\\BIT_Report\\testRule\\Commit.csv";
//		String csvPath = "D:\\BIT_Report\\testRule\\testCommit.csv";
		String csvPath = "D:\\BIT_Report\\testRule\\test.csv";
		readFile(csvPath);
		String project = projectName.get(commitId.size() - 1).toString();
		String beforeCommit = "";
		String newFieldName = "";
		for (int i = commitId.size() - 1; i >= 0; i--) {
			if (project.equals(projectName.get(i))) {
				if (i - 1 >= 0) {// 判断上一条数据存在
					if (!commitId.get(i).toString().equals(commitId.get(i - 1).toString())) {// commit不相同,
																								// tempMap加入dataMap,
																								// tempMap清空，
																								// 本次commit存入tempMap
						dataMap.putAll(tempMap);
						tempMap.clear();
						char[] bf = beforeField.get(i).toCharArray();
						char[][] bfs = spiltFieldToChar(bf, false);
						List<String> bfList = convertCharsToString(bfs);

						Set<Map.Entry<String, String>> entries = dataMap.entrySet();
						for (Entry<String, String> entry : entries) {
							String key = entry.getKey();
							if (key.equals(beforeField.get(i).toString())) {
								newFieldName = entry.getValue();
//								System.out.println("newfield:"+newFieldName);
								if (newFieldName != null) {
									remmondSums += 1;
									if (newFieldName.equals(afterField.get(i).replace("_", "").toLowerCase())) {
										correctSums += 1;
										break;
									}
								}
							} else {
								for (int a = 0; a < bfList.size(); a++) {
									System.out.println("list:" + bfList.get(a));
									if (bfList.get(a).toString().equals(entry.getKey())) {
										remmondSums += 1;
										if (entry.getValue() == null) {// remove token
											bfList.remove(a);
											break;
										} else {// replace token
											bfList.set(a, entry.getValue());
											break;
										}
									}
								}
								newFieldName = bfList.stream().reduce("", (str1, str2) -> str2 + str1);
//								System.out.println("newfield:"+newFieldName);
								if (newFieldName.equals(afterField.get(i).replace("_", "").toLowerCase())) {
									correctSums += 1;
									break;
								}
							}
						}

						deleteToken(beforeField.get(i).toString(), afterField.get(i).toString(), tempMap);

					} else {// 当前这条commit与上条commit相同 只使用commit之前的，本次commit存入tempMap
						char[] bf = beforeField.get(i).toCharArray();
						char[][] bfs = spiltFieldToChar(bf, false);
						List<String> bfList = convertCharsToString(bfs);

						Set<Map.Entry<String, String>> entries = dataMap.entrySet();
						for (Entry<String, String> entry : entries) {
							String key = entry.getKey();
							if (key.equals(beforeField.get(i).toString())) {
								newFieldName = entry.getValue();
//								System.out.println("newFieldName:" + newFieldName);
								if (newFieldName != null) {
									remmondSums += 1;
									if (newFieldName.equals(afterField.get(i).replace("_", "").toLowerCase())) {
										correctSums += 1;
										break;
									}
								}
							} else {// 判断是否单个token需要删除或替换
								for (int a = 0; a < bfList.size(); a++) {
									if (bfList.get(a).toString().equals(entry.getKey())) {
										remmondSums += 1;
										if (entry.getValue() == null) {// remove token
											bfList.remove(a);
											break;
										} else {// replace token
											bfList.set(a, entry.getValue());
										}
									}
								}
							}
							newFieldName = bfList.stream().reduce("", (str1, str2) -> str2 + str1);
//							System.out.println("newfield:"+newFieldName);
							if (newFieldName.equals(afterField.get(i).replace("_", "").toLowerCase())) {
								correctSums += 1;
								break;
							}
						}

						deleteToken(beforeField.get(i).toString(), afterField.get(i).toString(), tempMap);
					}

				} else {// 如果是第一条数据，直接存入temp
					deleteToken(beforeField.get(i).toString(), afterField.get(i).toString(), tempMap);
				}
			} else {
				dataMap.clear();
				tempMap.clear();
				project = projectName.get(i);
				deleteToken(beforeField.get(i).toString(), afterField.get(i).toString(), tempMap);
			}
		}
		System.out.println("remmondSums:" + remmondSums);
		System.out.println("correctSums:" + correctSums);
	}

	public static void deleteToken(String before, String after, Map<String, String> map) {
		char[] bf = before.toCharArray();
		char[][] bfs = spiltFieldToChar(bf, false);
		List<String> bfList = convertCharsToString(bfs);

		char[] af = after.toCharArray();
		char[][] afs = spiltFieldToChar(af, false);
		List<String> afList = convertCharsToString(afs);

		List<String> commonElements = new ArrayList<>();
		for (String element : bfList) {
			if (afList.contains(element)) {
				commonElements.add(element);
			}
		}

		bfList.removeAll(commonElements);
		afList.removeAll(commonElements);

		if (bfList.isEmpty()) {
			// 可能是增加元素或者减少元素
			map.put(before.toString(), after.toString());
		} else {
			if (afList.isEmpty()) {
				String result = bfList.stream().reduce("", (str1, str2) -> str2 + str1);
				map.put(result, null);
			} else {
				String bfString = "";
				String afString = "";
				if (bfList.size() > 1 || afList.size() > 1) {
					map.put(before.toString(), after.toString());
				} else {
					bfString = bfList.stream().reduce("", (str1, str2) -> str2 + str1);
					afString = afList.stream().reduce("", (str1, str2) -> str2 + str1);
					map.put(bfString, afString);
				}
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

	public static char[][] spiltFieldToChar(char[] sourceName, boolean isConstantField) {
		int length = sourceName.length;// userName -8

		if (length == 0) {
			return CharOperation.NO_CHAR_CHAR;
		}
		if (length == 1) {
			System.out.println("1:" + sourceName);
			return new char[][] { CharOperation.toLowerCase(sourceName) };
		}

		char[][] nameParts = new char[length][];
		namePartsPtr = -1;

		int endIndex = length;// 8
		char c = sourceName[length - 1];// e

		final int IS_LOWER_CASE = 1;
		final int IS_UPPER_CASE = 2;
		final int IS_UNDERSCORE = 3;
		final int IS_OTHER = 4;

		int previousCharKind = ScannerHelper.isLowerCase(c) ? IS_LOWER_CASE
				: ScannerHelper.isUpperCase(c) ? IS_UPPER_CASE : c == '_' ? IS_UNDERSCORE : IS_OTHER;// 1

		for (int i = length - 1; i >= 0; i--) {
			c = sourceName[i];// e m a N

			int charKind = ScannerHelper.isLowerCase(c) ? IS_LOWER_CASE
					: ScannerHelper.isUpperCase(c) ? IS_UPPER_CASE : c == '_' ? IS_UNDERSCORE : IS_OTHER;// 1

			switch (charKind) {
			case IS_LOWER_CASE:
				if (previousCharKind == IS_UPPER_CASE) {
					nameParts[++namePartsPtr] = CharOperation.subarray(sourceName, i + 1, endIndex);
					endIndex = i + 1;
				}
				previousCharKind = IS_LOWER_CASE;// 1
				break;
			case IS_UPPER_CASE:
				if (previousCharKind == IS_LOWER_CASE) {
					nameParts[++namePartsPtr] = CharOperation.subarray(sourceName, i, endIndex);
					if (i > 0) {
						char pc = sourceName[i - 1];
						previousCharKind = ScannerHelper.isLowerCase(pc) ? IS_LOWER_CASE
								: ScannerHelper.isUpperCase(pc) ? IS_UPPER_CASE : pc == '_' ? IS_UNDERSCORE : IS_OTHER;
					}
					endIndex = i;
				} else {
					previousCharKind = IS_UPPER_CASE;
				}
				break;
			case IS_UNDERSCORE:
				switch (previousCharKind) {
				case IS_UNDERSCORE:
					if (isConstantField) {
						if (i > 0) {
							char pc = sourceName[i - 1];
							previousCharKind = ScannerHelper.isLowerCase(pc) ? IS_LOWER_CASE
									: ScannerHelper.isUpperCase(pc) ? IS_UPPER_CASE
											: pc == '_' ? IS_UNDERSCORE : IS_OTHER;
						}
						endIndex = i;
					}
					break;
				case IS_LOWER_CASE:
				case IS_UPPER_CASE:
					nameParts[++namePartsPtr] = CharOperation.subarray(sourceName, i + 1, endIndex);
					if (i > 0) {
						char pc = sourceName[i - 1];
						previousCharKind = ScannerHelper.isLowerCase(pc) ? IS_LOWER_CASE
								: ScannerHelper.isUpperCase(pc) ? IS_UPPER_CASE : pc == '_' ? IS_UNDERSCORE : IS_OTHER;
					}
					// Include the '_' also. E.g. My_word -> "My_" and "word".
					endIndex = i + 1;
					break;
				default:
					previousCharKind = IS_UNDERSCORE;
					break;
				}
				break;
			default:
				previousCharKind = IS_OTHER;
				break;
			}
		}
		if (endIndex > 0) {
			nameParts[++namePartsPtr] = CharOperation.subarray(sourceName, 0, endIndex);
		}
		if (namePartsPtr == -1) {
			return new char[][] { sourceName };
		}
		return nameParts;// 可能存在问题
	}

	private static List<String> convertCharsToString(char[][] c) {
		int length = c == null ? 0 : c.length;
		String[] s = new String[length];
		List<String> sList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			if (c[i] == null) {
				break;
			}
			s[i] = String.valueOf(c[i]);
			sList.add(s[i].toLowerCase().replace("_", ""));
		}
		return sList;
	}
}
