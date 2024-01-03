package renamefield.projectPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

import renamefield.handlers.SampleHandler;
import renamefield.naming.CamelRule;

public class test {
	//D:\AllProject\dataset\SonarSource@sonarqube\sonar-core\src\main\java\org\sonar\core\persistence\profiling	
	//D:\AllProject\dataset\SonarSource@sonarqube\sonar-core\src\main\java\org\sonar\core\persistence\profiling\ProfilingStatementHandler.java
    static List<ReturnStatement> returnStatements = new ArrayList<>();
    public static int namePartsPtr = -1;
	public static void main(String[] args) {
		String stringa = "s_hello_World";
		String[] parts = stringa.split("(?=[A-Z])|_");
		System.out.println(Arrays.toString(parts));
		
		String string = "m_String";
		SampleHandler sHandler = new SampleHandler();
		char[][] s1 = sHandler.spiltFieldToChar(string.toCharArray(),true);// 数组太大，需要删
		List<String> s2 = charToStrings(s1);
		
		System.out.println("s2:"+s2);
		
		List<String> list1 =   Arrays.asList("jerry","res");
		String exp1 = "jerry.res.old";
		
		List<String> list2 =   Arrays.asList("Jerry","Res");
		String exp2 = "org.res.old";
		
//		record(list1,exp1,list2,exp2);
		isReplace("boolean","boolean",list1,list2,exp1,exp2);
//	       String string2 =charToTokenNext(s1);
//	    System.out.println("c.length:"+string.indexOf("String"));
        
//		System.out.println("s2:" + s2.get(2));
//		System.out.println(s1.length);
//		for(int s=0;s<s1.length;s++) {
////			for(int j=0;j<s1[s].length;j++) {
//				System.out.println(s1[s][0]);
////			}
//		}

//		List<String> strings = convertCharsToString(s1);
		
//		System.out.println(strings.size());
//		System.out.println("strings:"+strings);
//		if (strings.size() == 1) {
////		    char[][] t = generateConstantName(s1, SampleHandler.namePartsPtr, true);
//			char[][] t = generateNonConstantName(s1, SampleHandler.namePartsPtr, true);
//			List<String> s11 = convertCharsToString(t);
//			System.out.println(s11.get(0));
//		} else {
//	       char[][] t = generateConstantName(s1, SampleHandler.namePartsPtr, false);
//			char[][] t = generateNonConstantName(s1, SampleHandler.namePartsPtr, false);

//	       List<String> s2 = convertCharsToString(t);    

//	       System.out.println("s2:"+ CamelRule.underLineToHump(s2.get(0)));
//           for(int i=0;i<t.length;i++) {
//        	   for(int j=0;j<t[i].length;j++) {
//        		   System.out.println(t[i][j]);
//        	   }
//           }
//		}
//		for(int i=0;i<strings.size();i++) {
//			System.out.println(strings.get(i));
//		}
	}
	
	// 包含初始化，存在token来自exp, 类型相同，来自相邻两个field
	public String rule6(String type1,String type2, List<String> list1, List<String> list2, String intr1, String intr2, String expression, String oldExpression) {
		if(isReplace(type1, type2, list1, list2, intr1, intr2)) {
			record(list1, expression, list2, oldExpression);
		}
		return null;
	}
	
	public static boolean isReplace(String type1,String type2, List<String> list1, List<String> list2, String intr1, String intr2) {
		if(type1.equals(type2) && !intr1.equals(null) && !intr2.equals(null)) {
			if(hasCommonStrings(list1,list2)) {
				System.out.println("111");
				return true;
			}
		}
		System.out.println("222");
		return false;
	}
	
	public static void record(List<String> fieldList, String expression, List<String> oldFieldList, String oldExpression ) {
		Map<Integer, Integer> locationMap =  new HashMap<>();
		String expString = regularExpression(expression);
		char[] fieldChar = expString.toString().toCharArray();
		char[][] spiltField = spiltFieldToChar(fieldChar, false);
		List<String> expStrings = convertCharsToString(spiltField);
		for (int j = 0; j < expStrings.size(); j++) {
			
			for (int i = 0; i < fieldList.size(); i++) {
				if(expStrings.get(j).toLowerCase().replace("_", "").equals(fieldList.get(i).toLowerCase())) {
					locationMap.put(i, j);//key是field  value是exp
					break;
				}
			}
		}
		
		String expString1 = regularExpression(oldExpression);
		char[] fieldChar1 = expString.toString().toCharArray();
		char[][] spiltField1 = spiltFieldToChar(fieldChar, false);
		List<String> expStrings1 = convertCharsToString(spiltField);
		
		System.out.println("expS:"+expString1);
		if(!locationMap.isEmpty()) {
			for (Map.Entry<Integer, Integer> entry : locationMap.entrySet()) {
				if(entry.getKey()<oldFieldList.size() && entry.getValue()<expStrings1.size()) {
					oldFieldList.set(entry.getKey(), expStrings1.get(entry.getValue()));
				}
			}
		}
		System.out.println(" oldField"+oldFieldList);
	}
	
	public static boolean hasCommonStrings(List<String> list1, List<String> list2) {
	    for (String s : list1) {
	        if (list2.contains(s)) {
	            return true; // 包含公共字符串
	        }
	    }
	    return false; // 不包含公共字符串
	}
	
	public static String regularExpression(String expression) {
		return expression.replaceAll("[\"!#;,\".]", "_");
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
	
	public static String getFieldNameFromReturn(CompilationUnit cu,String string) {
		if(!returnStatements.isEmpty()) {
			returnStatements.clear();
		}
		getReturnStatement(cu, returnStatements);
		for(int i=0;i<returnStatements.size();i++) {
			String expression = returnStatements.get(i).getExpression().toString();
			if(expression.equals(string)) {
				if(returnStatements.get(i).getParent() != null) {
				    ASTNode astNode = returnStatements.get(i).getParent();
					while (astNode.getNodeType()!=ASTNode.METHOD_DECLARATION) {
						 astNode =astNode.getParent();				
					}
					MethodDeclaration mDeclaration = (MethodDeclaration) astNode;
					return mDeclaration.getName().toString();
				}
			}
		}
		return null;
	}
	
	public static void getReturnStatement(ASTNode cu, final List<ReturnStatement> types) {
		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(ReturnStatement node) {
				types.add(node);
				return true;
			}
		});
	}
	
	public static String charToTokenNext(char[][] c) {
		char[] n2 = c[c.length - 1];
		return String.copyValueOf(n2);
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

	public static String charToTokenPre(char[][] c) {
		char[] n1 = c[0];
		return String.copyValueOf(n1);
	}

	public static char[][] generateConstantName(char[][] nameParts, int namePartsPtr, boolean onlyLongest) {
		char[][] names;
		if (onlyLongest) {
			names = new char[1][];
		} else {
			names = new char[namePartsPtr + 1][];
		}

		char[] namePart = CharOperation.toUpperCase(nameParts[0]);
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
			namePart = nameParts[i];

			name = CharOperation.concat(CharOperation.toLowerCase(namePart), nameSuffix);
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=283539
			// Only the first word is converted to lower case and the rest of them are not
			// changed for non-constants

			if (!onlyLongest) {
				names[namePartsPtr - i] = name;
			}

			nameSuffix = CharOperation.concat(namePart, nameSuffix);
		}
		if (onlyLongest) {
			names[0] = name;
		}
		return names;
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
}
