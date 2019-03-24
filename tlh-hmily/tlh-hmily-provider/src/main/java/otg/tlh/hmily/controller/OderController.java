package otg.tlh.hmily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import otg.tlh.hmily.service.OrderService;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@RestController
@RequestMapping("/order")
public class OderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/payOrder/{orderId}")
    public String payOrder(@PathVariable(name = "orderId") String orderId, @RequestParam(name = "total") double total) {
        return orderService.pay(orderId, total);
    }

}
