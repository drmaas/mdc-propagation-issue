package mdc.propagation.issue;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;

@Controller("/test")
@Validated
public class TestLoggingController {

    private static final Logger log = LoggerFactory.getLogger(TestLoggingController.class);

    private final HttpClient httpClient;
    private final ApplicationContext applicationContext;

    @Inject
    public TestLoggingController(HttpClient httpClient, ApplicationContext applicationContext) {
        this.httpClient = httpClient;
        this.applicationContext = applicationContext;
    }

    @Post("1")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> post1(@Body User user) {
        log.info("Controller request");
        var url = applicationContext.getBean(EmbeddedServer.class).getURL();
        var request = HttpRequest.POST(url + "/test/2", user)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Api-Id", MDC.get("x-api-id"));
        var result = httpClient.toBlocking().exchange(request, Argument.of(User.class));
        log.info("Controller response");
        return HttpResponse.created(result.body()).header("X-Api-Id-From-Controller", MDC.get("x-api-id"));
    }

    @Post("2")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> post2(@Body User user) {
        return HttpResponse.created(user);
    }
}

