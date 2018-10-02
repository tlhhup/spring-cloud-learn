
1. 自定义示例ID
   
        instance-id: ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}
2. 心跳频率    
    
        lease-renewal-interval-in-seconds: 5 # 发送给注册中心的心跳频率
3. 失效时间
    
        lease-expiration-duration-in-seconds: 5 # 心跳间隔的超时时间，如果没有收到则移除该instance
4. 采用IP注册, 如果hostname不能被解析时使用IP向eureka发送请求   
    
        prefer-ip-address: true