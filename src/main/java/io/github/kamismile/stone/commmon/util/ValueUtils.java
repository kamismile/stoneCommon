package io.github.kamismile.stone.commmon.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用的对值处理的方法集合
 */
public class ValueUtils {

	/**
	 * 验证是否值为空
	 *
	 * @param obj  值
	 * @return true 不为空 false 值为空
	 */
	public static boolean isNotNull(Object obj) {
		return obj != null && StringUtils.isNotEmpty(obj.toString().trim());
	}

	public static boolean isNull(Object obj) {
		return !isNotNull(obj);
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为long型
	 *
	 * @param o 值
	 * @return 返回空或转换后的值
	 */
	public static Long isLongNull(Object o) {
		if (o != null && !"".equals(o.toString())) {
			return Long.valueOf(o.toString());
		}
		return null;
	}

	public static Float isFloatNull(Object o) {
		if (o != null && !"".equals(o.toString())) {
			return Float.valueOf(o.toString());
		}
		return null;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为long型
	 *
	 * @param o  值
	 * @param value 如果为空返回指定值
	 * @return 返回空或转换后的值
	 */
	public static Long isLongNull(Object o, Long value) {
		Long obj = isLongNull(o);
		if (obj != null) {
			return obj;
		}
		return value;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为long型
	 *
	 * @param o  值
	 * @return 返回空或转换后的值
	 */
	public static Integer isIntegerNull(Object o) {
		if (o != null && !"".equals(o.toString())) {
			return Integer.valueOf(o.toString());
		}
		return null;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为long型
	 *
	 * @param o 值
	 * @param value 如果为空返回指定值
	 * @return 返回空或转换后的值
	 */
	public static Integer isIntegerNull(Object o, Integer value) {
		Integer obj = isIntegerNull(o);
		if (obj != null) {
			return obj;
		}
		return value;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为long型
	 *
	 * @param o 值
	 * @return 当为空、非boolean类型、o值为false 时返回值为false 否则为true
	 */
	public static boolean isBooleanNull(Object o) {
		return o != null && !"".equals(o.toString()) && Boolean.valueOf(o.toString());

	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为Double型
	 *
	 * @param o  值
	 * @return 返回空或转换后的值
	 */
	public static Double isDoubleNull(Object o) {
		if (o != null && !"".equals(o.toString())) {
			return Double.valueOf(o.toString());
		}
		return null;
	}


	/**
	 * 是否为空如果不等于空并且是数值型是转换为Double型
	 *
	 * @param o 值
	 * @param value  默认值
	 * @return 返回空或转换后的值
	 */
	public static Double isDoubleNull(Object o, Double value) {

		Double obj = isDoubleNull(isStringNull(o));
		if (obj != null) {
			return obj;
		}
		return value;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为String型
	 *
	 * @param o   值
	 * @return 返回空或转换后的值
	 */
	public static String isStringNull(Object o) {
		if (o != null && !"".equals(o.toString())) {
			return o.toString();
		}
		return null;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为String型
	 *
	 * @param o   值
	 * @param value  默认值
	 * @return 返回空或转换后的值
	 */
	public static String isStringNull(Object o, String value) {
		String obj = isStringNull(o);

		if (obj != null) {
			return obj;
		}
		return value;
	}

	/**
	 * 如果对象为空时返回空值,否则返回对象
	 *
	 * @param o  值
	 * @return 返回空或转换后的值
	 */
	public static Object objectIsNullToBlank(Object o) {
		return isNotNull(o) ? o : "";
	}

	/**
	 * 把null值转换为空值
	 *
	 * @param map  需要转换的map
	 */
	public static void nullToEmpty(Map<String, Object> map) {
		Object[] basicInfoa = map.keySet().toArray();
		Object value;
		for (Object c : basicInfoa) {
			value = map.get(c.toString());
			map.put(c.toString(), value == null ? "" : map.get(c.toString()).toString());
		}
	}

	/**
	 * 从map中去掉NULL值
	 *
	 * @param map 需要转换的map
	 */
	public static void removeNull(Map<String, String> map) {
		Object[] basicInfoa = map.keySet().toArray();
		Object value;
		for (Object c : basicInfoa) {
			value = map.get(c.toString());
			if (isNull(value)) {
				map.remove(c.toString());
			}
		}
	}

	/**
	 * 将字符串分隔字符串数组
	 *
	 * @param string  字符串
	 * @param sp 分隔符 默认为 逗号
	 * @return 返回分隔后的数组
	 */
	public static String[] stringSplit(String string, String sp) {
		return (string + (isNull(sp) ? "," : sp)).split(isNull(sp) ? "," : sp);
	}

	/**
	 * 将字符串分隔字符串数组
	 *
	 * @param string 字符串
	 * @param sp 分隔符 默认为 逗号
	 * @return 返回分隔后的数组
	 * @throws Exception
	 *             字符有非数据的
	 */
	public static Long[] stringSplitToLong(String string, String sp) throws Exception {
		if (!isNotNull(string)) {
			return new Long[] {};
		}
		String[] str = (string + ",").split(sp == null ? "," : sp);
		Long[] list = new Long[str.length];
		int i = 0;
		for (String s : str) {
			if (isLongNull(s) == null) {
				throw new Exception();
			}
			list[i++] = Long.valueOf(s);
		}
		return list;
	}



	public static Date getDateDef(Date o,Date b) {
		if(isNull(o)){
			return  b;
		}
		return  o;
	}

	/**
	 * 是否为空如果不等于空并且是数值型是转换为Date型
	 *
	 * @param o   值
	 * @return 返回空或转换后的值
	 */
	public static Date isDateNull(Object o) {
		return isDateNull(o, DateUtil.TIMESTAMP_DF);
	}

	public static Date isDateNull(Object o, String sdf) {
		if (o != null && !"".equals(o.toString())) {
			Object obj = DateUtil.parseDate(o.toString(), sdf);
			return (Date) obj;
		}
		return null;
	}

	// GENERAL_PUNCTUATION 判断中文的“号
	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static final boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean isChineseCharacter(String chineseStr) {
		char[] charArray = chineseStr.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean isChineseCharacter_f2() {
		String str = "！？";
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fbb]+")) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, String> getParameters(String parameter) {
		Map<String, String> map = new HashMap<String, String>();
		if (isNull(parameter)) {
			return map;
		}

		if (!parameter.contains("&")) {
			String[] pars = StringUtils.split(parameter, "=");
			map.put(pars[0], (pars.length == 1) ? "" : pars[1]);
			return map;
		}

		String[] parameters = StringUtils.split(parameter, "&");
		for (String param : parameters) {
			String[] pars = StringUtils.split(param, "=");
			map.put(pars[0], (pars.length == 1) ? "" : pars[1]);
		}
		return map;
	}



	public static Double divide(Object v1, Object v2) {
		if (v1 == null || v2 == null) {
			return 0.0;
		}
		return divide(v1, v2, 3);
	}

	public static Double divide(Object v1, Object v2, int scale) {
		String number1 = ValueUtils.isStringNull(v1);
		number1 = getString(number1);

		String number2 = ValueUtils.isStringNull(v2);
		number2 = getString(number2);
		if (!NumberUtils.isNumber(number1) || !NumberUtils.isNumber(number2)
				|| NumberUtils.createDouble(number1).doubleValue() == NumberUtils.DOUBLE_ZERO
				|| NumberUtils.createDouble(number2).doubleValue() == NumberUtils.DOUBLE_ZERO) {
			return NumberUtils.DOUBLE_ZERO;
		}
		BigDecimal b1 = new BigDecimal(number1);
		BigDecimal b2 = new BigDecimal(number2);
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}


	public static double toRate(double d, int scale) {
		BigDecimal num = new BigDecimal(Double.toString(d));
		num = num.multiply(new BigDecimal("100"));
		num = num.setScale(scale, RoundingMode.DOWN);
		return num.doubleValue();
	}

	public static Double rounding(Object v1, int scale) {
		String dou = ValueUtils.isStringNull(v1);
		dou = getString(dou);

		if (!NumberUtils.isNumber(dou)) {
			return NumberUtils.DOUBLE_ZERO;
		}
		BigDecimal b1 = new BigDecimal(dou);
		return b1.setScale(scale, RoundingMode.DOWN).doubleValue();
	}

	private static String getString(String dou) {
		if (dou.indexOf(".") == 0) {
			dou = "0" + dou;
		}
		return dou;
	}

	public static Double rounding(Object v1) {
		return rounding(v1, 2);
	}

	public static Object roundingByDef(Object v1, int scale) {
		String dou = ValueUtils.isStringNull(v1, "");
		dou = getString(dou);
		if (!NumberUtils.isNumber(dou)) {
			return dou;
		}
		return rounding(v1, scale);
	}

	public static Object roundingByDef(Object v1) {
		return roundingByDef(v1, 2);
	}

	public static Object getInt(Object v1) {
		if (!NumberUtils.isNumber(ValueUtils.isStringNull(v1))) {
			return v1;
		}
		return ValueUtils.isFloatNull(v1).intValue();
	}


	public static long getLong(String str) {
		if (str == null) {
			return 0L;
		}
		return Long.parseLong(str);
	}


	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return rounding(b1.add(b2).doubleValue(), 3);
	}


	public static String stringIsNullToBlack(String val) {
		if (val == null) {
			val = "";
		}
		return val;
	}

	public static <T> T getMapValue(Map<String, Object> map, String key, Class<T> clazz) {
		Object obj = map.get(key);
		Object result = null;
		String className = clazz.getSimpleName();

		if ("String".equals(className)) {
			if (null == obj)
				result = "";
			else
				result = obj.toString();
		} else if ("Timestamp".equals(className)) {

		} else {
			// 数字
			BigDecimal b = null;
			if (null == obj) {
				b = new BigDecimal(0);
			} else {
				b = (BigDecimal) obj;
			}
			// 转
			if ("Integer".equals(className)) {
				result = b.intValue();
			} else if ("Float".equals(className)) {
				result = b.floatValue();
			} else if ("Double".equals(className)) {
				result = b.doubleValue();
			} else if ("Long".equals(className)) {
				result = b.longValue();
			}
		}
		return (T) result;
	}

	public static String getStringfromHex(String hex) {
		String hexArr[] = hex.split("\\\\");
		byte[] byValue=null;
		try{
			ArrayList<Byte> byteArr=new ArrayList<Byte>();
			for (int i = 0; i < hexArr.length; i++) {
				String str = hexArr[i];
				if(str.toLowerCase().startsWith("x")){
					String hexValue = str.substring(0, 3);
					str  = str.substring(3);
					Integer hexInt = Integer.decode("0" + hexValue);
					byteArr.add(hexInt.byteValue());
				}
				byte[] strbytes=org.apache.commons.codec.binary.StringUtils.getBytesUtf8(str);
				for(byte strb :strbytes){
					byteArr.add(strb);
				}
			}
			byValue=ArrayUtils.toPrimitive(byteArr.toArray(new Byte[0]));
		}catch (Exception e){
			byValue=new byte[0];
		}
		return  org.apache.commons.codec.binary.StringUtils.newStringUtf8(byValue);
	}


	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}


	public static String convertToFileSize(long size) {
        long kb = 1 << 10, mb = 1 << 20, gb = 1 << 30;
        String s = null;
        if (size >= gb) {
            s = String.format("%dGB", Math.round(size / gb));
        } else if (size >= mb) {
            s = String.format("%dMB", Math.round(size / mb));
        } else if (size >= kb) {
            s = String.format("%dKB", Math.round(size / kb));
        } else
            s = String.format("%dB", size);
        return s;
    }


	public static BigDecimal toBigDecimal(Object param, int scale) {
		if (ValueUtils.isNotNull(param)) {
			return new BigDecimal(param.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else {
			return null;
		}
	}
}
