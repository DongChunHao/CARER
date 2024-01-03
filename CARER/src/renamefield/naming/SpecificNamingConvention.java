package renamefield.naming;

import java.util.List;

import org.eclipse.jdt.core.NamingConventions;

public class SpecificNamingConvention {
	int flag;
	public String nameConvention;
	private int extractConvention(List<String> filed1,List<String> field2) {
		if(filed1.get(0)==field2.get(0)) {
			nameConvention = filed1.get(0);
			return 0;
		}else {
			if(filed1.get(filed1.size()-1)==field2.get(field2.size()-1)) {
				nameConvention = field2.get(filed1.size()-1);
				return 1;
			}
		}
		return -1;	
	}
	private boolean isExistConvention(List<String> fieldList, String nameConvention) {
		if(flag==0) {
			if(fieldList.get(0)==nameConvention) {
				return true;
			}
		}else {
			if(fieldList.get(fieldList.size()-1)==nameConvention) {
				return true;
			}
		}
		return false;
	}
}
