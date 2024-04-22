package arrays;

import java.util.HashMap;

public class ExtendedHashMap {
	HashMap<String, Object> wrappedHashMap = null; //

	public ExtendedHashMap() {
		 wrappedHashMap = new HashMap<String, Object>();
	}
	
	public HashMap<String, Object> getWrapperdHashMap() {
		return wrappedHashMap;
	}

	public void setWrapperdHashMap(HashMap<String, Object> wrapperdHashMap) {
		this.wrappedHashMap = wrapperdHashMap;
	}
	
	public int getInt(String strKey) {
		return ((Integer)wrappedHashMap.get(strKey)).intValue();
	}
	
	public long getLong(String strKey) {
		return ((Long)wrappedHashMap.get(strKey)).longValue();
	}
	
	public double getDouble(String strKey) {
		return ((Double)wrappedHashMap.get(strKey)).doubleValue();
	}
	
	public Boolean getBoolean(String strKey) {
		return (Boolean)wrappedHashMap.get(strKey);
	}
	
	public String getString(String strKey) {
		return (String)wrappedHashMap.get(strKey);
	}
	
	public HashMap<Object, Object> getHashMap(String strKey) {
		return (HashMap<Object, Object>)wrappedHashMap.get(strKey);
	}
	
	public void put(String key, Object value){ //put wrapper proxy method
		wrappedHashMap.put(key, value);
	}
	
	public Object get(String key){ //generic get wrapper proxy method (devuelve object, no castea auto) 
		return wrappedHashMap.get(key);
	}
	private void fakeForModif1(){};
}
