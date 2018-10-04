1. 使用git作为存储时依赖于github的API
2. 问题：
    1. env的contextPath不能为空，代码为做非空校验
    2. 不支持eureka开启认证(后期可以加上)
    	1. 修改界面及数据库字段，添加授权之后的密钥
    	2. 在请求获取注册中心中服务信息的请求头重添加Authorization字段 