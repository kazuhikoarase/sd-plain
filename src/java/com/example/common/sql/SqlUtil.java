package com.example.common.sql;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SqlUtil
 * @author Kazuhiko Arase
 */
public class SqlUtil {

	private static final String SETTER_PREFIX = "set";
	private static final String GETTER_PREFIX = "get";

	/**
     * 右トリミング
     * @param s 文字列
     * @return トリミング後の文字列
     */
    public static String rtrim(String s) {
        if (s == null) return null;
        return s.replaceAll("[\\s\u3000\u0000]+$", "");
    }

	/**
     * 左右トリミング
     * @param s 文字列
     * @return トリミング後の文字列
     */
    public static String trim(String s) {
        if (s == null) return null;
        return s.replaceAll("^[\\s\u3000]+|[\\s\u3000]+$", "");
    }
	
    /**
	 * 文字列をスクリプトの文字列としてエンコードする。
	 * @param s 文字列
	 * @return エンコードされた文字列
	 */
	public static String encodeScriptString(String s) {
	
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			String c = s.substring(i, i + 1);
			if (c.matches("[a-zA-Z0-9_-]") ) {
				buf.append(c);
			} else {
				buf.append(String.format("\\u%04x", (int)c.charAt(0) ) );
			}
		}
		return buf.toString();
	}
	
	public static <T> T setTo(Map<String,Method> setters, ResultSet rs, T o) {
		try {
			for (Entry<String, Method> entry : setters.entrySet() ) {
				final String columnName = entry.getKey();
				final Method setter = entry.getValue();
				final Class<?> clazz = setter.getParameterTypes()[0];
				final String propName = getPropertyNameBySetter(setter);
				Object value = SqlType.get(clazz, propName).
					getValue(rs, columnName);
				setter.invoke(o, value);
			}
			return o;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String[] getCandidateNames(String name) {
		return new String[] {
			name, 
			name.toUpperCase(),
			name.toUpperCase().replaceAll("_", "") 
		};
	}

	public static Map<String, Method> getAllSetters(Class<?> c)  {
		Map<String, Method> map = new HashMap<String, Method>();
		for (Method method : c.getMethods() ) {
			if (method.getName().startsWith(SETTER_PREFIX) ) {
				map.put(method.getName().substring(
						SETTER_PREFIX.length() ), method);
			}
		}
		return map;
	}
	
	private static List<Method> getCandidateMethods(String columnName, Entry<String,Method> setter) {
		List<Method> methods = new ArrayList<Method>();
		String[] cList = getCandidateNames(columnName);
		String[] pList = getCandidateNames(setter.getKey() );
		for (int i = 0; i < cList.length; i++) {
			if (cList[i].equals(pList[i]) ) {
				methods.add(setter.getValue() );
			}
		}
		return methods;
	}
	/**
	 * 
	 * @param columnNames
	 * @param c
	 * @return 列名をキーとするメソッドのマップ
	 */
	public static Map<String,Method> getSetters(String[] columnNames, Class<?> c) {
		
		Map<String,Method> allSetters = getAllSetters(c);

		Map<String,Method> setters = new HashMap<String, Method>();

		for (String columnName : columnNames) {
			for (Entry<String,Method> setter : allSetters.entrySet() ) {
				List<Method> cands = getCandidateMethods(columnName, setter);
				
				if (cands.size() > 0) {

					Method cand = cands.get(0);
					
					if (setters.containsKey(columnName) ) {
						// already exists.
						throw new IllegalStateException(String.format(
								"ambiguous property name between %s and %s",
								setters.get(columnName).getName(),
								cand.getName() ) );
					}
					
					setters.put(columnName, cand);
				}
			}
		}

		// validate if all found.
		for (String columnName : columnNames) {
			if (!setters.containsKey(columnName) ) {
				throw new IllegalStateException(String.format(
						"destination property not found for column %s",
						columnName) );
			}
		}
		return setters;
	}
	
	public static String[] getColumnNames(ResultSet rs) {
		try {
			List<String> columnNames = new ArrayList<String>();
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 0; i < meta.getColumnCount(); i++) {
				columnNames.add(meta.getColumnName(i + 1) );
			}
			return columnNames.toArray(new String[columnNames.size()]);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getPropertyNameByGetter(Method method) {
		return lowerName(method.getName().
				substring(GETTER_PREFIX.length() ) );
	}
	
	public static String getPropertyNameBySetter(Method method) {
		return lowerName(method.getName().
				substring(SETTER_PREFIX.length() ) );
	}
	
	private static boolean isNormalMethod(Method m) {
		if (!Modifier.isPublic(m.getModifiers() ) ||
				!Modifier.isAbstract(m.getModifiers() ) ) {
			// public でない or abstract でない
			return false;
		}
		return true;
	}
	
	public static boolean isSetter(Method m) {
		if (!isNormalMethod(m) ) {
			return false;
		} else if (!m.getReturnType().equals(Void.TYPE) ||
				m.getParameterTypes().length != 1) {
			// 戻りが void でない or 引数が 1 でない
			return false;
		} else if (!m.getName().startsWith(SETTER_PREFIX) ) {
			// メソッド名が set で始まっていない
			return false;
		}
		return true;
	}

	public static boolean isGetter(Method m) {
		if (!isNormalMethod(m) ) {
			return false;
		} else if (m.getReturnType().equals(Void.TYPE) ||
				m.getParameterTypes().length != 0) {
			// 戻りが void or 引数が 0 でない
			return false;
		} else if (!m.getName().startsWith(GETTER_PREFIX) ) {
			// メソッド名が get で始まっていない
			return false;
		}
		return true;
	}
	
	private static String lowerName(String name) {
		return (name.length() > 0)?
			name.substring(0, 1).toLowerCase() + name.substring(1) :
			name;
	}
}