package renamefield.handlers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CompilationJava {
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
}
