/*
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.common.net.marshall;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;

import megamek.common.net.Packet;

/**
 * Generic marshaller that [un]marshalls the {@link Packet}
 */
public abstract class PacketMarshaller {

    /** Java native serialization marshalling */
    public static final int NATIVE_SERIALIZATION_MARSHALING = 0;
    /** {@link Kryo} serialization */
    public static final int KRYO_SERIALIZATION_MARSHALING = 1;

    /**
     * Marshalls the packet data into the <code>byte[]</code>
     *
     * @param packet packet to marshall
     * @return marshalled representation of the given {@link Packet}
     */
    public byte[] marshall(Packet packet) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try {
            marshall(packet, bo);
            bo.flush();
            return bo.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Marshalls the packet data into the given {@link OutputStream}
     *
     * @param packet {@link Packet} to marshall
     * @param stream {@link OutputStream} to marshall the {@link Packet} to
     * @throws Exception
     */
    public abstract void marshall(Packet packet, OutputStream stream)
            throws Exception;

    /**
     * Unmarshalls the packet data from the given <code>byte[]</code> array
     *
     * @param data <code>byte[]</code> array to unmarshall the packet from
     * @return the new {@link Packet} unmarshalled from the given <code>byte[]</code> array
     */
    public Packet unmarshall(byte[] data) {
        try {
            return unmarshall(new ByteArrayInputStream(data));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Unmarshalls the packet data from the given {@link InputStream}
     *
     * @param stream {@link InputStream} to unmarshall the packet from
     * @return the new {@link Packet} unmarshalled from the given {@link InputStream}
     * @throws Exception
     */
    public abstract Packet unmarshall(InputStream stream) throws Exception;

}
