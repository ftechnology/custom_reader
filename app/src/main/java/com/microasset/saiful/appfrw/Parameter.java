/**
 * 
 * @author Mohammad Saiful Alam
 * Hold the list of parameters for any object.
 *
 */
package com.microasset.saiful.appfrw;

import org.json.JSONException;
import org.json.JSONObject;

// ALWAYS NEED TO CREATE NEW OBJECT WHEN INITATING NEW OPERATION. MUST
// setParameter(new DatabaseParameter(MethodName.INSERT));
public class Parameter extends JSONObject{
	protected String mMethodName = "";
	protected Object mValues;
	
	public Parameter(String method) {
		this.mMethodName = method;
	}
    
	/**
	 * @return the mValues
	 */
	public Object getValues() {
		return mValues;
	}

	/**
	 * @param mValues the mValues to set
	 */
	public void setValues(Object mValues) {
		this.mValues = mValues;
	}

	/**
	 * @return the mMethodName
	 */
	public String getMethodName() {
		return mMethodName;
	}
	
	/**
	 * Set the parameter's name and value as object.
	 * @param name
	 * @param value
	 */
	public void setParam(String name, Object value) {
		try {
			this.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the parameter's name and value as string.
	 * @param name
	 * @param value
	 */
	public void setParam(String name, String value) {
		try {
			this.put(name, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Object getParam(String name) {
		try {
			return this.get(name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Return the string value of the given name if found, else return "".
	 */
	public String getString(String name) {
		
		try {
			return super.getString(name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * Return the int value of the given name if found, else return Integer.MIN_VALUE.
	 */
	public int getInt(String name) {
		
		try {
			return super.getInt(name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Integer.MIN_VALUE;//INVALID VALUE
	}
	
	/**
	 * Return the value for given name, return null if no key found.
	 * @param name
	 * @return
	 */
	public Object getValue(String name) {
		try {
			return this.get(name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

