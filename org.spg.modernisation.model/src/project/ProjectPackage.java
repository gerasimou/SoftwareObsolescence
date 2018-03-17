/**
 */
package project;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see project.ProjectFactory
 * @model kind="package"
 * @generated
 */
public interface ProjectPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "project";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.spg.modernisation.model.project";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "project";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProjectPackage eINSTANCE = project.impl.ProjectPackageImpl.init();

	/**
	 * The meta object id for the '{@link project.impl.ElementImpl <em>Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.ElementImpl
	 * @see project.impl.ProjectPackageImpl#getElement()
	 * @generated
	 */
	int ELEMENT = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT__DESCRIPTION = 1;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT__COLOUR = 2;

	/**
	 * The number of structural features of the '<em>Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link project.impl.ProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.ProjectImpl
	 * @see project.impl.ProjectPackageImpl#getProject()
	 * @generated
	 */
	int PROJECT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__NAME = ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__DESCRIPTION = ELEMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__COLOUR = ELEMENT__COLOUR;

	/**
	 * The feature id for the '<em><b>Packages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__PACKAGES = ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link project.impl.PackageImpl <em>Package</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.PackageImpl
	 * @see project.impl.ProjectPackageImpl#getPackage()
	 * @generated
	 */
	int PACKAGE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__NAME = ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__DESCRIPTION = ELEMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__COLOUR = ELEMENT__COLOUR;

	/**
	 * The feature id for the '<em><b>Subpackages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__SUBPACKAGES = ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Files</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE__FILES = ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Package</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PACKAGE_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link project.impl.FileImpl <em>File</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.FileImpl
	 * @see project.impl.ProjectPackageImpl#getFile()
	 * @generated
	 */
	int FILE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__NAME = ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__DESCRIPTION = ELEMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__COLOUR = ELEMENT__COLOUR;

	/**
	 * The feature id for the '<em><b>Methods</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__METHODS = ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>File Usages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__FILE_USAGES = ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__HEIGHT = ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE__WIDTH = ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>File</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link project.impl.MethodImpl <em>Method</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.MethodImpl
	 * @see project.impl.ProjectPackageImpl#getMethod()
	 * @generated
	 */
	int METHOD = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__NAME = ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__DESCRIPTION = ELEMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__COLOUR = ELEMENT__COLOUR;

	/**
	 * The feature id for the '<em><b>Method Usages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__METHOD_USAGES = ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Start Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__START_LINE = ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>End Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD__END_LINE = ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Method</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METHOD_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link project.impl.UsageImpl <em>Usage</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see project.impl.UsageImpl
	 * @see project.impl.ProjectPackageImpl#getUsage()
	 * @generated
	 */
	int USAGE = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE__NAME = ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE__DESCRIPTION = ELEMENT__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Colour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE__COLOUR = ELEMENT__COLOUR;

	/**
	 * The feature id for the '<em><b>Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE__LINE = ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Command</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE__COMMAND = ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Usage</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USAGE_FEATURE_COUNT = ELEMENT_FEATURE_COUNT + 2;


	/**
	 * Returns the meta object for class '{@link project.Element <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element</em>'.
	 * @see project.Element
	 * @generated
	 */
	EClass getElement();

	/**
	 * Returns the meta object for the attribute '{@link project.Element#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see project.Element#getName()
	 * @see #getElement()
	 * @generated
	 */
	EAttribute getElement_Name();

	/**
	 * Returns the meta object for the attribute '{@link project.Element#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see project.Element#getDescription()
	 * @see #getElement()
	 * @generated
	 */
	EAttribute getElement_Description();

	/**
	 * Returns the meta object for the attribute '{@link project.Element#getColour <em>Colour</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Colour</em>'.
	 * @see project.Element#getColour()
	 * @see #getElement()
	 * @generated
	 */
	EAttribute getElement_Colour();

	/**
	 * Returns the meta object for class '{@link project.Project <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project</em>'.
	 * @see project.Project
	 * @generated
	 */
	EClass getProject();

	/**
	 * Returns the meta object for the containment reference list '{@link project.Project#getPackages <em>Packages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Packages</em>'.
	 * @see project.Project#getPackages()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Packages();

	/**
	 * Returns the meta object for class '{@link project.Package <em>Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Package</em>'.
	 * @see project.Package
	 * @generated
	 */
	EClass getPackage();

	/**
	 * Returns the meta object for the containment reference list '{@link project.Package#getSubpackages <em>Subpackages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Subpackages</em>'.
	 * @see project.Package#getSubpackages()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Subpackages();

	/**
	 * Returns the meta object for the containment reference list '{@link project.Package#getFiles <em>Files</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Files</em>'.
	 * @see project.Package#getFiles()
	 * @see #getPackage()
	 * @generated
	 */
	EReference getPackage_Files();

	/**
	 * Returns the meta object for class '{@link project.File <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>File</em>'.
	 * @see project.File
	 * @generated
	 */
	EClass getFile();

	/**
	 * Returns the meta object for the containment reference list '{@link project.File#getMethods <em>Methods</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Methods</em>'.
	 * @see project.File#getMethods()
	 * @see #getFile()
	 * @generated
	 */
	EReference getFile_Methods();

	/**
	 * Returns the meta object for the containment reference list '{@link project.File#getFileUsages <em>File Usages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>File Usages</em>'.
	 * @see project.File#getFileUsages()
	 * @see #getFile()
	 * @generated
	 */
	EReference getFile_FileUsages();

	/**
	 * Returns the meta object for the attribute '{@link project.File#getHeight <em>Height</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Height</em>'.
	 * @see project.File#getHeight()
	 * @see #getFile()
	 * @generated
	 */
	EAttribute getFile_Height();

	/**
	 * Returns the meta object for the attribute '{@link project.File#getWidth <em>Width</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Width</em>'.
	 * @see project.File#getWidth()
	 * @see #getFile()
	 * @generated
	 */
	EAttribute getFile_Width();

	/**
	 * Returns the meta object for class '{@link project.Method <em>Method</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Method</em>'.
	 * @see project.Method
	 * @generated
	 */
	EClass getMethod();

	/**
	 * Returns the meta object for the containment reference list '{@link project.Method#getMethodUsages <em>Method Usages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Method Usages</em>'.
	 * @see project.Method#getMethodUsages()
	 * @see #getMethod()
	 * @generated
	 */
	EReference getMethod_MethodUsages();

	/**
	 * Returns the meta object for the attribute '{@link project.Method#getStartLine <em>Start Line</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Start Line</em>'.
	 * @see project.Method#getStartLine()
	 * @see #getMethod()
	 * @generated
	 */
	EAttribute getMethod_StartLine();

	/**
	 * Returns the meta object for the attribute '{@link project.Method#getEndLine <em>End Line</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>End Line</em>'.
	 * @see project.Method#getEndLine()
	 * @see #getMethod()
	 * @generated
	 */
	EAttribute getMethod_EndLine();

	/**
	 * Returns the meta object for class '{@link project.Usage <em>Usage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Usage</em>'.
	 * @see project.Usage
	 * @generated
	 */
	EClass getUsage();

	/**
	 * Returns the meta object for the attribute '{@link project.Usage#getLine <em>Line</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Line</em>'.
	 * @see project.Usage#getLine()
	 * @see #getUsage()
	 * @generated
	 */
	EAttribute getUsage_Line();

	/**
	 * Returns the meta object for the attribute '{@link project.Usage#getCommand <em>Command</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Command</em>'.
	 * @see project.Usage#getCommand()
	 * @see #getUsage()
	 * @generated
	 */
	EAttribute getUsage_Command();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ProjectFactory getProjectFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link project.impl.ElementImpl <em>Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.ElementImpl
		 * @see project.impl.ProjectPackageImpl#getElement()
		 * @generated
		 */
		EClass ELEMENT = eINSTANCE.getElement();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT__NAME = eINSTANCE.getElement_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT__DESCRIPTION = eINSTANCE.getElement_Description();

		/**
		 * The meta object literal for the '<em><b>Colour</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT__COLOUR = eINSTANCE.getElement_Colour();

		/**
		 * The meta object literal for the '{@link project.impl.ProjectImpl <em>Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.ProjectImpl
		 * @see project.impl.ProjectPackageImpl#getProject()
		 * @generated
		 */
		EClass PROJECT = eINSTANCE.getProject();

		/**
		 * The meta object literal for the '<em><b>Packages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__PACKAGES = eINSTANCE.getProject_Packages();

		/**
		 * The meta object literal for the '{@link project.impl.PackageImpl <em>Package</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.PackageImpl
		 * @see project.impl.ProjectPackageImpl#getPackage()
		 * @generated
		 */
		EClass PACKAGE = eINSTANCE.getPackage();

		/**
		 * The meta object literal for the '<em><b>Subpackages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__SUBPACKAGES = eINSTANCE.getPackage_Subpackages();

		/**
		 * The meta object literal for the '<em><b>Files</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PACKAGE__FILES = eINSTANCE.getPackage_Files();

		/**
		 * The meta object literal for the '{@link project.impl.FileImpl <em>File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.FileImpl
		 * @see project.impl.ProjectPackageImpl#getFile()
		 * @generated
		 */
		EClass FILE = eINSTANCE.getFile();

		/**
		 * The meta object literal for the '<em><b>Methods</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FILE__METHODS = eINSTANCE.getFile_Methods();

		/**
		 * The meta object literal for the '<em><b>File Usages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FILE__FILE_USAGES = eINSTANCE.getFile_FileUsages();

		/**
		 * The meta object literal for the '<em><b>Height</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILE__HEIGHT = eINSTANCE.getFile_Height();

		/**
		 * The meta object literal for the '<em><b>Width</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILE__WIDTH = eINSTANCE.getFile_Width();

		/**
		 * The meta object literal for the '{@link project.impl.MethodImpl <em>Method</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.MethodImpl
		 * @see project.impl.ProjectPackageImpl#getMethod()
		 * @generated
		 */
		EClass METHOD = eINSTANCE.getMethod();

		/**
		 * The meta object literal for the '<em><b>Method Usages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference METHOD__METHOD_USAGES = eINSTANCE.getMethod_MethodUsages();

		/**
		 * The meta object literal for the '<em><b>Start Line</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METHOD__START_LINE = eINSTANCE.getMethod_StartLine();

		/**
		 * The meta object literal for the '<em><b>End Line</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METHOD__END_LINE = eINSTANCE.getMethod_EndLine();

		/**
		 * The meta object literal for the '{@link project.impl.UsageImpl <em>Usage</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see project.impl.UsageImpl
		 * @see project.impl.ProjectPackageImpl#getUsage()
		 * @generated
		 */
		EClass USAGE = eINSTANCE.getUsage();

		/**
		 * The meta object literal for the '<em><b>Line</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USAGE__LINE = eINSTANCE.getUsage_Line();

		/**
		 * The meta object literal for the '<em><b>Command</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USAGE__COMMAND = eINSTANCE.getUsage_Command();

	}

} //ProjectPackage
