package org.tolven.trim.ex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.tolven.trim.ActRelationship;

/**
 * This class presents an underlying list of participations as map.
 * @author Srini Kandula
 *
 */
public class ActRelationshipsMap implements Map<String, List<ActRelationship>> {

	private List<ActRelationship> list;
	private Map<String, List<ActRelationship>> _instance = new HashMap<String, List<ActRelationship>>();
	public ActRelationshipsMap( List<ActRelationship> list ) {
		this.list = list;
		for(ActRelationship ap : list){
			if(_instance.containsKey(ap.getName())){
				((List<ActRelationship>)_instance.get(ap.getName())).add(ap);
			}else{
				List<ActRelationship> _list = new ArrayList<ActRelationship>();
				_list.add(ap);
				_instance.put(ap.getName(),_list);
			}
		}
	}
	@Override
	public List<ActRelationship> get(Object key) {
    	if(_instance.get(key) != null)
    		return (List<ActRelationship>)_instance.get(key);
    	else{
    		List<ActRelationship> _list = new ArrayList<ActRelationship>();
    		_instance.put((String)key,_list);
    		return _list;
    	}
   }
	
	public void refreshList(){
		this.list.clear();
		for(String key :this._instance.keySet()){
			this.list.addAll(this._instance.get(key));
		}
		
	}
	

	@Override
	public boolean containsValue(Object value) {
    	for (ActRelationship ap : list) {
    		if (value == ap) return true;
    	}
		return false;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return _instance.containsKey( key );
	}

	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void clear() {
		list.clear();
		_instance.clear();
	}

	@Override
	public Set<Entry<String, List<ActRelationship>>> entrySet() {
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
	public List<ActRelationship> put(String arg0, List<ActRelationship> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void putAll(Map<? extends String, ? extends List<ActRelationship>> m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<ActRelationship> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Collection<List<ActRelationship>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	


}
