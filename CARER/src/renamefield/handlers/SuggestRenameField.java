package renamefield.handlers;

import java.beans.Expression;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jface.viewers.ISelection;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.ui.search.SearchParticipantsExtensionPoint;

public class SuggestRenameField {
	private static ITextSelection selection;

	private static int textLength;

	private static int textStartLine;

	private static int textOffset;

	private static String textSelect;
	static List<FieldAccess> fieldAccess = new ArrayList<>();
	static List<FieldDeclaration> fieldDeclarations= new ArrayList<>();
	static List<ReturnStatement> returnStatements= new ArrayList<>();
	static List<MethodDeclaration> methodDeclarations= new ArrayList<>();
	public static String getFieldNameSuggestions(ISelection sel, ExecutionEvent event) throws Exception {
		handleCommand(sel);
		IEditorPart editorPart1 = HandlerUtil.getActiveEditor(event);
		IFile file = (IFile) editorPart1.getEditorInput().getAdapter(IFile.class);
		IJavaProject javaProject= JavaCore.create(file.getProject());
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		ASTParser astParser = ASTParser.newParser(AST.JLS14);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		astParser.setSource(compilationUnit);
		CompilationUnit astRoot = (CompilationUnit) astParser.createAST(null);
		getFieldAccessSuggest(astRoot);
		return getFieldNameFromType(astRoot,javaProject);
//		handleCommand(sel);
//		IEditorInput input= HandlerUtil.getActiveEditorInput(event);
//		String javaPath= input.toString().substring(input.toString().indexOf('/') + 1, input.toString().length() - 1);
//		javaPath= javaPath.substring(javaPath.indexOf('/'));
//
//		IProject project= getProject(event);
//		String projectName= project.getName();
//		String projectPath= project.getLocation().toOSString();
//		String filePath= projectPath + javaPath;
//		IEditorPart editorPart= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//		IFile file= editorPart.getEditorInput().getAdapter(IFile.class);
//		ICompilationUnit icu= JavaCore.createCompilationUnitFrom(file);

	}

	public static String getFieldNameFromType(CompilationUnit cu,IJavaProject javaProject) {
		String fieldName= ""; //$NON-NLS-1$
		if (!fieldDeclarations.isEmpty()) {
			fieldDeclarations.clear();
		}
		getFieldDeclaration(cu, fieldDeclarations);
		String[] string= new String[fieldDeclarations.size()];
		for (int i= 0; i < fieldDeclarations.size(); i++) {
			FieldDeclaration fd= fieldDeclarations.get(i);
			VariableDeclarationFragment vdf= (VariableDeclarationFragment) fd.fragments().get(0);
			fieldName= vdf.getName().getIdentifier();
			string[i]= fieldName;
//			System.out.println("fieldName:"+textSelect);
			if (fieldName.equals(textSelect)) {
				int modifier= fd.getModifiers();
				Type fieldType= fd.getType();
				if(fieldType.toString().equals("float")||fieldType.toString().equals("int") //$NON-NLS-1$ //$NON-NLS-2$
						||fieldType.toString().equals("char")||fieldType.toString().equals("boolean") //$NON-NLS-1$ //$NON-NLS-2$
						||fieldType.toString().equals("long")||fieldType.toString().equals("double") //$NON-NLS-1$ //$NON-NLS-2$
						||fieldType.toString().equals("String")||fieldType.toString().equals("byte")) { //$NON-NLS-1$ //$NON-NLS-2$
					String returnString = getFieldNameFromReturn(cu);
					if (returnString!=null) {
						  String[] suggestedNames=getFieldNameSuggestions(javaProject,returnString,0, modifier, string);
		        		  if (suggestedNames.length > 0) {
		        			  return suggestedNames[0];
		        		   }
					}
					return textSelect;
				}else {
				 
                  String[] suggestedNames=getFieldNameSuggestions(javaProject,fieldType.toString(),0, modifier, string);
//                  System.out.println("ssssssssss:"+suggestedNames);
        		  if (suggestedNames.length > 0) {
        			  return suggestedNames[0];
        		   }
				}
			}
		}
		return textSelect;
	}

