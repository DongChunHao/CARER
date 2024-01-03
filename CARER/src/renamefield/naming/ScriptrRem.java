package renamefield.naming;

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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.plaf.synth.SynthScrollPaneUI;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import renamefield.dealField.GenerateNames;
import renamefield.handlers.SampleHandler;
import renamefield.projectPath.RenameData;

public class ScriptrRem {
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> preNewList = new ArrayList<>();
	static List<String> preOldList = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();
	static List<String> projectList = new ArrayList<String>();
	static List<String> fieldData = new ArrayList<String>();
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();
	static List<String> otherList = new ArrayList<>();
	static List<RenameData> allDatas = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		String filePathString = "int_String";
		filePathString=humpToUnderline(filePathString);
		System.out.println("file:"+filePathString);
		String projectNameString = "";
		String csvPath = "../dataset/test.csv";
		String csvPath1 = "../dataset/Rule2.csv";
		String saveCSV = "../dataset/result.csv";
		String fieldName = "";
		boolean flag1 = true;
		boolean flag2;
		boolean flagBoolean;
		int num = 0;
		int j = 0;
		int f = 0;
		String tempString = "";
		int rule2count = 0;
		readFile(csvPath);
		SampleHandler sHandler = new SampleHandler();

		CamelRule cRule = new CamelRule();
		GenerateNames fNaming = new GenerateNames();
		for (int n = 0; n < commitId.size(); n++) {
			String projectname = "D:\\AllProject\\dataset\\" + projectName.get(n);

			projectNameString = projectName.get(n);
			String cmd = "cmd /c D: && cd " + projectname + " " + "&& git reset --hard" + " " + commitId.get(n);
			Runtime run = Runtime.getRuntime();
			try {
				Process process = run.exec(cmd);
				Thread.sleep(1000);
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
						if (!otherList.isEmpty()) {
							otherList.clear();
						}
						getype(cu, fieldDeclarations);
//						for (int t = 0; t < fieldDeclarations.size(); t++) {
//							FieldDeclaration f = fieldDeclarations.get(t);
//							VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
//							fieldName = vdf.getName().getIdentifier();
//							if (!fieldName.equals(preNewList.get(n)) && !fieldName.equals(preOldList.get(n))) {
////								System.out.println("preNew:"+preNewList.get(n));
//								char[] chs1 = fieldName.toCharArray();
//								if (f.getModifiers() != 25 && f.getModifiers() != 26 && f.getModifiers() != 24) {
//									char[][] s1 = sHandler.spiltFieldToChar(chs1, false);
//									List<String> sList = convertCharsToString(s1);
//									flag2 = sHandler.isCamelCase(sList);
//									if (sList.size() >= 2) {
//										if (flag2 == true) {
//											flag1 = flag2;
//											otherList.clear();
//											break;
//										}
//										otherList.add(fieldName);
////											System.out.println("otherList:"+fieldName);										
//									}
//								}
//							}
//						}
//						int count = 0;
//						for (int r = 0; r < otherList.size(); r++) {
////							System.out.println("otherList:" + otherList.get(r));
//							if (cRule.isUnderCase(otherList.get(r)) == true
//									&& otherList.get(r).substring(1, otherList.get(r).length())
//											.substring(0, otherList.get(r).length() - 1).contains("_")) {
//								count += 1;
//							} else {
//								otherList.clear();
//								break;
//							}
//						}
//						if (count == otherList.size() && otherList.size() > 0) {
//							flag1 = false;
//						}
						if (flag1 == true) {

							for (int e = 0; e < fieldDeclarations.size(); e++) {
								FieldDeclaration fd = fieldDeclarations.get(e);
								VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
								String fieldName1 = vdf.getName().getIdentifier();// 新的field 名字
								// public static final 25; public static final 26；static 9; final 17 ; no static
								// final 1
								if (fieldName1.equals(preNewList.get(n))) {
									String oldNameString = preOldList.get(n);// 旧名字
									char[] chs = preOldList.get(n).toCharArray();// 旧field字符
									boolean f3 = false;
									if (fd.getModifiers() == 25 || fd.getModifiers() == 26 || fd.getModifiers() == 24) {
										f3 = true;
									}
									char[][] s = sHandler.spiltFieldToChar(chs, f3);// 拆分旧字符二维
									List<String> oldList = charToStrings(s);// 旧名字token

									List<String> fieldSpiltList = convertCharsToString(s); // 字符转变为token
									if (e - 1 >= 0 && e + 1 < fieldDeclarations.size()) {// 判断是否存在pre next
										FieldDeclaration fd1 = fieldDeclarations.get(e - 1);
										VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fieldDeclarations
												.get(e - 1).fragments().get(0);
										String fn1 = vdf1.getName().getIdentifier();
										boolean f1 = false;
										char[] fdChar1 = fn1.toCharArray();
										if (fd1.getModifiers() == 25 || fd1.getModifiers() == 26
												|| fd1.getModifiers() == 24) {
											f1 = true;
										}
										char[][] s1 = sHandler.spiltFieldToChar(fdChar1, f1);// 拆分pre
										List<String> preList = charToStrings(s1);

										FieldDeclaration fd2 = fieldDeclarations.get(e + 1);
										VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fieldDeclarations
												.get(e + 1).fragments().get(0);
										String fn2 = vdf2.getName().getIdentifier();// next name
										boolean f2 = false;
										char[] fdChar2 = fn2.toCharArray();
										if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26
												|| fd2.getModifiers() == 24) {
											f2 = true;
										}
										char[][] s2 = sHandler.spiltFieldToChar(fdChar2, f2);// 拆分field
										List<String> nextList = charToStrings(s2);// tokens
										if (preList.get(preList.size() - 1).equals(nextList.get(nextList.size() - 1))) {// pre
																														// ==
																														// next
											// 判断当前field是否包含相同规范
											if (preList.get(preList.size() - 1)
													.equals(oldList.get(oldList.size() - 1))) {
												// rule3
											} else {
												// 推荐规范+field
												String newField = "";

												System.out.println("前后存在命名规范，首规范");
												rule2count += 1;
												if (preOldList.get(n).contains(preList.get(preList.size() - 1))) {// 旧名字中是否包含相同token
													// userNameTest->nameTest
													newField = "推荐删除";
												} else {

													if (preList.get(preList.size() - 1).endsWith("_")
															|| preList.get(preList.size() - 1).endsWith("$")) {
														newField = preList.get(preList.size() - 1) + preOldList.get(n);
													} else {
														tempString = getFieldName(preOldList.get(n));
														if ((oldList.size() + 1 > preList.size())
																&& (oldList.size()+1 > nextList.size())) {// 大于两个field长度
															// 替换field,
															newField = "推荐替换";
														} else {
															newField = preList.get(preList.size() - 1) + tempString;
														}
													}
//												System.out.println("newField："+newField);
//												System.out.println("preNew:"+preNewList.get(n));
													if (newField.equals(preNewList.get(n))) {
														f += 1;
													} else {
														RenameData rData0 = new RenameData();
														rData0.setCommit(commitId.get(n));
														rData0.setProjectName(projectName.get(n));
														rData0.setOldFieldName(preOldList.get(n));
														rData0.setNewFieldName(preNewList.get(n));
														rData0.setFieldRem(newField);
														rData0.setPath(classPath.get(n));
														allDatas.add(rData0);
													}
//												System.out.println("commit:" + commitId.get(n));
//												System.out.println("old field:" + preOldList.get(n));
//												System.out.println("new field:" + preNewList.get(n));
												}
											}
										} else {
											if (preList.get(0).equals(nextList.get(0))) {
												if (preList.get(0).equals(oldList.get(0))) {

												} else {
													String newField = "";
													// field+尾命名规范
													System.out.println("前后存在命名规范，尾规范");
													rule2count += 1;
													if (preOldList.get(n).contains(preList.get(preList.size() - 1))) {
														// 推荐删除
														newField = "建议删除";
													} else {
														if (oldList.size() + 1 > preList.size()
																&& oldList.size()+1 > nextList.size()) {
															// 推荐替换
															newField = "建议替换";
														} else {
															newField = preOldList.get(n) + preList.get(0);
														}
														if (newField.equals(preNewList.get(n))) {
															f += 1;
														} else {

															RenameData rData = new RenameData();
															rData.setCommit(commitId.get(n));
															rData.setProjectName(projectName.get(n));
															rData.setOldFieldName(preOldList.get(n));
															rData.setNewFieldName(preNewList.get(n));
															rData.setFieldRem(newField);
															rData.setPath(classPath.get(n));
															allDatas.add(rData);
														}

													}
												}
											}
										}
									} else if (e - 2 >= 0 && e - 1 > 0) {
										FieldDeclaration fde = fieldDeclarations.get(e - 2);
										VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments()
												.get(0);
										String fn1 = vdf1.getName().getIdentifier();
										boolean f1 = false;
										char[] fdChar1 = fn1.toCharArray();
										if (fde.getModifiers() == 25 || fde.getModifiers() == 26
												|| fde.getModifiers() == 24) {
											f1 = true;
										}
										char[][] s1 = sHandler.spiltFieldToChar(fdChar1, f1);
										List<String> preList = charToStrings(s1);

										FieldDeclaration fd2 = fieldDeclarations.get(e - 1);
										VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments()
												.get(0);
										String fn2 = vdf2.getName().getIdentifier();
										boolean f2 = false;
										char[] fdChar2 = fn2.toCharArray();
										if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26
												|| fd2.getModifiers() == 24) {
											f2 = true;
										}
										char[][] s2 = sHandler.spiltFieldToChar(fdChar2, f2);
										List<String> nextList = convertCharsToString(s2);
//										System.out.println("fList2:"+fList2.get(0));
										if (preList.get(preList.size() - 1).equals(nextList.get(nextList.size() - 1))) {
											// 判断当前field是否包含相同规范
											if (preList.get(preList.size() - 1)
													.equals(oldList.get(oldList.size() - 1))) {
												// 推荐规范+field
											} else {
												String newField = "";
												System.out.println("两个前面field,存在首token规范");
												rule2count += 1;
												if (preOldList.get(n).contains(preList.get(preList.size() - 1))) {// 旧名字中是否包含相同token
													// userNameTest->nameTest
													newField = "推荐删除";
												} else {
													if (preList.get(preList.size() - 1).endsWith("_")
															|| preList.get(preList.size() - 1).endsWith("$")) {
														newField = preList.get(preList.size() - 1) + preOldList.get(n);
													} else {
														tempString = getFieldName(preOldList.get(n));
														if (oldList.size() + 1 > preList.size()
																&& oldList.size()+1 > nextList.size()) {// 大于两个field长度
															// 替换field,
															newField = "推荐替换";
														} else {
															newField = preList.get(preList.size() - 1) + tempString;
														}
													}
//													System.out.println("newField:" + newField);
//													System.out.println("preList:" + preNewList.get(n));
													if (newField.equals(preNewList.get(n))) {
														f += 1;
													} else {
														RenameData rData = new RenameData();
														rData.setCommit(commitId.get(n));
														rData.setProjectName(projectName.get(n));
														rData.setOldFieldName(preOldList.get(n));
														rData.setNewFieldName(preNewList.get(n));
														rData.setFieldRem(newField);
														rData.setPath(classPath.get(n));
														allDatas.add(rData);
													}
												}
											}
										} else {
											if (preList.get(0).equals(nextList.get(0))) {
//											System.out.println("3333333333");
												if (preList.get(0).equals(oldList.get(0))) {
													// field+尾命名规范
												} else {
													String newField = "";
													System.out.println("两个前面field,存在尾token规范");
													rule2count += 1;
//													System.out.println("commit:" + commitId.get(n));
//													System.out.println("newField：" + preNewList.get(n));
													if (preOldList.get(n).contains(preList.get(preList.size() - 1))) {
														// 推荐删除
														newField = "建议删除";
													} else {
														if (oldList.size() + 1 > preList.size()
																&& oldList.size()+1 > nextList.size()) {
															// 推荐替换
															newField = "建议替换";
														} else {
															newField = preOldList.get(n) + preList.get(0);
														}
														if (newField.equals(preNewList.get(n))) {
															f += 1;
														} else {
															RenameData rData = new RenameData();
															rData.setCommit(commitId.get(n));
															rData.setProjectName(projectName.get(n));
															rData.setOldFieldName(preOldList.get(n));
															rData.setNewFieldName(preNewList.get(n));
															rData.setFieldRem(newField);
															rData.setPath(classPath.get(n));
															allDatas.add(rData);
														}
													}
												}
											}
										}
									} else if (e + 2 < fieldDeclarations.size() && e + 1 < fieldDeclarations.size()) {
										FieldDeclaration fd1 = fieldDeclarations.get(e + 1);
										VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fd1.fragments()
												.get(0);
										String fn1 = vdf1.getName().getIdentifier();
										boolean f1 = false;
										char[] fdChar1 = fn1.toCharArray();
										if (fd1.getModifiers() == 25 || fd1.getModifiers() == 26
												|| fd1.getModifiers() == 24) {
											f1 = true;
										}
										char[][] s1 = sHandler.spiltFieldToChar(fdChar1, f1);
										List<String> fList1 = charToStrings(s1);
//										System.out.println("fList1:"+fList1.get(0));

										FieldDeclaration fd2 = fieldDeclarations.get(e + 2);
										VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments()
												.get(0);
										String fn2 = vdf2.getName().getIdentifier();
										boolean f2 = false;
										char[] fdChar2 = fn2.toCharArray();
										if (fd2.getModifiers() == 25 || fd2.getModifiers() == 26
												|| fd2.getModifiers() == 24) {
											f2 = true;
										}
										char[][] s2 = sHandler.spiltFieldToChar(fdChar2, f2);
										List<String> fList2 = charToStrings(s2);
//										System.out.println("fList2:"+fList2.get(0));
										if (fList1.get(fList1.size() - 1).equals(fList2.get(fList2.size() - 1))) {
											// 判断当前field是否包含相同规范
											if (fList1.get(fList1.size() - 1).equals(oldList.get(oldList.size() - 1))) {

											} else {
												// 推荐规范+field
												String newField = "";
												System.out.println("两个后面field,存在首token规范");
//												System.out.println("commit:" + commitId.get(n));
//												System.out.println("newField：" + preNewList.get(n));
												rule2count += 1;
												if (preOldList.get(n).contains(fList1.get(fList1.size() - 1))) {
													newField = "建议删除";
												} else {

													if (fList1.get(fList1.size() - 1).endsWith("_")
															|| fList1.get(fList1.size() - 1).endsWith("$")) {
														newField = fList1.get(fList1.size() - 1) + preOldList.get(n);
													} else {
														tempString = getFieldName(preOldList.get(n));
														if (oldList.size() + 1 > fList1.size()
																&& oldList.size()+1 > fList2.size()) {// 大于两个field长度
															// 替换field,
															newField = "推荐替换";
														} else {
															newField = fList1.get(fList1.size() - 1) + tempString;
														}
													}
													if (newField.equals(preNewList.get(n))) {
														f += 1;
													} else {
														RenameData rData = new RenameData();
														rData.setCommit(commitId.get(n));
														rData.setProjectName(projectName.get(n));
														rData.setOldFieldName(preOldList.get(n));
														rData.setNewFieldName(preNewList.get(n));
														rData.setFieldRem(newField);
														rData.setPath(classPath.get(n));
														allDatas.add(rData);
													}
												}
											}
										} else {
											if (fList1.get(0).equals(fList2.get(0))) {
//											System.out.println("3333333333");
												if (fList1.get(0).equals(oldList.get(0))) {
												} else {
													// field+尾命名规范
													String newField = "";
													System.out.println("两个后面field,存在尾token规范");
													System.out.println("commit:" + commitId.get(n));
													System.out.println("newField：" + preNewList.get(n));
													rule2count += 1;
//													System.out.println("commit:" + commitId.get(n));
//													System.out.println("old field:" + preOldList.get(n));
//													System.out.println("new field:" + preNewList.get(n));
													System.out.println(oldList);
													System.out.println(oldList.size());
													System.out.println(fList1.size());
													System.out.println(fList2.size());
													if (oldList.contains(fList1.get(0))) {
														newField = "建议删除";
													} else {
														if (oldList.size() + 1 > fList1.size()
																&& oldList.size() + 1 > fList2.size()) {
															newField = "建议替换";
														} else {
															newField = preOldList.get(n) + fList1.get(0);
														}
													}
													if (newField.equals(preNewList.get(n))) {
														f += 1;
													} else {
														RenameData rData = new RenameData();
														rData.setCommit(commitId.get(n));
														rData.setProjectName(projectName.get(n));
														rData.setOldFieldName(preOldList.get(n));
														rData.setNewFieldName(preNewList.get(n));
														rData.setFieldRem(newField);
														rData.setPath(classPath.get(n));
														allDatas.add(rData);
													}
												}
											}
										}
									} else {
//										System.out.println("没有命名规范");
									}

//_______________________________________________________________________________________________________________________________									
									if (fd.getModifiers() == 25 || fd.getModifiers() == 26 || fd.getModifiers() == 24) {
//										boolean flagC = sHandler.isStaticFinal(fieldSpiltList);
//										if (flagC == true) {
//											// 满足staic final 下一条规则
//										} else {
//											if (fieldSpiltList.size() == 1) {
//												flagBoolean = true;
//										  char[][]reField =  fNaming.generateConstantName(s, fieldSpiltList.size(), flagBoolean);
//											} else {
//												flagBoolean = false;
//											}
//										  char[][]reField =  fNaming.generateConstantName(s, fieldSpiltList.size(), flagBoolean);

											// 驼峰变大写
//										}
									} else {
										// 推荐

									}
								}

								// contant field
								if (fd.getModifiers() == 25 || fd.getModifiers() == 26 || fd.getModifiers() == 24) {
//										
//										boolean flag = sHandler.isStaticFinal(s);
//										if (flag == false) {
//											System.out.println(s);
//											num+=1;
//											if (chs.length == 0) {
//												return CharOperation.NO_CHAR_CHAR;
//											} else if (chs.length == 1) {
//												return generateConstantName(new char[][] { CharOperation.toLowerCase(chs) }, 0, true);// 推荐大写field
//											} else {
//												return generateConstantName(s, namePartsPtr, false);
//											}
//										}
								}
							}
						} else {
							String newField = cRule.humpToUnderline(preOldList.get(n));
							for (int e = 0; e < fieldDeclarations.size(); e++) {
								FieldDeclaration fd = fieldDeclarations.get(e);
								VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
								String fieldName1 = vdf.getName().getIdentifier();
								char[] chs = fieldName1.toCharArray();
								if (fieldName1.equals(preNewList.get(n)) || fieldName1.equals(preOldList.get(n))) {
									if (fd.getModifiers() == 25 || fd.getModifiers() == 26 || fd.getModifiers() == 24) {
										fieldName1 = fieldName1.toUpperCase();
									}
								}
							}
							// 程序满足下划线命名法
//							System.out.println("222222222222222");
							num += 1;
//							System.out.println("project："+projectList.get(n));
//							System.out.println("commit:" + commitId.get(n));
//							System.out.println("old field:"+preOldList.get(n));
//							System.out.println("new field:" + preNewList.get(n));
							if (newField.equals(preNewList.get(n))) {
								j += 1;
//								System.out.println("newfield:" + newField);
							}
						}
						flag1 = true;
					}
				}
			}

		}
		System.out.println("rule2Count:" + rule2count);
		System.out.println("推荐正确数量：" + f);
