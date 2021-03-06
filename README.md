# spring-cloud-learn
spring-cloud组件学习和第三方开源项目集成

1. chapter-2：spring-boot的一些练习
2. tlh-eureka-server：采用eureka实现的注册中心，并开启了认证服务
3. tlh-eureka-server-noauth：采用eureka实现的注册中心，无认证，主要用于后面的tlh-scc-config-admin使用
4. tlh-service-provider：一个服务端
5. tlh-service-consumer：通过eureka、ribbon、hystrix实现的消费端
6. tlh-consume-feign：通过openfeign实现的服务消费端
7. tlh-hystrix-dashboard：用于收集消费端服务调用失败、降级的可视化界面，并集成了turbine
8. tlh-gateway-zuul：通过zuul实现的网关
9. tlh-config-server：使用本地git实现的配置中心
10. tlh-config-server-db：使用mysql数据库实现的配置中心(集成消息总线) 
11. tlh-scc-config-admin：使用scca admin实现的基于git存储的配置中心管理后台(需要依赖于github的API)
12. tlh-scc-config-admin-db：使用scca admin实现的基于mysql存储的配置中心管理后台
13. tlh-scc-config-client：配置中心客户端(集成消息总线) 
14. tlh-spring-boot-admin：集成spring-boot-admin
15. tlh-auth：集成JTW实现统一认证
16. tlh-transaction-mq：可靠消息一致性(分布式事务：最终一致性)
17. tlh-gateway-gateway：Gateway实现网关
18. tlh-hmily：hmily分布式事务应用
19. tlh-config-alone：在Spring boot项目中使用config server进行配置管理


### 第三方开源项目
1. scca：https://github.com/dyc87112/spring-cloud-config-admin
2. spring-boot-admin：https://github.com/codecentric/spring-boot-admin
3. redission：https://github.com/redisson/redisson
4. spring-boot-swagger：https://github.com/SpringForAll/spring-boot-starter-swagger
5. Reflections：https://github.com/ronmamo/reflections
6. Hmily：https://dromara.org/website/zh-cn/