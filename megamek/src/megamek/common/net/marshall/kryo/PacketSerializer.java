package megamek.common.net.marshall.kryo;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import megamek.common.net.Packet;

public final class PacketSerializer extends Serializer<Packet> {
    @Override
    public void write(Kryo kryo, Output output, Packet object) {
        output.writeInt(object.getCommand());
        Object[] data = object.getData();
        if(null == data) {
            output.writeByte(-1);
        } else {
            output.writeByte(data.length);
            for(Object writeData : data) {
                kryo.writeClassAndObject(output, writeData);
            }
        }
    }

    @Override
    public Packet read(Kryo kryo, Input input, Class<Packet> type) {
        int available = -1;
        try {
            available = input.available();
        } catch(IOException e) {
        }
        int command = input.readInt();
        System.err.println("[COMMAND:"+command+"] " + available + " bytes");
        int length = input.readByte();
        if(length >= 0) {
            Object[] data = new Object[length];
            for(int i = 0; i < length; ++ i) {
                data[i] = kryo.readClassAndObject(input);
            }
            return new Packet(command, data);
        } else {
            return new Packet(command);
        }
    }

}
