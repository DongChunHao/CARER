package renamefield.dealField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplitField {
	 public List<String> underlineSplit(String field){
		 List<String> fieldList  = Arrays.asList(field.split("_").toString().toLowerCase());
		 return fieldList;
	 }
	 
	 public List<String> camelSpilt(String field){
		 String tempName = field.replaceAll("[A-Z]", "_$0");
		 List<String> fieldList = Arrays.asList(tempName.split("_").toString().toLowerCase());
		 return fieldList;
	 }
}