//		System.out.println("num:" + num);
//		System.out.println("j:" + j);
		try {
			Array2CSV(allDatas, saveCSV);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static String humpToUnderline(String str) {
        String regex = "([A-Z])";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group();
            str = str.replaceAll(target, "_"+target.toLowerCase());
        }
        return str;
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

	// 获取FieldDeaclaration
	private static void getype(ASTNode cuu, final List<FieldDeclaration> types) {
		cuu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldDeclaration node) {
				types.add(node);
				return true;
			}
		});
	}

// 变为完整field名，选择第一个就是最终结果
	private static List<String> convertCharsToString(char[][] c) {
		int length = c == null ? 0 : c.length;
		String[] s = new String[length];
		List<String> sList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			if (c[i] == null) {
				break;
			}
			s[i] = String.valueOf(c[i]);
			sList.add(s[i]);
		}
		return sList;
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
						+ dt.getNewFieldName()+ ","+ dt.getFieldRem() + "," + dt.getPath();
//					out.write(line + "\t\n");
				out.write(line + "\t\n");
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	// 二维数组获取第一个token 和最后一个token
	public static String charToTokenPre(char[][] c) {
		char[] n1 = c[0];
		return String.copyValueOf(n1);
	}

	public static String charToTokenNext(char[][] c) {
		char[] n2 = Arrays.copyOf(c[c.length - 1], c[c.length - 1].length);
		return String.copyValueOf(n2);
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
	//删除前面token
	public static List<String> removeListPre(List<String> list, int i){
		for(int n=0;n<list.size();n++) {
			if(n<i) {
				list.remove(n);
			}
		}
		return list;		
	}
	//删除后面token
	public static List<String> removeListNext(List<String> list, int i){
		for(int n=0;n<list.size();n++) {
			if(n>i) {
				list.remove(n);
			}
		}
		return list;		
	}
	// list 转string
	public static String listToString(List<String> list) {
		return list.stream().collect(Collectors.joining(""));		
	}
    //确定token 位置,还需要继续修改
	public static String posToken(List<String> list, String token, boolean flag) throws Exception {
		if(flag) {//判断是首token,还是尾token
			token = getFieldName(token);
			if(list.contains(token)) {				
				List<String> f1 = removeListPre(list, list.indexOf(token));
				return listToString(f1);
			}
		}else {
			if(list.contains(token)) {
				List<String> f2 = removeListNext(list, list.indexOf(token));
				return listToString(f2);
			}
		}
		return null;//null证明不包含token
	}
}
