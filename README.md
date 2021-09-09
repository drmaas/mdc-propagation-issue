## Overview
This app adds `micronaut-tracing` and a server filter to propagate and MDC value, `x-api-id`, across all logs for a request.
The problem is that when a thread is used for a subsequent request, it holds on to the MDC context from the previous request.
This can be seen in the logs below, where the old value is marked `STALE`.

## Running
Run `MdcPropagationIssueTest` to send 32+ requests from client to server. You will be able to see stale MDC values
for `x-api-id` once a netty thread is reused for its 2nd request:

```shell
21:46:52.054 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter request # NEW
21:46:52.201 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.TestLoggingController - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - Controller request # NEW
21:46:52.227 [default-nioEventLoopGroup-1-5] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter request
21:46:52.231 [default-nioEventLoopGroup-1-5] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter response
21:46:52.249 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.TestLoggingController - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - Controller response # NEW
21:46:52.250 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter response

...

21:46:52.368 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=7f62c7e6-3ee6-40ee-ba83-05e27463f190] - filter request # NEW
21:46:52.369 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.TestLoggingController - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - Controller request # STALE
21:46:52.374 [default-nioEventLoopGroup-1-5] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter request
21:46:52.374 [default-nioEventLoopGroup-1-5] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - filter response
21:46:52.376 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.TestLoggingController - [x-api-id=4627b528-6a66-4b35-b506-39c40d62e979] - Controller response # STALE
21:46:52.377 [default-nioEventLoopGroup-1-3] INFO  m.p.issue.MDCHttpServerFilter - [x-api-id=7f62c7e6-3ee6-40ee-ba83-05e27463f190] - filter response # NEW
```

## Micronaut 2.5.13 Documentation

- [User Guide](https://docs.micronaut.io/2.5.13/guide/index.html)
- [API Reference](https://docs.micronaut.io/2.5.13/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/2.5.13/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

