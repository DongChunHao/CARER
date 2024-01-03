package renamefield.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.NestingKind;
import javax.swing.event.ListDataListener;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.core.BecomeWorkingCopyOperation;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.template.java.NewFieldResolver;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import renamefield.dealField.GenerateNames;
import renamefield.handlers.CompilationJava;
import renamefield.naming.CamelRule;
import renamefield.projectPath.RenameData;
import renamefield.projectPath.SaveData;

public class CARERTest {
	private static final String[] KNOWN_METHOD_NAME_PREFIXES = { "get", "to", "has", "is" };
	static List<SaveData> allDataSave = new ArrayList<>();
	static List<SaveData> rl3 = new ArrayList<>();
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();	
	static List<ReturnStatement> returnStatement = new ArrayList<>();
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();
	static List<String> projectNames = new ArrayList<>();
	static List<String> beforeField = new ArrayList<>();
	static List<String> afterField = new ArrayList<>();
	static List<String> afterType = new ArrayList<>();
	static String reachFile = "";
	static List<TypeDeclaration> typeDeclarations = new ArrayList<TypeDeclaration>();
	static List<TypeDeclaration> methodDeclarations = new ArrayList<TypeDeclaration>();
	static Map<Integer, Integer> repalcePosition = new LinkedHashMap<>();
	static List<Assignment> assignments = new ArrayList<>();
	static List<String> projectJavaList = new ArrayList<>();
	static List<String> projectJavaListBefore = new ArrayList<>();
	static List<FieldAccess> fieldAccess = new ArrayList<FieldAccess>();
	public static int namePartsPtr = -1;
	static List<String> res = new ArrayList();
	static boolean flag2 = false;
	static boolean flag1 = false;
	static int allRemmondCount = 0;
	static int correctCount = 0;
	static int existCount = 0;
	static int top1 = 0;
	static int topAll = 0;
	static List<RecordData> dataList = new ArrayList<>();
	static int remmondCount0 = 0;
	static int remmondCount1 = 0;
	static int remmondCount2 = 0;
	static int remmondCount3 = 0;
	static int remmondCount4 = 0;
	static int remmondCount5 = 0;
	static int remmondCount6 = 0;
	static int remmondCount7 = 0;
	static int remmondCount8 = 0;
	static int remmondCount9 = 0;
	static int remmondCount10 = 0;
	static int remmondCount11 = 0;
	static int remmondCount12 = 0;
	static int rm0 = 0;
	static int rm1 = 0;
	static int rm2 = 0;
	static int rm3 = 0;
	static int rm4 = 0;
	static int rm5 = 0;
	static int rm6 = 0;
	static int rm7 = 0;
	static int rm8 = 0;
	static int rm9 = 0;
	static int rm10 = 0;
	static int rm11 = 0;
	static int rm12 = 0;
	static int init = 0;
	static int initCount = 0;
	static int expSum = 0;
	static int expCount = 0;
	static String commitBefore = "";
	static String javaPathBefore = "";
	static int baseType =0;
	static int customType = 0;
	static int otherType = 0;
	static int recBaseType = 0;
	static int recCustomType = 0;
	static int recOtherType = 0;
	static Map<String, Integer> historyAdd = new LinkedHashMap<>();// key:token value:位置
	static Map<String, Integer> historyRemove = new LinkedHashMap<>();// token-token
	static Map<String, String> historyReplace = new LinkedHashMap<>();// remove 位置
	static Map<String, String> historyField = new LinkedHashMap<>();// before after

	static Map<String, Integer> camelConventionAdd = new LinkedHashMap<>();
	static Map<String, Integer> camelConventionRemove = new LinkedHashMap<>();
	static Map<String, String> camelConventionReplace = new LinkedHashMap<>();

	static Map<String, Integer> constantConventionAdd = new LinkedHashMap<>();
	static Map<String, Integer> constantConventionRemove = new LinkedHashMap<>();
	static Map<String, String> constantConventionReplace = new LinkedHashMap<>();

	static Map<String, Integer> underConventionAdd = new LinkedHashMap<>();
	static Map<String, Integer> underConventionRemove = new LinkedHashMap<>();
	static Map<String, String> underConventionReplace = new LinkedHashMap<>();

	static List<HistoryData> historyRefactoringList = new ArrayList<>();

	static Map<String, Integer> addString = new LinkedHashMap<>();
	static Map<String, Integer> removeString = new LinkedHashMap<>();
	static int fieldTypes = 0;
	static int fFieldConventions = 0;

	static List<Long> timeCarer = new ArrayList(); 
	public static void main(String[] args) throws InterruptedException, NoHeadException, IOException, GitAPIException {
		long stime = System.currentTimeMillis();
		String csvPath = "../dataset/test.csv";

		readFile(csvPath);
		String dataBeforePath = "";
		
		for (int i = commitId.size() - 1; i >= 0; i--) {
		
//		for (int i =0; i <commitId.size(); i++) {
//			if(!projectJavaList.isEmpty()) {
//				projectJavaList.clear();
//			}
//			String projectname = "../dataset/" + projectName.get(i);
//			File folder = new File(projectname);
//			func(folder);
			
//			String gitString = "../dataset/" + projectName.get(i) + "\\.git";
//			File folder = new File(gitString);
//			File[] files = folder.listFiles();
//			if (files != null) {
//				for (File file : files) {
//					if (file.getName().equals("index.lock")) {
//						file.delete();
//						System.out.println("File deleted: " + file.getAbsolutePath());
//					}
//				}
//			}
//			String cmd = "cmd /c D: && cd " + projectname + " " + "&& git reset --hard" + " " + commitId.get(i);
//			Runtime run = Runtime.getRuntime();
//			try {
//				Process process = run.exec(cmd);
//				Thread.sleep(1000);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			boolean flags = GitReoisitory.hasSwitchedToCommit(gitString, commitId.get(i));
//			if (flags == false) {
//				Thread.sleep(1000);
//			}
			if (i - 1 >= 0) {
				dataBeforePath = classPath.get(i);
			}
			
			getFieldFromHistory(i, beforeField.get(i), afterField.get(i), classPath.get(i), dataBeforePath, afterType.get(i));
			if (!res.isEmpty()) {
				long endTime = System.currentTimeMillis();
				timeCarer.add(endTime-stime);
			}
			String dataPath = "D:\\BIT_Report\\dataSource\\" + i + "_" + afterField.get(i) + "_" + commitId.get(i)+ ".java";
			File fileAfter = new File(dataPath);

			if (fileAfter.exists()) {
				CompilationUnit cu = getCompilationUnit(dataPath);
				if (!fieldAccess.isEmpty()) {
					fieldAccess.clear();
				}
				getFieldAccess(cu, fieldAccess);

				if (!fieldDeclarations.isEmpty()) {
					fieldDeclarations.clear();
				}
				getFieldDeclaration(cu, fieldDeclarations);

				for (int t = 0; t < fieldDeclarations.size(); t++) {
					FieldDeclaration fd = fieldDeclarations.get(t);
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
					String fieldName = vdf.getName().getIdentifier();
					if (fieldName.equals(afterField.get(i))) {

						existCount += 1;
						Type fieldType = fd.getType();
						int modifier = fd.getModifiers();

						char[] fieldChar = beforeField.get(i).toString().toCharArray();
						// contant field
						if (modifier == 25 || modifier == 26 || modifier == 24 || modifier == 28) {
							flag1 = true;
						}
						char[][] spiltField = spiltFieldToChar(fieldChar, flag1);
						List<String> fieldStrings = convertCharsToString(spiltField);

						if (spiltField.length == 1) {
							flag2 = true;
						}


						staticFinalNamingRecommend(spiltField, flag1, t, beforeField.get(i).toString(),
								afterField.get(i).toString(), fieldStrings, fd, fieldType, i);// 规则1,2
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							res.clear();
							break;
						}
						
						
						getSpecificCovention(spiltField, fieldStrings, t, beforeField.get(i).toString(),
								afterField.get(i).toString(), flag1, i, fieldType);// 规则3
						if (!res.isEmpty()) {
							res.clear();
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							break;
						}
						
						commonCovention(spiltField, fieldStrings, flag1, t, beforeField.get(i).toString(),
								afterField.get(i).toString(), fieldType);// 规则4
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
							res.clear();
//							timeCarer.add(endTime-stime);
							break;
						}


						
						getNameFromFieldAccess2(cu, i, flag1, beforeField.get(i).toString(),
								afterField.get(i).toString(), vdf, fieldType);// 规则6，fieldAccess
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							res.clear();
							break;
						}
//
						getNameFromFieldAccess21(cu, i, flag1, beforeField.get(i).toString(),
								afterField.get(i).toString(), vdf, fieldType);// 规则6，fieldAccess
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							res.clear();
							break;
						}
						
						getNameFromReturnExpression(cu, beforeField.get(i).toString(), afterField.get(i).toString(), fieldType);// 规则12
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							res.clear();
							break;
						}
//						
						getFieldNameFromType(fieldType, modifier, beforeField.get(i).toString(),
								afterField.get(i).toString(), flag1, fieldStrings);// 规则7
						if (!res.isEmpty()) {
//							long endTime = System.currentTimeMillis();
//							System.out.println(endTime-stime);
//							timeCarer.add(endTime-stime);
							res.clear();
							break;
						}
//						isRule8(t, vdf, fieldStrings, beforeField.get(i).toString(), afterField.get(i));
						if (!res.isEmpty()) {
							res.clear();
							break;
						}					
					}
				}
			} else {
				RecordData recordData = new RecordData();
				recordData.setProjectPath(projectName.get(i)); 
				recordData.setBeforeField(beforeField.get(i));
				recordData.setAfterField(afterField.get(i));
				recordData.setCommitId(commitId.get(i));
				recordData.setClassPath(classPath.get(i));
				dataList.add(recordData);
			}
			flag1 = false;
			flag2 = false;
			res.clear();
		}
		long etime = System.currentTimeMillis();
		System.out.println("测试时间:" + (etime - stime));
		System.out.println("-------------------最终推荐结果----------------------");
		System.out.println("推荐数量：" + remmondCount0 + " " + "rule0推荐正确:" + rm0);
		System.out.println("推荐数量：" + (remmondCount0 + remmondCount1) + " " + "rule1推荐正确:"
				+ (rm0 + rm1));
		System.out.println("推荐数量：" + (remmondCount0 + remmondCount1 + remmondCount2) + " "
				+ "rule1+2推荐正确:" + (rm0 + rm1 + rm2));
		System.out.println(
				"推荐数量：" + (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3)
						+ " " + "rule1+rule2+rule3推荐正确:" + (rm0 + rm1 + rm2 + rm3));
		System.out.println("推荐数量："
				+ (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3 + remmondCount4) + " "
				+ "rule1+2+3+4推荐正确:" + (rm0 + rm1 + rm2 + rm3 + rm4));
		System.out.println("推荐数量："
				+ (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3 + remmondCount4 + remmondCount5) + " "
				+ "rule1+2+3+4+5推荐正确:" + (rm0 + rm1 + rm2 + rm3 + rm4 + rm5));
		System.out.println("推荐数量：" + (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3
						+ remmondCount4 + remmondCount5 + remmondCount6)
				+ " " + "rule1+2+3+4+5+6推荐正确:" + (rm0 + rm1 + rm2 + rm3 + rm4 + rm5 + rm6));
		System.out.println("推荐数量："
				+ (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3 + remmondCount4 + remmondCount5
						+ remmondCount6 + remmondCount7)
				+ " " + "rule1+2+3+4+5+6+7推荐正确:" + (rm0 + rm1 + rm2 + rm3 + rm4 + rm5 + rm6 + rm7));
		System.out.println("推荐数量："
				+ (remmondCount0 + remmondCount1 + remmondCount2 + remmondCount3 + remmondCount4 + remmondCount5
						+ remmondCount6 + remmondCount7 + remmondCount8 + remmondCount11+remmondCount12)
				+ " " + "推荐正确:" + (rm0 + rm1 + rm2 + rm3 + rm4 + rm5 + rm6 + rm7 + rm8 + rm11+rm12));
		System.out.println("推荐数量：" + remmondCount11 + " " + "推荐正确:" + rm11);
		System.out.println("base:"+baseType+" "+ "com:"+customType+" "+ "other:"+otherType);
		System.out.println("Recbase:"+recBaseType+" "+ "Rec:"+recCustomType+" "+ "other:"+ recOtherType);
