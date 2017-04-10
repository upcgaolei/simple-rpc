import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Copyright (c) 2017 zhougaolei
 * Date:17/4/10
 * Author: <a href="upcgaolei@qq.com">周高磊</a>
 * Desc:
 */
public class RpcClientProxy<T> {
    public static <T> T getRemoteProxyObject(final Class<T> serviceInterface, final InetSocketAddress address) {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?> [] {serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = null;
                        ObjectInputStream inputStream = null;
                        ObjectOutputStream outputStream = null;
                        try {
                            socket = new Socket();
                            socket.connect(address);

                            //将远程服务调用所需要的接口类, 方法名称, 参数类型, 参数等编码后发送给服务提供者
                            outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeUTF(serviceInterface.getName());
                            outputStream.writeUTF(method.getName());
                            outputStream.writeObject(method.getParameterTypes());
                            outputStream.writeObject(args);

                            //BIO同步阻塞等待服务器返回应答
                            inputStream = new ObjectInputStream(socket.getInputStream());
                            return inputStream.readObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        finally {
                            if(socket != null) socket.close();
                            if(inputStream != null) inputStream.close();
                            if(outputStream != null) outputStream.close();
                        }
                    }
                }
        );
    }
}
