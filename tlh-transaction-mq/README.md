###分布式事务常用解决方案
1. 分布式事务
	1. 二阶段型
	2. TCC补偿型
	3. 最终一致性：基于实时消息系统构建
		1. 消息服务系统---->存储主动方事务操作的业务数据，并对外提供操作这些数据的方法
			1. 存储预发送的消息：由**主动方**调用存储预发送的消息
			2. 确认并发送消息：由**消息管理系统**调用进行手动补偿
			3. 查询状态确认的过时消息：由**消息投递**系统、**消息管理**系统
			4. 确认消息已被成功消费：由事务**被动方**调用，确认消息已经被消费(数据一致性达到)
			5. 查询消费超时的消息：由**消息投递**系统、**消息管理**系统
			6. 查询未被消费的消息：由**消息投递**系统、**消息管理**系统
			7. .... 
		2. 消息投递系统---->定时任务，从消息服务系统获取尚未执行的消息，将其投递到实时消息系统中(具体的mq中间件) 
		3. 消息管理子系统-->消息服务系统的admin管理后台，提供对死亡消息(重试次数达到限制的消息)进行人工投递
	4. 最大努力通知
		1. 发送：调用被动方的接口，将消息推送过去但不一定都成功，通过反复的重试进行处理，次数不能操作重试次数(被动方需要幂等性设计)  
		2. 查询：同时为被动方提供查询状态的接口

###基于rabbitmq构件的可靠消息一致性
1. 整体架构

	![](pic/reliable-sources.jpeg)
2. 实现
	1. transaction-service-mq：消息服务系统(存储消息)
	2. transaction-client-mq：消息服务系统的client,主要用于对service调用的包装和提供软负载
		1. 将其封装为spring boot的starter了，来保证client的自动注入
		2. 通过自定义FeignFormatterRegistrar来注册时间的格式化器，实现方法参数可以使用Date类型数据 
	3. transaction-task-mq：消息投递系统，将消息投递到实时消息系统中(mq中间件) 
		1. 查询消息系统确认发送的消息
		2. 将消息投递到实时消息系统  
	4. transaction-example：demo
		1. 事务主动方，除了预发送消息到消息系统以外，需要根据发送的结果来流转消息的状态
		
				@Transactional
			    public boolean createStudent(Student student){
			        boolean success=false;
			        //1.保存本地事务
			        this.studentRepository.save(student);
			
			        //2.发送到消息系统
			        MessageRepDto messageRepDto = this.transactionMessageClient.sendMessage(buildMessage(student,"student.create"));
			        if(messageRepDto!=null&&messageRepDto.getSuccess()){
			            success=true;
			            //3.确认消息发送成功,避免消息入库，但是feign失败
			            this.transactionMessageClient.confirmSendMessages(messageRepDto.getMessageId());
			        }else{
			            log.error("send create student error");
			            //spring默认在出现runtime和error时会进行事务的回滚
			            throw new RuntimeException("消息发送失败");
			        }
			        return success;
			    } 