package flash.registry;

import java.util.HashMap;
import java.util.Map;


public class ObjectRegistry {

    private final Map<String, Object> idToObject = new HashMap<>();
    private final Map<String, String> nameToId = new HashMap<>();

    public void register(String objectId, Object obj, String name) {
        idToObject.put(objectId, obj);
        nameToId.put(name, objectId);
    }

    public Object getById(String objectId) {
        return idToObject.get(objectId);
    }

    public Object getByName(String name) {
        String id = nameToId.get(name);
        return id == null ? null : getById(id);
    }

    public String getIdByName(String name) {
        return nameToId.get(name);
    }
}

