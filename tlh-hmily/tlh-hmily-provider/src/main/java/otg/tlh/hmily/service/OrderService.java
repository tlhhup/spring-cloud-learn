package otg.tlh.hmily.service;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.stereotype.Service;

/**
 * Created by 离歌笑tlh/hu ping on 2019/3/24
 * <p>
 * Github: https://github.com/tlhhup
 */
@Service
public class OrderService {

    @Hmily(confirmMethod = "confirmPay",cancelMethod = "cancelPay")
    public String pay(String orderId,double total){
        System.out.println("orderId:" + orderId + " pay :" + total);
        return "success";
    }

    public boolean confirmPay(String orderId,double total){
        System.out.println("order confirm pay");
        return Boolean.TRUE;
    }

    public boolean cancelPay(String orderId,double total){
        System.out.println("order cancel pay");
        return Boolean.TRUE;
    }

}
