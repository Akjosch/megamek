package megamek.common.armor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Armor rules implementation
 */
public final class Armors {
    private static final Map<String, Armor> ID_MAP = new HashMap<>();
    private static final Map<Integer, Set<Armor>> EQUIPMENT_TYPE_MAP = new HashMap<>();
    private static final Map<Class<? extends Armor.Capability>, Set<Armor>> CAPABILITY_MAP = new HashMap<>();
    
    public static Armor getById(String id) {
        return ID_MAP.get(id);
    }
    
    public static Set<Armor> getByEquipmentType(int type) {
        return EQUIPMENT_TYPE_MAP.get(type);
    }
    
    public static Set<Armor> getByCapability(Class<? extends Armor.Capability> cls) {
        return CAPABILITY_MAP.get(cls);
    }
    
    private static void addEquipmentTypeEntry(Integer equipmentTypeId, Armor armor) {
        Set<Armor> armors = EQUIPMENT_TYPE_MAP.get(equipmentTypeId);
        if(null == armors) {
            armors = new HashSet<>();
            EQUIPMENT_TYPE_MAP.put(equipmentTypeId, armors);
        }
        armors.add(armor);
    }
    
    private static void addCapabilityEntry(Class<? extends Armor.Capability> cls, Armor armor) {
        Set<Armor> armors = CAPABILITY_MAP.get(cls);
        if(null == armors) {
            armors = new HashSet<>();
            CAPABILITY_MAP.put(cls, armors);
        }
        armors.add(armor);
    }

    private static void addArmor(Armor armor) {
        if(null == armor) {
            return;
        }
        ID_MAP.put(armor.getId(), armor);
        Integer equipmentTypeId = armor.getEquipmentTypeId();
        if(null != equipmentTypeId) {
            addEquipmentTypeEntry(equipmentTypeId, armor);
        }
        Set<Armor.Capability> capabilities = armor.getCapabilities();
        if(null != capabilities) {
            for(Armor.Capability cap : capabilities) {
                addCapabilityEntry(cap.getClass(), armor);
            }
        }
    }
}
