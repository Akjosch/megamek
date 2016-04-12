package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import megamek.common.GameTurn;
import megamek.common.Player;

public class EntityClassTurnFieldSerializer extends FieldSerializer<GameTurn.EntityClassTurn> {
    public EntityClassTurnFieldSerializer(Kryo kryo) {
        super(kryo, GameTurn.EntityClassTurn.class);
    }
    
    @Override
    public GameTurn.EntityClassTurn create(Kryo kryo, Input input, Class<GameTurn.EntityClassTurn> type) {
        return new GameTurn.EntityClassTurn(Player.PLAYER_NONE, 0);
     }
}
