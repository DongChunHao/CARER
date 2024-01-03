package renamefield.handlers;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.source.SourceViewer;

import java.awt.Button;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.spi.CharsetProvider;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.Workbench;

import renamefield.dealField.GenerateNames;
import renamefield.naming.CamelRule;
import renamefield.naming.StaticFinalRule;
import renamefield.projectPath.RenameData;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.eclipse.ltk.internal.ui.refactoring.InternalAPI;
import org.eclipse.ltk.internal.ui.refactoring.RefactoringUIMessages;
import org.eclipse.ltk.internal.ui.refactoring.RefactoringUIPlugin;
import org.eclipse.ltk.internal.ui.refactoring.actions.RenameResourceHandler;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ltk.ui.refactoring.resource.RenameResourceWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SampleHandler extends AbstractHandler {
	private static final String LTK_RENAME_COMMAND_NEWNAME_PARAMETER_KEY = "org.eclipse.ltk.ui.refactoring.commands.renameResource.newName.parameter.key"; //$NON-NLS-1$

	private static final String LTK_CHECK_COMPOSITE_RENAME_PARAMETER_KEY = "org.eclipse.ltk.ui.refactoring.commands.checkCompositeRename.parameter.key"; //$NON-NLS-1$
	private JavaEditor editor;
	private ITextSelection selection;
	private static int textLength = 0;
	private static int textStartLine = 0;
	private static int textOffset = 0;
	private static String textSelect;
	private CompilationUnit astRoot;
	private String classNameAST = null;
	public static int namePartsPtr = -1;
	static List<FieldDeclaration> fieldDeclarations = new ArrayList<>();
	List<FieldAccess> fieldAccess = new ArrayList<>();
	List<String> fieldList = new ArrayList<>();
	LinkedHashSet<String> res = new LinkedHashSet<>();
	boolean flag2 = false;// field长度是否为1

	private static IField fSelectedField;

	private static int fTextLength;

	private static int fTextStartLine;

	private static int fTextOffset;

	private static ASTNode foundNode;
	static boolean flag1 = false;// field是否为 static final

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelection(event);

		IEditorPart editorPart1 = HandlerUtil.getActiveEditor(event);
		IFile file1 = (IFile) editorPart1.getEditorInput().getAdapter(IFile.class);
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file1);
		ASTParser parser = ASTParser.newParser(AST.JLS14);
		parser.setSource(compilationUnit);
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
//      System.out.println("astRoot:"+astRoot);				

		IJavaElement element = SelectionConverter.getInputAsCompilationUnit(editor);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			handleCommand(sel,window);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ISelectionService selectionService = window.getSelectionService();
		selectionService.addSelectionListener(new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
				ISelection selection = selectionService.getSelection();
			}
		});
		IEditorPart editor = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
		IEditorInput input = editor.getEditorInput();// input:org.eclipse.ui.part.FileEditorInput(/Test/src/test.java)
		String javaPath = input.toString().substring(input.toString().indexOf('/') + 1, input.toString().length() - 1);
		javaPath = javaPath.substring(javaPath.indexOf('/'));
		Shell activeShell= HandlerUtil.getActiveShell(event);
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
		IJavaProject javaProject = JavaCore.create(file.getProject());
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);
//		System.out.println("file:"+file);
//		System.out.println("javaProject:"+javaProject);
//		System.out.println("cu:"+cu);
//		if (input instanceof IFileEditorInput)
//		{
//		IFile file = ((IFileEditorInput)input).getFile();
//		String filepath = ((IFile)file).getLocation().makeAbsolute().toFile().getAbsolutePath();
//		}
		performRename(event);
    
		return null;
	}
//------------------------------------test------------------------------------------------
	public static String getProjectSpecificCovention(List<String> fieldList, String initialSetting) {
		boolean notFlag= true;
		String pattern= "(?=[A-Z])|_"; //$NON-NLS-1$
		String[] initArray= initialSetting.split(pattern);
		if (initialSetting.charAt(0) == '_') {
			for (int i= 0; i < fieldList.size(); i++) {
				if (fieldList.get(i).charAt(0) == '_') {
					notFlag= true;
					break;
				} else {
					notFlag= false;
				}
			}
			if (!notFlag) {
				return initialSetting.substring(1);
			}
		} else if (initArray.length > 0 && initArray[0].length() == 1) {
			for (int i= 0; i < fieldList.size(); i++) {
				String[] tempList= fieldList.get(i).split(pattern);
				if (tempList.length > 0 && tempList[0].length() == 1) {
					notFlag= true;
					break;
				} else {
					notFlag= false;
				}
			}
			if (!notFlag) {
				if (initialSetting.length() > 2) {
					return initialSetting.substring(1, 2).toLowerCase() + initialSetting.substring(2);
				}
			}
		} else if (initArray.length > 0 && initArray[0].length() != 1) {
			String str= ""; //$NON-NLS-1$
			for (int i= 0; i < fieldList.size(); i++) {
				if (fieldList.get(i).charAt(0) == '_') {
					str = Character.toString(fieldList.get(i).charAt(0));
					return str+initialSetting;
				} else {
					String[] tempList= fieldList.get(i).split(pattern);
					if (tempList.length > 0 && tempList[0].length() == 1) {
						notFlag= false;
						str= Character.toString(tempList[0].charAt(0));
						break;
					} else {
						notFlag= true;
					}
				}
			}
			if (!notFlag && initialSetting.length() > 2) {
					return str + initialSetting.substring(0, 1).toUpperCase() + initialSetting.substring(1);
			}
		}
		return initialSetting;
	}
