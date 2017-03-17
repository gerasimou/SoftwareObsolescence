package org.spg.refactoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.JSONGenerator;
import org.spg.refactoring.utilities.Utility;


public class ProjectVisualiser {
	/** City */
	City city = null;
	
	/** list of packages */
	List<District> districtsList;

	/** list of subpackages */
	List<SubDistrict> subDistrictsList;

	/** list of source or header files*/
	List<Building> buildingsList;
	
	
	final String DISTRICT_COLOR 		 = "0xF7AB29";
	final String CITY_COLOR	 			 = "0xD5C8B2";
	final String BUILDING_COLOR 		 = "0x2A75B3";
	final String BUILDING_COLOR_AFFECTED = "0xB22029";
	final String[] SUB_DISTRICT_COLOR 	 = {"0xA0522D", "0xD2691E", "0xDAA520", "0xF4A460", "0xD2B48C", "0xF5DEB3", "0xFFF8DC"};
	
	
	Random rand 		= new Random(System.currentTimeMillis());
	
	
	/**
	 * Class constructor
	 */
	public ProjectVisualiser() {
		districtsList 		= new ArrayList<District>();
		subDistrictsList	= new ArrayList<SubDistrict>();
		buildingsList 		= new ArrayList<Building>();  
	}
	
	
	public String run (IProject project, String jsonPath, Collection<String> tusUsing){
		try {
			
			generateCityElements(project, tusUsing);
			
			String json = generateJSON();
			
			String filename = project.getName()+".json";
			
			Utility.exportToFile(jsonPath+filename, json, false);
			
			return filename;
			
		}
		catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private void generateCityElements(IProject project, Collection<String> tusUsing) throws CoreException{
		List<ICElement> icElementsList = new ArrayList<ICElement>(); 
		ICProject cproject = CdtUtilities.getICProject(project);
		
		//add city
		city = new City(project.getName(), project.getName());
		
		//add default district (src/)
		District defaultDistrict = new District("default", "default package", DISTRICT_COLOR, city.name);
		districtsList.add(defaultDistrict);
		
		
		for (ISourceRoot sourceRoot : cproject.getSourceRoots()){
			if (sourceRoot.getElementName().equals("src")){
//				System.out.println(sourceRoot.getElementName()+"\t"+ sourceRoot.getLocationURI().getRawPath());
				CdtUtilities.getICElementsFromProject(sourceRoot, ICElement.class, icElementsList);
			}
		}
//		
		for (ICElement element : icElementsList){
			if ( (element instanceof ICContainer) && (element.getParent() instanceof ISourceRoot) ){//a package
				String name 	= element.getElementName();
				String tooltip	= "Folder: " + name;
				District district = new District(name, tooltip, DISTRICT_COLOR, city.name);
				districtsList.add(district);
			}
			else if ( (element instanceof ICContainer) && !(element.getParent() instanceof ISourceRoot) ){//a subpackage
				String name 	= element.getElementName();
				String tooltip	= "Folder: "; 
				getTooltipForInnerElements(element, name, tooltip);
				tooltip 		+= name;
				String colour	= getSubDistrictColour(element); 
				SubDistrict sDistrict = new SubDistrict(name, tooltip, colour, element.getParent().getElementName());
				subDistrictsList.add(sDistrict);
			}
			else if (element instanceof ITranslationUnit){//source/header
				String name 	= element.getElementName();
				String tooltip	= name + ", LoC : ";
				String colour	= tusUsing.contains(name) ? BUILDING_COLOR_AFFECTED : BUILDING_COLOR;
				String height	= rand.nextInt(500) + "";
				String width	= rand.nextInt(100) + "";
				String district = element.getParent() instanceof ISourceRoot ? defaultDistrict.name : element.getParent().getElementName(); 
				Building building = new Building(name, tooltip, colour, height, width, district);
				buildingsList.add(building);
			}
			else 
				throw new IllegalArgumentException("Not a TranslationUnit or a Container");
		}
	}
	
	
	private void getTooltipForInnerElements(ICElement element, String eName, String tooltip){
		if (!(element.getParent() instanceof ISourceRoot))
			getTooltipForInnerElements(element.getParent(), eName, tooltip);
		tooltip += element.getElementName() + "/"; 
	}
	
	
	private String getSubDistrictColour(ICElement element){
//		int r = 250;
//		int g = 230;
//		int b = 10;
		int index=0;
		while (element != null && !(element.getParent() instanceof ISourceRoot)){
//			r -= 25;
//			g -= 25;
			index ++;
			element = element.getParent();
		}
//		return String.format("0x%02X%02X%02X", r, g, b);
		return SUB_DISTRICT_COLOR[index];
	}
	
	
	private String generateJSON(){
		StringBuilder JSON = new StringBuilder("{\n\n");
		
		//add city
		JSON.append("\"city\":\n");
		JSON.append(JSONGenerator.toJSON(city));
		JSON.append(",\n\n");		

		//add districts
		JSON.append("\"districts\":[\n");
		Iterator<District> itD = districtsList.iterator();
		while (itD.hasNext()){
			JSON.append(JSONGenerator.toJSON(itD.next()));
			if (itD.hasNext())
				JSON.append(",\n");
		}

		Iterator<SubDistrict> itSD = subDistrictsList.iterator();
		while (itSD.hasNext()){
			if (itSD.hasNext())
				JSON.append(",\n");
			JSON.append(JSONGenerator.toJSON(itSD.next()));
		}		
		JSON.append("],\n\n");
		
		//add buildings
		JSON.append("\"buildings\":[\n");
		Iterator<Building> itB = buildingsList.iterator();
		while (itB.hasNext()){
			JSON.append(JSONGenerator.toJSON(itB.next()));
			if (itB.hasNext())
				JSON.append(",\n");
		}
		JSON.append("]\n");

		JSON.append("\n}");
		
//		System.out.println(JSON.toString());
		return JSON.toString();
	}
	
	
	
	class City{
		/** Project name*/
		String name;
		
		/** Tooltip shown*/
		String tooltip;
		
		/**
		 * Create a new City
		 * @param name
		 * @param tooltip
		 */
		protected City(String name, String tooltip){
			this.name 		= name;
			this.tooltip	= tooltip;
		}
	}
	
	
	class District{
		/** package name*/
		String name;
		
		/** Tooltip shown*/
		String tooltip;
		
		/** Colour*/
		String color;
		
		/** if it's in the 1st level - src dir*/
		String city;
		
		
		/**
		 * Create a new District
		 * @param name
		 * @param tooltip
		 */
		protected District(String name, String tooltip, String color, String city){
			this.name 		= name;
			this.tooltip	= tooltip;
			this.city		= city;
			this.color		= color;
		}
	}
	

	class SubDistrict{
		/** package name*/
		String name;
		
		/** Tooltip shown*/
		String tooltip;
		
		/** Colour*/
		String color;
		
		/** if it's a subpackage- src/xxx/.. */
		String district;
		
		
		/**
		 * Create a new District
		 * @param name
		 * @param tooltip
		 */
		protected SubDistrict(String name, String tooltip, String color, String district){
			this.name 		= name;
			this.tooltip	= tooltip;
			this.district	= district;
			this.color		= color;
		}
	}
	
	
	class Building{
		/** source/header name*/
		String name;
		
		/** Tooltip shown*/
		String tooltip;
		
		/** Colour*/
		String color;
		
		/** owning package  */
		String district;
		
		/** LoC*/
		String height;
		
		/** Number of obsolete methods*/
		String width;
		
		
		/**
		 * Create a new Building
		 */
		protected Building(String name, String tooltip, String color, String height, String width, String district){
			this.name 		= name;
			this.tooltip	= tooltip;
			this.color		= color;
			this.height		= height;
			this.width		= width;
			this.district	= district;
		}
	}
}
