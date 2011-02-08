package org.tolven.app.el;

import java.beans.BeanInfo;
import java.beans.Expression;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import org.tolven.logging.TolvenLogger;
import org.tolven.trim.ex.TrimFactory;

public class TrimBeanResolver extends ELResolver {
	TrimFactory trimFactory;
	private boolean settingValue;
	
	/**
	 * Return true if in the process of setting a value
	 * @return
	 */
	public boolean isSettingValue() {
		return settingValue;
	}
	public void setSettingValue(boolean settingValue) {
		this.settingValue = settingValue;
	}
	public TrimBeanResolver(TrimFactory trimFactory) {
		super();
		this.trimFactory = trimFactory;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		// If we're not setting a value, don't do this
		if (!isSettingValue()) return null;
		if (base==null) return null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(base.getClass());
			for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
				if (property.equals(propertyDescriptor.getName())) {
					String simpleClassName = propertyDescriptor.getPropertyType().getSimpleName();
					Method getMethod = propertyDescriptor.getReadMethod();
					// See if the property value is there yet
					Object object = getMethod.invoke(base);
					// If the value is not set yet, ask the factory to create one
					if (object!=null) {
						context.setPropertyResolved(true);
						return object;
					}
					BeanInfo factoryBeanInfo = Introspector.getBeanInfo(trimFactory.getClass());
					String creatorName = "create" + simpleClassName;
					for (MethodDescriptor factoryMethodDescriptor : factoryBeanInfo.getMethodDescriptors()) {
						if (creatorName.equals(factoryMethodDescriptor.getName())) {
							TolvenLogger.info( "Create property: " + propertyDescriptor.getName()+ " type: " + simpleClassName, TrimBeanResolver.class);
							Expression createExpression = new Expression(trimFactory, creatorName, null);
							object = createExpression.getValue();
							// Now set the new value in the base object
//								Method createMethod = factoryMethodDescriptor.getMethod();
//								createMethod.invoke(base, newObject);
							Method setMethod = propertyDescriptor.getWriteMethod();
							setMethod.invoke(base, object);
							context.setPropertyResolved(true);
							return object;
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Unable to introspect bean", e);
		}
		return null;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
	}

	@Override
	public Class getCommonPropertyType(ELContext arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getFeatureDescriptors(ELContext arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getType(ELContext arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return false;
	}


}
