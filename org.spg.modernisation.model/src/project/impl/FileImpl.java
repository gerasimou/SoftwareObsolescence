/**
 */
package project.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import project.Clazz;
import project.File;
import project.Method;
import project.ProjectPackage;
import project.Usage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link project.impl.FileImpl#getMethods <em>Methods</em>}</li>
 *   <li>{@link project.impl.FileImpl#getFileUsages <em>File Usages</em>}</li>
 *   <li>{@link project.impl.FileImpl#getHeight <em>Height</em>}</li>
 *   <li>{@link project.impl.FileImpl#getWidth <em>Width</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FileImpl extends ElementImpl implements File {
	/**
	 * The cached value of the '{@link #getMethods() <em>Methods</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMethods()
	 * @generated
	 * @ordered
	 */
	protected EList<Method> methods;

	/**
	 * The cached value of the '{@link #getFileUsages() <em>File Usages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFileUsages()
	 * @generated
	 * @ordered
	 */
	protected EList<Usage> fileUsages;

	/**
	 * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeight()
	 * @generated
	 * @ordered
	 */
	protected static final Double HEIGHT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHeight() <em>Height</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeight()
	 * @generated
	 * @ordered
	 */
	protected Double height = HEIGHT_EDEFAULT;

	/**
	 * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected static final Double WIDTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected Double width = WIDTH_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ProjectPackage.Literals.FILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Method> getMethods() {
		if (methods == null) {
			methods = new EObjectContainmentEList<Method>(Method.class, this, ProjectPackage.FILE__METHODS);
		}
		return methods;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Usage> getFileUsages() {
		if (fileUsages == null) {
			fileUsages = new EObjectContainmentEList<Usage>(Usage.class, this, ProjectPackage.FILE__FILE_USAGES);
		}
		return fileUsages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Double getHeight() {
		return height;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHeight(Double newHeight) {
		Double oldHeight = height;
		height = newHeight;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.FILE__HEIGHT, oldHeight, height));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Double getWidth() {
		return width;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWidth(Double newWidth) {
		Double oldWidth = width;
		width = newWidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectPackage.FILE__WIDTH, oldWidth, width));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProjectPackage.FILE__METHODS:
				return ((InternalEList<?>)getMethods()).basicRemove(otherEnd, msgs);
			case ProjectPackage.FILE__FILE_USAGES:
				return ((InternalEList<?>)getFileUsages()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ProjectPackage.FILE__METHODS:
				return getMethods();
			case ProjectPackage.FILE__FILE_USAGES:
				return getFileUsages();
			case ProjectPackage.FILE__HEIGHT:
				return getHeight();
			case ProjectPackage.FILE__WIDTH:
				return getWidth();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ProjectPackage.FILE__METHODS:
				getMethods().clear();
				getMethods().addAll((Collection<? extends Method>)newValue);
				return;
			case ProjectPackage.FILE__FILE_USAGES:
				getFileUsages().clear();
				getFileUsages().addAll((Collection<? extends Usage>)newValue);
				return;
			case ProjectPackage.FILE__HEIGHT:
				setHeight((Double)newValue);
				return;
			case ProjectPackage.FILE__WIDTH:
				setWidth((Double)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ProjectPackage.FILE__METHODS:
				getMethods().clear();
				return;
			case ProjectPackage.FILE__FILE_USAGES:
				getFileUsages().clear();
				return;
			case ProjectPackage.FILE__HEIGHT:
				setHeight(HEIGHT_EDEFAULT);
				return;
			case ProjectPackage.FILE__WIDTH:
				setWidth(WIDTH_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ProjectPackage.FILE__METHODS:
				return methods != null && !methods.isEmpty();
			case ProjectPackage.FILE__FILE_USAGES:
				return fileUsages != null && !fileUsages.isEmpty();
			case ProjectPackage.FILE__HEIGHT:
				return HEIGHT_EDEFAULT == null ? height != null : !HEIGHT_EDEFAULT.equals(height);
			case ProjectPackage.FILE__WIDTH:
				return WIDTH_EDEFAULT == null ? width != null : !WIDTH_EDEFAULT.equals(width);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (height: ");
		result.append(height);
		result.append(", width: ");
		result.append(width);
		result.append(')');
		return result.toString();
	}

} //FileImpl
