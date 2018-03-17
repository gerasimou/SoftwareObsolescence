package org.spg.refactoring.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;

import project.Project;
import project.ProjectPackage;

public class EmfUtilities {

	public static void saveResource(Project project, String exportPath, String modelName, final String modelExtension){
		//Register the XMI resource factory for the .testemf extension
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> map = reg.getExtensionToFactoryMap();
		map.put(modelExtension, new XMIResourceFactoryImpl());

		//Obtain a new resource set
		ResourceSet resourceSet = new ResourceSetImpl();
		
		//Create a resource
		Resource resource = resourceSet.createResource(URI.createFileURI(exportPath + modelName +"."+ modelExtension));
		
		//Get the first model element and cast it to the right type
		resource.getContents().add(project);

		//Save content
		try {
			resource.save(Collections.EMPTY_MAP);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static Resource loadResource (String path, String modelName, final String modelExtension){
        // Initialize the model
		ProjectPackage.eINSTANCE.eClass();

		//Register the XMI resource factory for the .testemf extension
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> map = reg.getExtensionToFactoryMap();
		map.put(modelExtension, new XMIResourceFactoryImpl());

		//Obtain a new resource set
		ResourceSet resourceSet = new ResourceSetImpl();
		
		//Get the resource
		Resource resource = resourceSet.getResource(URI.createFileURI(path + modelName +"."+ modelExtension), true);
     
		return resource;
	}
	

	public static EmfModel loadEmfModel(String metamodelURI, String modelFile, String modelName, String readOnLoad, String storeOnDisposal){
		StringProperties properties	= new StringProperties();
		properties.put(EmfModel.PROPERTY_METAMODEL_URI, metamodelURI);
		properties.put(EmfModel.PROPERTY_MODEL_URI, URI.createFileURI(modelFile));//URI.createURI(modelFile));
		properties.put(EmfModel.PROPERTY_NAME, modelName);
		properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad);
		properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, storeOnDisposal);
		EmfModel model 				= new EmfModel();
		try {
			model.load(properties, (IRelativePathResolver) null);
		} 
		catch (EolModelLoadingException e) {
			e.printStackTrace();
		}		
		return model;
	}
	
	
	public static void runEOL(IModel model, String eolScript ){
		EolModule eolModule = new EolModule();
		eolModule.getContext().getModelRepository().addModel(model); 
		try {
			eolModule.parse(new File(eolScript).getAbsoluteFile());
			eolModule.execute();
//			eolModule.getContext().getModelRepository().dispose();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String runEGL(IModel model, String eglScript){
		try {
			EglFileGeneratingTemplateFactory factory = new EglFileGeneratingTemplateFactory();
			File eglFile = new File(eglScript);		
			EglTemplateFactoryModuleAdapter eglModule = new EglTemplateFactoryModuleAdapter(factory);
			eglModule.getContext().getModelRepository().addModel(model);

			//method1
			eglModule.parse(eglFile);
			String output = eglModule.execute().toString();
			System.out.println(output);			
			
			//method 2
//			EglFileGeneratingTemplate template = (EglFileGeneratingTemplate) factory.load(eglFile);			
//			template.process();
//			File target = new File("data/output.txt");
//			target.createNewFile();
//			template.generate(target.getAbsolutePath());
			
			eglModule.getContext().getModelRepository().dispose();
			return output;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
}
