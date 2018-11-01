### 请求限流
1. 通过spring内置的监听器(ContextRefreshedEvent)在spring容器初始化完成之后来获取所有添加了RequestRateLimit注解的方法
2. spring boot提供的listener用于监听Application的动作，并不是spring容器的动作，可以理解为一些预处理的动作。
3. 两种限流方式：基于filter和HandlerInterceptor方式，其两者为互斥
	1. 配置文件

			tlh:
			  rate:
			    basePackage: org.tlh.springcloud.controller #指定扫描的包，添加了限流注解
			#  filter:
			#    enable: true	#采用filter的限流方式,默认采用HandlerInterceptor的方式
	2. 注解  	

			@GetMapping("/end")
		    @RequestRateLimit(tokenKey = "exam.end")
		    public String end(){
		        return "end";
		    }