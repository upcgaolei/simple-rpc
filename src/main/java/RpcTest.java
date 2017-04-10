import java.net.InetSocketAddress;

/**
 * Copyright (c) 2017 zhougaolei
 * Date:17/4/10
 * Author: <a href="upcgaolei@qq.com">周高磊</a>
 * Desc:
 */
public class RpcTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ServerCenter serverCenter = new ServerCenterImpl(8088);
                    serverCenter.register(RpcService.class, RpcServiceImpl.class);
                    serverCenter.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        RpcService rpcService = RpcClientProxy.getRemoteProxyObject(RpcService.class, new InetSocketAddress("127.0.0.1", 8088));
        System.out.println(rpcService.sayHello("zhougaolei"));
    }
}
