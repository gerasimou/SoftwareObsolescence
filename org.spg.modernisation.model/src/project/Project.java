/**
 */
package project;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.Project#getPackages <em>Packages</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getProject()
 * @model
 * @generated
 */
public interface Project extends Element {
	/**
	 * Returns the value of the '<em><b>Packages</b></em>' containment reference list.
	 * The list contents are of type {@link project.Package}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Packages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Packages</em>' containment reference list.
	 * @see project.ProjectPackage#getProject_Packages()
	 * @model containment="true"
	 * @generated
	 */
	EList<project.Package> getPackages();

} // Project
