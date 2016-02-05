package com.player.util;

import java.awt.event.ItemEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.sun.istack.internal.Nullable;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.axes.ChildIterator;

import java.lang.reflect.Array;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TDataUtil {
	/** 转为json的最大递归深度 */
	static final private int MaxRecursionDepth = 20;
	
	private static boolean isBasicType(Class<?> cls) {
		if(cls == null){
			
		} else if(cls.isAssignableFrom(Integer.class)) {
			
		} else if(cls.isAssignableFrom(Byte.class)) {
			
		} else if(cls.isAssignableFrom(Short.class)) {
			
		} else if(cls.isAssignableFrom(Float.class)) {
			
		} else if(cls.isAssignableFrom(Double.class)) {
			
		} else if(cls.isAssignableFrom(Long.class)) {
			
		} else if(cls.isAssignableFrom(Boolean.class)) {
			
		} else if(cls.isAssignableFrom(String.class)) {
			
		} else {//not basic model
			return false;
		}
		return true;
	}
	
	private static boolean isBasicType(Object target) {
		if(target == null) {
			return true;
		}
		Class<?> cls = target.getClass();
		return isBasicType(cls);
	}
	
	private static boolean isArray(Object target) {
		if(target == null) {
			return false;
		}
		Class<?> cls = target.getClass();
		if(cls == null) {
			return false;
		}
		if(cls.isArray()){
			return true;
		} else {
			return false;
		}
	}
	private static boolean isCollection(Object target) {
		if(target == null) {
			return false;
		}
		Class<?> cls = target.getClass();
		if(cls == null) {
			return false;
		}
		if(Collection.class.isAssignableFrom(cls)){
			return true;
		} else {
			return false;
		}
	}
	private static @Nullable JSONObject parseObject(Object target,int curDepth) 
			throws IllegalArgumentException {
		if(curDepth >= MaxRecursionDepth) {
			throw new IllegalArgumentException("Convertion depth out of expectation.");
		}
		if(target == null) {
			return null;
		}
		
		JSONObject json = new JSONObject();
		Class<?> cls = target.getClass();
		Field[] fieldList = cls.getDeclaredFields();
		for(int i=0;i<fieldList.length;i++) {
			if((fieldList[i].getModifiers()&Modifier.STATIC) == Modifier.STATIC) {
				//跳过非公共或静态成员
				continue;
			}
			try {
				Object memTarget = fieldList[i].get(target);
				Object child = null;
				if(isArray(memTarget) || isCollection(memTarget)) {
					child = parseArray(memTarget, curDepth+1);
				} else if(isBasicType(memTarget)){
					child = memTarget;
				}else {
					child = parseObject(memTarget, curDepth+1);
				}
				if(child != null) {
					json.put(fieldList[i].getName(), child);
				}
			} catch (IllegalAccessException e) {
				TWebLogUtil.d(e);
			}
		}
		return json;
	}
	private static int getArrayLength(Object target) {
		if(isArray(target)) {
			return Array.getLength(target);
		} else if(isCollection(target)) {
			return ((Collection)target).size();
		} else {
			return 0;
		}
	}
	private static Object getArrayItem(Object target, int index) {
		if(isArray(target)) {
			return Array.get(target, index);
		} else if(isCollection(target)) {
			Object[] ary = ((Collection)target).toArray();
			return ary[index];
		} else {
			return null;
		}
	}
	private static @Nullable JSONArray parseArray(Object target, int curDepth) 
			throws IllegalArgumentException{
		JSONArray json = new JSONArray();
		int length = getArrayLength(target);
		for(int i=0;i<length;i++) {
			Object memTarget = getArrayItem(target, i);
			Object item = null;
			if(isArray(memTarget) || isCollection(memTarget)) {
				item = parseArray(memTarget, curDepth+1);
			} else if(isBasicType(memTarget)){
				item = memTarget;
			}else {
				item = parseObject(memTarget, curDepth+1);
			}
			if(item != null) {
				json.add(item);
			}
		}
		if(json.size() != 0) {
			return json;
		} else {
			return null;
		}
	}
	
	/**
	 * 将一个JAVA对象转为JSON
	 * NOTE:转换的内容仅限于非静态公共成员变量，key与成员变量名一致
	 * WARN:target及其成员不得含有相互引用的结构，或者成员深度大于{@see MaxRecursionDepth}
	 * @param target
	 * @return JSON字符串
	 */
	public static String serialize(Object target) {
		try{
			if(isArray(target) || isCollection(target)) {
				JSONArray ary = parseArray(target, 0);
				if(ary != null) {
					return ary.toString();
				} 
			} else if(isBasicType(target)){
				return target.toString();
			} else {
				JSONObject obj = parseObject(target, 0);
				if(obj != null) {
					return obj.toString();
				}
			}
		}catch(IllegalArgumentException e) {
			TWebLogUtil.d(e);
		}
		return "";
	}
	
	private static Object createObject(JSONObject jsonObject, Class<?> cls) {
		if(isBasicType(cls)) {
			return createBasicTypeFromString(jsonObject.toString(), cls);
		}
		Object res = null;
		try {
			Constructor<?> constructor = cls.getConstructor();
			res = constructor.newInstance();
		} catch (Exception e) {
			TWebLogUtil.d(e);
		}
		if(res == null) {
			return null;
		}
		
		Field[] fields = cls.getDeclaredFields();
		for(int i=0;i<fields.length;i++) {
			Field field = fields[i];
			Object child = jsonObject.get(field.getName());
			if(child != null) {
				Object value = null;
				if(child instanceof JSONObject) {
					value = createObject((JSONObject)child, field.getType());
				} else if(child instanceof JSONArray) {
					value = createArray((JSONArray)child, field.getGenericType());
				} else {
					value = createBasicTypeFromString(child.toString(), child.getClass());
				}
				if(value != null) {
					field.setAccessible(true);
					try {
						field.set(res, value);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						TWebLogUtil.d(e);
					}
				}
			}
		}
		return res;	
	}
	private static Object createArray(JSONArray jsonArray, Type type) {
		if(jsonArray == null || jsonArray.size() == 0) {
			return null;
		}
		Object res = null;
		Type componentType = null;
		boolean isBasicArray = false;
		if(type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType)type;
			componentType = ptype.getActualTypeArguments()[0];
			try {
				Class<?> cls = (Class<?>)ptype.getRawType();
				Constructor<?> constructor = cls.getConstructor();
				res = constructor.newInstance();
			} catch(Exception e) {
				TWebLogUtil.d(e);
			}
		} else if(type instanceof Class<?>){
			Class<?> cls = (Class<?>)type;
			if(cls.isArray()) {
				isBasicArray = true;
				componentType = cls.getComponentType();
				if(componentType != null && componentType instanceof Class<?>) {
					res = Array.newInstance((Class<?>)componentType, jsonArray.size());
				}
			}
		}
		if(componentType != null && res != null) {
			for(int i=0;i<jsonArray.size();i++) {
				Object object = jsonArray.get(i);
				Object item = null;
				if(object instanceof JSONObject && componentType instanceof Class<?>) {
					item = createObject((JSONObject)object, (Class<?>)componentType);
				} else if(object instanceof JSONArray) {
					item = createArray((JSONArray)object, componentType);
				} else {
					item = createBasicTypeFromString(object.toString(), object.getClass());
				}
				if(item != null) {
					if(isBasicArray) {
						Array.set(res, i, item);
					} else if(res instanceof Collection) {
						try{
							((Collection)res).add(item);
						}catch(Exception e) {
							TWebLogUtil.d(e);
						}
					}
				}
			}
			return res;
		} else{
			//Illegal arguments
			return null;
		}
	}
	
	static private Object createBasicTypeFromString(String strVal, Class<?> cls) {
		Object res = null;
		try{
			if(cls == null){
				
			} else if(cls.isAssignableFrom(Object.class)){

			} else if(cls.isAssignableFrom(Integer.class)) {
				res = Integer.parseInt(strVal);
			} else if(cls.isAssignableFrom(Byte.class)) {
				res = Byte.parseByte(strVal);
			} else if(cls.isAssignableFrom(Short.class)) {
				res = Short.parseShort(strVal);
			} else if(cls.isAssignableFrom(Float.class)) {
				res = Float.parseFloat(strVal);
			} else if(cls.isAssignableFrom(Double.class)) {
				res = Double.parseDouble(strVal);
			} else if(cls.isAssignableFrom(Long.class)) {
				res = Long.parseLong(strVal);
			} else if(cls.isAssignableFrom(Boolean.class)) {
				res = Boolean.parseBoolean(strVal);
			} else if(cls.isAssignableFrom(String.class)) {
				res = strVal;
			} else {
				//not basic model
			}
		}catch(NumberFormatException e){
			TWebLogUtil.d(e);
		}
		return res;
	}
	
	public static Object deserialize(String json, Class<?> cls) {
		if(json == null || json.isEmpty() || cls == null) {
			return null;
		}
		JSONObject jsonObj = null;
		try{
			jsonObj = JSONObject.fromObject(json);
		}catch(Exception e){
			TWebLogUtil.d(e);
		}
		if(jsonObj instanceof JSONObject) {
			return createObject((JSONObject)jsonObj, cls);
		} else {
			return null;
		}
	}
	
}
