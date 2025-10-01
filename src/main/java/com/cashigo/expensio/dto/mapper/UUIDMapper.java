package com.cashigo.expensio.dto.mapper;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class UUIDMapper implements Mapper<byte[], UUID> {
    @Override
    public UUID map(byte[] entity) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(entity);
        long msb = byteBuffer.getLong();
        long lsb = byteBuffer.getLong();
        return new UUID(msb, lsb);
    }
}
