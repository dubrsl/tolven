/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */

package org.tolven.queue.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.tolven.logging.TolvenLogger;
/**
 * The utility class to load properties from one object to another if name is same.
 * 
 * @author Sabu Antony
 */
public class BeanPropertyLoader {
		public BeanPropertyLoader() {
	}

	// ---------------------------------------------------------------------------/
	public static Object populate(Object outObject, Object inObject) {
		// TolvenLogger.info(">>--populate-->>", BeanPropertyLoader.class);

		// Object outObj = null;
		Class inputClass = null;
		Class outPutClass = null;
		Method getterObjMethlist[] = null;
		Method setterObjMethlist[] = null;
		Method setterMeth = null;
		Method getterMeth = null;
		String getterAttrName = null;
		String getterMethName = null;
		String setterAttrName = null;
		String setterMethName = null;
		Object gettterArglist[] = null;
		Object setterArglist[] = null;
		// validate the input object and output class name
		if (inObject != null && outObject != null) {
			try {
				// getting the Class Attribute of the Input Object
				inputClass = inObject.getClass();
				outPutClass = outObject.getClass(); // should catch
				// ClassNotFoundException
				// getting the method list of input class
				getterObjMethlist = inputClass.getDeclaredMethods();
				// getting the method list of Output class
				setterObjMethlist = outPutClass.getDeclaredMethods();
				gettterArglist = new Object[0];
				setterArglist = new Object[1];
				// Outer for loop will loop throught the output class method
				// list one by one
				// if the method is a setter (start with set) then we loop
				// through the
				// method list of the input class to get the equvallent getter
				// method.
				// outer for loop starts ---------------->>
				for (int j = 0; j < setterObjMethlist.length; j++) {
					try {
						setterMeth = null;
						setterMethName = null;
						setterMeth = setterObjMethlist[j];
						setterMethName = setterMeth.getName();
						// check for the method to find it is a setter or not
						if (setterMethName.startsWith("set")) {
							// if setter get the method name/attribute name to
							// get the equivallent getter from the inObject
							setterAttrName = setterMethName.substring(3,
									setterMethName.length());

							// inner for loop starts ------------->>
							// loop through all the get methods to find the
							// equivallet getter mehod

							for (int i = 0; i < getterObjMethlist.length; i++) {

								getterMeth = null;
								getterMeth = getterObjMethlist[i];
								getterMethName = getterMeth.getName();

								// check that it is a getter method
								if (getterMethName.startsWith("get")
										|| getterMethName.startsWith("is")) {
									// if getter method the get the attribute
									// name.
									if (getterMethName.startsWith("is")) {
										getterAttrName = getterMethName
												.substring(2, getterMethName
														.length());
									} else {
										getterAttrName = getterMethName
												.substring(3, getterMethName
														.length());
									}
									// compare the attribute with attribute name
									// of
									// the out Object
									if (getterAttrName.equals(setterAttrName)) {

										// should catch
										// InvocationTargetException,IllegalAccessException
										// create a list to invoke the method in
										// out
										// object
										Object variableValue = null;
										try {
											variableValue = getterMeth.invoke(
													inObject, gettterArglist);

										} catch (IllegalArgumentException illArgEx) {

											// illArgEx.printStackTrace();
										}

										if (variableValue != null
												&& !variableValue.toString()
														.trim().equals("")) {
											// ---------------------------------------------------if
											// getter method is returning a
											// null-------------------------------------------//
											// -------------------- or empty
											// string
											// we don't have to invoke its
											// setter-------------------//

											setterArglist[0] = getterMeth
													.invoke(inObject,
															gettterArglist);

											{

												setterMeth.invoke(outObject,
														setterArglist);
											}
										}// <<<<----------getter method
											// return

									} // end if
									// getterAttrName.equals(setterAttrName))

								} // end if outter

							} // end for //inner for loop ends <<-----------
						} // end if
					} catch (IllegalArgumentException illArgEx) {
						illArgEx.printStackTrace();
						TolvenLogger.info("getterMethName : " + getterMethName, BeanPropertyLoader.class);
						TolvenLogger.info("getterMeth Return type : "
								+ getterMeth.getReturnType(), BeanPropertyLoader.class);

					}
				} // outer for loop ends ---------------->>
			} catch (IllegalAccessException illAccEx) {
				// illAccEx.printStackTrace();
			} catch (InvocationTargetException invoEx) {
				// invoEx.printStackTrace();
			}
		}
		/**
		 * @todo now returning the null if any exception occered. should throw a
		 *       user defined Exception to make it simple.
		 * 
		 */
		// TolvenLogger.info("<<--populate--<<", BeanPropertyLoader.class);
		return outObject;
	}
	
	public static void print(Object inObject) {
		 TolvenLogger.info(">>--Print Object-->>", BeanPropertyLoader.class);
		
		//    Object outObj = null;
		Class inputClass = null;
		Method inObjMethlist[] = null;
		Method inMeth = null;
		String inAttrName = null;
		String inMethName = null;
		Object inArglist[] = null;
		Object outArglist[] = null;
		// validate the input object and output class name
		if (inObject != null) {
			//			try
			//			{
			inputClass = inObject.getClass();
			inObjMethlist = inputClass.getDeclaredMethods();
			inArglist = new Object[0];
			for (int i = 0; i < inObjMethlist.length; i++) {
				inMeth = null;
				inMeth = inObjMethlist[i];
				inMethName = inMeth.getName();
				//check that it is a getter method
				if (inMethName.startsWith("get")) {
					//if getter method the get the attribute name.
					try {
						inAttrName = inMethName.substring(3, inMethName
								.length());
						TolvenLogger.info(inAttrName + "   : "
								+ inMeth.invoke(inObject, null), BeanPropertyLoader.class);
					} catch (Exception illAccEx) {
						//        illAccEx.printStackTrace();
					}
				} //end for //inner for loop ends <<-----------
			} //end if


		}
		 TolvenLogger.info("<<--print Object--<<", BeanPropertyLoader.class);
		
	}

	// ---------------------------------------------------------------------------/
}

// ---------------------------------------------------------------------------/

