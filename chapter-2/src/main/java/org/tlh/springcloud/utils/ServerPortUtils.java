package org.tlh.springcloud.utils;

import java.util.Random;

/**
 * @author huping
 * @desc
 * @date 18/10/1
 */
public class ServerPortUtils {

    public static int getAvailablePort(){
        int max=65535;
        int min=2000;
        Random random=new Random();
        int port=random.nextInt(max)%(max-min+1)+min;
        boolean using = NetUtil.isLoclePortUsing(port);
        if(using){
            return getAvailablePort();
        }else{
            return port;
        }
    }

}
