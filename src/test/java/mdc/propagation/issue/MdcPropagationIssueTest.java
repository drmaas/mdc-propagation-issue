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
    void testItWorks() {
        Assertions.assertTrue(server.isRunning());
    }

    @Test
    void testPost() {
        // given
        for (int i = 0; i < 32; i++) {
            var httpRequest = HttpRequest.POST("/test/1",
                            new User("userId", "firstName", "lastName"))
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Api-Id", UUID.randomUUID().toString());
            client.toBlocking().exchange(httpRequest,
                    Argument.of(User.class));
        }

        var xApiId = UUID.randomUUID().toString();
        var user = new User("userId", "firstName", "lastName");
        var httpRequest = HttpRequest.POST("/test/1", user)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Api-Id", xApiId);

        // when
        var result = client.toBlocking().exchange(httpRequest,
                Argument.of(User.class));

        // then
        assert result.status() == HttpStatus.CREATED;
        assert xApiId.equalsIgnoreCase(result.header("X-Api-Id"));
    }

}
