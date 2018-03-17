/**
 */
package project;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.Package#getSubpackages <em>Subpackages</em>}</li>
 *   <li>{@link project.Package#getFiles <em>Files</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getPackage()
 * @model
 * @generated
 */
public interface Package extends Element {
	/**
	 * Returns the value of the '<em><b>Subpackages</b></em>' containment reference list.
	 * The list contents are of type {@link project.Package}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Subpackages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subpackages</em>' containment reference list.
	 * @see project.ProjectPackage#getPackage_Subpackages()
	 * @model containment="true"
	 * @generated
	 */
	EList<Package> getSubpackages();

	/**
	 * Returns the value of the '<em><b>Files</b></em>' containment reference list.
	 * The list contents are of type {@link project.File}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Files</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Files</em>' containment reference list.
	 * @see project.ProjectPackage#getPackage_Files()
	 * @model containment="true"
	 * @generated
	 */
	EList<File> getFiles();

} // Package
