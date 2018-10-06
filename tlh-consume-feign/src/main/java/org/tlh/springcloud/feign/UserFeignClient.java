package org.tlh.springcloud.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.tlh.springcloud.config.FeignConfiguration;
import org.tlh.springcloud.feign.fallback.UserFeignClientFallBack;

/**
 * @author huping
 * @desc
 * @date 18/10/3
 */
@FeignClient(name = "user-service",path = "/UserController",fallback = UserFeignClientFallBack.class,configuration = FeignConfiguration.class)
public interface UserFeignClient {

    @PostMapping("/add")
    String register();

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") int id);

}
