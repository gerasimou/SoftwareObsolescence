/**
 */
package project;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Method</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.Method#getMethodUsages <em>Method Usages</em>}</li>
 *   <li>{@link project.Method#getStartLine <em>Start Line</em>}</li>
 *   <li>{@link project.Method#getEndLine <em>End Line</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getMethod()
 * @model
 * @generated
 */
public interface Method extends Element {
	/**
	 * Returns the value of the '<em><b>Method Usages</b></em>' containment reference list.
	 * The list contents are of type {@link project.Usage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Method Usages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Method Usages</em>' containment reference list.
	 * @see project.ProjectPackage#getMethod_MethodUsages()
	 * @model containment="true"
	 * @generated
	 */
	EList<Usage> getMethodUsages();

	/**
	 * Returns the value of the '<em><b>Start Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start Line</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start Line</em>' attribute.
	 * @see #setStartLine(int)
	 * @see project.ProjectPackage#getMethod_StartLine()
	 * @model
	 * @generated
	 */
	int getStartLine();

	/**
	 * Sets the value of the '{@link project.Method#getStartLine <em>Start Line</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start Line</em>' attribute.
	 * @see #getStartLine()
	 * @generated
	 */
	void setStartLine(int value);

	/**
	 * Returns the value of the '<em><b>End Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>End Line</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Line</em>' attribute.
	 * @see #setEndLine(int)
	 * @see project.ProjectPackage#getMethod_EndLine()
	 * @model
	 * @generated
	 */
	int getEndLine();

	/**
	 * Sets the value of the '{@link project.Method#getEndLine <em>End Line</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Line</em>' attribute.
	 * @see #getEndLine()
	 * @generated
	 */
	void setEndLine(int value);

} // Method
