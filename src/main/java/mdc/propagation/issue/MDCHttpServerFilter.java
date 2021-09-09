package mdc.propagation.issue;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

@Filter("/**")
public class MDCHttpServerFilter extends OncePerRequestHttpServerFilter {

    private static final Logger log = LoggerFactory.getLogger(MDCHttpServerFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilterOnce(
            HttpRequest<?> request, ServerFilterChain chain) {

        // important attributes from headers
        var requestId =
                Optional.ofNullable(request.getHeaders().get("X-Api-Id"))
                        .orElse(UUID.randomUUID().toString());
        MDC.put("x-api-id", requestId);
        log.info("filter request");

        return Publishers.then(
                chain.proceed(request),
                (response) -> {
                    if (response != null) {
                        response.getHeaders().add("X-Api-Id", requestId);
                    }
                    log.info("filter response");
                    MDC.remove("x-api-id");
                });
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.TRACING.order();
    }

}
