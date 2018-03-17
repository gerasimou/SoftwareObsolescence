/** Copyright (c) 2017 University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/

 package org.spg.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.EmfUtilities;

import project.Clazz;
import project.Element;
import project.File;
import project.Method;
import project.Package;
import project.Project;
import project.ProjectFactory;
import project.ProjectPackage;

/**
 * Class that instantiates the model
 * @author sgerasimou
 *
 */
public class ProjectModelGenerator {
	
	//Colouring
	private final String DISTRICT_COLOR 		 = "0xF7AB29";
	private final String CITY_COLOR	 			 = "0xF9F7F4";
	private final String BUILDING_COLOR 		 = "0x2A75B3";
	private final String METHOD_COLOR 		 	 = "0x27A5B3";
	private final String BUILDING_COLOR_AFFECTED = "0xB22029";
	private final String[] SUB_DISTRICT_COLOR 	 = {"0xA0522D", "0xD2691E", "0xDAA520", "0xF4A460", "0xD2B48C", "0xF5DEB3", "0xFFF8DC"};

	String path				;
	String modelName	  	;
	String modelExtension 	= "projectModel";

	/** project */
	protected ICProject currentCProject = null;

	/** project index */
	protected IIndex projectIndex = null;

	
	public ProjectModelGenerator(){
	}

	
	public void generateProjectModel (IProject project){ 
		try {
			//get existing cProject
			this.currentCProject	= CdtUtilities.getICProject(project);
			this.projectIndex 		= CCorePlugin.getIndexManager().getIndex(currentCProject); 

			path 		= project.getLocation().toString() +"/SMILO/models/";
			modelName	= project.getName();

			List<ICElement> icElementsList = new ArrayList<ICElement>(); 
			ICProject cproject;
		
			cproject = CdtUtilities.getICProject(project);
				
			ProjectPackage.eINSTANCE.eClass();
			//retrieve the default factory singleton
			ProjectFactory factory = ProjectFactory.eINSTANCE;
			
			//create an instance of project
			Project projectModel = factory.createProject();
			projectModel.setName(project.getName());
			projectModel.setDescription(project.getName());
			projectModel.setColour(CITY_COLOR);
			
			//add default package (default/)
			Package defaultPackage = factory.createPackage();
			defaultPackage.setName("default");
			defaultPackage.setDescription("default package");
			defaultPackage.setColour(DISTRICT_COLOR);
			projectModel.getPackages().add(defaultPackage);

			//add default package (src/)
			Package srcPackage = factory.createPackage();
			srcPackage.setName("src");
			srcPackage.setDescription("src package");
			srcPackage.setColour(DISTRICT_COLOR);
			projectModel.getPackages().add(srcPackage);

			
			//Get all classes
			for (ISourceRoot sourceRoot : cproject.getSourceRoots()){
				if (sourceRoot.getElementName().equals("src")){
					CdtUtilities.getICElementsFromProject(sourceRoot, ICElement.class, icElementsList);
				}
			}
			
			for (ICElement element : icElementsList){
	
//				//a package
//				if ( (element instanceof ICContainer) && (element.getParent() instanceof ISourceRoot) ){
//					Package aPackage = factory.createPackage();
//					aPackage.setName(element.getElementName());
//					aPackage.setDescription("Folder: " + element.getElementName());
//					aPackage.setColour(DISTRICT_COLOR);
//					projectModel.getPackages().add(aPackage);
//				}
//				
//				//a subpackage
//				else if ( (element instanceof ICContainer) && !(element.getParent() instanceof ISourceRoot) ){
				if (element instanceof ICContainer){
					Package subPackage = factory.createPackage();
					subPackage.setName(element.getElementName());
	
					String description	= ""; 
					getTooltipForInnerElements(element, description);
					subPackage.setDescription("Folder: " + description +"/"+ element.getElementName());
					subPackage.setColour(getSubDistrictColour(element));
						
					
					EObject parentPackage = findParent(projectModel, element);
					if ( (parentPackage == null) || (!(parentPackage instanceof Package)) )
						throw new NullPointerException("Parent package of " + subPackage.getName() + " not found!");
					((Package)parentPackage).getSubpackages().add(subPackage);
				}
				
				//source/header
				else if (element instanceof ITranslationUnit){				
					String name 	= element.getElementName();
					String colour	= BUILDING_COLOR;//tusUsingMap.keySet().contains(name) ? BUILDING_COLOR_AFFECTED : BUILDING_COLOR;
	
					project.File aFile = factory.createFile();
					aFile.setName(name);
					aFile.setDescription(name + ", LoC : ");
					aFile.setColour(colour);
					aFile.setHeight(2.0);
					aFile.setWidth(4.0);
	
					EObject parentPackage = findParent(projectModel, element);
					if ( (parentPackage == null) || (!(parentPackage instanceof Package)) )
						throw new NullPointerException("Parent package of " + aFile.getName() + " not found!");
					((Package)parentPackage).getFiles().add(aFile);
						
					generateModelForTU(factory, projectModel, (ITranslationUnit) element);
				}
				else 
					throw new IllegalArgumentException("Not a TranslationUnit or a Container");
			}
			
			//save model
			EmfUtilities.saveResource(projectModel, path, modelName, modelExtension);	
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
				
	}
	
	
	/**
	 * Find matching EObject object to ICElement element
	 * by comparing the ancestors up to the project root 
	 * @param projectModel
	 * @param element
	 * @return
	 */
	private EObject findParent(Project projectModel, ICElement element){
		element = findAppropriateElement(element);
		TreeIterator<EObject> it = projectModel.eAllContents();
		while (it.hasNext()){
			Element obj = (Element)it.next();
			if(obj.getName().equals(element.getElementName())){
				Element parentE = obj;
				boolean matches = true;
				while (element.getParent() != null && (!(element.getParent() instanceof ICProject)) && (!(parentE instanceof Project))){
//					element = element.getParent();
					element = findAppropriateElement(element);
					parentE = (Element)parentE.eContainer();
					if (!parentE.getName().equals(element.getElementName())){
						matches = false;
						break;
					}
				}
				if (matches)
					return obj;
			}
		}
		return null;
	}
	
	
	private ICElement findAppropriateElement (ICElement element){
		do{
			element = element.getParent();			
		}
		while ( (element.getElementType() !=  ICElement.C_CCONTAINER) &&
				(element.getElementType() !=  ICElement.C_CLASS)      &&
				(element.getElementType() !=  ICElement.C_PROJECT)    &&
				(element.getElementType() !=  ICElement.C_UNIT));
		return element;
	}
	
	
	private void generateModelForTU(ProjectFactory factory, Project projectModel, ITranslationUnit tu){
		try {
			tu.accept(new ICElementVisitor() {
				@Override
				public boolean visit(ICElement element) throws CoreException {
					int elementType = element.getElementType();
					switch (elementType){
//						case (ICElement.C_CLASS):
//						{   
//							Clazz aClass = factory.createClazz();
//							aClass.setName(element.getElementName());
//							aClass.setDescription(element.getElementName());
//							aClass.setColour(BUILDING_COLOR);
//							aClass.setHeight(0.0);
//							aClass.setWidth(2.0);
//							//find parent
//							EObject parent = findParent(projectModel, element);
//							if (parent instanceof File)
//								((File)parent).getClasses().add(aClass);
//							else if (parent instanceof Clazz){
//								((Clazz)parent).getSubclasses().add(aClass);
//							}
//							else
//								throw new NullPointerException("Parent package of " + aClass.getName() + " not found!");
//							return true;
//						}
						case (ICElement.C_FUNCTION):	
						case (ICElement.C_FUNCTION_DECLARATION):	
						case (ICElement.C_METHOD):
						case (ICElement.C_METHOD_DECLARATION):
						{ 
							Method method = factory.createMethod();
							method.setName(element.getElementName());
							if (element instanceof IFunctionDeclaration)
								method.setDescription(((IFunctionDeclaration)element).getSignature());
							method.setColour(METHOD_COLOR); 
							//find parent
							EObject parent = findParent(projectModel, element);
							if (parent instanceof File)
								((File)parent).getMethods().add(method);
							else if (parent instanceof Clazz){
								((Clazz)parent).getMethods().add(method);
							}
							else
								throw new NullPointerException("Parent package of " + method.getName() + " not found!");
							return true;
						}
						case (ICElement.C_VARIABLE):	
						case (ICElement.C_ENUMERATION):
						case (ICElement.C_STRUCT):	
						case (ICElement.C_UNION):	
						case (ICElement.C_VARIABLE_DECLARATION):	
						case (ICElement.C_TEMPLATE_CLASS):	
						case (ICElement.C_TEMPLATE_STRUCT):	
						case (ICElement.C_TEMPLATE_UNION):	
						case (ICElement.C_TEMPLATE_FUNCTION):
						{
							System.out.println(element +"\t"+ element.getClass() +"\t"+ element.getElementType());							
							return true;
						}
					}
					return true;
				}
				
			});
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void getTooltipForInnerElements(ICElement element, String tooltip){
		if (!(element.getParent() instanceof ISourceRoot))
			getTooltipForInnerElements(element.getParent(), tooltip);
		tooltip += element.getElementName() + "/"; 
	}
	
	
	
	private String getSubDistrictColour(ICElement element){
		int index=0;
		while (element != null && !(element.getParent() instanceof ISourceRoot)){
			index ++;
			element = element.getParent();
		}
		return SUB_DISTRICT_COLOR[index];
	}
	
	

}
