package renamefield.test;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

public class FromExpression {

	
		public static int namePartsPtr = -1;
		static List<FieldDeclaration> fieldDeclarations = new ArrayList<>();
		static List<FieldAccess> fieldAccess = new ArrayList<>();
		static LinkedHashSet<String> res = new LinkedHashSet<>();
		static int allRemmondCount = 0;
		static int correctCount = 0;
		static int existCount = 0;
		static int top1 = 0;
		static int topAll = 0;
		static List<String> commitId = new ArrayList<>();
		static List<String> classPath = new ArrayList<>();
		static List<String> projectName = new ArrayList<>();// 项目名
		static List<String> beforeField = new ArrayList<>();
		static List<String> afterField = new ArrayList<>();
		static List<String> afterType = new ArrayList<>();
		static boolean flag2 = false;
		static boolean flag1 = false;
		int configurer;
	    static int accessCount =0;
	    static boolean ifFieldAccess=false;
		public static void main(String[] args) throws InterruptedException, NoHeadException, IOException, GitAPIException {
			String csvPath = "../dataset/test.csv";
			readFile(csvPath);
			for (int i = commitId.size() - 1; i >= 0; i--) {
				String projectname = "D:\\AllProject\\dataset\\" + projectName.get(i);
				String gitString = "D:\\AllProject\\dataset\\" + projectName.get(i) + "\\.git";
				File folder = new File(gitString);
				File[] files = folder.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.getName().equals("index.lock")) {
							file.delete();
						}
					}
				}
				String cmd = "cmd /c D: && cd " + projectname + " " + "&& git reset --hard" + " " + commitId.get(i);
				Runtime run = Runtime.getRuntime();
				try {
					Process process = run.exec(cmd);
					Thread.sleep(1000);
				} catch (IOException e) {
					e.printStackTrace();
				}
				boolean flags = GitReoisitory.hasSwitchedToCommit(gitString, commitId.get(i));
				if (flags == false) {
					Thread.sleep(1000);
				}

				File fileAfter = new File(classPath.get(i));
				if (fileAfter.exists()) {

					CompilationUnit cu = getCompilationUnit(classPath.get(i));
					if (!fieldDeclarations.isEmpty()) {
						fieldDeclarations.clear();
					}
					getFieldDeclaration(cu, fieldDeclarations);
					for (int s = 0; s < fieldDeclarations.size(); s++) {
						FieldDeclaration fd = fieldDeclarations.get(s);
						VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
						String fieldName = vdf.getName().getIdentifier();
						if(fieldName.equals(afterField.get(i))) {
							Type fieldType = fd.getType();
							int modifier = fd.getModifiers();
							// static final 25, 26, 24；static 9; final 17 ; no static final 1
							char[] fieldChar = beforeField.get(i).toString().toCharArray();
							if (modifier == 25 || modifier == 26 || modifier == 24 || modifier == 28) {
								flag1 = true;// modifier是否为static final
							}
							char[][] spiltField = spiltFieldToChar(fieldChar, flag1);
						}
					}
					

//					if (!fieldAccess.isEmpty()) {
//						fieldAccess.clear();
//					}
//					getFieldAccess(cu, fieldAccess);
//					for (int s = 0; s < fieldAccess.size(); s++) {
//						if (fieldAccess.get(s).toString().contains(afterField.get(i))) {
////							System.out.println("fieldAccess:" + fieldAccess.get(s));
////							System.out.println("parent:"+fieldAccess.get(s).getParent());
//							if(fieldAccess.get(s).getParent().getNodeType()==ASTNode.ASSIGNMENT) {
//								Assignment assignment = (Assignment) fieldAccess.get(s).getParent();
////								System.out.println("assignment:"+" "+assignment.getLeftHandSide()+" "+assignment.getRightHandSide());
//								Expression exp = assignment.getRightHandSide();
//								allRemmondCount+=1;
//								if(exp.toString().equals(afterField.get(i))) {
//									accessCount+=1;
////									System.out.println("推荐正确："+beforeField.get(i)+" "+afterField.get(i)+" "+exp.toString());
//									break;
//								}else {
//									System.out.println("推荐错误例子："+beforeField.get(i)+" "+afterField.get(i)+" "+exp.toString());
//									break;
//								}
//							}
//						}
//					}
//					
//					for (int t = 0; t < fieldDeclarations.size(); t++) {
//						FieldDeclaration fd = fieldDeclarations.get(t);
//						VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
	//
//						String fieldName = vdf.getName().getIdentifier();
//						if (fieldName.equals(afterField.get(i))) {
//							res.clear();
//							existCount += 1;
//							Type fieldType = fd.getType();
//							int modifier = fd.getModifiers();
//							// static final 25, 26, 24；static 9; final 17 ; no static final 1
//							char[] fieldChar = beforeField.get(i).toString().toCharArray();
//							// contant field
//							if (modifier == 25 || modifier == 26 || modifier == 24 || modifier == 28) {
//								flag1 = true;// modifier是否为static final
//							}
//							char[][] spiltField = spiltFieldToChar(fieldChar, flag1);// 拆分字符串
//							List<String> fieldStrings = convertCharsToString(spiltField);
//							getSpecificCovention(spiltField, fieldStrings, t, beforeField.get(i).toString(),
//									afterField.get(i).toString());
//							getNameFromType(fieldType,beforeField.get(i).toString(),afterField.get(i).toString(),flag1,vdf.getInitializer());
//						}
//					}
				}

			}
		}

		private static String getNameFromType(Type fieldType, String beforeField, String afterField, boolean flags,
				Expression exp) {

			String newField = "";
			char[][] s1 = spiltFieldToChar(fieldType.toString().toCharArray(), false);
			List<String> typeList = convertCharsToString(s1);
			if (fieldType.toString().equals("float") || fieldType.toString().equals("int")
					|| fieldType.toString().equals("char") || fieldType.toString().equals("Boolean")
					|| fieldType.toString().equals("boolean") || fieldType.toString().equals("long")
					|| fieldType.toString().equals("double") || fieldType.toString().equals("String")
					|| fieldType.toString().equals("byte") || fieldType.toString().equals("object")
					|| fieldType.toString().equals("Set") || fieldType.toString().equals("List")
					|| fieldType.isArrayType() == true) {

			} else {
//				System.out.println(fieldType+" "+beforeField+" "+afterField+" "+typeList);
				allRemmondCount += 1;
				if (typeList.size() == 1) {
					newField = typeList.get(0).substring(0, 1).toLowerCase() + typeList.get(0).substring(1).toString();
					if (newField.equals(afterField)) {
						correctCount += 1;
					}
					res.add(newField);
				} else if (typeList.size() == 2) {
					newField = typeList.get(1);
					newField = newField.substring(0, 1).toLowerCase() + newField.substring(1).toString();
					if (!newField.equals(beforeField)) {
						if (newField.equals(afterField)) {
							correctCount += 1;
						}
						res.add(newField);
					} else {// 推荐类型全称
						newField = fieldType.toString().substring(0, 1).toLowerCase() + newField.substring(1).toString();
						if (!newField.equals(beforeField)) {
							if (newField.equals(afterField)) {
								correctCount += 1;
							}
							res.add(newField);
						}
					}
					newField = typeList.get(0);
					newField = newField.substring(0, 1).toLowerCase() + newField.substring(1).toString();
					if (!newField.equals(beforeField)) {
						if (newField.equals(afterField)) {
							correctCount += 1;
						}
						res.add(newField);
					}
				} else {
					char[][] s = generateNonConstantName(s1, namePartsPtr, false);
					List<String> sList = convertCharsToString(s);
					String nf = "";
					if (exp != null) {
						for (int a = sList.size() - 1; a >= 0; a--) {
							nf = sList.get(a).toString().substring(0, 1).toLowerCase()
									+ sList.get(a).substring(1).toString();
							if (!nf.equals(beforeField)) {
								if (nf.equals(afterField)) {
									correctCount += 1;
								}
								res.add(nf);
							}
						}
					} else {
						for (int a = 0; a < sList.size(); a++) {
							nf = sList.get(a).toString().substring(0, 1).toLowerCase()
									+ sList.get(a).substring(1).toString();
							if (!nf.equals(beforeField)) {
								if (nf.equals(afterField)) {
									correctCount += 1;
								}
								res.add(nf);
							}
						}
					}
				}
			}
			return null;
		}
		
		private static String getSpecificCovention(char[][] field, List<String> fieldLists, int i, String beforeField,
				String afterField) {
			int counts = fieldLists.size() - 1;
			if (fieldLists.get(counts).length() == 1) {
				for (int x = 0; x < i; x++) {
					FieldDeclaration f = fieldDeclarations.get(x);
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
					String fieldNames = vdf.getName().getIdentifier();
					char[] chs1 = fieldNames.toCharArray();
					char[][] s1 = spiltFieldToChar(chs1, false);
					List<String> sList = convertCharsToString(s1);
//					System.out.println("field:"+fieldNames+" "+"before:"+beforeField+" "+"afterField:"+afterField);
					if (sList.get(sList.size() - 1).length() == 1) {// 其他field存在起始为单字符

						if (sList.get(sList.size() - 1).equals(fieldLists.get(counts))) {
							return null;
						} else {
							if (Character.isLetter(sList.get(sList.size() - 1).toString().charAt(0))) {// 判断是否为字母开头
								fieldLists.set(counts, sList.get(sList.size() - 1));
								String newF = "";
								for (int s = fieldLists.size() - 1; s >= 0; s--) {
									newF = newF + fieldLists.get(s);
								}
								allRemmondCount += 1;
								if (newF.equals(afterField)) {
									correctCount += 1;
								}
								res.add(newF);
								return newF;
							} else {
								fieldLists.set(counts, Character.toLowerCase(fieldLists.get(counts).charAt(0))
										+ fieldLists.get(counts).substring(1));
								fieldLists.set(counts, sList.get(sList.size() - 1));
								String newF = "";
								for (int s = fieldLists.size() - 1; s >= 0; s--) {
									newF = newF + fieldLists.get(s);
								}

								allRemmondCount += 1;
								if (newF.equals(afterField)) {
									correctCount += 1;
								}
								res.add(newF);
								return newF;
							}

						}
					} else {// 其他field没有，需要把首字符去掉_ m $
						if (Character.isLetter(fieldLists.get(fieldLists.size() - 1).toString().charAt(0))) {
							fieldLists.remove(fieldLists.size() - 1);
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							if (newF.length() > 0) {
								newF = newF.substring(0, 1).toLowerCase() + newF.substring(1).toString();
							}
							allRemmondCount += 1;
							if (newF.equals(afterField)) {
								correctCount += 1;
							}
							res.add(newF);
							return newF;
						} else {// 首字符不是字母
							fieldLists.remove(fieldLists.size() - 1);
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							allRemmondCount += 1;
							if (newF.equals(afterField)) {
								correctCount += 1;
							}
							res.add(newF);
							return newF;
						}
					}
				}
			} else {// 不包含特定标识符起始，需要添加
				for (int x = 0; x < i; x++) {
					FieldDeclaration f = fieldDeclarations.get(x);
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
					String fieldNames = vdf.getName().getIdentifier();
					char[] chs1 = fieldNames.toCharArray();
					char[][] s1 = spiltFieldToChar(chs1, false);
					List<String> sList = convertCharsToString(s1);
					if (sList.get(sList.size() - 1).length() == 1) {
						if (Character.isLetter(sList.get(sList.size() - 1).toString().charAt(0))) {
							fieldLists.set(counts, Character.toLowerCase(fieldLists.get(counts).charAt(0))
									+ fieldLists.get(counts).substring(1));
							fieldLists.add(sList.get(sList.size() - 1));
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							allRemmondCount += 1;
							if (newF.equals(afterField)) {
								correctCount += 1;
							}
							res.add(newF);
							return newF;
						} else {
							fieldLists.add(sList.get(sList.size() - 1));
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							allRemmondCount += 1;
							if (newF.equals(afterField)) {
								correctCount += 1;
							}
							res.add(newF);
							return newF;
						}
					}
				}
			}
			return null;
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
				sList.add(s[i]);
			}
			return sList;
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

		public static char[][] generateNonConstantName(char[][] nameParts, int namePartsPtr, boolean onlyLongest) {
			char[][] names;
			if (onlyLongest) {
				names = new char[1][];
			} else {
				names = new char[namePartsPtr + 1][];
			}

			char[] namePart = nameParts[0];

			char[] name = CharOperation.toLowerCase(namePart);

			if (!onlyLongest) {
				names[namePartsPtr] = name;
			}

			char[] nameSuffix = namePart;

			for (int i = 1; i <= namePartsPtr; i++) {
				if (i < nameParts.length) {
					namePart = nameParts[i];

					name = CharOperation.concat(CharOperation.toLowerCase(namePart), nameSuffix);

					if (!onlyLongest) {
						names[namePartsPtr - i] = name;
					}
					nameSuffix = CharOperation.concat(namePart, nameSuffix);
				}
			}
			if (onlyLongest) {
				names[0] = name;
			}
			return names;
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
