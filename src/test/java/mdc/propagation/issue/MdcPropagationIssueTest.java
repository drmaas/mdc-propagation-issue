package mdc.propagation.issue;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MicronautTest
class MdcPropagationIssueTest {

    @Inject
    EmbeddedServer server;

    @Inject
    @Client("/")
    HttpClient client;

    private Map<String, Integer> trackingIdIndexes = new HashMap<>();

    @Test
    void testPostFails() {
        for (int i = 0; i < 32; i++) {
            var tempTrackingId = UUID.randomUUID().toString();
            trackingIdIndexes.put(tempTrackingId, i);
            var httpRequest = HttpRequest.POST("/test/1",
                            new User("userId", "firstName", "lastName"))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Api-Id", tempTrackingId);
            var tempResult = client.toBlocking().exchange(httpRequest,
                    Argument.of(User.class));
            Assertions.assertEquals(tempTrackingId, tempResult.header("X-Api-Id-From-Filter"),
                    "Iteration=" + i + ", tracking id in filter mistakenly reused from previous request on iteration=" + trackingIdIndexes.get(tempResult.header("X-Api-Id-From-Controller")));
            Assertions.assertEquals(tempTrackingId, tempResult.header("X-Api-Id-From-Controller"),
                    "Iteration=" + i + ", tracking id in controller mistakenly reused from previous request on iteration=" + trackingIdIndexes.get(tempResult.header("X-Api-Id-From-Controller")));
        }
    }

}
