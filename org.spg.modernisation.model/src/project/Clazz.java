/**
 */
package project;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Clazz</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.Clazz#getSubclasses <em>Subclasses</em>}</li>
 *   <li>{@link project.Clazz#getMethods <em>Methods</em>}</li>
 *   <li>{@link project.Clazz#getClassUsages <em>Class Usages</em>}</li>
 *   <li>{@link project.Clazz#getHeight <em>Height</em>}</li>
 *   <li>{@link project.Clazz#getWidth <em>Width</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getClazz()
 * @model
 * @generated
 */
public interface Clazz extends Element {
	/**
	 * Returns the value of the '<em><b>Subclasses</b></em>' containment reference list.
	 * The list contents are of type {@link project.Clazz}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Subclasses</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subclasses</em>' containment reference list.
	 * @see project.ProjectPackage#getClazz_Subclasses()
	 * @model containment="true"
	 * @generated
	 */
	EList<Clazz> getSubclasses();

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
	 * @see project.ProjectPackage#getClazz_Methods()
	 * @model containment="true"
	 * @generated
	 */
	EList<Method> getMethods();

	/**
	 * Returns the value of the '<em><b>Class Usages</b></em>' containment reference list.
	 * The list contents are of type {@link project.Usage}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class Usages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class Usages</em>' containment reference list.
	 * @see project.ProjectPackage#getClazz_ClassUsages()
	 * @model containment="true"
	 * @generated
	 */
	EList<Usage> getClassUsages();

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
	 * @see project.ProjectPackage#getClazz_Height()
	 * @model
	 * @generated
	 */
	Double getHeight();

	/**
	 * Sets the value of the '{@link project.Clazz#getHeight <em>Height</em>}' attribute.
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
	 * @see project.ProjectPackage#getClazz_Width()
	 * @model
	 * @generated
	 */
	Double getWidth();

	/**
	 * Sets the value of the '{@link project.Clazz#getWidth <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Width</em>' attribute.
	 * @see #getWidth()
	 * @generated
	 */
	void setWidth(Double value);

} // Clazz
