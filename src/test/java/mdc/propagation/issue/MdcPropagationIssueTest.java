package mdc.propagation.issue;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

@MicronautTest
class MdcPropagationIssueTest {

    @Inject
    EmbeddedServer server;

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testPostFails() {
        for (int i = 0; i < 32; i++) {
            var tempTrackingId = UUID.randomUUID().toString();
            var httpRequest = HttpRequest.POST("/test/1",
                            new User("userId", "firstName", "lastName"))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Api-Id", tempTrackingId);
            var tempResult = client.toBlocking().exchange(httpRequest,
                    Argument.of(User.class));
            assert tempTrackingId.equalsIgnoreCase(tempResult.header("X-Api-Id"));
            assert tempTrackingId.equalsIgnoreCase(tempResult.header("X-Api-Id-From-Controller"));
        }
    }

}
