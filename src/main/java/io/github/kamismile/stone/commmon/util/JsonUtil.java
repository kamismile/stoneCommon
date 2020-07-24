package io.github.kamismile.stone.commmon.util;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import net.sf.json.xml.XMLSerializer;

/**
 * Created by IntelliJ IDEA.
 * User: lidong
 * Date: 12-3-8
 * Time: 下午12:54
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtil {
    /**
     * 获取对象的string字符
     *
     * @param obj 需要转换的对象
     * @return 对象的string字符
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * 获取string字符的对象.
     * {@code new TypeToken<List<Map<String,String>>>(){}.getType()}
     *
     * @param str  需要转换的字符串
     * @param type 需要转换的对象类型
     * @param <T> 泛型
     * @return 对象
     */
    public static <T> T  fromJson(String str, Type type) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new ObjectTypeAdapter()).create();


        return gson.fromJson(str, type);
    }

     public static String xml2JSON(String xml){
        return new XMLSerializer().read(xml).toString();
    }

    static class ObjectTypeAdapter extends TypeAdapter<Object> {
        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();
                case NUMBER:
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     */
                    double dbNum = in.nextDouble();
                    // 数字超过long的最大值，返回浮点类型
                    if (dbNum > Long.MAX_VALUE) {
                        return dbNum;
                    }

                    // 判断数字是否为整数值
                    long lngNum = (long) dbNum;
                    if (dbNum == lngNum) {
                        return lngNum;
                    } else {
                        return dbNum;
                    }

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
        }
    }

    public static  Map<String,Object> json2Map(String str){
        return (Map<String, Object>) fromJson(ValueUtils.isStringNull(str),new TypeToken<Map<String,Object>>(){}.getType());
    }

    public static  Map<String,String> jsonToMapString(String str){
        return (Map<String, String>) fromJson(ValueUtils.isStringNull(str),new TypeToken<Map<String,String>>(){}.getType());
    }

    /**
     * 获取string字符的对象
     *
     * @param str  需要转换的字符串
     * @param clazz 需要转换的对象类型
     * @param <E> 泛型
     * @return 对象
     */
    public static <E> E fromJson(String str, Class<E> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(str, clazz);
    }

    /**
     * 将标准形式的JSON字符串转换为Map对象
     *
     * @param str 字符串
     * @return 转换后的Map对象
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String str) {
        if (StringUtils.isEmpty(str))
            return new HashMap<String, Object>(0);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(str);
        if (!jsonElement.isJsonObject()) {
            System.out.println("can't parse '" + str + "' to map ");
            return new HashMap<String, Object>(0);
        }

        return (Map<String, Object>) getValue(jsonElement);
    }

    /**
     * 递归查询
     *
     * @param jsonElement 元素对象
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
	private static Object getValue(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = object.entrySet();
            Object[] obja = set.toArray();
            Map<String, Object> map = new HashMap<String, Object>();
            for (Object objcc : obja) {
                Map.Entry<String, JsonElement> mapTemp = (Map.Entry<String, JsonElement>) objcc;
                map.put(mapTemp.getKey(), getValue(mapTemp.getValue()));
            }
            return map;

        } else if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            int size = array.size();
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < size; i++) {
                list.add(getValue(array.get(i)));
            }
            return list;
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isString() && ValueUtils.isDateNull(primitive.getAsString()) == null) {
                return primitive.getAsString();
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
            if (primitive.isNumber()) {
                return primitive.getAsNumber();
            }
            if(ValueUtils.isDateNull(primitive.getAsString()) != null){
                return ValueUtils.isDateNull(primitive.getAsString());
            }

            return primitive.getAsString();
        }
        return null;
    }

    public static void main(String[] args) {
        String json="{\"appId\":\"1\",\"json\":{\"a\":\"adf\"},\"verify\":\"83FAB91F2A3F5419558842723AB78576\",\"cmd\":\"user@goods@time\",\"account\":10,\"token\":\"0160708b-1e15-4e2b-88aa-93b6026efe01\"}";
        System.out.println(JsonUtil.json2Map(json));

    }
}
