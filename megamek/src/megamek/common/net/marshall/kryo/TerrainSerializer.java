package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import megamek.common.Terrain;

public class TerrainSerializer extends Serializer<Terrain> {
    @Override
    public void write(Kryo kryo, Output output, Terrain object) {
        output.writeString(object.toString());
    }

    @Override
    public Terrain read(Kryo kryo, Input input, Class<Terrain> type) {
        return new Terrain(input.readString());
    }
}
