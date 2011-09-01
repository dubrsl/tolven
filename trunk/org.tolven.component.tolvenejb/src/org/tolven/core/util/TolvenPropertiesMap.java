package org.tolven.core.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.tolven.core.TolvenPropertiesLocal;


public class TolvenPropertiesMap implements Map<String, String> {

		// Local IP address
		private String localAddr;
		private TolvenPropertiesLocal propertiesBean;

		public TolvenPropertiesMap(String localAddr, TolvenPropertiesLocal propertiesBean) {
			this.localAddr = localAddr;
			this.propertiesBean = propertiesBean;
		}

		public String get(Object key) {
			String rslt;
			String ipkey = key+"."+localAddr;
			rslt = propertiesBean.getProperty(ipkey);
			if (rslt==null) {
//				TolvenLogger.info(ipkey + " not found", TolvenPropertiesMap.class );
				rslt = propertiesBean.getProperty(key.toString());
			}
			return rslt;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<String, String>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String put(String key, String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends String> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Collection<String> values() {
			// TODO Auto-generated method stub
			return null;
		}


}