//		try {
//			Array2CSV(dataList, saveCSV1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		String sa = "../dataset/results.csv";
		try {
			Array2CSV1(rl3, sa);
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveData();
	}


	private static void camelNamingRecommend(char[][] field, String beforeField, String afterField,
			List<String> fieldStrings, int t) {
		String fieldName = "";
		boolean flags = isCamelCase(fieldStrings);
		boolean iftBol = false;
		if (flags == false) {

			if (beforeField.substring(0, 1).equals("_")) {
				for (int a = 0; a < t; a++) {
					if (fieldDeclarations.get(a).toString().substring(0, 1).equals("_")) {
						iftBol = true;
						break;
					}
				}
				if (iftBol == true) {
				} else {
					fieldName = beforeField.substring(1);
					if (!fieldName.equals(beforeField)) {
						remmondCount2 += 1;
						if (fieldName.equals(afterField)) {
							rm2 += 1;
						} else {
//							System.out.println("规则2推荐错误bf:" + beforeField + " " + "af:" + afterField + " "
//									+ "fdRemmond:" + fieldName);
						}
					}
					res.add(fieldName);
				}
			} else {
				char[][] newField = GenerateNames.generateNonConstantName(field, namePartsPtr, false);
				List<String> fieldList = GenerateNames.convertCharsToString(newField);

				String newFieldString = convertToCamelCase(fieldList.get(0).toString());
				newFieldString = newFieldString.substring(0, 1).toLowerCase() + newFieldString.substring(1).toString();
				if (!newFieldString.equals(beforeField)) {
					remmondCount2 += 1;
					if (newFieldString.charAt(newFieldString.length() - 1) == '_') {
						newFieldString.replace("_", "");
					}

					char[] newFieldSChar = newFieldString.toCharArray();
					char[][] spiltFieldChars = spiltFieldToChar(newFieldSChar, false);
					List<String> newFieldList = convertCharsToString(spiltFieldChars);

					if (newFieldList.get(newFieldList.size() - 1).length() == 1) {
						newFieldString = newFieldString.substring(1, 2).toLowerCase() + newFieldString.substring(2);

//						for (int r = 0; r < fieldDeclarations.size(); r++) {
//							if (r != t) {
//								FieldDeclaration fd = fieldDeclarations.get(r);
//								VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
//								String fieldlist = vdf.getName().getIdentifier();
//
//								char[] fieldChar = fieldlist.toCharArray();
//								char[][] spiltField = spiltFieldToChar(fieldChar, false);// 拆分字符串
//								List<String> fieldStr = convertCharsToString(spiltField);
//
//								if (fieldStr.get(fieldStr.size() - 1).length() == 1) {
//									break;
//								} else {
//									if (newFieldString.length() >= 2) {
//										newFieldString = newFieldString.substring(1, 2).toLowerCase()
//												+ newFieldString.substring(2);
//									} 
//								}
//							}
//						}
					} else {

						for (int r = 0; r < fieldDeclarations.size(); r++) {
							if (r != t) {
								FieldDeclaration fd = fieldDeclarations.get(r);
								VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
								String fieldlist = vdf.getName().getIdentifier();

								char[] fieldChar = fieldlist.toCharArray();
								char[][] spiltField = spiltFieldToChar(fieldChar, false);// 拆分字符串
								List<String> fieldStr = convertCharsToString(spiltField);

								if (fieldStr.get(fieldStr.size() - 1).length() == 1) {
									if (newFieldString.length() >= 2) {
										newFieldString = fieldStr.get(fieldStr.size() - 1)
												+ newFieldString.substring(0, 1).toUpperCase()
												+ newFieldString.substring(2);
									} else {
										newFieldString = fieldStr.get(fieldStr.size() - 1)
												+ newFieldString.substring(0, 1).toUpperCase();
									}
								} else {
									break;
								}
							}
						}
					}

					if (newFieldString.toLowerCase().equals(afterField.toLowerCase())) {
						rm2 += 1;
					} else {
//						System.out.println("规则2推荐错误" + "before:" + beforeField + " " + "after:" + afterField + " "
//								+ "newfield:" + newFieldString);
					}
					res.add(newFieldString);
				}
			}
		}
//		int count = 0;
//		for (int t = 0; t < fieldDeclarations.size(); t++) {
//			FieldDeclaration f = fieldDeclarations.get(t);
//			VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
//			fieldName = vdf.getName().getIdentifier();
//			if (!fieldName.equals(afterField)) {
//				char[] chs1 = fieldName.toCharArray();// 拆分before field
//				char[][] s1 = spiltFieldToChar(chs1, false);
//				List<String> sList = convertCharsToString(s1);// token list
//				boolean flags = isCamelCase(sList);
//				if (sList.size() >= 2) {
//					if (flags == true) {
//						char[][] newField = FieldNaming.generateNonConstantName(field, namePartsPtr, flag2);
//						List<String> fieldList = FieldNaming.convertCharsToString(newField);
//						if (!fieldList.get(0).equals(beforeField)) {
//							System.out.println("fList:"+fieldList.get(0)+" "+afterField);
//							// 判断推荐结果是否正确
//							allRemmondCount += 1;
//							if (fieldList.get(0).equals(afterField)) {
//								correctCount += 1;
//							}
//							res.add(fieldList.get(0));
//						}
//						otherList.clear();
//						break;
//					}
//					otherList.add(fieldName);
//				}
//			}
//		}
//		for (int r = 0; r < otherList.size(); r++) {
//			if (CamelRule.isUnderCase(otherList.get(r)) == true
//					&& otherList.get(r).substring(1, otherList.get(r).length())
//							.substring(0, otherList.get(r).length() - 1).contains("_")) {
//				count += 1;
//			} else {
//				char[][] newField = FieldNaming.generateNonConstantName(field, namePartsPtr, flag2);
//				List<String> fieldList = FieldNaming.convertCharsToString(newField);
//				if (!fieldList.get(0).equals(beforeField)) {
//					System.out.println("fList:"+fieldList.get(0)+" "+afterField);
//					allRemmondCount += 1;
//					if (fieldList.get(0).equals(afterField)) {
//						correctCount += 1;
//					}
//					res.add(fieldList.get(0));
//				}
//				otherList.clear();
//				break;
//			}
//		}
//		if (count == otherList.size() && otherList.size() > 0) {
//			String str = CamelRule.humpToUnderline(beforeField);
//			if (!str.equals(beforeField)) {
//				System.out.println("fList:"+str+" "+afterField);
//				allRemmondCount += 1;
//				if (str.equals(afterField)) {
//					correctCount += 1;
//				}
//				res.add(str);
//			}
//		}
	}


	private static String commonCovention(char[][] field, List<String> fieldList, boolean isStaticFinal, int i,
			String beforeString, String afterString, Type fieldType) {
		List<String> commonTypeList = new ArrayList<>();
		if (fieldDeclarations.size() > 2) {
			for (int s = 0; s < fieldDeclarations.size(); s++) {
				FieldDeclaration fd = fieldDeclarations.get(s);
				Type fdType = fd.getType();
				VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
				String fdName = vdf.getName().getIdentifier();
				if (fdType.toString().equals(fieldType.toString()) && !fdName.equals(afterString)) {
					commonTypeList.add(fdName);
				}
			}

			if (commonTypeList.size() >= 2) {
				char[] fdChar = commonTypeList.get(0).toCharArray();
				char[][] s1 = spiltFieldToChar(fdChar, isStaticFinal);
				List<String> preList = charToStrings(s1);
				if (preList.size() >= 2) { 
					String prefix = preList.get(preList.size() - 1);
					String suffix = preList.get(0);
					String tmpToken = null;
					int maxCount = preList.size() + 1;
					for (int m = 1; m < commonTypeList.size(); m++) {
						String tmp = commonTypeList.get(m);
						if (beforeString.toLowerCase().equals(prefix.toLowerCase())) {
							if (!fieldList.get(fieldList.size() - 1).equals(prefix)) {

								int q = fieldList.indexOf(prefix);
								for (int r = fieldList.size() - 1; r > q; r--) {
									fieldList.remove(r);
								}

								String newFieldRemove = "";
								for (int r = fieldList.size() - 1; r >= 0; r--) {
									newFieldRemove = newFieldRemove + fieldList.get(r);
								}

								if (isStaticFinal) {
									newFieldRemove = camelCaseToConstant(newFieldRemove);
								}
								if (!newFieldRemove.equals(beforeString)) {
									remmondCount4 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (newFieldRemove.toLowerCase().equals(afterString.toLowerCase())) {
										rm4 += 1;
										correctTypeCount(rct);
									} else {
            							
									}
									res.add(newFieldRemove);
									return null;
								}
							}
						} else {
							if (tmp.toLowerCase().contains(prefix)) {
								tmpToken = prefix;
							} else {
								tmpToken = null;
								break;
							}
						}
					}
					if (tmpToken != null && !beforeString.contains(prefix)) {

						if (maxCount < fieldList.size()) {
							fieldList.set(fieldList.size() - 1, prefix);
						} else {
							fieldList.add(prefix);
						}

						String newFieldAdd = "";

						if (isStaticFinal) {
							for (int r = fieldList.size() - 1; r >= 0; r--) {
								newFieldAdd = newFieldAdd + "_" + fieldList.get(r);
							}
						} else {
							for (int r = fieldList.size() - 1; r >= 0; r--) {
								newFieldAdd = newFieldAdd + fieldList.get(r).substring(0, 1).toUpperCase()
										+ fieldList.get(r).substring(1);
							}
							newFieldAdd = newFieldAdd.substring(0, 1).toLowerCase() + newFieldAdd.substring(1);
						}

						if (!newFieldAdd.toLowerCase().equals(beforeString.toLowerCase())) {
							remmondCount4 += 1;
							int rct = recTypeCount(fieldType.toString());
							if (newFieldAdd.toLowerCase().equals(afterString.toLowerCase())) {
								rm4 += 1;
//								System.out.println("规则4删除prefix错误：" + beforeString + " " + afterString);
								correctTypeCount(rct);
							} 
							res.add(newFieldAdd);
							return null;
						}
					}

					for (int m = 1; m < commonTypeList.size(); m++) {
						String tmp = commonTypeList.get(m);
						if (beforeString.toLowerCase().equals(suffix.toLowerCase())) {
							if (!fieldList.get(0).equals(suffix)) { 

								int q = fieldList.indexOf(suffix);
								for (int r = 0; r < q; r++) {
									fieldList.remove(r);
								}

								String newFieldRemove = "";
								for (int r = fieldList.size() - 1; r >= 0; r--) {
									newFieldRemove = newFieldRemove + fieldList.get(r);
								}
								newFieldRemove = newFieldRemove.substring(0, 1).toLowerCase()
										+ newFieldRemove.substring(1);
								if (isStaticFinal) {
									newFieldRemove = camelCaseToConstant(newFieldRemove);
								}

								if (!newFieldRemove.equals(beforeString)) {
									remmondCount4 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (newFieldRemove.toLowerCase().equals(afterString.toLowerCase())) {
										rm4 += 1;
										correctTypeCount(rct);
										System.out.println("规则4删除prefix错误：" + beforeString + " " + afterString);
									} 
									res.add(newFieldRemove);
									return null;
								}
							}
						} else {
							if (tmp.toLowerCase().contains(suffix.toLowerCase())) {
								tmpToken = suffix;
							} else {
								tmpToken = null;
								break;
							}
						}
					}
					
//					if (tmpToken != null && !beforeString.contains(suffix)) { 
//						int minCount=preList.size();
//						if(minCount<fieldList.size()+1) {
//							fieldList.set(0, suffix);
//						}else{
//							fieldList.add(0,suffix);
//						}
//						
//						String newFieldAdd="";
//						if(isStaticFinal) {
//    						for(int r=fieldList.size()-1;r>=0;r--) {
//        						newFieldAdd = newFieldAdd+"_"+fieldList.get(r);           						
//        					}
//    					}else {
//    						for(int r=fieldList.size()-1;r>=0;r--) {
//    							newFieldAdd = newFieldAdd+fieldList.get(r).substring(0,1).toUpperCase()+fieldList.get(r).substring(1);
//    						}
//    						newFieldAdd = newFieldAdd.substring(0,1).toLowerCase()+newFieldAdd.substring(1);
//    					}
//    					if(!newFieldAdd.toLowerCase().equals(beforeString.toLowerCase())) {
//    						remmondCount4 += 1;
//    						if(newFieldAdd.toLowerCase().equals(afterString.toLowerCase())) {
//    							rm4 += 1;
//    						}else {
//    							System.out.println("规则4添加suffix错误：" + beforeString + " " + afterString + " " + newFieldAdd);
//							}
//    						res.add(newFieldAdd);
//    						return null;
//    					} 
//					}
				}
			}
		}
		return null;
	}


	private static String getSpecificCovention(char[][] field, List<String> fieldLists, int i, String beforeField,
			String afterField, boolean isConstant, int n, Type fieldType) {
		List<String> commonFieldType = new ArrayList<>();
		List<String> allFieldData = new ArrayList<>();
		List<String> typeTmpTokenPrefix = new ArrayList<>();
		List<String> typeTmpTokenSuffix = new ArrayList<>();
		List<String> allTmpTokenPrefix = new ArrayList<>();
		List<String> allTmpTokenSuffix = new ArrayList<>();
		boolean isFieldLength = false;
		int t = -1;
		int counts = fieldLists.size() - 1;
		if (isConstant == false) {
			for (int x = 0; x < fieldDeclarations.size(); x++) {
				if (x != i) {
					FieldDeclaration f = fieldDeclarations.get(x);
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
					String fieldNames = vdf.getName().getIdentifier();
					int modifiers = f.getModifiers();
					if (modifiers == 25 || modifiers == 26 || modifiers == 24 || modifiers == 28) {
					} else {
						if (f.getType().equals(fieldType)) {
							commonFieldType.add(fieldNames);
							char[] fieldChar = fieldNames.toString().toCharArray();
							char[][] spiltField = spiltFieldToChar(fieldChar, false);
							List<String> fieldStrings = convertCharsToString(spiltField);
							typeTmpTokenPrefix.add(fieldStrings.get(fieldStrings.size() - 1));
							typeTmpTokenSuffix.add(fieldStrings.get(0));
						}
						allFieldData.add(fieldNames);
						char[] fieldChar = fieldNames.toString().toCharArray();
						char[][] spiltField = spiltFieldToChar(fieldChar, false);
						List<String> fieldStrings = convertCharsToString(spiltField);
						allTmpTokenPrefix.add(fieldStrings.get(fieldStrings.size() - 1));
						allTmpTokenSuffix.add(fieldStrings.get(0));
					}
				}
			}
			if (commonFieldType.size() >= 2) {
				String tmpTokenString = hasCommonElement(allTmpTokenPrefix);
				if (tmpTokenString != null) {
					if (!fieldLists.get(fieldLists.size() - 1).equals(tmpTokenString)) {// 不包含需要添加
						String newFieldRule = "";
						remmondCount3 += 1;
						int rct = recTypeCount(fieldType.toString());
						if (tmpTokenString.equals("_")) {
							newFieldRule = tmpTokenString + beforeField;
						} else {
							newFieldRule = tmpTokenString + beforeField.substring(0, 1).toUpperCase()
									+ beforeField.substring(1);
						}
						if (newFieldRule.equals(afterField)) {
							rm3 += 1;
							correctTypeCount(rct);
						} else {
//							System.out.println("类型错误"+"bf:"+beforeField+" "+"af:"+afterField+" "+"newName:"+newFieldRule);
						}
					}
				}

//				String tmpTokenSuffix= hasCommonElement(typeTmpTokenSuffix);
//				if(tmpTokenSuffix!=null) {
//					if(!fieldLists.get(0).equals(tmpTokenString)) {//不包含需要添加
//						String newFieldRule ="";
//						remmondCount3+=1;						
//						newFieldRule = beforeField+tmpTokenSuffix;
//						if(newFieldRule.equals(afterField) ) {
//							rm3+=1;
//						}else {
//							System.out.println("类型错误"+"bf:"+beforeField+" "+"af:"+afterField+" "+"newName:"+newFieldRule);
//						}
//					}
//				}

			} else if (allFieldData.size() >= 2) {
				String tmpTokenString = hasCommonElement(allTmpTokenPrefix);
				if (tmpTokenString != null) {
					if (!fieldLists.get(fieldLists.size() - 1).equals(tmpTokenString)
							&& !beforeField.subSequence(0, 1).toString().toLowerCase().equals(tmpTokenString)
							&& isConstantFieldName(beforeField) == false
							&& fieldLists.get(fieldLists.size() - 1).length() != 1
							&& !beforeField.subSequence(0, 1).toString().toLowerCase().equals("_")) {// 不包含需要添加
						String newFieldRule = "";
						remmondCount3 += 1;
						int rct = recTypeCount(fieldType.toString());
						if (tmpTokenString.equals("_")) {
							newFieldRule = tmpTokenString + beforeField;
						} else {
							newFieldRule = tmpTokenString + beforeField.substring(0, 1).toUpperCase()
									+ beforeField.substring(1);
						}
						if (newFieldRule.equals(afterField)) {
							rm3 += 1;
							correctTypeCount(rct);
						} else {
//							System.out.println("bf:"+beforeField+" "+"af:"+afterField+" "+"newName:"+newFieldRule);
						}
					}
				}

//				String tmpTokenSuffix = hasCommonElement(allTmpTokenSuffix);
//				if(tmpTokenSuffix!=null) {
//					if(!fieldLists.get(fieldLists.size()-1).equals(tmpTokenSuffix)							
//							&& isConstantFieldName(beforeField)==false 							
//							) {//不包含需要添加
//						String newFieldRule ="";
//						remmondCount3+=1;						
//						newFieldRule = beforeField+tmpTokenSuffix;						
//						if(newFieldRule.equals(afterField)) {
//							rm3+=1;
//						}else {
//							System.out.println("bf:"+beforeField+" "+"af:"+afterField+" "+"newName:"+newFieldRule);
//						}
//					}
//				}	
			}
		}
		return null;
	}

	public static String hasCommonElement(List<String> list) {
		Set<String> set = new HashSet<>();
//    	System.out.println(list);
		for (String element : list) {
			if (set.contains(element) && element.length() == 1) {
				return element;
			}
			set.add(element);
		}
		return null;
	}

	public static String getLongestCommonSubstring(List<String> list) {
		if (list.isEmpty()) {
			return "";
		}

		String commonSubstring = list.get(0);

		for (int i = 1; i < list.size(); i++) {
			commonSubstring = getCommonSubstring(commonSubstring, list.get(i));
		}

		return commonSubstring;
	}

	private static String getCommonSubstring(String str1, String str2) {
		int[][] dp = new int[str1.length() + 1][str2.length() + 1];
		int maxLength = 0;
		int endIndex = 0;

		for (int i = 1; i <= str1.length(); i++) {
			for (int j = 1; j <= str2.length(); j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1] + 1;

					if (dp[i][j] > maxLength) {
						maxLength = dp[i][j];
						endIndex = i - 1;
					}
				}
			}
		}

		return str1.substring(endIndex - maxLength + 1, endIndex + 1);
	}

	public static void getFieldNameFromType(Type fieldType, int modifier, String beforeField, String afterField,
			boolean isConstant, List<String> fieldList) {
		String newField = "";
		if (fieldType.toString().equals("float") || fieldType.toString().equals("int") //$NON-NLS-1$ //$NON-NLS-2$
				|| fieldType.toString().equals("char") || fieldType.toString().equals("short")
				|| fieldType.toString().equals("long") || fieldType.toString().equals("double") //$NON-NLS-1$ //$NON-NLS-2$
				|| fieldType.toString().equals("String") || fieldType.toString().equals("byte")
				|| fieldType.toString().contains("<") || fieldType.toString().contains("[") //$NON-NLS-1$ //$NON-NLS-2$
				|| fieldType.toString().contains(".") || fieldType.toString().equals("boolean")
				|| fieldType.toString().equals("AtomicBoolean") || fieldType.toString().contains("[]")
				|| fieldType.toString().contains("<") || fieldType.toString().contains(".")
				|| fieldType.toString().equals("AtomicLong") || fieldType.toString().equals("Integer")
				|| fieldType.toString().equals("AtomicBoolean") || fieldType.toString().equals("File")
				|| fieldType.toString().equals("Method") || fieldType.toString().equals("Class")
				|| fieldType.toString().equals("Field") || fieldType.toString().contains("int")
				|| fieldType.toString().equals("atomicInteger") || fieldType.toString().equals("Long")) {
			if (fieldType.toString().equals("boolean")) {
				if (!beforeField.toLowerCase().contains("is") && beforeField.length() > 2 && isConstant == false) {
					newField = "is" + beforeField.substring(0, 1).toUpperCase() + beforeField.substring(1);
					if (!newField.equals(beforeField)) {
						remmondCount10 += 1;
						int rct = recTypeCount(fieldType.toString());
						if (newField.equals(afterField)) {
							rm10 += 1;
							correctTypeCount(rct);
						} else {
//								System.out.println("规则7预测错误：" + beforeField + " " + afterField + " " + newField);
						}
					}
				} else if (!beforeField.startsWith("_") && !beforeField.contains("has")
						&& beforeField.toLowerCase().contains("is") && isConstant == false) {
					newField = beforeField.replace("is", "");
					newField = newField.substring(0, 1).toLowerCase() + newField.substring(1);
					remmondCount10 += 1;
					int rct = recTypeCount(fieldType.toString());
					if (newField.equals(afterField)) {
						rm10 += 1;
						correctTypeCount(rct);
					} else {
//			    			System.out.println("规则7预测错误：" + beforeField + " " + afterField + " " + newField);
					}
//			    		System.out.println(beforeField+" "+afterField);

				}
			}
//				if (beforeField.startsWith("is")) {
//					if (beforeField.length() < 2)
//						newField = beforeField.substring(2, 3).toLowerCase() + beforeField.substring(3);
//					if (!newField.equals(beforeField)) {
//						remmondCount7 += 1;
//						if (newField.equals(afterField)) {
//							rm7 += 1;
//						} else {
//							System.out.println("规则7预测错误：" + beforeField + " " + afterField + " " + newField);
//						}
//					}
//					res.add(beforeField.replace("is", ""));
//				} else {
//					newField = "is" + beforeField.substring(0, 1).toUpperCase() + beforeField.substring(1);
//					if (!newField.equals(beforeField)) {
//						remmondCount7 += 1;
//						if (newField.equals(afterField)) {
//							rm7 += 1;
//						} else {
//							System.out.println("规则7预测错误：" + beforeField + " " + afterField + " " + newField);
//						}
//					}
//					res.add(newField);
//				}
//			} 

		}
//		
		else if (flag1 == false) {
			char[] fieldTypeChar = fieldType.toString().toCharArray();
			char[][] spiltFieldType = spiltFieldToChar(fieldTypeChar, false);// 拆分字符串
			List<String> fieldTypeList = convertCharsToString(spiltFieldType);

			char[] afterFieldChar = afterField.toString().toCharArray();
			char[][] spiltAfterField = spiltFieldToChar(afterFieldChar, false);
			List<String> afterFieldList = convertCharsToString(spiltAfterField);

			List<String> tempList = new ArrayList<>();
			if (hasCommonTokens(fieldList, fieldTypeList)) {
				// 存在公共token
				if (fieldList.size() == 1 && fieldTypeList.size() >= 2) {
					for (int r = 0; r < 2; r++) {
						tempList.add(fieldTypeList.get(r));
					}
					if (!tempList.equals(fieldList)) {
						remmondCount7 += 1;
						int rct = recTypeCount(fieldType.toString());
						if (hasCommonTokens(tempList, afterFieldList)) {
							rm7 += 1;
							correctTypeCount(rct);
//							System.out.println(fieldType + " " + beforeField+" "+afterField);
						} else {
//							System.out.println("777777777777777" + fieldType + " " + beforeField+" "+afterField);
						}
						res.add(tempList.get(0));
					}
				}
//				else if (beforeField.toLowerCase().equals(fieldType.toString().toLowerCase())
//						&& fieldTypeList.size() > 1) {
				// 重构前field 与类型相同
//					for (int r = 0; r < fieldTypeList.size() - 1; r++) {
//						tempList.add(fieldTypeList.get(r));
//					}
//                    tempList.add(fieldTypeList.get(0));
//					if (!tempList.equals(fieldList)) {
//						remmondCount7 += 1;
//						if (tempList.equals(afterFieldList)) {
//							rm7 += 1;
//						}
//					}
//					res.add(tempList.get(0));
//				} 
				else if (fieldList.size() == 2 && !fieldType.toString().equals(beforeField)
						&& fieldType.toString().contains(beforeField)) {
					tempList.add(fieldTypeList.get(0));
					remmondCount7 += 1;
					int rct = recTypeCount(fieldType.toString());
					if (tempList.equals(afterFieldList)) {
						rm7 += 1;
						correctTypeCount(rct);
//						System.out.println(fieldType + " " + beforeField+" "+afterField);
					} else {
//						System.out.println("777777777777777" + fieldType + " " + beforeField+" "+afterField);
//						System.out.println("222222222222222" + tempList + " " + afterFieldList + afterField);
					}
					res.add(tempList.get(0));
				}
			} else {
				if (fieldList.size() - fieldTypeList.size() >= 1) {

				} else {

					newField = fieldType.toString().substring(0, 1).toLowerCase() + fieldType.toString().substring(1);
					if (!newField.equals(beforeField)) {
						remmondCount7 += 1;
						int rct = recTypeCount(fieldType.toString());
						if (newField.toLowerCase().equals(afterField.toLowerCase())) {
							rm7 += 1;
							correctTypeCount(rct);
//							System.out.println(fieldType + " " + beforeField+" "+afterField);
						} else {
//							System.out.println("规则7预测错误：" + beforeField + " " + afterField + " " + newField);
//							System.out.println("777777777777777" + fieldType + " " + beforeField+" "+afterField);
						}
					}
					res.add(newField);
				}
			}
		}
	}

	public static <T> boolean hasCommonTokens(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<>(list1);
		for (T item : list2) {
			if (set.contains(item)) {
				return true;
			}
		}
		return false;
	}

	public static void getNameFromFieldAccess2(CompilationUnit cu, int i, boolean isStaticFinal, String oldName,
			String reName, VariableDeclarationFragment vdf, Type fieldType) {
		if (isStaticFinal && isConstantFieldName(oldName) == false) {
		} else if (isStaticFinal == false && isCamelCase(oldName) == false) {
		} else {
			if (vdf.getInitializer() != null) {
				Expression initExp = vdf.getInitializer();
				String newFieldString = getBaseNameFromExpression(initExp, isStaticFinal);
				if (newFieldString != null) {
					if (!newFieldString.equals(oldName) && !newFieldString.toLowerCase().contains("of")
							&& !newFieldString.contains("mock") && !newFieldString.contains("create")
							&& !newFieldString.contains("none")) {
						remmondCount6 += 1;
						initCount += 1;
						int rct = recTypeCount(fieldType.toString());
						if (newFieldString.toLowerCase().equals(reName.toLowerCase())) {
							rm6 += 1;
							init += 1;
							correctTypeCount(rct);
//						System.out.println(
//								"初始化预测正确：" + oldName + " " + reName + " " + newFieldString);
						} else {
//						System.out.println(
//								"初始化推荐错误例子：" + oldName + " " + reName + " " + newFieldString);
						}
						res.add(newFieldString);
					}
				}
			}
//			else if(newFieldString != null && isStaticFinal==false && isCamelCase(newFieldString)) {
//			if (!newFieldString.equals(oldName) && !newFieldString.toLowerCase().contains("of")
//					&& !newFieldString.contains("mock")&& !newFieldString.contains("create")&& !newFieldString.contains("none")) {
//				remmondCount11 += 1;
//				initCount += 1;
//				if (newFieldString.toLowerCase().equals(reName.toLowerCase())) {
//					rm11 += 1;
//					init += 1;
////					System.out.println(
////							"初始化预测正确：" + oldName + " " + reName + " " + newFieldString);
//				} else {
//					System.out.println(
//							"初始化推荐错误例子：" + oldName + " " + reName + " " + newFieldString);
//				}
//			}
//		}

		}

	}

	public static void getNameFromFieldAccess21(CompilationUnit cu, int i, boolean isStaticFinal, String oldName,
			String reName, VariableDeclarationFragment vdf, Type fieldType) {
//		if (vdf.getInitializer() != null) {
//			Expression initExp = vdf.getInitializer();
//			String newFieldString = getBaseNameFromExpression(initExp, isStaticFinal);
//			if (newFieldString != null) {
//				if (!newFieldString.equals(oldName)) {
//					remmondCount6 += 1;
//					initCount += 1;
//					if (newFieldString.equals(reName)) {
//						rm6 += 1;
//						init += 1;
////						System.out.println(
////								"规则6初始化预测正确：" + oldName + " " + reName + " " + newFieldString);
//					} else {
////						System.out.println(
////								"规则6初始化推荐错误例子：" + oldName + " " + reName + " " + newFieldString);
//					}
//				}
//			}
//		} 
//		else {

		if (!fieldAccess.isEmpty()) {
			fieldAccess.clear();
		}
		getFieldAccess(cu, fieldAccess);
		boolean isExistAccess = false;
		for (int s = 0; s < fieldAccess.size(); s++) {
			if (fieldAccess.get(s).toString().contains(afterField.get(i))) {
//				System.out.println("fieldAccess:" + fieldAccess.get(s));
//				System.out.println("parent:" + fieldAccess.get(s).getParent());
				isExistAccess = true;
				if (fieldAccess.get(s).getParent().getNodeType() == ASTNode.ASSIGNMENT) {
					Assignment assignment = (Assignment) fieldAccess.get(s).getParent();
//					System.out.println("assignment:"+" "+assignment.getLeftHandSide()+" "+assignment.getRightHandSide());
//					Expression exp = assignment.getRightHandSide();
//					String  newField = getBaseNameFromExpression(assignment, isStaticFinal);
					String exp = assignment.getRightHandSide().toString();
					if (!exp.equals(oldName)) {
						if (!exp.contains("new") && !exp.contains("[]") && !exp.contains("<") && !exp.contains(".")) {
//						allRemmondCount += 1;
							remmondCount6 += 1;
							expCount += 1;
							int rct = recTypeCount(fieldType.toString());
							res.add(exp.toString());
							if (exp.equals(reName)) {
//							correctCount += 1;
								rm6 += 1;
								expSum += 1;
								correctTypeCount(rct);
//								System.out.println("推荐正确6666666666：" + beforeField.get(i) + " " + afterField.get(i)
//										+ " " + commitId.get(i));
								break;
							} else {
//								System.out.println("规则6推荐错误例子：" + oldName + " " + reName + " " + exp.toString());
								break;
							}
						}
					}
				}
			}
		}

		if (isExistAccess == false) {
			if (!assignments.isEmpty()) {
				assignments.clear();
			}
			getAssign(cu, assignments);
//				for (int a = 0; a < assignments.size(); a++) {
//					String leftString = assignments.get(a).getLeftHandSide().toString();
//					if (leftString.equals(reName)) {
//						Expression exp1 = assignments.get(a).getRightHandSide();
//						String newFieldString = getBaseNameFromExpression(exp1, isStaticFinal);
//						if (newFieldString != null) {
//							remmondCount6 += 1;
//							expCount += 1;
//							if (newFieldString.equals(reName)) {
//								rm6 += 1;
//								expSum += 1;
//							} else {
////							System.out.println(
////									"规则6 assignment推荐错误例子：" + oldName + " " + reName + " " + newFieldString);
//							}
//							}
//						break;
//						}
//				}

			for (int a = 0; a < assignments.size(); a++) {
				String leftString = assignments.get(a).getLeftHandSide().toString();
				if (leftString.equals(reName)) {
					Expression exp1 = assignments.get(a).getRightHandSide();
					String newFieldString = getBaseNameFromExpression(exp1, isStaticFinal);
					if (newFieldString != null) {
						if (!newFieldString.equals(oldName)) {
							remmondCount6 += 1;
							expCount += 1;
							int rct = recTypeCount(fieldType.toString());
							if (newFieldString.equals(reName)) {
								rm6 += 1;
								expSum += 1;
								correctTypeCount(rct);
							} else {
//								System.out.println(
//										"规则6 assignment推荐错误例子：" + oldName + " " + reName + " " + newFieldString);
							}
							break;
						}
					}
				}

			}
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
		}
//		else if (flag
//				&& (assignedExpression instanceof StringLiteral || assignedExpression instanceof NumberLiteral)) {
//			String string = assignedExpression instanceof StringLiteral
//					? ((StringLiteral) assignedExpression).getLiteralValue()
//					: ((NumberLiteral) assignedExpression).getToken();
//			StringBuilder res = new StringBuilder();
//			boolean needsUnderscore = false;
//			for (char ch : string.toCharArray()) {
//				if (Character.isJavaIdentifierPart(ch)) {
//					if (res.length() == 0 && !Character.isJavaIdentifierStart(ch) || needsUnderscore) {
//						res.append('_');
//					}
//					res.append(ch);
//					needsUnderscore = false;
//				} else {
//					needsUnderscore = res.length() > 0;
//				}
//			}
//			if (res.length() > 0) {
//				return res.toString();
//			}
//		}
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

	public static void getNameFromReturnExpression(CompilationUnit cu, String beforeField, String afterField, Type fieldType) {
		if (!returnStatement.isEmpty()) {
			returnStatement.clear();
		}
		getReturnStatement(cu, returnStatement);

		for (int t = 0; t < returnStatement.size(); t++) {
			ReturnStatement fd = returnStatement.get(t);
			if (fd.getExpression() != null) {
				String fieldName = fd.getExpression().toString();
				ASTNode parent = fd.getParent();
				if (fieldName.equals(afterField) || fieldName.equals("this." + afterField)) {

					while (parent.getNodeType() != ASTNode.METHOD_DECLARATION) {
						parent = parent.getParent();
					}
					if (parent instanceof MethodDeclaration) {
						MethodDeclaration md = (MethodDeclaration) parent;
						String methodName = md.getName().toString();

						if (methodName != null) {
							for (String curr : KNOWN_METHOD_NAME_PREFIXES) {
								if (methodName.startsWith(curr)) {
									if (methodName.equals(curr)) {
										// don't suggest 'get' as variable name
									} else if (Character.isUpperCase(methodName.charAt(curr.length()))) {
//										allRemmondCount += 1;
										remmondCount5 += 1;
										int rct = recTypeCount(fieldType.toString());
										if (curr.length() + 1 < methodName.length()) {
											String newField = methodName.substring(curr.length(), curr.length() + 1)
													.toLowerCase() + methodName.substring(curr.length() + 1).toString();
											if (!newField.equals(beforeField)) {
												if (newField.equals(afterField)) {
//											correctCount += 1;
													rm5 += 1;
													correctTypeCount(rct);
//												System.out.println("推荐正确555555555：" + beforeField + " " + afterField);
												} else {
//													System.out.println("规则5预测错误：" + beforeField + " " + afterField + " "
//															+ newField);
												}
												res.add(methodName.substring(curr.length()));
											}
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}

	}

	public static String getFieldFromHistory(int i, String beforeField, String afterField, String dataPath,
			String beforePath,String fieldType) {
		char[] bf = beforeField.toCharArray();
		char[][] bfs = spiltFieldToChar(bf, false);
		List<String> bfList = convertCharsToString(bfs);

		boolean flag = false;
		char[] af = afterField.toCharArray();
		char[][] afs = spiltFieldToChar(af, false);
		List<String> afList = convertCharsToString(afs);

		List<String> tempBeforeList = new ArrayList<>();
		for (int a = 0; a < bfList.size(); a++) {
			tempBeforeList.add(bfList.get(a));
		}
		if (commitBefore.equals(commitId.get(i))) {

			if (!historyRefactoringList.isEmpty()) {
				for (int e = historyRefactoringList.size() - 1; e >= 0; e--) {
					boolean bln = historyRefactoringList.get(e).isChanged();
					int cot = historyRefactoringList.get(e).getConventions();
					Map<String, String> hField = historyRefactoringList.get(e).getHistoryField();
					Map<String, Integer> hRemove = historyRefactoringList.get(e).getHistoryRemove();
					Map<String, String> hReplace = historyRefactoringList.get(e).getHistoryReplace();
					Map<String, Integer> hAdd = historyRefactoringList.get(e).getHistoryAdd();
					if (bln) {
						String newFieldString = "";
						if (cot == 1) {
							if (!hField.isEmpty()) {
								for (Map.Entry<String, String> entry : hField.entrySet()) {
									String key = entry.getKey();
									if (beforeField.equals(key) && !beforeField.equals(entry.getValue())) {
										remmondCount0 += 1;
										int rct = recTypeCount(fieldType.toString());
										if (entry.getValue().equals(afterField)) {
											rm0 += 1;
											correctTypeCount(rct);
										} else {
//											System.out.println("规则0相同odlField推荐错误：" + beforeField + " " + afterField + " "
//													+ entry.getValue());
										}
										deleteToken(afList, bfList, beforeField, afterField);
										return null;
									}
								}
							}

							if (!hReplace.isEmpty()) {
								for (Map.Entry<String, String> entry : hReplace.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.set(n, entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList)) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)
											|| (bfList.size() == 1 && bfList.get(0).equals(afterField))) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0替换推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hRemove.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hRemove.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.remove(entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0删除推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hAdd.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hAdd.entrySet()) {
									String key = entry.getKey();
									if (beforePath.equals(dataPath) && entry.getValue() < bfList.size()) {
										bfList.add(entry.getValue(), entry.getKey());
										flag = true;
									} else {
										if (key.length() == 1 && entry.getValue() == 0) {
											bfList.add(entry.getValue(), entry.getKey());
											flag = true;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0添加推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}

							}

						} else if (cot == 2) {

							if (!hField.isEmpty()) {
								for (Map.Entry<String, String> entry : hField.entrySet()) {
									String key = entry.getKey();
									if (beforeField.equals(key) && !beforeField.equals(entry.getValue())) {
										remmondCount0 += 1;
										int rct = recTypeCount(fieldType.toString());
										if (entry.getValue().equals(afterField)) {
											rm0 += 1;
											correctTypeCount(rct);
										} else {
//											System.out.println("规则0相同odlField推荐错误：" + beforeField + " " + afterField + " "
//													+ entry.getValue());
										}
										deleteToken(afList, bfList, beforeField, afterField);
										return null;
									}
								}
							}

							if (!hReplace.isEmpty()) {
								for (Map.Entry<String, String> entry : hReplace.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.set(n, entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList)) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)
											|| (bfList.size() == 1 && bfList.get(0).equals(afterField))) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0替换推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hRemove.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hRemove.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.remove(entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0删除推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hAdd.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hAdd.entrySet()) {
									String key = entry.getKey();
									if (beforePath.equals(dataPath) && entry.getValue() < bfList.size()) {
										bfList.add(entry.getValue(), entry.getKey());
										flag = true;
									} else {
										if (key.length() == 1 && entry.getValue() == 0) {
											bfList.add(entry.getValue(), entry.getKey());
											flag = true;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0添加推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}

							}

						} else if (cot == 3) {
							if (!hField.isEmpty()) {
								for (Map.Entry<String, String> entry : hField.entrySet()) {
									String key = entry.getKey();
									if (beforeField.equals(key) && !beforeField.equals(entry.getValue())) {
										remmondCount0 += 1;
										int rct = recTypeCount(fieldType.toString());
										if (entry.getValue().equals(afterField)) {
											rm0 += 1;
											correctTypeCount(rct);
										} else {
//											System.out.println("规则0相同odlField推荐错误：" + beforeField + " " + afterField + " "
//													+ entry.getValue());
										}
										deleteToken(afList, bfList, beforeField, afterField);
										return null;
									}
								}
							}

							if (!hReplace.isEmpty()) {
								for (Map.Entry<String, String> entry : hReplace.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.set(n, entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList)) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)
											|| (bfList.size() == 1 && bfList.get(0).equals(afterField))) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0替换推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hRemove.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hRemove.entrySet()) {
									String key = entry.getKey();
									for (int n = 0; n < bfList.size(); n++) {
										if (bfList.get(n).equals(key)) {
											bfList.remove(entry.getValue());
											flag = true;
											break;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0删除推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}

							if (!hAdd.isEmpty()) {
								for (Map.Entry<String, Integer> entry : hAdd.entrySet()) {
									String key = entry.getKey();
									if (beforePath.equals(dataPath) && entry.getValue() < bfList.size()) {
										bfList.add(entry.getValue(), entry.getKey());
										flag = true;
									} else {
										if (key.length() == 1 && entry.getValue() == 0) {
											bfList.add(entry.getValue(), entry.getKey());
											flag = true;
										}
									}
								}

								Iterator<String> iterator = bfList.iterator();
								while (iterator.hasNext()) {
									String element = iterator.next();
									if (element == null || element.isEmpty()) {
										iterator.remove();
									}
								}

								Iterator<String> iterator1 = afList.iterator();
								while (iterator.hasNext()) {
									String element = iterator1.next();
									if (element == null || element.isEmpty()) {
										iterator1.remove();
									}
								}

								if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
										&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (areListsEqual(bfList, afList)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0添加推荐错误：" + beforeField + " " + afterField + " " + bfList
//												+ " " + afList);
									}
									res.add(newFieldString);
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}

							}
						}
						deleteToken(afList, bfList, beforeField, afterField);
						return null;
					} else {
						String newFieldString = "";
						if (!historyField.isEmpty()) {
							for (Map.Entry<String, String> entry : historyField.entrySet()) {
								String key = entry.getKey();
								if (beforeField.equals(key) && !beforeField.equals(entry.getValue())) {
									remmondCount0 += 1;
									int rct = recTypeCount(fieldType.toString());
									if (entry.getValue().equals(afterField)) {
										rm0 += 1;
										correctTypeCount(rct);
									} else {
//										System.out.println("规则0相同odlField推荐错误：" + beforeField + " " + afterField + " "
//												+ entry.getValue());
									}
									deleteToken(afList, bfList, beforeField, afterField);
									return null;
								}
							}
						}
//
//			if (!removeString.isEmpty()) {
//
//				for (Map.Entry<String, Integer> entry : removeString.entrySet()) {
//					if (entry.getValue() == 0 && entry.getKey().length() == 1
//							&& bfList.get(bfList.size() - 1).length() == 1) {
//						String newField = beforeField.substring(1, 2).toLowerCase() + beforeField.substring(2);
//						if (newField != beforeField) {
//							remmondCount0 += 1;
//							if (newField.equals(afterField)) {
//								rm0 += 1;
//							} else {
//								System.out.println("规则0字符删除推荐 ：" + beforeField + " " + afterField + " " + newField);
//							}
//							deleteToken(afList, bfList, beforeField, afterField);
//							return null;
//						}
//					}
//				}
//			}
//
////			//字符串级别转换
////			if(!addString.isEmpty()) {
////				for (Map.Entry<String, Integer> entry : addString.entrySet()) {
////					if(entry.getValue()==1) {												
////						String newField = beforeField+entry.getKey().substring(0).toUpperCase();
////						if(newField!=beforeField) {
////							remmondCount0+=1;
////							if(newField.equals(afterField)) {
////								rm0+=1;
////							}else {
////								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
////							}
////							deleteToken(afList,bfList,beforeField,afterField);
////							return null;
////						}
////					}
////					else if(entry.getValue()==0) {
////						String newField =entry.getKey() + beforeField.substring(0,1).toUpperCase()+beforeField.substring(1);
////						if(newField!=beforeField) {
////							remmondCount0+=1;
////							if(newField.equals(afterField)) {
////								rm0 +=1;
////							}else {
////								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
////							}
////							deleteToken(afList,bfList,beforeField,afterField);
////							return null;
////						}
////					}
////				} 
////			}
//
						if (!historyReplace.isEmpty()) {
							for (Map.Entry<String, String> entry : historyReplace.entrySet()) {
								String key = entry.getKey();
								for (int n = 0; n < bfList.size(); n++) {
									if (bfList.get(n).equals(key)) {
										bfList.set(n, entry.getValue());
										flag = true;
										break;
									}
								}
							}

							Iterator<String> iterator = bfList.iterator();
							while (iterator.hasNext()) {
								String element = iterator.next();
								if (element == null || element.isEmpty()) {
									iterator.remove();
								}
							}

							Iterator<String> iterator1 = afList.iterator();
							while (iterator.hasNext()) {
								String element = iterator1.next();
								if (element == null || element.isEmpty()) {
									iterator1.remove();
								}
							}

							if (flag && !bfList.equals(tempBeforeList)) {
								remmondCount0 += 1;
								int rct = recTypeCount(fieldType.toString());
								if (areListsEqual(bfList, afList)
										|| (bfList.size() == 1 && bfList.get(0).equals(afterField))) {
									rm0 += 1;
									correctTypeCount(rct);
//						System.out.println("推荐正确："+beforeField+" "+afterField);
								} else {
//									System.out.println("规则0替换推荐错误：" + beforeField + " " + afterField + " " + bfList
//											+ " " + afList);
								}
								res.add(newFieldString);
								deleteToken(afList, bfList, beforeField, afterField);
								return null;
							}
						}

						if (!historyAdd.isEmpty()) {
							for (Map.Entry<String, Integer> entry : historyAdd.entrySet()) {
								String key = entry.getKey();
								if (beforePath.equals(dataPath) && entry.getValue() < bfList.size()) {
									bfList.add(entry.getValue(), entry.getKey());
									flag = true;
								} else {
									if (key.length() == 1 && entry.getValue() == 0) {
										bfList.add(entry.getValue(), entry.getKey());
										flag = true;
									}
								}
								// Process the key-value pair
							}

							Iterator<String> iterator = bfList.iterator();
							while (iterator.hasNext()) {
								String element = iterator.next();
								if (element == null || element.isEmpty()) {
									iterator.remove();
								}
							}

							Iterator<String> iterator1 = afList.iterator();
							while (iterator.hasNext()) {
								String element = iterator1.next();
								if (element == null || element.isEmpty()) {
									iterator1.remove();
								}
							}

							if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
									&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
								remmondCount0 += 1;
//					if (bfList.equals(afList)) {
								int rct = recTypeCount(fieldType.toString());
								if (areListsEqual(bfList, afList)) {
									rm0 += 1;
									correctTypeCount(rct);
								} else {
//									System.out.println("规则0添加推荐错误：" + beforeField + " " + afterField + " " + bfList
//											+ " " + afList);
								}
								res.add(newFieldString);
								deleteToken(afList, bfList, beforeField, afterField);
								return null;
							}

						}

						if (!historyRemove.isEmpty()) {
							for (Map.Entry<String, Integer> entry : historyRemove.entrySet()) {
								String key = entry.getKey();
								for (int n = 0; n < bfList.size(); n++) {
									if (bfList.get(n).equals(key)) {
										bfList.remove(entry.getValue());
										flag = true;
										break;
									}
								}
							}

							Iterator<String> iterator = bfList.iterator();
							while (iterator.hasNext()) {
								String element = iterator.next();
								if (element == null || element.isEmpty()) {
									iterator.remove();
								}
							}

							Iterator<String> iterator1 = afList.iterator();
							while (iterator.hasNext()) {
								String element = iterator1.next();
								if (element == null || element.isEmpty()) {
									iterator1.remove();
								}
							}

							if (flag && !bfList.equals(tempBeforeList) && !hasDuplicates(bfList)
									&& (bfList.size() - afList.size()) < 2 && (afList.size() - bfList.size()) < 2) {
								remmondCount0 += 1;
								int rct = recTypeCount(fieldType.toString());
								if (areListsEqual(bfList, afList)) {
//					if (bfList.equals(afList)) {
									rm0 += 1;
									correctTypeCount(rct);
//						System.out.println("推荐正确："+beforeField+" "+afterField);
								} else {
//									System.out.println("规则0删除推荐错误：" + beforeField + " " + afterField + " " + bfList
//											+ " " + afList);
								}
								res.add(newFieldString);
								deleteToken(afList, bfList, beforeField, afterField);
								return null;
							}
						}
					}
				}
			}
		} else {
			historyAdd.clear();
			historyRemove.clear();
			historyReplace.clear();
			historyField.clear();
			fieldTypes = 0;
			fFieldConventions = 0;
			addString.clear();
			removeString.clear();
			historyRefactoringList.clear();

			camelConventionAdd.clear();
			camelConventionRemove.clear();
			camelConventionReplace.clear();
			constantConventionAdd.clear();
			constantConventionRemove.clear();
			constantConventionReplace.clear();
			underConventionAdd.clear();
			underConventionRemove.clear();
			underConventionReplace.clear();

			commitBefore = commitId.get(i);
			// 比较两个list，存入map
			deleteToken(bfList, afList, beforeField, afterField);
		}
		return null;
	}

	public static <T> boolean areListsEqual(List<T> list1, List<T> list2) {
		Set<T> set1 = new HashSet<>(list1);
		Set<T> set2 = new HashSet<>(list2);

		return set1.equals(set2);
	}

	public static void deleteToken(List<String> bfList, List<String> afList, String beforeField, String afterField) {
		fieldTypes = 0;
		fFieldConventions = 0;
		boolean fFlag = typeRem(beforeField, afterField);
//		if (fieldTypes == 0) {
		retrieveLocation(beforeField, afterField);
		List<String> tempList = new ArrayList<>();
		List<String> befList = new ArrayList<>();
		for (int a = 0; a < afList.size(); a++) {
			tempList.add(afList.get(a));
		}

		for (int b = 0; b < bfList.size(); b++) {
			befList.add(bfList.get(b));
		}

		List<String> commonElements = new ArrayList<>();
		for (String element : bfList) {
			if (afList.contains(element)) {
				commonElements.add(element);
			}
		}

		bfList.removeAll(commonElements);
		afList.removeAll(commonElements);

		if (bfList.isEmpty() && !afList.isEmpty()) {
			// 可能是增加元素或者减少元素
			if (afList.size() == 1) {
//				System.out.println("afList:"+afList);
//				System.out.println("temp:"+tempList);
				historyAdd.put(afList.get(0), tempList.indexOf(afList.get(0)));
			}
			historyField.put(beforeField, afterField);

		} else if (!bfList.isEmpty() && afList.isEmpty()) {
			if (bfList.size() == 1) {
				historyRemove.put(bfList.get(0), befList.indexOf(bfList.get(0)));
			}
			historyField.put(beforeField, afterField);

		} else if (afList.isEmpty() && bfList.isEmpty()) {
			historyField.put(beforeField, afterField);
		} else {
			if ((afList.size() == 1) && (bfList.size() == 1)) {
				historyReplace.put(bfList.get(0), afList.get(0));
			}
			historyField.put(beforeField, afterField);
		}
//		}else {
//			if(isCamelCase(afterField)) {
//				List<String> tempList = new ArrayList<>();
//				List<String> befList = new ArrayList<>();
//				for (int a = 0; a < afList.size(); a++) {
//					tempList.add(afList.get(a));
//				}
//
//				for (int b = 0; b < bfList.size(); b++) {
//					befList.add(bfList.get(b));
//				}
//				
//				List<String> commonElements = new ArrayList<>();
//				for (String element : bfList) {
//					if (afList.contains(element)) {
//						commonElements.add(element);
//					}
//				}
//
//				bfList.removeAll(commonElements);
//				afList.removeAll(commonElements);
//
//				if (bfList.isEmpty() && !afList.isEmpty()) {				
//					    camelConventionAdd.put(afList.get(0), tempList.indexOf(afList.get(0)));				
//
//				} else if (!bfList.isEmpty() && afList.isEmpty()) {
//					    camelConventionRemove.put(bfList.get(0), befList.indexOf(bfList.get(0)));				
//				} else if(!bfList.isEmpty() && !afList.isEmpty()){
//					    camelConventionReplace.put(bfList.get(0), afList.get(0));
//				}
//			}else if(isConstantFieldName(afterField)) {
//				List<String> tempList = new ArrayList<>();
//				List<String> befList = new ArrayList<>();
//				for (int a = 0; a < afList.size(); a++) {
//					tempList.add(afList.get(a));
//				}
//
//				for (int b = 0; b < bfList.size(); b++) {
//					befList.add(bfList.get(b));
//				}
//				
//				List<String> commonElements = new ArrayList<>();
//				for (String element : bfList) {
//					if (afList.contains(element)) {
//						commonElements.add(element);
//					}
//				}
//
//				bfList.removeAll(commonElements);
//				afList.removeAll(commonElements);
//
//				if (bfList.isEmpty() && !afList.isEmpty()) {				
//					    constantConventionAdd.put(afList.get(0), tempList.indexOf(afList.get(0)));				
//
//				} else if (!bfList.isEmpty() && afList.isEmpty()) {
//					    constantConventionRemove.put(bfList.get(0), befList.indexOf(bfList.get(0)));				
//				} else if(!bfList.isEmpty() && !afList.isEmpty()){
//					    constantConventionReplace.put(bfList.get(0), afList.get(0));
//				}
//			}else {
//				List<String> tempList = new ArrayList<>();
//				List<String> befList = new ArrayList<>();
//				for (int a = 0; a < afList.size(); a++) {
//					tempList.add(afList.get(a));
//				}
//
//				for (int b = 0; b < bfList.size(); b++) {
//					befList.add(bfList.get(b));
//				}
//				
//				List<String> commonElements = new ArrayList<>();
//				for (String element : bfList) {
//					if (afList.contains(element)) {
//						commonElements.add(element);
//					}
//				}
//
//				bfList.removeAll(commonElements);
//				afList.removeAll(commonElements);
//
//				if (bfList.isEmpty() && !afList.isEmpty()) {				
//					    underConventionAdd.put(afList.get(0), tempList.indexOf(afList.get(0)));				
//
//				} else if (!bfList.isEmpty() && afList.isEmpty()) {
//					    underConventionRemove.put(bfList.get(0), befList.indexOf(bfList.get(0)));				
//				} else if(!bfList.isEmpty() && !afList.isEmpty()){
//					    underConventionReplace.put(bfList.get(0), afList.get(0));
//				}
//			}
//		}

		HistoryData hRefactoring = new HistoryData();
		hRefactoring.setBeforeField(beforeField);
		hRefactoring.setAfterField(afterField);
		hRefactoring.setChanged(fFlag);
		hRefactoring.setConventions(fFieldConventions);
		hRefactoring.setHistoryField(historyField);
		hRefactoring.setHistoryAdd(historyAdd);
		hRefactoring.setHistoryRemove(historyRemove);
		hRefactoring.setHistoryReplace(historyReplace);
		historyRefactoringList.add(hRefactoring);
	}

	public static boolean typeRem(String beforeField, String afterField) {
		if (isCamelCase(beforeField)) {
			if (isCamelCase(afterField)) {
				// 一致不推荐
				return false;
			} else if (isConstantFieldName(afterField)) {
				// 存在 camel-> constant 转换
				fFieldConventions = 1;
			} else if (isUnderscoreCase(afterField)) {
				// 存在 camel-> under
				fFieldConventions = 3;
			} else {
			}
			return true;
		} else if (isConstantFieldName(beforeField)) {
			if (isConstantFieldName(afterField)) {
				return false;
				// 一致，不转化
			} else if (isCamelCase(afterField)) {
				// constant -> camel
				fFieldConventions = 2;
			} else if (isUnderscoreCase(afterField)) {
				fFieldConventions = 3;
			} else {
			}
			return true;
		} else if (isUnderscoreCase(beforeField)) {
			if (isUnderscoreCase(afterField)) {
				// 一致不推荐
				return false;
			} else if (isCamelCase(afterField)) {
				// under - > camel
				fFieldConventions = 2;
			} else if (isConstantFieldName(afterField)) {
				// under -> Constant
				fFieldConventions = 1;
			} else {
			}
			return true;
		}
		return false;
	}

	public static void retrieveLocation(String beforeField, String afterField) {
		// 存在问题
		if (afterField.toLowerCase().contains(beforeField.toLowerCase())) {
			// 添加
			String addToken = afterField.toLowerCase().replace(beforeField.toLowerCase(), "");
			int position = afterField.toLowerCase().indexOf(beforeField.toLowerCase());
			if (position == 0) {// 表示添加在最后
				addString.put(addToken, 1);
			} else {// 添加在起始位置
				addString.put(addToken, 0);
			}

		} else if (beforeField.toLowerCase().contains(afterField.toLowerCase())) {
			// 删除
			String addToken = beforeField.toLowerCase().replace(afterField.toLowerCase(), "");
			int position = beforeField.toLowerCase().indexOf(afterField.toLowerCase());
			if (position == 0) {
				// 删除后边
				removeString.put(addToken, 1);
			} else {
				// 删除前面
				removeString.put(addToken, 0);
			}
		}
	}

	public static void commonTokenRecommend(int t, boolean f3, String beforeField, String afterField) throws Exception {
		String tempString = "";
		char[] chs = beforeField.toCharArray();// 旧field字符
		char[][] s = spiltFieldToChar(chs, f3);// 拆分旧字符二维
		List<String> oldList = charToStrings(s);// 旧名字token

		List<String> fieldSpiltList = convertCharsToString(s); // 字符转变为token
		if (t - 1 >= 0 && t + 1 < fieldDeclarations.size()) {// 判断是否存在pre next
			FieldDeclaration fd1 = fieldDeclarations.get(t - 1);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fieldDeclarations.get(t - 1).fragments()
					.get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fd1.getModifiers() == 25 || fd1.getModifiers() == 26 || fd1.getModifiers() == 24) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);// 拆分pre
			List<String> preList = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(t + 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fieldDeclarations.get(t + 1).fragments()
					.get(0);
			String fn2 = vdf2.getName().getIdentifier();// next name
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26 || fd2.getModifiers() == 24) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);// 拆分field
			List<String> nextList = charToStrings(s2);// tokens
			if (preList.get(preList.size() - 1).equals(nextList.get(nextList.size() - 1))) {// pre
																							// ==
																							// next
				// 判断当前field是否包含相同规范
				if (preList.get(preList.size() - 1).equals(oldList.get(oldList.size() - 1))) {
					// rule3
				} else {
					// 推荐规范+field
					String newField = "";

					System.out.println("前后存在命名规范，首规范");
					allRemmondCount += 1;
					if (beforeField.contains(preList.get(preList.size() - 1))) {// 旧名字中是否包含相同token
						// userNameTest->nameTest
						newField = "推荐删除";
					} else {
						if (preList.get(preList.size() - 1).endsWith("_")
								|| preList.get(preList.size() - 1).endsWith("$")) {
							newField = preList.get(preList.size() - 1) + beforeField;
						} else {
							tempString = getFieldName(beforeField);
							if ((oldList.size() + 1 > preList.size()) && (oldList.size() + 1 > nextList.size())) {// 大于两个field长度
								// 替换field,
								newField = "推荐替换";
							} else {
								newField = preList.get(preList.size() - 1) + tempString;
							}
						}
//							System.out.println("newField："+newField);
//							System.out.println("preNew:"+preNewList.get(n));
						if (newField.equals(afterField)) {
							correctCount += 1;
						}
					}
				}
			} else {
				if (preList.get(0).equals(nextList.get(0))) {
					if (preList.get(0).equals(oldList.get(0))) {

					} else {
						String newField = "";
						// field+尾命名规范
						System.out.println("前后存在命名规范，尾规范");
						allRemmondCount += 1;
						if (beforeField.contains(preList.get(preList.size() - 1))) {
							// 推荐删除
							newField = "建议删除";
						} else {
							if (oldList.size() + 1 > preList.size() && oldList.size() + 1 > nextList.size()) {
								// 推荐替换
								newField = "建议替换";
							} else {
								newField = beforeField + preList.get(0);
							}
							if (newField.equals(afterField)) {
								correctCount += 1;
							}

						}
					}
				}
			}
		} else if (t - 2 >= 0 && t - 1 > 0) {
			FieldDeclaration fde = fieldDeclarations.get(t - 2);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fde.getModifiers() == 25 || fde.getModifiers() == 26 || fde.getModifiers() == 24) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);
			List<String> preList = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(t - 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments().get(0);
			String fn2 = vdf2.getName().getIdentifier();
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26 || fd2.getModifiers() == 24) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);
			List<String> nextList = convertCharsToString(s2);
//					System.out.println("fList2:"+fList2.get(0));
			if (preList.get(preList.size() - 1).equals(nextList.get(nextList.size() - 1))) {
				// 判断当前field是否包含相同规范
				if (preList.get(preList.size() - 1).equals(oldList.get(oldList.size() - 1))) {
					// 推荐规范+field
				} else {
					String newField = "";
					System.out.println("两个前面field,存在首token规范");
					allRemmondCount += 1;
					if (beforeField.contains(preList.get(preList.size() - 1))) {// 旧名字中是否包含相同token
						// userNameTest->nameTest
						newField = "推荐删除";
					} else {
						if (preList.get(preList.size() - 1).endsWith("_")
								|| preList.get(preList.size() - 1).endsWith("$")) {
							newField = preList.get(preList.size() - 1) + beforeField;
						} else {
							tempString = getFieldName(beforeField);
							if (oldList.size() + 1 > preList.size() && oldList.size() + 1 > nextList.size()) {// 大于两个field长度
								// 替换field,
								newField = "推荐替换";
							} else {
								newField = preList.get(preList.size() - 1) + tempString;
							}
						}
//								System.out.println("newField:" + newField);
//								System.out.println("preList:" + preNewList.get(n));
						if (newField.equals(beforeField)) {
							correctCount += 1;
						}
					}
				}
			} else {
				if (preList.get(0).equals(nextList.get(0))) {
//						System.out.println("3333333333");
					if (preList.get(0).equals(oldList.get(0))) {
						// field+尾命名规范
					} else {
						String newField = "";
						System.out.println("两个前面field,存在尾token规范");
						allRemmondCount += 1;
						if (beforeField.contains(preList.get(preList.size() - 1))) {
							// 推荐删除
							newField = "建议删除";
						} else {
							if (oldList.size() + 1 > preList.size() && oldList.size() + 1 > nextList.size()) {
								// 推荐替换
								newField = "建议替换";
							} else {
								newField = beforeField + preList.get(0);
							}
							if (newField.equals(beforeField)) {
								correctCount += 1;
							}
						}
					}
				}
			}
		} else if (t + 2 < fieldDeclarations.size() && t + 1 < fieldDeclarations.size()) {
			FieldDeclaration fd1 = fieldDeclarations.get(t + 1);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fd1.fragments().get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fd1.getModifiers() == 25 || fd1.getModifiers() == 26 || fd1.getModifiers() == 24) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);
			List<String> fList1 = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(t + 2);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments().get(0);
			String fn2 = vdf2.getName().getIdentifier();
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26 || fd2.getModifiers() == 24) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);
			List<String> fList2 = charToStrings(s2);
