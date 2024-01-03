package renamefield.test;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.internal.codeassist.impl.AssistOptions;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

public class BaseNameFromExpression {
	private static final String[] KNOWN_METHOD_NAME_PREFIXES= { "get", "is", "to" }; 
	private static String getBaseNameFromExpression(Expression assignedExpression,
			int variableKind) {
		String name = null;
		if (assignedExpression instanceof CastExpression) {
			assignedExpression = ((CastExpression) assignedExpression).getExpression();
		}
		if (assignedExpression instanceof Name) {
			Name simpleNode = (Name) assignedExpression;
			IBinding binding = simpleNode.resolveBinding();
			if (binding instanceof IVariableBinding)
				return getBaseName((IVariableBinding) binding);

			return ASTNodes.getSimpleNameIdentifier(simpleNode);
		} else if (assignedExpression instanceof MethodInvocation) {
			name = ((MethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof SuperMethodInvocation) {
			name = ((SuperMethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof FieldAccess) {
			return ((FieldAccess) assignedExpression).getName().getIdentifier();
		} else if (variableKind == NamingConventions.VK_STATIC_FINAL_FIELD
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
						return null; 
					} else if (Character.isUpperCase(name.charAt(curr.length()))) {
						return name.substring(curr.length());
					}
				}
			}
		}
		return name;
	}

	public static String getBaseName(IVariableBinding binding) {
		return getBaseName(getKind(binding), binding.getName());
	}

	public static String getBaseName(
			int variableKind,
			String variableName) {
		return String.valueOf(getBaseName(variableKind, variableName.toCharArray(), true));
	}
	
	private static int getKind(IVariableBinding binding) {
		if (binding.isField())
			return getFieldKind(binding.getModifiers());

		if (binding.isParameter())
			return NamingConventions.VK_PARAMETER;

		return NamingConventions.VK_LOCAL;
	}

	private static int getFieldKind(int modifiers) {
		if (!Modifier.isStatic(modifiers))
			return NamingConventions.VK_INSTANCE_FIELD;

		if (!Modifier.isFinal(modifiers))
			return NamingConventions.VK_STATIC_FIELD;

		return NamingConventions.VK_STATIC_FINAL_FIELD;
	}
	public static char[] getBaseName(
			int variableKind,
			char[] name,
			boolean updateFirstCharacter) {

		AssistOptions assistOptions = null;
		char[][] prefixes = null;
		char[][] suffixes = null;
		switch (variableKind) {
			case NamingConventions.VK_INSTANCE_FIELD:
				prefixes = assistOptions.fieldPrefixes;
				suffixes = assistOptions.fieldSuffixes;
				break;
			case NamingConventions.VK_STATIC_FIELD:
				prefixes = assistOptions.staticFieldPrefixes;
				suffixes = assistOptions.staticFieldSuffixes;
				break;
			case NamingConventions.VK_STATIC_FINAL_FIELD:
				prefixes = assistOptions.staticFinalFieldPrefixes;
				suffixes = assistOptions.staticFinalFieldSuffixes;
				break;
			case NamingConventions.VK_LOCAL:
				prefixes = assistOptions.localPrefixes;
				suffixes = assistOptions.localSuffixes;
				break;
			case NamingConventions.VK_PARAMETER:
				prefixes = assistOptions.argumentPrefixes;
				suffixes = assistOptions.argumentSuffixes;
				break;
		}
		return getBaseName(name, prefixes, suffixes, variableKind == NamingConventions.VK_STATIC_FINAL_FIELD, updateFirstCharacter);
	}
	
	private static char[] getBaseName(char[] name, char[][] prefixes, char[][] suffixes, boolean isConstant, boolean updateFirstCharacter) {
		char[] nameWithoutPrefixAndSiffix = removeVariablePrefixAndSuffix(name, prefixes, suffixes, updateFirstCharacter);

		char[] baseName;
		if (isConstant) {
			int length = nameWithoutPrefixAndSiffix.length;
			baseName = new char[length];
			int baseNamePtr = -1;

			boolean previousIsUnderscore = false;
			for (int i = 0; i < length; i++) {
				char c = nameWithoutPrefixAndSiffix[i];
				if (c != '_') {
					if (previousIsUnderscore) {
						baseName[++baseNamePtr] = ScannerHelper.toUpperCase(c);
						previousIsUnderscore = false;
					} else {
						baseName[++baseNamePtr] = ScannerHelper.toLowerCase(c);
					}
				} else {
					previousIsUnderscore = true;
				}
			}
			System.arraycopy(baseName, 0, baseName = new char[baseNamePtr + 1], 0, baseNamePtr + 1);
		} else {
			baseName = nameWithoutPrefixAndSiffix;
		}

		return baseName;
	}
	
	public static char[] removeVariablePrefixAndSuffix(
			int variableKind,
			IJavaProject javaProject,
			char[] name) {
		AssistOptions assistOptions;
		if (javaProject != null) {
			assistOptions = new AssistOptions(javaProject.getOptions(true));
		} else {
			assistOptions = new AssistOptions(JavaCore.getOptions());
		}

		char[][] prefixes = null;
		char[][] suffixes = null;
		switch (variableKind) {
			case NamingConventions.VK_INSTANCE_FIELD:
				prefixes = assistOptions.fieldPrefixes;
				suffixes = assistOptions.fieldSuffixes;
				break;
			case NamingConventions.VK_STATIC_FIELD:
				prefixes = assistOptions.staticFieldPrefixes;
				suffixes = assistOptions.staticFieldSuffixes;
				break;
			case NamingConventions.VK_STATIC_FINAL_FIELD:
				prefixes = assistOptions.staticFinalFieldPrefixes;
				suffixes = assistOptions.staticFinalFieldSuffixes;
				break;
			case NamingConventions.VK_LOCAL:
				prefixes = assistOptions.localPrefixes;
				suffixes = assistOptions.localSuffixes;
				break;
			case NamingConventions.VK_PARAMETER:
				prefixes = assistOptions.argumentPrefixes;
				suffixes = assistOptions.argumentSuffixes;
				break;
		}

		return removeVariablePrefixAndSuffix(name, prefixes, suffixes, true);
	}
	
	private static char[] removeVariablePrefixAndSuffix(char[] name, char[][] prefixes, char[][] suffixes, boolean updateFirstCharacter) {
		// remove longer prefix
		char[] withoutPrefixName = name;
		if (prefixes != null) {
			int bestLength = 0;
			for (int i= 0; i < prefixes.length; i++) {
				char[] prefix = prefixes[i];
				if (CharOperation.prefixEquals(prefix, name)) {
					int currLen = prefix.length;
					boolean lastCharIsLetter = ScannerHelper.isLetter(prefix[currLen - 1]);
					if(!lastCharIsLetter || (lastCharIsLetter && name.length > currLen && ScannerHelper.isUpperCase(name[currLen]))) {
						if (bestLength < currLen && name.length != currLen) {
							withoutPrefixName = CharOperation.subarray(name, currLen, name.length);
							bestLength = currLen;
						}
					}
				}
			}
		}

		// remove longer suffix
		char[] withoutSuffixName = withoutPrefixName;
		if(suffixes != null) {
			int bestLength = 0;
			for (int i = 0; i < suffixes.length; i++) {
				char[] suffix = suffixes[i];
				if(CharOperation.endsWith(withoutPrefixName, suffix)) {
					int currLen = suffix.length;
					if(bestLength < currLen && withoutPrefixName.length != currLen) {
						withoutSuffixName = CharOperation.subarray(withoutPrefixName, 0, withoutPrefixName.length - currLen);
						bestLength = currLen;
					}
				}
			}
		}

		if (updateFirstCharacter) withoutSuffixName[0] = ScannerHelper.toLowerCase(withoutSuffixName[0]);
		return withoutSuffixName;
	}
}
