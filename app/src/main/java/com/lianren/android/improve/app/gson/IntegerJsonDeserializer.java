package com.lianren.android.improve.app.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class IntegerJsonDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return json.getAsInt();
        } catch (Exception e) {
            return 0;
        }
    }
}