//					System.out.println("fList2:"+fList2.get(0));
			if (fList1.get(fList1.size() - 1).equals(fList2.get(fList2.size() - 1))) {
				// 判断当前field是否包含相同规范
				if (fList1.get(fList1.size() - 1).equals(oldList.get(oldList.size() - 1))) {

				} else {
					// 推荐规范+field
					String newField = "";
					System.out.println("两个后面field,存在首token规范");

					allRemmondCount += 1;
					if (beforeField.contains(fList1.get(fList1.size() - 1))) {
						newField = "建议删除";
					} else {

						if (fList1.get(fList1.size() - 1).endsWith("_")
								|| fList1.get(fList1.size() - 1).endsWith("$")) {
							newField = fList1.get(fList1.size() - 1) + beforeField;
						} else {
							tempString = getFieldName(beforeField);
							if (oldList.size() + 1 > fList1.size() && oldList.size() + 1 > fList2.size()) {// 大于两个field长度
								// 替换field,
								newField = "推荐替换";
							} else {
								newField = fList1.get(fList1.size() - 1) + tempString;
							}
						}
						if (newField.equals(beforeField)) {
							correctCount += 1;
						}
					}
				}
			} else {
				if (fList1.get(0).equals(fList2.get(0))) {
//						System.out.println("3333333333");
					if (fList1.get(0).equals(oldList.get(0))) {
					} else {
						// field+尾命名规范
						String newField = "";
						System.out.println("两个后面field,存在尾token规范");
						System.out.println("newField：" + beforeField);
						allRemmondCount += 1;
						if (oldList.contains(fList1.get(0))) {
							newField = "建议删除";
						} else {
							if (oldList.size() + 1 > fList1.size() && oldList.size() + 1 > fList2.size()) {
								newField = "建议替换";
							} else {
								newField = beforeField + fList1.get(0);
							}
						}
						if (newField.equals(beforeField)) {
							correctCount += 1;
						}
					}
				}
			}
		}
	}

	public static String convertToCamelCase(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		String[] words = str.trim().split("[\\s_-]+");
		for (int i = 1; i < words.length; i++) {
			words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
		}
		return String.join("", words);
	}


	private static void staticFinalNamingRecommend(char[][] field, boolean isModiferStatic, int i, String beforeField,
			String afterField, List<String> fieldStrings, FieldDeclaration fd, Type fieldType, int t) {
		List<String> commonTypeData = new ArrayList<>();
		boolean isStaticFinal = false;
		int camelCase=0;
		int underCase=0;
		int constantCase =0;
		if (fd.toString().contains("static") && fd.toString().contains("final")) {
			isStaticFinal = true;
		}

		if (fieldDeclarations.size() > 1 ) {
			if (isStaticFinal) {
				for (int r = 0; r < fieldDeclarations.size(); r++) {
					// 获取所有相同元素集合
					if (fieldDeclarations.get(r).toString().contains("static")
							&& fieldDeclarations.get(r).toString().contains("final")) {
						FieldDeclaration fde = fieldDeclarations.get(r);
						VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
						String fn1 = vdf1.getName().getIdentifier();
						char[] fieldChar = fn1.toCharArray();
						char[][] spiltField = spiltFieldToChar(fieldChar, true);
						List<String> fdList = convertCharsToString(spiltField);
						if (!fn1.startsWith("_") && fdList.size() > 1 && !fn1.equals(beforeField)) {
						      commonTypeData.add(fn1);
						}
					}
				}
			} else {// 不包含 staic final
				for (int r = 0; r < fieldDeclarations.size(); r++) {
					if (!fieldDeclarations.get(r).toString().contains("static")
							|| !fieldDeclarations.get(r).toString().contains("final")) {
						FieldDeclaration fde = fieldDeclarations.get(r);
						VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
						String fn1 = vdf1.getName().getIdentifier();
						char[] fieldChar = fn1.toCharArray();
						char[][] spiltField = spiltFieldToChar(fieldChar, false);
						List<String> fdList = convertCharsToString(spiltField);

						if (!fn1.startsWith("_") && fdList.size() > 1 && !fn1.equals(beforeField)) {
							commonTypeData.add(fn1);
						}
					}
				}
			}
            String newField = null;
			if (commonTypeData.size() > 0 ) {
                 for(int j=0;j<commonTypeData.size();j++) {
                	 if(isCamelCase(commonTypeData.get(j).toString())) {
                		 camelCase+=1;
                	 }else if(isConstantFieldName(commonTypeData.get(j).toString())) {
						 constantCase+=1;
					}else if(isUnderscoreCase(commonTypeData.get(j).toString())) {
						 underCase+=1;
					}
                 }
                 if(camelCase>0 && camelCase>=constantCase && camelCase>=underCase && isCamelCase(beforeField)==false) { //需要满足驼峰
                	 if(isConstantFieldName(beforeField)) { //
                		newField =  constantToCamelCase(beforeField);
                		if(newField!=null && ! newField.equals(beforeField)) {
                			remmondCount12+=1;
                			int rct = recTypeCount(fieldType.toString());
                			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
                				rm12+=1;
                				correctTypeCount(rct);
                			}else {
//                				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                			}
                			res.add(newField);
                		}
                	 }else if(isUnderscoreCase(beforeField)) {
                		 newField = underscoreToCamelCase(beforeField);
                		 if(newField!=null && ! newField.equals(beforeField)) {
                 			remmondCount12+=1;
                 			int rct = recTypeCount(fieldType.toString());
                 			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
                 				rm12+=1;
                 				correctTypeCount(rct);
                 			}else {
//                 				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                 			}
                 			res.add(newField);
                 		}
                	 }
                 }else if(constantCase >0 && constantCase>=camelCase && constantCase >= underCase 
                		 && isConstantFieldName(beforeField)==false) {
                	 if(isCamelCase(beforeField)) {
                		 newField = camelCaseToConstant(beforeField);
                		 if(newField!=null && ! newField.equals(beforeField)) {
                 			remmondCount12+=1;
                 			int rct = recTypeCount(fieldType.toString());
                 			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
                 				rm12+=1;
                 				correctTypeCount(rct);
                 			}else {
//                				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                			}
                 			res.add(newField);
                 		}
                	 }else if(isUnderscoreCase(beforeField)) {
                		 newField = underscoreToConstant(beforeField);
                		 if(newField!=null && ! newField.equals(beforeField)) {
                  			remmondCount12+=1;
                  			int rct = recTypeCount(fieldType.toString());
                  			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
                  				rm12+=1;
                  				correctTypeCount(rct);
                  			}else {
//                				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                			}
                  			res.add(newField);
                  		}
					}                	 	 
                 }else if(underCase>0 && underCase>=constantCase && underCase>=camelCase && isUnderscoreCase(beforeField)==false) {
                	 if(isCamelCase(beforeField)) {
                		 newField = constantToUnderscore(beforeField);
                		 if(newField!=null && ! newField.equals(beforeField)) {
                  			remmondCount12+=1;
                  			int rct = recTypeCount(fieldType.toString());
                  			if(newField.equals(afterField)) {
                  				rm12+=1;
                  				correctTypeCount(rct);
                  			}else {
//                				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                			}
                  			res.add(newField);
                  		}
                	 }else if(isConstantFieldName(beforeField)) {
                		 newField = constantToUnderscore(beforeField);
                		 if(newField!=null && ! newField.equals(beforeField)) {
                   			remmondCount12+=1;
                   			int rct = recTypeCount(fieldType.toString());
                   			if(newField.equals(afterField)) {
                   				rm12+=1;
                   				correctTypeCount(rct);
                   			}else {
//                				System.out.println("rule1:"+beforeField+" "+afterField+" "+newField);
                			}
                   			res.add(newField);
                   		}
                	 }
                 }
                 
			} else {
				// 相同修饰符的没有， 直接执行规范2和3
				if(isModiferStatic && isConstantFieldName(beforeField)==false) {
				  newField = camelCaseToConstant(beforeField);
				  if(newField!=null && ! newField.equals(beforeField)) {
             			remmondCount1+=1;
             			int rct = recTypeCount(fieldType.toString());
             			if(newField.equals(afterField)) {
             				rm1+=1;
             				correctTypeCount(rct);
             			}
             			res.add(newField);
             		}
				}
				else 
					if(isModiferStatic==false && isCamelCase(beforeField)==false){
					 newField = constantToCamelCase(beforeField);
					 if(newField!=null && !newField.toLowerCase().equals(beforeField.toLowerCase()) 
							 && !beforeField.startsWith("_")) {
	             			remmondCount2+=1;
	             			int rct = recTypeCount(fieldType.toString());
	             			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
	             				rm2+=1;
	             				correctTypeCount(rct);
	             			}else {
//	             				System.out.println("reul2:"+beforeField+" "+afterField+" "+newField);
	             			}
	             			res.add(newField);
	             		}
				}
			}
		} else {
			// fd没有，直接执行规则2和3
			String newField = null;
			if(isModiferStatic && isConstantFieldName(beforeField)==false) {
				  newField = camelCaseToConstant(beforeField);
				  if(newField!=null && ! newField.equals(beforeField)) {
           			remmondCount1+=1;
           			int rct = recTypeCount(fieldType.toString());
           			if(newField.equals(afterField)) {
           				rm1+=1;
           				correctTypeCount(rct);
           			}
           			res.add(newField);
           		}
				}else if(isModiferStatic==false && isCamelCase(beforeField)==false){
					 newField = constantToCamelCase(beforeField);
					 if(newField!=null && ! newField.equals(beforeField) && !newField.toLowerCase().equals(beforeField.toLowerCase())
							 && !beforeField.startsWith("_")) {
	             			remmondCount2+=1;
	             			int rct = recTypeCount(fieldType.toString());
	             			if(newField.toLowerCase().equals(afterField.replace("_", "").toLowerCase())) {
	             				rm2+=1;
	             				correctTypeCount(rct);
	             			}else {
//	             				System.out.println("reul2:"+beforeField+" "+afterField+" "+newField);
	             			}
	             			res.add(newField);
	             		}
				}
		}

//		String fieldString = "";
//		boolean noType = true;
//		if (isModiferStatic) {
//			for (int r = 0; r < fieldDeclarations.size(); r++) {
//				FieldDeclaration fde = fieldDeclarations.get(r);
//				int mod = fde.getModifiers();
//				VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
//				String fn1 = vdf1.getName().getIdentifier();
//				if (mod == 25 || mod == 26 || mod == 24 || mod == 28) {
//					if (fn1.matches("[A-Z]+(_[A-Z]+)*")) {
//						noType = true;
//						break;
//					} else {
//						noType = false;
//					}
//				}
//
//			}
//
//			if (noType == false) {
//
//			} else {
////            
//				boolean matches = beforeField.matches("[A-Z]+(_[A-Z]+)*");
//				if (matches == false) {
////				System.out.println("错误地方" + beforeField);
//					char[][] newField = FieldNaming.generateConstantName(field, namePartsPtr, flag2);
//					List<String> fieldList = FieldNaming.convertCharsToString(newField);
//					if (!fieldList.get(0).equals(beforeField)) {
////				System.out.println("bf:" + beforeField + " " + "af:" + afterField + " " + "fdRemmond:" + fieldList.get(0));
////				allRemmondCount += 1;
//						remmondCount1 += 1;
//						fieldString = fieldList.get(0);
//						if (fieldList.get(0).toString().subSequence(0, 1).equals("_")) {
//							fieldString = fieldString.replaceFirst("_", "");
//						}
//
//						if (fieldString.equals(afterField)) {
////					correctCount += 1;
//							rm1 += 1;
//						} else {
////						System.out.println("规则1预测错误：" + beforeField + " " + afterField + " " + fieldList.get(0)+commitId.get(t));
//						}
//						res.add(fieldList.get(0));
//					}
//				}
//			}
//		} else {
//			// 是否当前文件不满足不是静态字段，但是全都大写
//			for (int r = 0; r < fieldDeclarations.size(); r++) {
//				FieldDeclaration fde = fieldDeclarations.get(r);
//				int mod = fde.getModifiers();
//				VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
//				String fn1 = vdf1.getName().getIdentifier();
//				if (mod == 25 || mod == 26 || mod == 24 || mod == 28) {
//				} else {
//					if (fn1.matches("[A-Z]+(_[A-Z]+)*")) {
//						noType = true;
//					} else {
//						noType = false;
//						break;
//					}
//				}
//
//			}
//			if (noType) {
//				// 推荐常量命名规范
//				boolean matches = beforeField.matches("[A-Z]+(_[A-Z]+)*");
//				if (matches == false) {
//					char[][] newField = FieldNaming.generateConstantName(field, namePartsPtr, flag2);
//					List<String> fieldList = FieldNaming.convertCharsToString(newField);
//					if (!fieldList.get(0).equals(beforeField)) {
//						remmondCount1 += 1;
//						fieldString = fieldList.get(0);
//						if (fieldList.get(0).toString().subSequence(0, 1).equals("_")) {
//							fieldString = fieldString.replaceFirst("_", "");
//						}
//						if (fieldString.equals(afterField)) {
//							rm1 += 1;
//						} else {
//						}
//						res.add(fieldList.get(0));
//					}
//				}
//			} else {
//
//				TypeDeclaration tp = getTypeDeclaration(fd);
//				if (tp != null && tp.isInterface()) {
//					// 不使用camel
//					char[][] newField = FieldNaming.generateConstantName(field, namePartsPtr, true);
//					List<String> fieldList = FieldNaming.convertCharsToString(newField);
//					if (!fieldList.get(0).equals(beforeField)) {
//						remmondCount1 += 1;
//						if (fieldList.get(0).toString().subSequence(0, 1).equals("_")) {
//							fieldList.get(0).replaceFirst("_", "");
//						}
//						if (fieldList.get(0).equals(afterField)) {
////						correctCount += 1;
//							rm1 += 1;
//						} else {
////						System.out.println("规则1预测错误：" + beforeField + " " + afterField + " " + fieldList.get(0));
//						}
//						res.add(fieldList.get(0));
//					}
//				} else if ((fieldType.toString().equals("float") || fieldType.toString().equals("int") //$NON-NLS-1$ //$NON-NLS-2$
//						|| fieldType.toString().equals("char") //$NON-NLS-1$ //$NON-NLS-2$
//						|| fieldType.toString().equals("long") || fieldType.toString().equals("double") //$NON-NLS-1$ //$NON-NLS-2$
//						|| fieldType.toString().equals("byte")) && fd.toString().contains("[]")
//						&& fd.toString().contains("static")) {
//
//					char[][] newField = FieldNaming.generateConstantName(field, namePartsPtr, true);
//					List<String> fieldList = FieldNaming.convertCharsToString(newField);
//					if (!fieldList.get(0).equals(beforeField)) {
//						remmondCount1 += 1;
//						if (fieldList.get(0).toString().subSequence(0, 1).equals("_")) {
//							fieldList.get(0).replaceFirst("_", "");
//						}
//						if (fieldList.get(0).equals(afterField)) {
////						correctCount += 1;
//							rm1 += 1;
//						} else {
////						System.out.println("规则1预测错误：" + beforeField + " " + afterField + " " + fieldList.get(0));
//						}
//						res.add(fieldList.get(0));
//					}
//				} else {
//					boolean underFlag = false;
//					for (int q = 0; q < fieldDeclarations.size(); q++) {
//
//					}
//					if (beforeField.substring(0, 1).equals("_")) {
//
//					} else {
//
//						camelNamingRecommend(field, beforeField, afterField, fieldStrings, i);
//					}
//				}
//			}
//		}
	}
	

	public static void saveData() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\BIT_Report\\testRule\\CARER_Time1.csv"));
            for (int j = 0; j < timeCarer.size(); j++) {
                bw.write(timeCarer.get(j).toString());
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
//						System.out.println("line:"+line);
						String item[] = line.split(",");
						projectName.add(item[0]);
						projectNames.add(item[0]);
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

	// 规则8
	public static void isRule8(int i, VariableDeclarationFragment vdf, List<String> oldField, String oldFieldName,
			String newField, Type fieldType) {
		if (i - 1 >= 0) {
			FieldDeclaration upFd = fieldDeclarations.get(i - 1);
			VariableDeclarationFragment upVdf = (VariableDeclarationFragment) upFd.fragments().get(0);
			if (upVdf.getInitializer() != null && vdf.getInitializer() != null) {
				String uFd = upVdf.getName().getIdentifier();
				List<String> upList = converList(uFd);
				List<String> newFieldList = converList(newField);
				String upExp = upVdf.getInitializer().toString();
				String fieldExp = vdf.getInitializer().toString();
				testRule8(upList, oldField, newFieldList, upExp, fieldExp, oldFieldName, newField, fieldType);
			}
		} else if (i + 1 < fieldDeclarations.size()) {
			FieldDeclaration downFd = fieldDeclarations.get(i + 1);
			VariableDeclarationFragment downVdf = (VariableDeclarationFragment) downFd.fragments().get(0);
			if (downVdf.getInitializer() != null && vdf.getInitializer() != null) {
				String dFd = downVdf.getName().getIdentifier();
				List<String> downList = converList(dFd);
				List<String> newFieldList = converList(newField);
				String downExp = downVdf.getInitializer().toString();
				String fieldExp = vdf.getInitializer().toString();
				testRule8(downList, oldField, newFieldList, downExp, fieldExp, oldFieldName, newField, fieldType);
			}
		}
	}

	public static List<String> converList(String field) {
		char[] fieldChar = field.toString().toCharArray();
		char[][] spiltField = spiltFieldToChar(fieldChar, false);// 拆分字符串
		return convertCharsToString(spiltField);
	}

	public static String testRule8(List<String> upField, List<String> oldField, List<String> newField, String upInitExp,
			String fieldInitExp, String oldfieldName, String newFieldName, Type fieldType) {
		if (upField.size() == oldField.size() && haveSameTokens(upField, oldField)) {
			List<String> tempList = new ArrayList<>();
			for (String ofd : oldField) {
				tempList.add(ofd);
			}
			similarityRule(upField, oldField, newField, upInitExp, fieldInitExp);
			if (!tempList.equals(oldField)) {
				remmondCount8 += 1;
				int rct = recTypeCount(fieldType.toString());
				if (oldField.equals(newField)) {
					rm8 += 1;
					correctTypeCount(rct);
//					System.out.println(
//							"OldName:" + oldfieldName + " " + "NewName:" + newFieldName + " " + "Exp:" + fieldInitExp);
				} else {
//					System.out.println("规则8推荐错误：" + oldField + " " + newField);
				}
				res.add(oldField.get(0));// 需要修改
			}

		}
		return null;
	}

	public static String similarityRule(List<String> upField, List<String> oldField, List<String> newField,
			String upInitExp, String fieldInitExp) {
		String[] upArray = upInitExp.split("[^a-zA-Z0-9]+");
		String[] fieldArray = fieldInitExp.split("[^a-zA-Z0-9]+");
		isContainsToken(upArray, upField);
		if (!repalcePosition.isEmpty()) {
			repalcePosition.forEach((key, value) -> {
				if (key < oldField.size() && value < fieldArray.length) {
					oldField.set(key, fieldArray[value]);
				}
			});
		}
		repalcePosition.clear();
		return null;
	}

	public static boolean haveSameTokens(List<String> list1, List<String> list2) {
		for (String ls : list1) {
			for (String ls2 : list2) {
				if (ls2.equals(ls)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void isContainsToken(String[] str, List<String> field1) {
		for (int n = 0; n < field1.size(); n++) {
			for (int i = 0; i < str.length; i++) {
				if (str[i].equals(field1.get(n))) {
					repalcePosition.put(n, i);
					break;
				}
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
//			System.out.println("1:" + sourceName);
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

	public static boolean isCamelCase(List<String> c) {

		for (int i = 0; i < c.size(); i++) {

			if (i != c.size() - 1) {
				if (c.get(i).charAt(0) >= 'a' && c.get(i).charAt(0) <= 'z') {
					return false;
				}
				if (c.get(i).contains("_")) {
					return false;
				}
			} else {
				if (c.get(i).charAt(0) >= 'A' && c.get(i).charAt(0) <= 'Z') {
					return false;
				}
				if (c.get(i).substring(1, c.get(i).length()).contains("_")) {
					return false;
				}
			}
		}
		return true;
	}

	public static TypeDeclaration getTypeDeclaration(FieldDeclaration fieldDeclaration) {
		ASTNode parentNode = fieldDeclaration.getParent();
		if (parentNode instanceof TypeDeclaration) {
			return (TypeDeclaration) parentNode;
		}
		return null;
	}

	public boolean isUnderCase(List<String> c) {
		for (int s = 0; s < c.size(); s++) {
			if (s == 0) {

			} else {
				if (!c.get(s).contains("_")) {
					return false;
				}
			}
		}
		return true;
	}

	private static char[][] generateConstantName(char[][] nameParts, int namePartsPtr, boolean onlyLongest) {
		char[][] names;
		if (onlyLongest) {
			names = new char[1][];
		} else {
			names = new char[namePartsPtr + 1][];
		}

		char[] namePart = CharOperation.toUpperCase(nameParts[0]);// ��һ�����ʴ�д
		int namePartLength = namePart.length;
		System.arraycopy(namePart, 0, namePart, 0, namePartLength);

		char[] name = namePart;

		if (!onlyLongest) {
			names[namePartsPtr] = name;
		}
		for (int i = 1; i <= namePartsPtr; i++) {
			namePart = CharOperation.toUpperCase(nameParts[i]);
			namePartLength = namePart.length;
			if (namePart[namePartLength - 1] != '_') {
				name = CharOperation.concat(namePart, name, '_');
			} else {
				name = CharOperation.concat(namePart, name);
			}

			if (!onlyLongest) {
				names[namePartsPtr - i] = name;
			}
		}
		if (onlyLongest) {
			names[0] = name;
		}
		return names;
	}

	public static void Array2CSV(List<RecordData> data, String path) throws IOException {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));

			for (RecordData dt : data) {
				String line = dt.getProjectPath() + "," + dt.getCommitId() + "," + dt.getBeforeField() + ","
						+ dt.getAfterField() + "," + dt.getClassPath();
				out.write(line + "\t\n");

			}

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Array2CSV1(List<SaveData> data, String path) throws IOException {
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
						+ dt.getNewRe() + "," + dt.getRmName() + "," + dt.getClassPath();
				out.write(line + "\t\n");
			}

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static void getFieldDeclaration(ASTNode cuu, final List<FieldDeclaration> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldDeclaration node) {
				types.add(node);
				return true;
			}
		});
	}

	private static void getReturnStatement(ASTNode cuu, final List<ReturnStatement> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(ReturnStatement node) {
				types.add(node);
				return true;
			}
		});
	}

	public static void getAssign(ASTNode cuu, final List<Assignment> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(Assignment node) {
				types.add(node);
				return true;
			}
		});
	}

	public static List<String> charToStrings(char[][] arr) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				list.add(String.copyValueOf(arr[i]));
			}
		}
		return list;
	}

	// 字符首字母大写
	public static String getFieldName(String fieldName) throws Exception {
		char[] chars = fieldName.toCharArray();
		chars[0] = toUpperCase(chars[0]);
		return String.valueOf(chars);
	}

	public static char toUpperCase(char c) {
		if (97 <= c && c <= 122) {
			c ^= 32;
		}
		return c;
	}

	public static String underLineToHump(String str) {
		String regex = "_(.)";
		Matcher matcher = Pattern.compile(regex).matcher(str);
		while (matcher.find()) {
			String target = matcher.group(1);
			str = str.replaceAll("_" + target, target.toUpperCase());
		}
		return str;
	}

	// list 转为 二维数组
	public static char[][] listToChar(List<String> field) {
		char[][] charArray = new char[field.size()][];
		for (int i = 0; i < field.size(); i++) {
			charArray[i] = field.get(i).toCharArray();
		}
		return charArray;
	}

	public static boolean hasDuplicates(List<String> list) {
		Map<String, Integer> map = new HashMap<>();
		for (String num : list) {
			if (map.containsKey(num)) {
				return true; // 存在重复元素
			}
			map.put(num, 1);
		}
		return false; // 不存在重复元素
	}

	// history ---------------------------
