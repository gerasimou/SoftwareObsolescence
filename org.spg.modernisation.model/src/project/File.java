/**
 */
package project;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.File#getMethods <em>Methods</em>}</li>
 *   <li>{@link project.File#getFileUsages <em>File Usages</em>}</li>
 *   <li>{@link project.File#getHeight <em>Height</em>}</li>
 *   <li>{@link project.File#getWidth <em>Width</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getFile()
 * @model
 * @generated
 */
public interface File extends Element {
	/**
	 * Returns the value of the '<em><b>Methods</b></em>' containment reference list.
	 * The list contents are of type {@link project.Method}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Methods</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Methods</em>' containment reference list.
	 * @see project.ProjectPackage#getFile_Methods()
	 * @model containment="true"
	 * @generated
	 */
	EList<Method> getMethods();

	/**
	 * Returns the value of the '<em><b>File Usages</b></em>' containment reference list.
	 * The list contents are of type {@link project.Usage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Usages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File Usages</em>' containment reference list.
	 * @see project.ProjectPackage#getFile_FileUsages()
	 * @model containment="true"
	 * @generated
	 */
	EList<Usage> getFileUsages();

	/**
	 * Returns the value of the '<em><b>Height</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Height</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Height</em>' attribute.
	 * @see #setHeight(Double)
	 * @see project.ProjectPackage#getFile_Height()
	 * @model
	 * @generated
	 */
	Double getHeight();

	/**
	 * Sets the value of the '{@link project.File#getHeight <em>Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Height</em>' attribute.
	 * @see #getHeight()
	 * @generated
	 */
	void setHeight(Double value);

	/**
	 * Returns the value of the '<em><b>Width</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Width</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Width</em>' attribute.
	 * @see #setWidth(Double)
	 * @see project.ProjectPackage#getFile_Width()
	 * @model
	 * @generated
	 */
	Double getWidth();

	/**
	 * Sets the value of the '{@link project.File#getWidth <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Width</em>' attribute.
	 * @see #getWidth()
	 * @generated
	 */
	void setWidth(Double value);

} // File
