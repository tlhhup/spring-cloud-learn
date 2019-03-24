package org.tlh.hmily.feign;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@FeignClient(name = "tlh-hmily-provider",path = "/order")
public interface OrderService {

    @Hmily
    @PostMapping(value = "/payOrder/{orderId}")
    String payOrder(@PathVariable(name = "orderId") String orderId, @RequestParam("total") double total);

}
