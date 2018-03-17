/**
 */
package project;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Usage</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link project.Usage#getLine <em>Line</em>}</li>
 *   <li>{@link project.Usage#getCommand <em>Command</em>}</li>
 * </ul>
 *
 * @see project.ProjectPackage#getUsage()
 * @model
 * @generated
 */
public interface Usage extends Element {
	/**
	 * Returns the value of the '<em><b>Line</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Line</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Line</em>' attribute.
	 * @see #setLine(String)
	 * @see project.ProjectPackage#getUsage_Line()
	 * @model
	 * @generated
	 */
	String getLine();

	/**
	 * Sets the value of the '{@link project.Usage#getLine <em>Line</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Line</em>' attribute.
	 * @see #getLine()
	 * @generated
	 */
	void setLine(String value);

	/**
	 * Returns the value of the '<em><b>Command</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Command</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Command</em>' attribute.
	 * @see #setCommand(String)
	 * @see project.ProjectPackage#getUsage_Command()
	 * @model
	 * @generated
	 */
	String getCommand();

	/**
	 * Sets the value of the '{@link project.Usage#getCommand <em>Command</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Command</em>' attribute.
	 * @see #getCommand()
	 * @generated
	 */
	void setCommand(String value);

} // Usage
