package flash.marshalling;


import com.fasterxml.jackson.databind.ObjectMapper;
import flash.annotations.Param;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Marshaller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object[] unmarshall(JSONObject input, Method method) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Param paramAnnotation = param.getAnnotation(Param.class);

            if (paramAnnotation != null) {
                String paramName = paramAnnotation.name();
                Object value = null;
                if (input.has(paramName)) {
                    value = input.get(paramName);
                }

                if (value == null) {
                    args[i] = null;
                } else if (param.getType().isPrimitive() || param.getType() == String.class) {
                    args[i] = convertSimple(value, param.getType());
                } else {
                    args[i] = objectMapper.convertValue(value, param.getType());
                }
            } else if (param.getType().equals(JSONObject.class)) {
                args[i] = input;
            } else {
                throw new IllegalArgumentException("Parâmetro não anotado ou tipo não suportado: " + param.getName());
            }
        }

        return args;
    }


    private Object convertSimple(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value.toString());
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value.toString());
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value.toString());
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value.toString());
        if (targetType == String.class) return value.toString();
        return value;
    }

    public JSONObject marshall(Object result) {
        if (result == null) {
            return new JSONObject().put("result", JSONObject.NULL);
        }
        if (result instanceof JSONObject) {
            return (JSONObject) result;
        } else if (result instanceof String || result instanceof Number || result instanceof Boolean) {
            return new JSONObject().put("result", result);
        } else if (result instanceof Collection<?> collection) {
            List<Object> list = collection.stream()
                    .map(item -> objectMapper.convertValue(item, Map.class))
                    .collect(Collectors.toList());
            return new JSONObject().put("result", list);
        } else {
            Map<String, Object> map = objectMapper.convertValue(result, Map.class);
            return new JSONObject(map);
        }
    }
}