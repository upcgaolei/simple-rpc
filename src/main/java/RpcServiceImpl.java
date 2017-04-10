/**
 * Copyright (c) 2017 zhougaolei
 * Date:17/4/10
 * Author: <a href="upcgaolei@qq.com">周高磊</a>
 * Desc:
 */
public class RpcServiceImpl implements RpcService {

    public String sayHello(String name) {
        return "hello " + name;
    }

}
