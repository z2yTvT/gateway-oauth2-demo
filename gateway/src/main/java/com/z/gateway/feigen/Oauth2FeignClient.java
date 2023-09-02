package com.z.gateway.feigen;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("oauth2-service")
public interface Oauth2FeignClient {

    @RequestMapping(value = "/oauth/check_token")
    Map<String,Object> check(@RequestParam String token);

}
