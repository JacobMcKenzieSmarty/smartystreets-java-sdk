package com.smartystreets.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;

import java.io.*;

public class SmartySerializer implements Serializer {

    public SmartySerializer() {}

    public byte[] serialize(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] payload, Class<T> type, Headers headers) throws IOException {
        return deserializeInternal(payload, type);
    }

    public <T> T deserialize(byte[] payload, Class<T> type) throws IOException {
        return deserializeInternal(payload, type);
    }

    private <T> T deserializeInternal(byte[] payload, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(payload, type);
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            System.err.println("Deserialization error: " + e.getOriginalMessage());
            System.err.println("At: " + e.getPathReference());

            // Print the full path through nested objects
            for (com.fasterxml.jackson.databind.JsonMappingException.Reference ref : e.getPath()) {
                System.err.println(" -> field: " + ref.getFieldName() + " (index: " + ref.getIndex() + ")");
            }

            throw e; // rethrow so caller still sees failure
        }
    }
}