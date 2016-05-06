package megamek.common.armor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.xalan.internal.utils.Objects;

public class Armor {
    private String id;
    private String name;
    
    private double kgPerPoint;
    
    // Old EquipmentType ID
    private Integer equipmentTypeId;
    
    private int techRating;
    
    /**
     * A map of capabilities, determining which types of units this armor can be mounted on
     */
    private Map<Class<? extends Capability>, Capability> capabilities;

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getKgPerPoint() {
        return kgPerPoint;
    }
    
    public int getTechRating() {
        return techRating;
    }
    
    public Integer getEquipmentTypeId() {
        return equipmentTypeId;
    }
    
    public Set<Capability> getCapabilities() {
        return new HashSet<>(capabilities.values());
    }
    
    public <T extends Capability> T getCapability(Class<T> cls) {
        if((null == capabilities) || !capabilities.containsKey(cls)) {
            return null;
        }
        final Capability cap = capabilities.get(cls);
        try {
            return cls.cast(cap);
        } catch(ClassCastException ccex) {
            return null;
        }
    }
    
    public boolean hasCapability(Class<? extends Capability> cls) {
        if((null == capabilities) || !capabilities.containsKey(cls)) {
            return false;
        }
        return cls.isInstance(capabilities.get(cls));
    }
    
    public void setCapability(Capability cap) {
        if(null == cap) {
            return;
        }
        if(null == capabilities) {
            capabilities = new HashMap<>();
        }
        capabilities.put(cap.getClass(), cap);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if((null == object) || (getClass() != object.getClass())) {
            return false;
        }
        final Armor other = (Armor) object;
        return Objects.equals(id, other.id);
    }
    
    /** Marker interface */
    public interface Capability {}
    
    public static class Aero implements Capability {
        private int slots;
        
        public Aero(int slots) {
            this.slots = slots;
        }
        
        public int getSlots() {
            return slots;
        }
    }
}
