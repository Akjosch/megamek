package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import megamek.common.UnitLocation;

public class UnitLocationFieldSerializer extends FieldSerializer<UnitLocation> {
    public UnitLocationFieldSerializer(Kryo kryo) {
        super(kryo, UnitLocation.class);
    }

    @Override
    protected UnitLocation create(Kryo kryo, Input input, Class<UnitLocation> type) {
        return new UnitLocation(0, null, 0, 0);
    }
}
