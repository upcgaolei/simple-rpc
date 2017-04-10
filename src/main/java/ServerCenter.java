import java.io.IOException;

/**
 * Copyright (c) 2017 zhougaolei
 * Date:17/4/10
 * Author: <a href="upcgaolei@qq.com">周高磊</a>
 * Desc:
 */
public interface ServerCenter {

    /**
     * 服务启动
     */
    void start() throws IOException;

    /**
     * 服务停止
     */
    void stop();

    /**
     * 服务运行状态
     */
    Boolean isRunning();

    /**
     * 暴露的端口号
     * @return port
     */
    int getPort();

    /**
     * 服务注册
     * @param serviceInterface 服务接口
     * @param serviceImpl 服务接口实现
     */
    void register(Class serviceInterface, Class serviceImpl);

}
