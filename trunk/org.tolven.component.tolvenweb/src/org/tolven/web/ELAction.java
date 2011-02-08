package org.tolven.web;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.tolven.web.el.ELMap;

public class ELAction {

	public class ELActionMap extends ELMap {
		private ELContext context;
		private ExpressionFactory factory = ExpressionFactory.newInstance();

		public ELActionMap(ELContext context ) {
			this.context = context;
		}
		public String get(Object key) {
			//	TolvenLogger.info( "ELActionMap::get=" + key, ELAction.class);
			ValueExpression rslt = factory.createValueExpression(context, key.toString(), Object.class );
			return rslt.getValue(context).toString();
//			ELText rslt = ELText.parse( factory, context, key.toString());
//			return rslt.toString(context);
		}

	}
//	public class ELActionMap implements Map<String, String> {
//		private ELContext context;
//		private ExpressionFactory factory = ExpressionFactory.newInstance();
//
//		public ELActionMap(ELContext context ) {
//			this.context = context;
//		}
//
//		public String get(Object key) {
////			TolvenLogger.info( "ELActionMap::get=" + key, ELAction.class);
//	        ELText rslt = ELText.parse( factory, context, key.toString());
//	        return rslt.toString(context);
//        }
//
//		@Override
//		public void clear() {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public boolean containsKey(Object key) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean containsValue(Object value) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public Set<java.util.Map.Entry<String, String>> entrySet() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//
//		@Override
//		public boolean isEmpty() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public Set<String> keySet() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public String put(String key, String value) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public void putAll(Map<? extends String, ? extends String> m) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public String remove(Object key) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public int size() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public Collection<String> values() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//	}
	public ELActionMap getParse( ) {
	    FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
	    ELContext elContext = fc.getELContext( );
//		TolvenLogger.info( "ELAction::parse", ELAction.class );
		return new ELActionMap(elContext);
	}

}
