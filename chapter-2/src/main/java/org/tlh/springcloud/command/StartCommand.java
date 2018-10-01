package org.tlh.springcloud.command;

import org.springframework.util.StringUtils;
import org.tlh.springcloud.utils.ServerPortUtils;

/**
 * @author huping
 * @desc
 * @date 18/10/1
 */
public class StartCommand {

    public StartCommand(String[] args) {
        boolean isServerPort = false;
        String serverPort = "";
        if (args != null) {
            for (String arg : args) {
                if (StringUtils.hasText(arg) && arg.startsWith("--server.port")) {
                    isServerPort = true;
                    serverPort = arg;
                    break;
                }
            }
            //如果没有设置端口号
            if(!isServerPort){
                serverPort=String.valueOf(ServerPortUtils.getAvailablePort());
            }else{
                serverPort=serverPort.split("=")[1];
            }
            //通过设置JVM系统参数改变server.port的值，其加载顺序后于配置文件，故会覆盖
            System.setProperty("server.port",serverPort);
        }
    }
}
