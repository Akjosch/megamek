package megamek.common.net.marshall.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import megamek.common.BoardDimensions;

public final class BoardDimensionsSerializer extends Serializer<BoardDimensions> {
    @Override
    public void write(Kryo kryo, Output output, BoardDimensions object) {
        output.writeInt(object.width());
        output.writeInt(object.height());
    }

    @Override public BoardDimensions read(Kryo kryo, Input input, Class<BoardDimensions> type) {
        int width = input.readInt();
        int height = input.readInt();
        return new BoardDimensions(width, height);
    }

}
