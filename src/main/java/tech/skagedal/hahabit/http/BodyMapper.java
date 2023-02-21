package tech.skagedal.hahabit.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class BodyMapper {
    private final ObjectMapper objectMapper;

    public BodyMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> HttpResponse.BodyHandler<T> receiving(Class<T> klass) {
        return new ResponseHandler<>(klass, objectMapper);
    }

    public <T> HttpRequest.BodyPublisher sending(T body) {
        try {
            return HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ResponseHandler<Type> implements HttpResponse.BodyHandler<Type> {
        private final Class<Type> klass;
        private final ObjectMapper mapper;

        public ResponseHandler(Class<Type> klass, ObjectMapper mapper) {
            this.klass = klass;
            this.mapper = mapper;
        }

        private Type deserializeBytes(byte[] bytes) {
            try {
                return mapper.readValue(bytes, klass);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public HttpResponse.BodySubscriber<Type> apply(HttpResponse.ResponseInfo responseInfo) {
            return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofByteArray(),
                this::deserializeBytes
            );
        }
    }
}
