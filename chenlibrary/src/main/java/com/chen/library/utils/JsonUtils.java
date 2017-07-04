package com.chen.library.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MP2 on 2015/12/10.
 */
public class JsonUtils {
    public static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder().addSerializationExclusionStrategy(new IgnoreStrategy());
//        builder.registerTypeAdapter(Integer.class, new BooleanTypeAdapter());
        gson = builder.create();
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (!TextUtils.isEmpty(json)) {
            try {
                return gson.fromJson(json, classOfT);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static List fromJsonToList(String json, Type type) {
        List list = null;
        if (!TextUtils.isEmpty(json)) {
            list = gson.fromJson(json, type);
        }
        return list == null ? new ArrayList() : list;
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> classOfT) {
        return fromJsonToList(json, new ListOfSomething<T>(classOfT));
    }

    private static class ListOfSomething<X> implements ParameterizedType {
        private Class<?> wrapped;

        public ListOfSomething(Class<X> wrapped) {
            this.wrapped = wrapped;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        public Type getRawType() {
            return List.class;
        }

        public Type getOwnerType() {
            return null;
        }
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    static class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int code = json.getAsInt();
            return code != 0;
        }
    }
}