//	public static String getFieldFromHistory(int i, String beforeField, String afterField, String dataPath,
//			String beforePath) {
//		
//		char[] bf = beforeField.toCharArray();
//		char[][] bfs = spiltFieldToChar(bf, false);
//		List<String> bfList = convertCharsToString(bfs);
////		System.out.println("bfList:"+bfList);
////		letterToLowerCase(bfList);
//
//		boolean flag = false;
//		char[] af = afterField.toCharArray();
//		char[][] afs = spiltFieldToChar(af, false);
//		List<String> afList = convertCharsToString(afs);
////		letterToLowerCase(afList);// token 都变为小写
////		System.out.println("lt:"+afList);
//		List<String> tempBeforeList = new ArrayList<>();
//		for(int a=0;a<bfList.size();a++) {
//			tempBeforeList.add(bfList.get(a));
//		}
////		List<String> tempBeforeList = bfList;
//		String newFieldString = "";
//		if (commitBefore.equals(commitId.get(i))) {
////			if(beforePath.equals(dataPath)) {
//			// 路径相同
////			System.out.println(historyAdd+" "+historyRemove+" "+historyReplace);
////			if(fieldType==1 && isCamelCase(beforeField)) {
////				String newField =  camelCaseToConstant(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////				}
////			}
////			else if(fieldType==2&& isCamelCase(beforeField)) {
//////				c
////				String newField = camelCaseToUnderscore(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////				}
////			}
////			else if(fieldType==3 && isConstantFieldName(beforeField)) {
////				String newField = constantToCamelCase(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////				}
////			}
////			else if(fieldType==4 && isConstantFieldName(beforeField)) {
////				String newField = camelCaseToUnderscore(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////				}
////			}
////			else if(fieldType==5 && isUnderscoreCase(beforeField)) {
////				String newField = underscoreToCamelCase(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////				}
////			}
////			else if(fieldType==6 && isUnderscoreCase(beforeField)) {
////				String newField = underscoreToConstant(beforeField);
////				if(!newField.equals(beforeField)) {
////					rmCount+=1;
////					if(newField.equals(afterField)) {
////						historyCount += 1;
////					}else {
////						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
////					}
////					deleteToken(afList,bfList,beforeField,afterField);
////					return null;
////											
////			}
////			
//			
//			if(!historyField.isEmpty()) {
//				for (Map.Entry<String, String> entry : historyField.entrySet()) {
//					String key = entry.getKey();
//					if(beforeField.equals(key)) {
//						remmondCount0 += 1;
//						if(entry.getValue().equals(afterField)) {
//							rm0 += 1;
//						}else {
//							System.out.println("相同odlField推荐错误："+beforeField+" "+afterField+" "+entry.getValue());
//						}						
//						deleteToken(afList,bfList,beforeField,afterField);
//						return null;
//					}
//				}
//			}
//			
////			//字符串级别转换
////			if(!addString.isEmpty()) {
////				for (Map.Entry<String, Integer> entry : addString.entrySet()) {
////					if(entry.getValue()==1) {
////						String newField ="";
////						if(entry.getKey().length()>1) {
////						newField= beforeField+entry.getKey().substring(0, 1).toLowerCase()+entry.getKey().substring(1);
////						}else {
////							newField= beforeField+entry.getKey().substring(0).toLowerCase();
////						}
////						if(newField!=beforeField) {
////							rmCount+=1;
////							if(newField.equals(afterField)) {
////								historyCount+=1;
////							}else {
////								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
////							}
////							deleteToken(afList,bfList,beforeField,afterField);
////							return null;
////						}
////					}
////					else if(entry.getValue()==0) {
////						String newField =entry.getKey() + beforeField.substring(0,1).toUpperCase()+beforeField.substring(1);
////						if(newField!=beforeField) {
////							rmCount+=1;
////							if(newField.equals(afterField)) {
////								historyCount+=1;
////							}else {
////								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
////							}
////							deleteToken(afList,bfList,beforeField,afterField);
////							return null;
////						}
////					}
////				} 
////			}
//			
//			if(!removeString.isEmpty()) {
//				for (Map.Entry<String, Integer> entry : removeString.entrySet()) {
////					if(entry.getValue()==1) {
////						String newField = beforeField.substring(0,beforeField.indexOf(entry.getKey()));
////						if(newField!=beforeField) {
////							rmCount+=1;
////							if(newField.equals(afterField)) {
////								historyCount+=1;
////							}else {
////								System.out.println("字符删除推荐错误："+beforeField+" "+afterField+" "+newField);
////							}
////							deleteToken(afList,bfList,beforeField,afterField);
////							return null;
////						}
////					}
//					if(entry.getValue()==0 && entry.getKey().length()==1) {
//						String newField = beforeField.substring(1,2).toLowerCase()+beforeField.substring(2);
//						if(newField!=beforeField) {
//							remmondCount0+=1;
//							if(newField.equals(afterField)) {
//								rm0+=1;
//							}else {
//								System.out.println("字符删除推荐错误："+beforeField+" "+afterField+" "+newField);
//							}
//							deleteToken(afList,bfList,beforeField,afterField);
//							return null;
//						}
//					}
//				}
//			}
//			
//			//token级别转换
//			if (!historyReplace.isEmpty()) {
//				for (Map.Entry<String, String> entry : historyReplace.entrySet()) {
//					String key = entry.getKey();
//					for (int n = 0; n < bfList.size(); n++) {
//						if (bfList.get(n).equals(key)) {
//							bfList.set(n, entry.getValue());
//							flag = true;
//							break;
//						}
//					}
//					// Process the key-value pair
//				}
//				
//				Iterator<String> iterator = bfList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator.remove();
//				    }
//				}
//				
//				Iterator<String> iterator1 = afList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator1.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator1.remove();
//				    }
//				}
//								
//				if (flag && !bfList.equals(tempBeforeList)) {
//					remmondCount0 += 1;
//					if (bfList.equals(afList)||bfList.equals(afterField)) {
//						rm0 += 1;
////						System.out.println("推荐正确："+beforeField+" "+afterField);
//					}else {
//						System.out.println("替换推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
//					}
//					
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			if (!historyAdd.isEmpty()) {
////				System.out.println("111111");
////				System.out.println("add:"+historyAdd);
//				for (Map.Entry<String, Integer> entry : historyAdd.entrySet()) {
//					String key = entry.getKey();
//					if(entry.getValue()==-1) {bfList.add(key);}
//					else if(beforePath.equals(dataPath)&& entry.getValue()<bfList.size() && !bfList.contains(entry.getKey())) {
//						bfList.add(entry.getValue(), entry.getKey());
//						flag = true;
//					}
////					else {
////						if(key.length()==1 && entry.getValue()<bfList.size()) {
////							bfList.add(entry.getValue(), entry.getKey());
////							flag = true;
////						}
////					}
//					
////					for (int n = 0; n < bfList.size(); n++) {
////						if (bfList.get(n).equals(key)) {
////							if (entry.getValue() < bfList.size()) {
////								bfList.add(entry.getValue(), entry.getKey());
////								flag = true;
////								break;
////							}
////						}
////					}
//					// Process the key-value pair
//				}
//
//				
//				
//				Iterator<String> iterator = bfList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator.remove();
//				    }
//				}
//				
//				Iterator<String> iterator1 = afList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator1.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator1.remove();
//				    }
//				}
//				
//				if (flag && !bfList.equals(tempBeforeList)) {
//					remmondCount0 += 1;
//					if (bfList.equals(afList)) {
//						rm0 += 1;
////						System.out.println("推荐正确："+beforeField+" "+afterField);
//					}else {
//						System.out.println("添加推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
//					}
//					
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//
//			}
//			if (!historyRemove.isEmpty()) {
//				for (Map.Entry<String, Integer> entry : historyRemove.entrySet()) {
//					String key = entry.getKey();
//					for (int n = 0; n < bfList.size(); n++) {
//						if (bfList.get(n).equals(key)) {
//							bfList.remove(entry.getValue());
//							flag = true;
//							break;
//						}
//					}
//				}								
//				
//				Iterator<String> iterator = bfList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator.remove();
//				    }
//				}
//				
//				Iterator<String> iterator1 = afList.iterator();
//				while (iterator.hasNext()) {
//				    String element = iterator1.next();
//				    if (element == null || element.isEmpty()) {
//				        iterator1.remove();
//				    }
//				}
//				
//				if (flag && !bfList.equals(tempBeforeList)) {
//					remmondCount0 += 1;
//					if (bfList.equals(afList)) {
//						rm0 += 1;
////						System.out.println("推荐正确："+beforeField+" "+afterField);
//					}else {
//						System.out.println("删除推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
//					}
//					
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}				
//			
//			}
//		} else {
//			historyAdd.clear();
//			historyRemove.clear();
//			historyReplace.clear();
//			historyField.clear();
//			fieldType = 0;
//			addString.clear();
//			removeString.clear();
//			commitBefore = commitId.get(i);
//			// 比较两个list，存入map
//			deleteToken(bfList, afList, beforeField, afterField);
//		}
//		
//		return null;
//	}

	public static boolean isConstantFieldName(String fieldName) {
		// 检查是否以大写字母或下划线开头
		if (!Character.isUpperCase(fieldName.charAt(0)) && fieldName.charAt(0) != '_') {
			return false;
		}

		// 检查是否只包含大写字母、下划线和数字
		for (int i = 1; i < fieldName.length(); i++) {
			char c = fieldName.charAt(i);
			if (!Character.isUpperCase(c) && !Character.isDigit(c) && c != '_') {
				return false;
			}
		}

		return true;
	}

	public static String camelCaseToUnderscore(String camelCase) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < camelCase.length(); i++) {
			char c = camelCase.charAt(i);
			if (Character.isUpperCase(c)) {
				// 如果是大写字母，则在前面插入下划线，并将字母转换为小写
				if (i > 0) {
					result.append('_');
				}
				result.append(Character.toLowerCase(c));
			} else {
				// 如果是小写字母或数字，则直接添加到结果中
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String underscoreToCamelCase(String underscore) {
		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = false;

		for (int i = 0; i < underscore.length(); i++) {
			char c = underscore.charAt(i);

			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(c);
				}
			}
		}

		return result.toString();
	}

	public static String camelCaseToConstant(String camelCase) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < camelCase.length(); i++) {
			char c = camelCase.charAt(i);

			if (Character.isUpperCase(c)) {
				result.append('_');
				result.append(Character.toUpperCase(c));
			} else {
				result.append(Character.toUpperCase(c));
			}
		}

		return result.toString();
	}

	public static String constantToCamelCase(String constant) {
		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = false;

		for (int i = 0; i < constant.length(); i++) {
			char c = constant.charAt(i);

			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(Character.toLowerCase(c));
				}
			}
		}

		return result.toString();
	}

	public static String underscoreToConstant(String underscore) {
		StringBuilder result = new StringBuilder();
		boolean capitalizeNext = false;

		for (int i = 0; i < underscore.length(); i++) {
			char c = underscore.charAt(i);

			if (c == '_') {
				capitalizeNext = true;
			} else {
				if (capitalizeNext) {
					result.append('_');
					result.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					result.append(Character.toUpperCase(c));
				}
			}
		}

		return result.toString();
	}

	public static String constantToUnderscore(String constant) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < constant.length(); i++) {
			char c = constant.charAt(i);

			if (Character.isUpperCase(c)) {
				if (i > 0) {
					result.append('_');
				}
				result.append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}
   
	public static void correctTypeCount(int nums) {
		if(nums==0) {
			 baseType +=1;
		}else if(nums==1) {
			customType +=1;
		}else {
		   otherType +=1;
		}
	}
	
	public static int recTypeCount(String fieldType) {
		if(fieldType.equals("float") || fieldType.equals("int")
				|| fieldType.equals("char") || fieldType.equals("short")
				|| fieldType.equals("long") || fieldType.equals("double") //$NON-NLS-1$ //$NON-NLS-2$
				|| fieldType.equals("boolean") || fieldType.equals("byte")) {
			 recBaseType+=1;
			 return 0;
		}else if(fieldType.equals("String") || fieldType.contains("List") || fieldType.contains("Map")) {
			recOtherType+=1;
			return 2;
 		}else if(projectJavaList.size()>0){
 			 boolean isContians = false;
	         for(int a=0;a<projectJavaList.size();a++) {	       
	        	 String proString = projectJavaList.get(a).replace(".java", "");	        	 
	        	 if(proString.contains(fieldType)) {
	        		 isContians = true;
	        		 recCustomType+=1;
	        		 return 1;
	        	 }
	         }
	         if(isContians==false) {
	        	 recOtherType+=1;
	        	 return 2;
	         }
		}else {
		   recOtherType+=1;
		   return 2;
		}
		return 2; 
	}
	
	public static boolean isCamelCase(String str) {
		// 检查是否以字母开头
		if (!Character.isLetter(str.charAt(0))) {
			return false;
		}

		// 检查是否包含非法字符
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_') {
				return false;
			}
		}

		// 检查是否符合驼峰规范
		boolean uppercaseExpected = false;
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				if (!uppercaseExpected) {
					return false;
				}
				uppercaseExpected = false;
			} else if (c == '_') {
				uppercaseExpected = true;
			}
		}

		return true;
	}

	public static boolean isUnderscoreCase(String str) {
		// 检查是否以字母开头
		if (!Character.isLetter(str.charAt(0))) {
			return false;
		}

		// 检查是否包含非法字符
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_') {
				return false;
			}
		}

		// 检查是否符合下划线规范
		if (str.startsWith("_") || str.endsWith("_")) {
			return false;
		}

		if (str.contains("__")) {
			return false;
		}

		return true;
	}

}
