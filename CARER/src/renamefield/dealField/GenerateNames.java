package renamefield.dealField;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;

public class GenerateNames {
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
				if (i >= nameParts.length) {
					break;
				}
				namePart = CharOperation.toUpperCase(nameParts[i]);
				namePartLength = namePart.length;
				if (namePartLength - 1 >= 0 && namePart[namePartLength - 1] != '_') {
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

	public static List<String> convertCharsToString(char[][] c) {
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
}
