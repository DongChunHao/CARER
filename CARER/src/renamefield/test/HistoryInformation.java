package renamefield.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jgit.api.StatusCommand;
//import org.graalvm.compiler.lir.StandardOp.ImplicitNullCheck;

import renamefield.projectPath.SaveData;

public class HistoryInformation {
	static List<SaveData> allDataSave = new ArrayList<>();// 保存所有数据
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<FieldDeclaration>();// 所有的field
	static List<String> commitId = new ArrayList<>();
	static List<String> classPath = new ArrayList<>();
	static List<String> projectName = new ArrayList<>();// 项目名
	static List<String> beforeField = new ArrayList<>();
	static List<String> afterField = new ArrayList<>();
	static List<String> afterType = new ArrayList<>();
	static List<String> projectJavaList = new ArrayList<>();
	public static int namePartsPtr = -1;
	static String commitBefore = "";
	static String javaPathBefore = "";
	static Map<String, Integer> historyAdd = new LinkedHashMap<>();// key:token value:位置
	static Map<String, Integer> historyRemove = new LinkedHashMap<>();// token-token
	static Map<String, String> historyReplace = new LinkedHashMap<>();// remove 位置
	static Map<String, String> historyField = new LinkedHashMap<>();// before after
	
	static Map<String, Integer> addString = new LinkedHashMap<>();
	static Map<String, Integer> removeString = new LinkedHashMap<>();
	static int historyCount = 0;
	static int rmCount = 0;
    int oldNameNorm;
    int newNameNorm;
    static int fieldType=0;
	public static void main(String[] args) throws InterruptedException {
		String saveCSV = "D:\\BIT_Report\\testRule\\Commit.csv";
//		String csvPath = "D:\\BIT_Report\\testRule\\test_his.csv";
		String csvPath = "D:\\BIT_Report\\testRule\\test.csv";
		readFile(csvPath);
		String project = projectName.get(commitId.size() - 1).toString();
		String dataBeforePath = "";
		for (int i = commitId.size() - 1; i >= 0; i--) {
//			for (int i =0; i <commitId.size(); i++) {

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
			if (i - 1 >= 0) {
				dataBeforePath = classPath.get(i-1);
			}
			getFieldFromHistory(i, beforeField.get(i), afterField.get(i), classPath.get(i), dataBeforePath);
			System.out.println("historyCount:"+historyCount);
			System.out.println("rmCount:"+rmCount);

		}
	}

