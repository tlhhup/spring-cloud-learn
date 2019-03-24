package org.tlh.hmily.service;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tlh.hmily.feign.OrderService;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Service
public class AccountService {

    @Autowired
    private OrderService orderService;

    @Hmily(confirmMethod = "confirmPay",cancelMethod = "cancelPay")
    public String pay(String orderId){
        this.orderService.payOrder(orderId,90d);
        return "success";
    }


    public boolean confirmPay(String orderId){
        System.out.println("account confirm pay");
        return Boolean.TRUE;
    }

    public boolean cancelPay(String orderId){
        System.out.println("account cancel pay");
        return Boolean.TRUE;
    }

}
