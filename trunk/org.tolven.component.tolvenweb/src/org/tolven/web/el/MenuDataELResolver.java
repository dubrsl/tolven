package org.tolven.web.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.tolven.app.entity.MenuData;

public class MenuDataELResolver extends ELResolver {

	public MenuDataELResolver() {
		super();
	}

	@Override
	public Class getType(ELContext context, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (base instanceof MenuData && property instanceof String) {
			MenuData md = (MenuData)base;
			context.setPropertyResolved(true);
			return md.getField( (String)property );
		}
		return null;
	}

	@Override
	public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValue(ELContext arg0, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub

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


}
