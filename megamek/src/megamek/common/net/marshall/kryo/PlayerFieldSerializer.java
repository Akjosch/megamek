package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import megamek.common.Player;

public final class PlayerFieldSerializer extends FieldSerializer<Player> {
    public PlayerFieldSerializer(Kryo kryo) {
        super(kryo, Player.class);
    }

    @Override
    public Player create(Kryo kryo, Input input, Class<Player> type) {
        return new Player(0, "unnamed"); //$NON-NLS-1$
     }
}