	public static String getFieldNameFromReturn(CompilationUnit cu) {
		if(!returnStatements.isEmpty()) {
			returnStatements.clear();
		}
		getReturnStatement(cu, returnStatements);
		for(int i=0;i<returnStatements.size();i++) {
			String expression = returnStatements.get(i).getExpression().toString();
			if(expression.equals(textSelect)) {
				if(returnStatements.get(i).getParent() != null) {
				    ASTNode astNode = returnStatements.get(i).getParent();
					while (astNode.getNodeType()!=ASTNode.METHOD_DECLARATION) {
						 astNode =astNode.getParent();				
					}
					MethodDeclaration mDeclaration = (MethodDeclaration) astNode;
					String name = mDeclaration.getName().toString();
					for (String curr : KNOWN_METHOD_NAME_PREFIXES) {
						if (name.startsWith(curr)) {
							if (name.equals(curr)) {
								return null; // don't suggest 'get' as variable name
							} else if (Character.isUpperCase(name.charAt(curr.length()))) {
								return name.substring(curr.length());// 去除get,is,to的方法名
							}
						}
					}
					return name;
				}
			}
		}
		return null;
	}
	// 规则7
	private static void getFieldAccessSuggest(CompilationUnit cu) {
		if (!fieldAccess.isEmpty()) {
			fieldAccess.clear();
		}
		getFieldAcess(cu, fieldAccess);
		for (int n = 0; n < fieldAccess.size(); n++) {
//			System.out.println("fieldAccess:" + fieldAccess.get(n));
			org.eclipse.jdt.core.dom.Expression exp =  fieldAccess.get(n).getExpression();
			ASTNode astNode = fieldAccess.get(n).getParent();
			if(astNode.getNodeType() == ASTNode.ASSIGNMENT) {
				Assignment assignment = (Assignment) astNode;
				//获取赋值语句右边表达式作为field名
				System.out.println("exp:"+assignment.getRightHandSide());
			}
		}
	}
	
	public static String[] getFieldNameSuggestions(IJavaProject project, String baseName, int dimensions, int modifiers, String[] excluded) {
		if (Flags.isFinal(modifiers) && Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(NamingConventions.VK_STATIC_FINAL_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
		} else if (Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(NamingConventions.VK_STATIC_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
		}
		return getVariableNameSuggestions(NamingConventions.VK_INSTANCE_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
	}

	public static String[] getVariableNameSuggestions(int variableKind, IJavaProject project, String baseName, int dimensions, Collection<String> excluded, boolean evaluateDefault) {
		return NamingConventions.suggestVariableNames(variableKind, NamingConventions.BK_TYPE_NAME, removeTypeArguments(baseName), project, dimensions, getExcludedArray(excluded), evaluateDefault);
	}

	private static String removeTypeArguments(String baseName) {
		int idx= baseName.indexOf('<');
		if (idx != -1) {
			return baseName.substring(0, idx);
		}
		return baseName;
	}

	private static String[] getExcludedArray(Collection<String> excluded) {
		if (excluded == null) {
			return null;
		} else if (excluded instanceof ExcludedCollection) {
			return ((ExcludedCollection) excluded).getExcludedArray();
		}
		return excluded.toArray(new String[excluded.size()]);
	}

	public static void handleCommand(ISelection sel) throws Exception {
		String str= sel.toString();
		String[] splitStr= str.split("[, :]"); //$NON-NLS-1$
		textOffset= Integer.parseInt(splitStr[3]);
		textStartLine= Integer.parseInt(splitStr[7]);
		textLength= Integer.parseInt(splitStr[11]);
		textSelect= splitStr[15];
	}

	public static IJavaProject findJavaProject(String projectName) {
		IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i= 0; i < projects.length; ++i) {
			if (JavaCore.create(projects[i]).getPath().lastSegment().contains(projectName)) {
				return JavaCore.create(projects[i]);
			}
		}
		return null;
	}

	public static IProject getProject(ExecutionEvent event) {
		IProject project= null;
		IEditorPart part= HandlerUtil.getActiveEditor(event);
		if (part != null) {
			Object object= part.getEditorInput().getAdapter(IFile.class);
			if (object != null) {
				project= ((IFile) object).getProject();
			}
		}
		return project;
	}

	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		byte[] input= null;
		try {
			BufferedInputStream bufferedInputStream= new BufferedInputStream(new FileInputStream(javaFilePath));
			input= new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ASTParser astParser= ASTParser.newParser(AST.JLS17);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		astParser.setSource(new String(input).toCharArray());
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		CompilationUnit unit= (CompilationUnit) (astParser.createAST(null));
		return unit;
	}


	public static void getMethodDeclaration(ASTNode cu, final List<MethodDeclaration> types) {
		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(MethodDeclaration node) { 
				types.add(node);
				return true;
			}
		});
	}
	public static void getFieldDeclaration(ASTNode cu, final List<FieldDeclaration> types) {
		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldDeclaration node) { 
				types.add(node);
				return true;
			}
		});
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
	
	public static void getFieldAcess(ASTNode cu, final List<FieldAccess> types) {
		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldAccess node) {
				types.add(node);
				return true;
			}
		});
	}
	
	private static class ExcludedCollection extends AbstractList<String> {
		private String[] fExcluded;

		public ExcludedCollection(String[] excluded) {
			fExcluded= excluded;
		}

		public String[] getExcludedArray() {
			return fExcluded;
		}

		@Override
		public int size() {
			return fExcluded.length;
		}

		@Override
		public String get(int index) {
			return fExcluded[index];
		}

		@Override
		public int indexOf(Object o) {
			if (o instanceof String) {
				for (int i= 0; i < fExcluded.length; i++) {
					if (o.equals(fExcluded[i]))
						return i;
				}
			}
			return -1;
		}

		@Override
		public boolean contains(Object o) {
			return indexOf(o) != -1;
		}
	}
	private static final String[] KNOWN_METHOD_NAME_PREFIXES= { "get", "is", "to" };
}
