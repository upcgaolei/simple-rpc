import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2017 zhougaolei
 * Date:17/4/10
 * Author: <a href="upcgaolei@qq.com">周高磊</a>
 * Desc:
 */
public class ServerCenterImpl implements ServerCenter {

    /**
     * new fixed thread pool
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * <InterfaceName, Reference>
     */
    private static final HashMap<String, Class> SERVICE_REGISTRY = new HashMap<String, Class>();

    private static Boolean isRunning = Boolean.FALSE;

    private static int port;

    public ServerCenterImpl(int port) {
        this.port = port;
    }

    public void start() throws IOException{
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        isRunning = Boolean.TRUE;
        System.out.println("Server started");

        try {
            while(true) {
                executorService.execute(new ServiceTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    public void stop() {
        isRunning = Boolean.FALSE;
        executorService.shutdown();
    }

    public Boolean isRunning() {
        return Boolean.TRUE;
    }

    public int getPort() {
        return port;
    }

    public void register(Class serviceInterface, Class serviceImpl) {
        SERVICE_REGISTRY.put(serviceInterface.getName(), serviceImpl);
    }

    public static class ServiceTask implements Runnable {
        Socket client = null;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;

            try {
                //BIO Blocking IO 面向流
                //输入流
                inputStream = new ObjectInputStream(client.getInputStream());

                //接口名称
                String serviceName = inputStream.readUTF();
                //方法名称
                String methodName = inputStream.readUTF();
                //参数类型
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                //参数
                Object[] aguments = (Object[]) inputStream.readObject();
                //通过注册中心HashMap获取对应的实现类
                Class serviceClass = SERVICE_REGISTRY.get(serviceName);
                if(serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + " not found.");
                }
                //反射Reflect
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                //反射Reflect
                Object result = method.invoke(serviceClass.newInstance(), aguments);

                //输出流
                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                if(client != null) {
                    try {
                        client.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
