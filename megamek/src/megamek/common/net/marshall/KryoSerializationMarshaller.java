package megamek.common.net.marshall;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import megamek.common.Board;
import megamek.common.BoardDimensions;
import megamek.common.Building;
import megamek.common.Coords;
import megamek.common.GameTurn;
import megamek.common.Hex;
import megamek.common.IGame;
import megamek.common.InitiativeRoll;
import megamek.common.MapSettings;
import megamek.common.Mounted;
import megamek.common.PlanetaryConditions;
import megamek.common.Player;
import megamek.common.Terrain;
import megamek.common.UnitLocation;
import megamek.common.net.Packet;
import megamek.common.net.marshall.kryo.BoardDimensionsSerializer;
import megamek.common.net.marshall.kryo.EntityClassTurnFieldSerializer;
import megamek.common.net.marshall.kryo.PacketSerializer;
import megamek.common.net.marshall.kryo.PlayerFieldSerializer;
import megamek.common.net.marshall.kryo.TerrainSerializer;
import megamek.common.net.marshall.kryo.UnitLocationFieldSerializer;
import megamek.common.net.marshall.kryo.UnitNumberTurnFieldSerializer;
import megamek.common.options.GameOptions;
import megamek.common.options.Option;

/**
 * Marshaller/unmarshaller based on the {@link Kryo} serialization library
 */
public class KryoSerializationMarshaller extends PacketMarshaller {
    private final static ThreadLocal<Kryo> KRYOS = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            //kryo.setRegistrationRequired(true);
            /* IDs need to stay consistent, -2 and -1 are reserved, 0-8 used for primitive types */
            kryo.register(Packet.class, new PacketSerializer(), 9);
            kryo.register(Vector.class);
            kryo.register(ArrayList.class);
            kryo.register(HashMap.class);
            kryo.register(TreeSet.class);
            kryo.register(Player.class, new PlayerFieldSerializer(kryo));
            kryo.register(GameOptions.class);
            kryo.register(PlanetaryConditions.class);
            kryo.register(MapSettings.class);
            kryo.register(IGame.Phase.class);
            kryo.register(InitiativeRoll.class);
            kryo.register(Option.class);
            kryo.register(BoardDimensions.class, new BoardDimensionsSerializer());
            kryo.register(Coords.class);
            kryo.register(Board.class);
            kryo.register(Hex.class);
            kryo.register(Terrain.class, new TerrainSerializer());
            kryo.register(Building.class);
            kryo.register(Mounted.class);
            kryo.register(GameTurn.class);
            kryo.register(GameTurn.EntityClassTurn.class, new EntityClassTurnFieldSerializer(kryo));
            kryo.register(GameTurn.UnitNumberTurn.class, new UnitNumberTurnFieldSerializer(kryo));
            kryo.register(UnitLocation.class, new UnitLocationFieldSerializer(kryo));
            return kryo;
        };
    };

    
    /*
     * (non-Javadoc)
     * 
     * @see PacketMarshaller#marshall(megamek.common.net.Packet,
     *      java.io.OutputStream)
     */
    @Override public void marshall(Packet packet, OutputStream stream) throws Exception {
        try(Output output = new Output(stream)) {
            KRYOS.get().writeObject(output, packet);
            output.flush();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see PacketMarshaller#unmarshall(java.io.InputStream)
     */
    @Override public Packet unmarshall(InputStream stream) throws Exception {
        try(Input input = new Input(stream)) {
            return KRYOS.get().readObject(input, Packet.class);
        }
    }

}
