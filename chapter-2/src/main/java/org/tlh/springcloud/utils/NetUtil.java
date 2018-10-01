package org.tlh.springcloud.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author huping
 * @desc 判断端口是否被占用
 * @date 18/10/1
 */
public class NetUtil {

    public static boolean isLoclePortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }
}