	public static String getFieldFromHistory(int i, String beforeField, String afterField, String dataPath,
			String beforePath) {
		
		char[] bf = beforeField.toCharArray();
		char[][] bfs = spiltFieldToChar(bf, false);
		List<String> bfList = convertCharsToString(bfs);

		boolean flag = false;
		char[] af = afterField.toCharArray();
		char[][] afs = spiltFieldToChar(af, false);
		List<String> afList = convertCharsToString(afs);
//		letterToLowerCase(afList);// token 都变为小写
//		System.out.println("lt:"+afList);
		List<String> tempBeforeList = new ArrayList<>();
		for(int a=0;a<bfList.size();a++) {
			tempBeforeList.add(bfList.get(a));
		}
//		List<String> tempBeforeList = bfList;
		String newFieldString = "";
		if (commitBefore.equals(commitId.get(i))) {
//			if(beforePath.equals(dataPath)) {
			// 路径相同
//			System.out.println(historyAdd+" "+historyRemove+" "+historyReplace);
//			if(fieldType==1 && isCamelCase(beforeField)) {
//				String newField =  camelCaseToConstant(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			else if(fieldType==2&& isCamelCase(beforeField)) {
////				c
//				String newField = camelCaseToUnderscore(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			else if(fieldType==3 && isConstantFieldName(beforeField)) {
//				String newField = constantToCamelCase(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			else if(fieldType==4 && isConstantFieldName(beforeField)) {
//				String newField = camelCaseToUnderscore(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			else if(fieldType==5 && isUnderscoreCase(beforeField)) {
//				String newField = underscoreToCamelCase(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//				}
//			}
//			else if(fieldType==6 && isUnderscoreCase(beforeField)) {
//				String newField = underscoreToConstant(beforeField);
//				if(!newField.equals(beforeField)) {
//					rmCount+=1;
//					if(newField.equals(afterField)) {
//						historyCount += 1;
//					}else {
//						System.out.println("命名规范推荐错误："+beforeField+" "+afterField+" "+ newField);
//					}
//					deleteToken(afList,bfList,beforeField,afterField);
//					return null;
//											
//			}
//			
			
			if(!historyField.isEmpty()) {
				for (Map.Entry<String, String> entry : historyField.entrySet()) {
					String key = entry.getKey();
					if(beforeField.equals(key)) {
						rmCount += 1;
						if(entry.getValue().equals(afterField)) {
							historyCount += 1;
						}else {
							System.out.println("相同odlField推荐错误："+beforeField+" "+afterField+" "+entry.getValue());
						}						
						deleteToken(afList,bfList,beforeField,afterField);
						return null;
					}
				}
			}
			
//			//字符串级别转换
//			if(!addString.isEmpty()) {
//				for (Map.Entry<String, Integer> entry : addString.entrySet()) {
//					if(entry.getValue()==1) {
//						String newField ="";
//						if(entry.getKey().length()>1) {
//						newField= beforeField+entry.getKey().substring(0, 1).toLowerCase()+entry.getKey().substring(1);
//						}else {
//							newField= beforeField+entry.getKey().substring(0).toLowerCase();
//						}
//						if(newField!=beforeField) {
//							rmCount+=1;
//							if(newField.equals(afterField)) {
//								historyCount+=1;
//							}else {
//								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
//							}
//							deleteToken(afList,bfList,beforeField,afterField);
//							return null;
//						}
//					}
//					else if(entry.getValue()==0) {
//						String newField =entry.getKey() + beforeField.substring(0,1).toUpperCase()+beforeField.substring(1);
//						if(newField!=beforeField) {
//							rmCount+=1;
//							if(newField.equals(afterField)) {
//								historyCount+=1;
//							}else {
//								System.out.println("字符添加推荐错误："+beforeField+" "+afterField+" "+newField);
//							}
//							deleteToken(afList,bfList,beforeField,afterField);
//							return null;
//						}
//					}
//				} 
//			}
			
			if(!removeString.isEmpty()) {
				for (Map.Entry<String, Integer> entry : removeString.entrySet()) {
					if(entry.getValue()==0 && entry.getKey().length()==1) {
						String newField = beforeField.substring(1,2).toLowerCase()+beforeField.substring(2);
						if(newField!=beforeField) {
							rmCount+=1;
							if(newField.equals(afterField)) {
								historyCount+=1;
							}else {
								System.out.println("字符删除推荐错误："+beforeField+" "+afterField+" "+newField);
							}
							deleteToken(afList,bfList,beforeField,afterField);
							return null;
						}
					}
				}
			}
			
			//token级别转换
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
								
				if (flag && !bfList.equals(tempBeforeList)) {
					rmCount += 1;
					if (bfList.equals(afList)||bfList.equals(afterField)) {
						historyCount += 1;
//						System.out.println("推荐正确："+beforeField+" "+afterField);
					}else {
						System.out.println("替换推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
					}
					
					deleteToken(afList,bfList,beforeField,afterField);
					return null;
				}
			}
			if (!historyAdd.isEmpty()) {
//				System.out.println("111111");
//				System.out.println("add:"+historyAdd);
				for (Map.Entry<String, Integer> entry : historyAdd.entrySet()) {
					String key = entry.getKey();
					if(entry.getValue()==-1) {bfList.add(key);}
					else if(beforePath.equals(dataPath)&& entry.getValue()<bfList.size() && !bfList.contains(entry.getKey())) {
						bfList.add(entry.getValue(), entry.getKey());
						flag = true;
					}
//					else {
//						if(key.length()==1 && entry.getValue()<bfList.size()) {
//							bfList.add(entry.getValue(), entry.getKey());
//							flag = true;
//						}
//					}
					
//					for (int n = 0; n < bfList.size(); n++) {
//						if (bfList.get(n).equals(key)) {
//							if (entry.getValue() < bfList.size()) {
//								bfList.add(entry.getValue(), entry.getKey());
//								flag = true;
//								break;
//							}
//						}
//					}
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
				
				if (flag && !bfList.equals(tempBeforeList)) {
					rmCount += 1;
					if (bfList.equals(afList)) {
						historyCount += 1;
//						System.out.println("推荐正确："+beforeField+" "+afterField);
					}else {
						System.out.println("添加推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
					}
					
					deleteToken(afList,bfList,beforeField,afterField);
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
				
				if (flag && !bfList.equals(tempBeforeList)) {
					rmCount += 1;
					if (bfList.equals(afList)) {
						historyCount += 1;
//						System.out.println("推荐正确："+beforeField+" "+afterField);
					}else {
						System.out.println("删除推荐错误："+beforeField+" "+afterField+" "+ bfList+" "+afList);
					}
					
					deleteToken(afList,bfList,beforeField,afterField);
					return null;
				}				
			
			}
		} else {
			historyAdd.clear();
			historyRemove.clear();
			historyReplace.clear();
			historyField.clear();
			fieldType = 0;
			addString.clear();
			removeString.clear();
			commitBefore = commitId.get(i);
			// 比较两个list，存入map
			deleteToken(bfList, afList, beforeField, afterField);
		}
		
		return null;
	}

	public static void typeRem(String beforeField, String afterField) {
		if(isCamelCase(beforeField)) {
			if(isCamelCase(afterField)) {
				//一致不推荐
			}else if(isConstantFieldName(afterField)) {
				//存在 camel-> constant 转换
				fieldType =1;
			}else if(isUnderscoreCase(afterField)) {
				//存在 camel-> under
				fieldType=2;
			}
		}
		else if(isConstantFieldName(beforeField)) {
			if(isConstantFieldName(afterField)) {
				//一致，不转化
			}
			else if(isCamelCase(afterField)) {
				// constant -> camel
				fieldType=3;
			}else if(isUnderscoreCase(afterField)) {
				fieldType=4;
			}
		}
		else if(isUnderscoreCase(beforeField)) {
			if(isUnderscoreCase(afterField)) {
			//一致不推荐
			}else if(isCamelCase(afterField)) {
				//under - > camel
				fieldType=5;
			}
			else if(isConstantFieldName(afterField)) {
				// under -> Constant
				fieldType = 6;
			}
		}
	}
	
	public static void retrieveLocation(String beforeField, String afterField) {
		//存在问题
		if(afterField.toLowerCase().contains(beforeField.toLowerCase())) {
			//添加
			String addToken = afterField.toLowerCase().replace(beforeField.toLowerCase(), "");
			int position = afterField.toLowerCase().indexOf(beforeField.toLowerCase());
		    String newName = "";
		    if(position==0) {//表示添加在最后
		    	addString.put(addToken, 1);
		    }else {//添加在起始位置
		    	addString.put(addToken, 0);
		    }
//		    if(position==0) {
//		    	newName = addToken+beforeField.toLowerCase();
//		    	if(!newName.equals("")&&newName.equals(beforeField.toLowerCase())) {
//		    		rmCount+=1;
//		    		if(newName.equals(afterField.toLowerCase())) {
//		    			historyCount+=1;
//		    		}
//		    	}
//		    }else {
//		    	newName = beforeField.substring(0, position)+ addToken + beforeField.substring(position);
//		    	if(!newName.equals("")&&newName.equals(beforeField.toLowerCase())) {
//		    		rmCount+=1;
//		    		if(newName.equals(afterField.toLowerCase())) {
//		    			historyCount+=1;
//		    		}
//		    	}
//		    }
		}else if(beforeField.toLowerCase().contains(afterField.toLowerCase())) {
			//删除
			String addToken = beforeField.toLowerCase().replace(afterField.toLowerCase(), "");
			int position = beforeField.toLowerCase().indexOf(afterField.toLowerCase());
			if(position==0) {
				//删除后边
				removeString.put(addToken,1);
			}else {
				//删除前面
				removeString.put(addToken,0);
			}
		}
		
	}
	
	public static void deleteToken(List<String> bfList, List<String> afList, String beforeField, String afterField) {
		typeRem(beforeField, afterField);
		retrieveLocation(beforeField,afterField);
		List<String> tempList = new ArrayList<>();
		List<String> befList = new ArrayList<>();
		for(int a=0;a<afList.size();a++) {
			tempList.add(afList.get(a));
		}
		
		for(int b=0;b<bfList.size();b++) {
			befList.add(bfList.get(b));
		}
//		List<String> tempList = afList;
//		List<String> befList = bfList;
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
				if(afList.get(0).length()==1) {
					historyAdd.put(afList.get(0), -1);
				}else {
				historyAdd.put(afList.get(0), tempList.indexOf(afList.get(0)));
				}
			}
			historyField.put(beforeField, afterField);

		} else if (!bfList.isEmpty() && afList.isEmpty()) {
			if (bfList.size() == 1) {
				historyRemove.put(bfList.get(0), befList.indexOf(bfList.get(0)));
			} 
			historyField.put(beforeField, afterField);

		} else if (afList.isEmpty() && bfList.isEmpty()) {

		} else {
			if ((afList.size() == 1) && (bfList.size() == 1)) {
				historyReplace.put(bfList.get(0), afList.get(0));
			}
			historyField.put(beforeField, afterField);
		}
	}

	public static void letterToLowerCase(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String element = list.get(i).toLowerCase();
			list.set(i, element);
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
}

