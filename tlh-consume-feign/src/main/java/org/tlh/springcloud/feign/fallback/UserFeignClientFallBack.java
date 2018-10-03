package org.tlh.springcloud.feign.fallback;

import org.springframework.stereotype.Component;
import org.tlh.springcloud.feign.UserFeignClient;

/**
 * @author huping
 * @desc
 * @date 18/10/3
 */
@Component
public class UserFeignClientFallBack implements UserFeignClient {

    @Override
    public String register() {
        return "register--->fail";
    }

    @Override
    public String delete(int id) {
        return "delete---->fail";
    }
}
