package com.cashigo.expensio.dto.mapper;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class UUIDMapper {

    public UUID mapToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long msb = byteBuffer.getLong();
        long lsb = byteBuffer.getLong();
        return new UUID(msb, lsb);
    }

    public byte[] mapToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

}
