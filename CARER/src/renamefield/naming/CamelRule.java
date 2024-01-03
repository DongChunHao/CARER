package renamefield.naming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.compiler.CharOperation;

public class CamelRule {

	private static final char SEPARATOR = '_';

	public static String toCapitalizeCamelCase(String s) {
		s = underToCamel(s);
		return s.substring(0, 1) + s.substring(1);
	}

	private static String underToCamel(String param) {
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = Character.toLowerCase(param.charAt(i));
			if (c == SEPARATOR) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String camelToUnder(String field) {
		field = field.replaceAll("([a-z])([A-Z])", "$1" + SEPARATOR + "$2").toUpperCase();
		return field;
	}

	public boolean isCamelCase(String field) {
		
		if(field.startsWith("_")) {
			field = field.substring(1,field.length());
		}
		for (int i = 0; i < field.length(); i++) {
			if(field.indexOf('_')>=0) {
				return false;
			}else {
				if(field.charAt(i)>= 'A' && field.charAt(i)<='Z') {
					if((i+1)<field.length()) {
						if(field.charAt(i+1)>= 'A' && field.charAt(i+1)<='Z') {
							return false; 
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean isUnderCase(String field) {
		for (int i = 0; i < field.length(); i++) {
			char c = field.charAt(i);
			if (((c >= 'a') && (c <= 'z')) || (c >= 'A' && c <= 'Z')) {
				if (c < 'A' && c > 'Z') {
					return false;
				}
			}
		}
		return true;
	}
	//camel to under
    public static String humpToUnderline(String str) {
        String regex = "([A-Z])";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group();
            str = str.replaceAll(target, "_"+target.toLowerCase());
        }
        return str;
    }
   //under to camel
    public static String underLineToHump(String str) {
        String regex = "_(.)";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group(1);
            str = str.replaceAll("_"+target, target.toUpperCase());
        }
        return str;
    }
    //field to Static final
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
}
