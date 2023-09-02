package com.z.gateway.filter;


import com.z.gateway.feigen.Oauth2FeignClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;


import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class Oauth2Filter implements GlobalFilter, Ordered {

    @Autowired
    @Lazy
    private Oauth2FeignClient oauth2FeignClient;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestUrl = request.getURI().getPath();

        //排除不需要验证的path
        if (requestUrl.contains("/user/register")) {
            chain.filter(exchange);
        }

        //验证token
        String token = request.getHeaders().getFirst("Authorization");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        //        Map<String, Object> tokenRes = oauth2FeignClient.check(token);
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> oauth2FeignClient.check(token));
        Map<String, Object> map = future.get();
        if (!Boolean.valueOf(String.valueOf(map.get("active")))) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //添加tracingId以便后续log追踪
        String tracingId = UUID.randomUUID().toString().replaceAll("-", "");
        ServerHttpRequest serverHttpRequest = request.mutate().headers(header -> {
            header.set("tracingId", tracingId);
        }).build();
        exchange.mutate().request(serverHttpRequest);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
