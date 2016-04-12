package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import megamek.common.GameTurn;
import megamek.common.Player;

public class UnitNumberTurnFieldSerializer extends FieldSerializer<GameTurn.UnitNumberTurn> {
    public UnitNumberTurnFieldSerializer(Kryo kryo) {
        super(kryo, GameTurn.UnitNumberTurn.class);
    }
    
    @Override
    public GameTurn.UnitNumberTurn create(Kryo kryo, Input input, Class<GameTurn.UnitNumberTurn> type) {
        return new GameTurn.UnitNumberTurn(Player.PLAYER_NONE, (short) 0);
     }
}