//------------------------------------test-------------------------------------------------
	// 原始的feild推荐
	public static String getFieldNameSuggestions(ExecutionEvent event, ISelection sel) throws Exception {
		getOffsetAndLength(sel);
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
		IJavaProject project = JavaCore.create(file.getProject());
		ICompilationUnit compilationUnit = file.getAdapter(ICompilationUnit.class);

		ASTNode selectedNode = findNodeByOffsetAndLength(convertToCompilationUnit(compilationUnit), getfTextOffset(),
				getfTextLength());
		if (selectedNode instanceof SimpleName) {
			ASTNode parent = selectedNode.getParent();
			if (parent instanceof VariableDeclarationFragment) {
				ASTNode fieldDeclaration = parent.getParent();
				if (fieldDeclaration instanceof FieldDeclaration) {
					String[] excluded = new String[0];
					if (compilationUnit != null) {
						IType[] types;
						try {
							types = compilationUnit.getAllTypes();
							for (IType type : types) {
								IField[] fields = type.getFields();
								for (IField field : fields) {
									String fieldName = field.getElementName();
									excluded = Arrays.copyOf(excluded, excluded.length + 1);
									excluded[excluded.length - 1] = fieldName;
								}
							}
							int fieldModifiers = fSelectedField.getFlags();
							String[] newNames = getFieldNameSuggestions(project, sel.toString(), fieldModifiers,
									excluded);
							if (newNames.length > 0) {
								return newNames[0];
							}
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}
		return sel.toString();
	}

	private void performRename(ExecutionEvent event) {
		Shell activeShell= HandlerUtil.getActiveShell(event);
		Object newNameValue= HandlerUtil.getVariable(event, LTK_RENAME_COMMAND_NEWNAME_PARAMETER_KEY);
		String newName= null;
		if (newNameValue instanceof String) {
			newName= (String) newNameValue;
		}
		IEditorPart editor= HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
		IEditorInput input= editor.getEditorInput();
		String javaPath= input.toString().substring(input.toString().indexOf('/') + 1, input.toString().length() - 1);
		javaPath= javaPath.substring(javaPath.indexOf('/'));

		IProject project= getProject(event);
		String projectPath= project.getLocation().toOSString();
		String filePath= projectPath + javaPath;
		ISelection sel= HandlerUtil.getCurrentSelection(event);

		try {
			newName= getFieldNameSuggestions(filePath, event, sel,newName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//
//		if (sel instanceof IStructuredSelection) {
//			System.out.println("-------------------");
//			IResource resource= getCurrentResource((IStructuredSelection) sel);
////			if (resource != null) {
//				RenameResourceWizard refactoringWizard;
//				Change change= null;
//				RefactoringProcessor processor= null;
//				if (newName != null) {
//					refactoringWizard= new RenameResourceWizard(resource, newName);
//					processor= ((ProcessorBasedRefactoring) refactoringWizard.getRefactoring()).getProcessor();
//					change= getChange(refactoringWizard);
//					//Reset the state of the wizard once we have the change it will perform
//					refactoringWizard= new RenameResourceWizard(resource, newName);
//				} else {
//					refactoringWizard= new RenameResourceWizard(resource);
//				}
//
//				try {
//					// Let user see rename dialog with preview page for composite changes or if another RefactoringProcessor is used (which may offer rename options)
//					if (newName == null || change == null || isCompositeChange(change) || !(processor instanceof RenameResourceProcessor)) {
//						RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(refactoringWizard);
//						op.run(activeShell, RefactoringUIMessages.RenameResourceHandler_title);
//					} else {
//						//Silently perform the rename without the dialog
//						RefactoringCore.getUndoManager().aboutToPerformChange(change);
//						Change undo= change.perform(new NullProgressMonitor());
//						RefactoringCore.getUndoManager().changePerformed(change, true);
//						RefactoringCore.getUndoManager().addUndo(RefactoringUIMessages.RenameResourceHandler_title, undo);
//					}
//				} catch (InterruptedException e) {
//					// do nothing
//				} catch (CoreException e) {
//					RefactoringCore.getUndoManager().changePerformed(change, false);
//					RefactoringUIPlugin.log(e);
//				}
//			}
		}
//	}

	
	
	public static String getFieldNameSuggestions(String filePath, ExecutionEvent event, ISelection sel,String newField)
			throws Exception {
		getOffsetAndLength(sel);
		System.out.println("ff:" + fTextLength + " " + fTextOffset + " " + fTextStartLine);
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
		IJavaProject project = JavaCore.create(file.getProject());
		List<String> tempFieldList= new ArrayList<>();
		int modifier = 0;
		CompilationJava compilationJava = new CompilationJava();
		CompilationUnit cu = compilationJava.getCompilationUnit(filePath);
		if (!fieldDeclarations.isEmpty()) {
			fieldDeclarations.clear();
		}
		compilationJava.getFieldDeclaration(cu, fieldDeclarations);
		String[] excluded = new String[fieldDeclarations.size()];
		if (fieldDeclarations.size() > 1) {
			for (int i= 0; i < fieldDeclarations.size(); i++) {
				FieldDeclaration fd= fieldDeclarations.get(i);
				VariableDeclarationFragment vdf= (VariableDeclarationFragment) fd.fragments().get(0);
				String fieldName= vdf.getName().getIdentifier();
				if (textSelect.equals(fieldName)) {
					if (fd.toString().contains("static") && fd.toString().contains("final")) { //$NON-NLS-1$ //$NON-NLS-2$
						break;
					}
				} else {
					String fieldDeclaration= fd.toString();
					if ((!fieldDeclaration.contains("static") || !fieldDeclaration.contains("final") )&& fieldName.length()>1) { //$NON-NLS-1$ //$NON-NLS-2$
						excluded[i]= fieldName;
						tempFieldList.add(fieldName);
					}
				}
			}
		}
//		for (int i = 0; i < fieldDeclarations.size(); i++) {
//
//			FieldDeclaration fd = fieldDeclarations.get(i);
//			VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
//			fieldName = vdf.getName().getIdentifier();
//			if (fieldName.equals(textSelect)) {
//				flag1 = true;
//				modifier = fd.getModifiers();
//			}
//			excluded[i] = fieldName;
//		}
//		if (flag1 = true) {
//			String[] newNames = getFieldNameSuggestions(project, textSelect, modifier, excluded);
//			if (newNames.length > 0) {
//				return newNames[0];
//			}
//		}
		String newFieldString = getProjectSpecificCovention(tempFieldList, textSelect);
		System.out.println("11111111111111");
		System.out.println(newFieldString);
		return newFieldString;
	}

	public static void getOffsetAndLength(ISelection sel) throws Exception {
		String str = sel.toString();
		String[] splitStr = str.split("[, :]"); //$NON-NLS-1$
		setfTextOffset(Integer.parseInt(splitStr[3]));
		setfTextStartLine(Integer.parseInt(splitStr[7]));
		setfTextLength(Integer.parseInt(splitStr[11]));
	}

	public static ASTNode findNodeByOffsetAndLength(CompilationUnit compilationUnit, int offset, int length) {
		foundNode = null;
		compilationUnit.accept(new ASTVisitor() {
			@SuppressWarnings("unused")
			public boolean visit(ASTNode node) {
				int nodeStart = node.getStartPosition();
				int nodeEnd = nodeStart + node.getLength();

				if (offset >= nodeStart && (offset + length) <= nodeEnd) {
					foundNode = node;
					return false;
				}

				return true;
			}
		});

		return foundNode;
	}

	public static int getfTextLength() {
		return fTextLength;
	}

	public static void setfTextLength(int sTextLength) {
		fTextLength = sTextLength;
	}

	public static int getfTextStartLine() {
		return fTextStartLine;
	}

	public static void setfTextStartLine(int sTextStartLine) {
		fTextStartLine = sTextStartLine;
	}

	public static int getfTextOffset() {
		return fTextOffset;
	}

	public static void setfTextOffset(int sTextOffset) {
		fTextOffset = sTextOffset;
	}

	private Change getChange(RenameResourceWizard refactoringWizard) {
		refactoringWizard.setChangeCreationCancelable(true);
		refactoringWizard.setInitialComputationContext((boolean fork, boolean cancelable,
				IRunnableWithProgress runnable) -> runnable.run(new NullProgressMonitor()));
		return refactoringWizard.internalCreateChange(InternalAPI.INSTANCE,
				new CreateChangeOperation(new CheckConditionsOperation(refactoringWizard.getRefactoring(),
						CheckConditionsOperation.FINAL_CONDITIONS), RefactoringStatus.FATAL),
				true);
	}

	private boolean isCompositeChange(Change change) {
		return (change instanceof CompositeChange && ((CompositeChange) change).getChildren().length > 1);
	}

	private IResource getCurrentResource(IStructuredSelection sel) {
		IResource[] resources = getSelectedResources(sel);
		if (resources.length == 1) {
			return resources[0];
		}
		return null;
	}

	protected IResource[] getSelectedResources(IStructuredSelection sel) {
		List<IResource> resources = new ArrayList<>(sel.size());
		for (Object next : sel) {
			if (next instanceof IResource) {
				resources.add((IResource) next);
				continue;
			} else if (next instanceof IAdaptable) {
				IResource resource = ((IAdaptable) next).getAdapter(IResource.class);
				if (resource != null) {
					resources.add(resource);
					continue;
				}
			} else {
				IAdapterManager adapterManager = Platform.getAdapterManager();
				ResourceMapping mapping = adapterManager.getAdapter(next, ResourceMapping.class);

				if (mapping != null) {

					ResourceTraversal[] traversals = null;
					try {
						traversals = mapping.getTraversals(ResourceMappingContext.LOCAL_CONTEXT,
								new NullProgressMonitor());
					} catch (CoreException exception) {
						RefactoringUIPlugin.log(exception.getStatus());
					}

					if (traversals != null) {
						for (ResourceTraversal traversal : traversals) {
							IResource[] traversalResources = traversal.getResources();
							if (traversalResources != null) {
								resources.addAll(Arrays.asList(traversalResources)); // for
							} // if
						} // for
					} // if
				} // if
			}
		}
		return resources.toArray(new IResource[resources.size()]);
	}

	private String[] getFieldNameSuggestions(String filePath, String projectPath) {

		boolean flag1 = false;// field是否为 static final
		String fieldName = "";
		CompilationJava compilationJava = new CompilationJava();
		CompilationUnit cu = compilationJava.getCompilationUnit(filePath);
		if (!fieldDeclarations.isEmpty()) {
			fieldDeclarations.clear();
		}
		compilationJava.getFieldDeclaration(cu, fieldDeclarations);
		for (int i = 0; i < fieldDeclarations.size(); i++) {
			FieldDeclaration fd = fieldDeclarations.get(i);
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) fd.fragments().get(0);
			fieldName = vdf.getName().getIdentifier();
//			System.out.println("modifier:"+fd.getModifiers());
			if (fieldName.equals(textSelect)) {
				Type fieldType = fd.getType();
				int modifier = fd.getModifiers();
				// static final 25, 26, 24；static 9; final 17 ; no static final 1
				char[] fieldChar = fieldName.toCharArray();
				// contant field
				if (modifier == 25 || modifier == 26 || modifier == 24) {
					flag1 = true;
				}
				char[][] spiltField = spiltFieldToChar(fieldChar, flag1);// 拆分字符串
				List<String> fieldStrings = convertCharsToString(spiltField);
				if (spiltField.length == 1) {
					flag2 = true;
				}
				staticFinalNamingRecommend(spiltField, flag1, i);
				commonCovention(spiltField, fieldStrings, flag1, i);
			}
		}
		System.out.println("推荐结果");
		Iterator<String> iterator = res.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		res.clear();
		return null;

	}

	private void staticFinalNamingRecommend(char[][] field, boolean isModiferStatic, int i) {
		if (isModiferStatic) {
			char[][] newField = GenerateNames.generateConstantName(field, namePartsPtr, flag2);
			List<String> fieldList = GenerateNames.convertCharsToString(newField);
			if (!fieldList.get(0).equals(textSelect)) {
				res.add(fieldList.get(0));
			}
		} else {
//            namingRecommend(field,i);
			namingRecommend1(field, i);
		}
	}

//规则1
	private void namingRecommend(char[][] field, int i) {
		List<String> otherList = new ArrayList<>();
		String fieldName = "";
		int count = 0;
		for (int t = 0; t < fieldDeclarations.size(); t++) {
			FieldDeclaration f = fieldDeclarations.get(t);
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
			fieldName = vdf.getName().getIdentifier();
			if (!fieldName.equals(textSelect)) {
				char[] chs1 = fieldName.toCharArray();
				char[][] s1 = spiltFieldToChar(chs1, false);
				List<String> sList = convertCharsToString(s1);
				boolean flags = isCamelCase(sList);
				if (sList.size() >= 2) {
					if (flags == true) {
						char[][] newField = GenerateNames.generateNonConstantName(field, namePartsPtr, flag2);
						List<String> fieldList = GenerateNames.convertCharsToString(newField);
						if (!fieldList.get(0).equals(textSelect)) {
							res.add(fieldList.get(0));
						}
						otherList.clear();
						break;
					}
					otherList.add(fieldName);
				}
			}
		}
		for (int r = 0; r < otherList.size(); r++) {
			if (CamelRule.isUnderCase(otherList.get(r)) == true
					&& otherList.get(r).substring(1, otherList.get(r).length())
							.substring(0, otherList.get(r).length() - 1).contains("_")) {
				count += 1;
			} else {
				char[][] newField = GenerateNames.generateNonConstantName(field, namePartsPtr, flag2);
				List<String> fieldList = GenerateNames.convertCharsToString(newField);
				if (!fieldList.get(0).equals(textSelect)) {
					res.add(fieldList.get(0));
				}
				otherList.clear();
				break;
			}
		}
		if (count == otherList.size() && otherList.size() > 0) {
			System.out.println("1111");
			String str = CamelRule.humpToUnderline(textSelect);
			if (!str.equals(textSelect)) {
				res.add(str);
			}
		}
	}

	public static CompilationUnit convertToCompilationUnit(ICompilationUnit compilationUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS18);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);

		return (CompilationUnit) parser.createAST(null);
	}

	public static CompilationUnit convertToCompilationUnit(String javaFilePath) {
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

	// 规则1 改进
	private void namingRecommend1(char[][] field, int i) {
		boolean flag2 = false;
		List<String> otherList = new ArrayList<>();
		for (int x = 0; x < i; x++) {
			FieldDeclaration f = fieldDeclarations.get(x);
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
			String fieldNames = vdf.getName().getIdentifier();
			char[] chs1 = fieldNames.toCharArray();
			char[][] s1 = spiltFieldToChar(chs1, false);
			List<String> sList = convertCharsToString(s1);
			boolean flags = isUnderCase(sList);
			if (sList.size() >= 2 && flags) {
				flag2 = true;
				break;
			}
		}
		if (flag2) {
			String str1 = CamelRule.humpToUnderline(textSelect);
			if (!str1.equals(textSelect)) {
				res.add(str1);
			}
		} else {
			String str2 = CamelRule.underLineToHump(textSelect);
			if (!str2.equals(textSelect)) {
				res.add(str2);
			}
		}
	}

	// 规则3 是否有一个相似性field,进行语义替换
	private String getLevenshteinSuggest(Type type1, int i) {
		if (i - 2 >= 0) {
			FieldDeclaration f1 = fieldDeclarations.get(i - 2);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) f1.fragments().get(0);
			if (vdf1.getInitializer() != null) {

			}
			Type t1 = fieldDeclarations.get(i - 2).getType();

			FieldDeclaration f2 = fieldDeclarations.get(i - 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) f2.fragments().get(0);
			if (vdf2.getInitializer() != null) {

			}
			Type t2 = fieldDeclarations.get(i - 1).getType();
		} else if (i - 1 >= 0) {
			FieldDeclaration f2 = fieldDeclarations.get(i - 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) f2.fragments().get(0);
			if (vdf2.getInitializer() != null) {

			}
			Type t2 = fieldDeclarations.get(i - 1).getType();
		} else {
			if (i + 2 <= fieldDeclarations.size() - 1) {
				FieldDeclaration f3 = fieldDeclarations.get(i + 1);
				VariableDeclarationFragment vdf3 = (VariableDeclarationFragment) f3.fragments().get(0);
				Type t3 = fieldDeclarations.get(i + 1).getType();
				if (vdf3.getInitializer() != null) {

				}

				FieldDeclaration f4 = fieldDeclarations.get(i + 2);
				VariableDeclarationFragment vdf4 = (VariableDeclarationFragment) f4.fragments().get(0);
				Type t4 = fieldDeclarations.get(i + 2).getType();
				if (vdf3.getInitializer() != null) {

				}
			} else if (i + 1 <= fieldDeclarations.size() - 1) {
				FieldDeclaration f3 = fieldDeclarations.get(i + 1);
				VariableDeclarationFragment vdf3 = (VariableDeclarationFragment) f3.fragments().get(0);
				Type t3 = fieldDeclarations.get(i + 1).getType();
				if (vdf3.getInitializer() != null) {

				}
			}
		}
		return null;
	}

	private String getFiltration() {

		return null;
	}

	// 规则4 判断是字符还是特殊符号,以_,m,$等单个字符作为首单词，或存在首单词，需要去掉的两种情况
	private String getSpecificCovention(char[][] field, List<String> fieldLists, int i) {
		int counts = fieldLists.size() - 1;
		if (fieldLists.get(counts).length() == 1) {
			for (int x = 0; x < i; x++) {
				FieldDeclaration f = fieldDeclarations.get(x);
				VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(0);
				String fieldNames = vdf.getName().getIdentifier();
				char[] chs1 = fieldNames.toCharArray();
				char[][] s1 = spiltFieldToChar(chs1, false);
				List<String> sList = convertCharsToString(s1);
				if (sList.get(sList.size() - 1).length() == 1) {
					if (sList.get(sList.size() - 1).equals(fieldLists.get(counts))) {
						return null;
					} else {
						if (Character.isLetter(sList.get(sList.size() - 1).toString().charAt(0))) {// 判断是否为字母开头
							fieldLists.set(counts, sList.get(sList.size() - 1));
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							return newF;
						} else {
							fieldLists.set(counts - 1, Character.toLowerCase(fieldLists.get(counts).charAt(0))
									+ fieldLists.get(counts).substring(1));
							fieldLists.set(counts, sList.get(sList.size() - 1));
							String newF = "";
							for (int s = fieldLists.size() - 1; s >= 0; s--) {
								newF = newF + fieldLists.get(s);
							}
							return newF;
						}

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
						fieldLists.set(counts - 1, Character.toLowerCase(fieldLists.get(counts).charAt(0))
								+ fieldLists.get(counts).substring(1));
						fieldLists.add(sList.get(sList.size() - 1));
						String newF = "";
						for (int s = fieldLists.size() - 1; s >= 0; s--) {
							newF = newF + fieldLists.get(s);
						}
						return newF;
					} else {
						fieldLists.add(sList.get(sList.size() - 1));
						String newF = "";
						for (int s = fieldLists.size() - 1; s >= 0; s--) {
							newF = newF + fieldLists.get(s);
						}
						return newF;
					}
				}
			}
		}
		return null;
	}

	public static String[] getFieldNameSuggestions(IJavaProject project, String originalField, int fieldModifiers,
			String[] excluded) {
		return getFieldNameSuggestions(project, originalField, 0, fieldModifiers, excluded);
	}

	public static String[] getFieldNameSuggestions(IJavaProject project, String baseName, int dimensions, int modifiers,
			String[] excluded) {
		if (Flags.isFinal(modifiers) && Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(NamingConventions.VK_STATIC_FINAL_FIELD, project, baseName, dimensions,
					new ExcludedCollection(excluded), true);
		} else if (Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(NamingConventions.VK_STATIC_FIELD, project, baseName, dimensions,
					new ExcludedCollection(excluded), true);
		}
		return getVariableNameSuggestions(NamingConventions.VK_INSTANCE_FIELD, project, baseName, dimensions,
				new ExcludedCollection(excluded), true);
	}

	public static String[] getVariableNameSuggestions(int variableKind, IJavaProject project, String baseName,
			int dimensions, Collection<String> excluded, boolean evaluateDefault) {
//		return NamingConventions.suggestVariableNames(variableKind, NamingConventions.BK_TYPE_NAME,
//				removeTypeArguments(baseName), project, dimensions, getExcludedArray(excluded), evaluateDefault);
		String[] str= NamingConventions.suggestVariableNames(variableKind, NamingConventions.BK_TYPE_NAME,
				baseName, project, dimensions, getExcludedArray(excluded), evaluateDefault);
		return str;
	}

	private static String removeTypeArguments(String baseName) {
		int idx = baseName.indexOf('<');
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

	// 规则2
	private void commonCovention(char[][] field, List<String> fieldList, boolean isStaticFinal, int i) {
		List<String> tempList = fieldList;
		List<String> tempList1 = fieldList;
		if (i - 1 >= 0 && i + 1 < fieldDeclarations.size()) {// 判断是否存在pre next
			FieldDeclaration fd1 = fieldDeclarations.get(i - 1);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fieldDeclarations.get(i - 1).fragments()
					.get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fdChar1.length == 1) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);// 拆分pre
			List<String> preList = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(i + 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fieldDeclarations.get(i + 1).fragments()
					.get(0);
			String fn2 = vdf2.getName().getIdentifier();// next name
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fdChar2.length == 1) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);// 拆分field
			List<String> nextList = charToStrings(s2);// tokens

			if (preList.get(preList.size() - 1).toLowerCase().equals(nextList.get(nextList.size() - 1).toLowerCase())) {
				String tempToken = preList.get(preList.size() - 1).toLowerCase();
				// 判断当前field是否包含相同规范
				if (tempToken.length() < 2) {
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						if (tempList.size() + 1 > preList.size() && tempList.size() + 1 > nextList.size()) {
							tempList1.set(tempList1.size() - 1, tempToken);// 替换
						}
						tempList.add(tempToken);
					}
				} else {
					// 大于2
					int n = 0;
					boolean fl = false;
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						for (int r = 0; r < tempList.size(); r++) {
							if (tempList.get(r).toLowerCase().contains(tempToken)) {
								f1 = true;
								n = r;
								break;
							}
						}
						if (fl) {
							// 删除
							for (int a = n + 1; a < tempList.size(); a++) {
								tempList.remove(a);
							}
						} else {
							if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
								tempList1.set(tempList1.size() - 1, tempToken);// 替换
							}
							tempList.add(tempToken);
						}
					}
				}
			} else {
				if (preList.get(0).toLowerCase().equals(nextList.get(0).toLowerCase())) {
					String tempToken = preList.get(0).toLowerCase();
					if (!preList.get(0).equals(fieldList.get(0))) {
						if (tempToken.length() < 2) {
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
									tempList1.set(0, tempToken);// 替换
								}
								tempList.add(0, tempToken);
							}
						} else {
							int n = 0;
							boolean fl = false;
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								for (int r = 0; r < tempList.size(); r++) {
									if (tempList.get(r).toLowerCase().contains(tempToken)) {
										f1 = true;
										n = r;
										break;
									}
								}
								if (fl) {
									// 移除token后的字符串
									for (int a = 0; a < n; a++) {
										tempList.remove(a);
									}
								} else {
									if (tempList1.size() + 1 > preList.size()
											&& tempList1.size() + 1 > nextList.size()) {
										tempList1.set(0, tempToken);// 替换
									}
									tempList.add(0, tempToken);
								}
							}
						}
					}
				}
			}
		} else if (i - 2 >= 0 && i - 1 > 0) {
			FieldDeclaration fde = fieldDeclarations.get(i - 2);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fde.fragments().get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fdChar1.length == 1) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);
			List<String> preList = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(i - 1);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments().get(0);
			String fn2 = vdf2.getName().getIdentifier();
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fdChar2.length == 1) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);
			List<String> nextList = convertCharsToString(s2);

			if (preList.get(preList.size() - 1).toLowerCase().equals(nextList.get(nextList.size() - 1).toLowerCase())) {
				String tempToken = preList.get(preList.size() - 1).toLowerCase();
				// 判断当前field是否包含相同规范
				if (tempToken.length() < 2) {
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						if (tempList.size() + 1 > preList.size() && tempList.size() + 1 > nextList.size()) {
							tempList1.set(tempList1.size() - 1, tempToken);// 替换
						}
						tempList.add(tempToken);
					}
				} else {
					// 大于2
					int n = 0;
					boolean fl = false;
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						for (int r = 0; r < tempList.size(); r++) {
							if (tempList.get(r).toLowerCase().contains(tempToken)) {
								f1 = true;
								n = r;
								break;
							}
						}
						if (fl) {
							// 删除
							for (int a = n + 1; a < tempList.size(); a++) {
								tempList.remove(a);
							}
						} else {
							if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
								tempList1.set(tempList1.size() - 1, tempToken);// 替换
							}
							tempList.add(tempToken);
						}
					}
				}
			} else {
				if (preList.get(0).toLowerCase().equals(nextList.get(0).toLowerCase())) {
					String tempToken = preList.get(0).toLowerCase();
					if (!preList.get(0).equals(fieldList.get(0))) {
						if (tempToken.length() < 2) {
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
									tempList1.set(0, tempToken);// 替换
								}
								tempList.add(0, tempToken);
							}
						} else {
							int n = 0;
							boolean fl = false;
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								for (int r = 0; r < tempList.size(); r++) {
									if (tempList.get(r).toLowerCase().contains(tempToken)) {
										f1 = true;
										n = r;
										break;
									}
								}
								if (fl) {
									// 移除token后的字符串
									for (int a = 0; a < n; a++) {
										tempList.remove(a);
									}
								} else {
									if (tempList1.size() + 1 > preList.size()
											&& tempList1.size() + 1 > nextList.size()) {
										tempList1.set(0, tempToken);// 替换
									}
									tempList.add(0, tempToken);
								}
							}
						}
					}
				}
			}
		} else if (i + 2 < fieldDeclarations.size() && i + 1 < fieldDeclarations.size()) {
			FieldDeclaration fd1 = fieldDeclarations.get(i + 1);
			VariableDeclarationFragment vdf1 = (VariableDeclarationFragment) fd1.fragments().get(0);
			String fn1 = vdf1.getName().getIdentifier();
			boolean f1 = false;
			char[] fdChar1 = fn1.toCharArray();
			if (fdChar1.length == 1) {
				f1 = true;
			}
			char[][] s1 = spiltFieldToChar(fdChar1, f1);
			List<String> preList = charToStrings(s1);

			FieldDeclaration fd2 = fieldDeclarations.get(i + 2);
			VariableDeclarationFragment vdf2 = (VariableDeclarationFragment) fd2.fragments().get(0);
			String fn2 = vdf2.getName().getIdentifier();
			boolean f2 = false;
			char[] fdChar2 = fn2.toCharArray();
			if (fdChar2.length == 1) {
				f2 = true;
			}
			char[][] s2 = spiltFieldToChar(fdChar2, f2);
			List<String> nextList = charToStrings(s2);

			if (preList.get(preList.size() - 1).toLowerCase().equals(nextList.get(nextList.size() - 1).toLowerCase())) {
				String tempToken = preList.get(preList.size() - 1).toLowerCase();
				// 判断当前field是否包含相同规范
				if (tempToken.length() < 2) {
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						if (tempList.size() + 1 > preList.size() && tempList.size() + 1 > nextList.size()) {
							tempList1.set(tempList1.size() - 1, tempToken);// 替换
						}
						tempList.add(tempToken);
					}
				} else {
					// 大于2
					int n = 0;
					boolean fl = false;
					if (!preList.get(preList.size() - 1).equals(tempList.get(tempList.size() - 1).toLowerCase())) {
						for (int r = 0; r < tempList.size(); r++) {
							if (tempList.get(r).toLowerCase().contains(tempToken)) {
								f1 = true;
								n = r;
								break;
							}
						}
						if (fl) {
							// 删除
							for (int a = n + 1; a < tempList.size(); a++) {
								tempList.remove(a);
							}
						} else {
							if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
								tempList1.set(tempList1.size() - 1, tempToken);// 替换
							}
							tempList.add(tempToken);
						}
					}
				}
			} else {
				if (preList.get(0).toLowerCase().equals(nextList.get(0).toLowerCase())) {
					String tempToken = preList.get(0).toLowerCase();
					if (!preList.get(0).equals(fieldList.get(0))) {
						if (tempToken.length() < 2) {
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								if (tempList1.size() + 1 > preList.size() && tempList1.size() + 1 > nextList.size()) {
									tempList1.set(0, tempToken);// 替换
								}
								tempList.add(0, tempToken);
							}
						} else {
							int n = 0;
							boolean fl = false;
							if (!tempToken.equals(tempList.get(0).toLowerCase())) {
								for (int r = 0; r < tempList.size(); r++) {
									if (tempList.get(r).toLowerCase().contains(tempToken)) {
										f1 = true;
										n = r;
										break;
									}
								}
								if (fl) {
									// 移除token后的字符串
									for (int a = 0; a < n; a++) {
										tempList.remove(a);
									}
								} else {
									if (tempList1.size() + 1 > preList.size()
											&& tempList1.size() + 1 > nextList.size()) {
										tempList1.set(0, tempToken);// 替换
									}
									tempList.add(0, tempToken);
								}
							}
						}
					}
				}
			}
		} else {
			System.out.println("没有命名规范");
			// 新的命名规范
		}
		char[][] c1 = listToChar(tempList);
		char[][] c2 = listToChar(tempList1);
		if (isStaticFinal) {
			char[][] newF1 = GenerateNames.generateConstantName(c1, namePartsPtr, flag2);
			char[][] newF2 = GenerateNames.generateConstantName(c2, namePartsPtr, flag2);
			List<String> list2 = GenerateNames.convertCharsToString(newF2);
			List<String> list1 = GenerateNames.convertCharsToString(newF1);
			if (!list2.get(0).equals(textSelect)) {
				res.add(list2.get(0));
			}
			if (!list1.get(0).equals(textSelect)) {
				res.add(list1.get(0));
			}
		} else {
			char[][] newF1 = GenerateNames.generateNonConstantName(c1, namePartsPtr, flag2);
			char[][] newF2 = GenerateNames.generateNonConstantName(c2, namePartsPtr, flag2);
			List<String> list2 = GenerateNames.convertCharsToString(newF2);
			List<String> list1 = GenerateNames.convertCharsToString(newF1);
			if (!list2.get(0).equals(textSelect)) {
				res.add(list2.get(0));
			}
			if (!list1.get(0).equals(textSelect)) {
				res.add(list1.get(0));
			}
		}

	}

	// 规则7
	private void getFieldAccessSuggest(CompilationUnit cu) {
		if (!fieldAccess.isEmpty()) {
			fieldAccess.clear();
		}
		getFieldAcess(cu, fieldAccess);
		for (int n = 0; n < fieldAccess.size(); n++) {

			if (fieldAccess.get(n).getName().equals(selection)) {
				System.out.println("fieldAccess:" + fieldAccess.get(n));
				Expression exp = (Expression) fieldAccess.get(n).getParent();
				System.out.println("exp:" + exp);
			}
		}
	}

	private void handleCommand(ISelection window1, IWorkbenchWindow window) throws Exception {
		String string = window1.toString();
		String[] splitStrings = string.split("[, :]");
		textOffset = Integer.parseInt(splitStrings[3]);
		textStartLine = Integer.parseInt(splitStrings[7]);
		textLength = Integer.parseInt(splitStrings[11]);
		textSelect = splitStrings[15];// 选择的内容
	}

	public static IProject getProject(ExecutionEvent event) {
		IProject project = null;
		IEditorPart part = HandlerUtil.getActiveEditor(event);
		if (part != null) {
			Object object = part.getEditorInput().getAdapter(IFile.class);
			if (object != null) {
				project = ((IFile) object).getProject();
			}
		}

//		if (project == null) {
//			ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow()
//					.getSelectionService();
//			ISelection selection = selectionService.getSelection();
//			if (selection instanceof IStructuredSelection) {
//				Object element = ((IStructuredSelection) selection).getFirstElement();
//				System.out.println("ele:" + element);
//				if (element instanceof IResource) {
//					project = ((IResource) element).getProject();
//				} else if (element instanceof PackageFragmentRootContainer) {
//					IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
//					project = jProject.getProject();
//				} else if (element instanceof IJavaElement) {
//					IJavaProject jProject = ((IJavaElement) element).getJavaProject();
//					project = jProject.getProject();
//				}
////                } else if(element instanceof EditPart){  
////                    IFile file = (IFile) ((DefaultEditDomain)((EditPart)element).getViewer().getEditDomain()).getEditorPart().getEditorInput().getAdapter(IFile.class);  
////                    project = file.getProject();  
////                }   
//			}
//		}

		return project;
	}

	public static IJavaProject findJavaProject(String projectName) {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; ++i) {
			if (JavaCore.create(projects[i]).getPath().lastSegment().contains(projectName)) {
				return JavaCore.create(projects[i]);
			}
		}
		return null;
	}

	// spilt filed name to char
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

	public static void getFieldAcess(ASTNode cu, final List<FieldAccess> types) {
		cu.accept(new ASTVisitor() {
			@SuppressWarnings("unchecked")
			public boolean visit(FieldAccess node) {
				types.add(node);
				return true;
			}
		});
	}

	public boolean isStaticFinal(char[][] c) {
		for (int s = 0; s < c.length; s++) {
			for (int j = 0; j < c[s].length; j++) {
				if ((c[s][j] >= 'a' && c[s][j] <= 'z')) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isCamelCase(List<String> c) {

		for (int i = 0; i < c.size(); i++) {

			if (i != c.size() - 1) {
				if (c.get(i).charAt(0) >= 'a' && c.get(i).charAt(0) <= 'z') {
					return false;
				}
				if (c.get(i).contains("_")) {
					return false;
				}
			} else {
				if (c.get(i).charAt(0) >= 'A' && c.get(i).charAt(0) <= 'Z') {
					return false;
				}
				if (c.get(i).substring(1, c.get(i).length()).contains("_")) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isUnderCase(List<String> c) {
		for (int s = 0; s < c.size(); s++) {
			if (s == 0) {

			} else {
				if (!c.get(s).contains("_")) {
					return false;
				}
			}
		}
		return true;
	}

	private static char[][] generateConstantName(char[][] nameParts, int namePartsPtr, boolean onlyLongest) {
		char[][] names;
		if (onlyLongest) {
			names = new char[1][];
		} else {
			names = new char[namePartsPtr + 1][];
		}

		char[] namePart = CharOperation.toUpperCase(nameParts[0]);// ��һ�����ʴ�д
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

	// 转化为整个 field ，且 fieldNameString - nameString - string
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

	// 转化为token
	public static List<String> charToStrings(char[][] arr) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null) {
				list.add(String.copyValueOf(arr[i]));
			}
		}
		return list;
	}

	// list 转为 二维数组
	public static char[][] listToChar(List<String> field) {
		char[][] charArray = new char[field.size()][];
		for (int i = 0; i < field.size(); i++) {
			charArray[i] = field.get(i).toCharArray();
		}
		return charArray;
	}

	private static class ExcludedCollection extends AbstractList<String> {
		private String[] fExcluded;

		public ExcludedCollection(String[] excluded) {
			fExcluded = excluded;
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
				for (int i = 0; i < fExcluded.length; i++) {
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
}
