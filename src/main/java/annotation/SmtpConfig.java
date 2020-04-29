package annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 另一种注入配置的方式是先通过一个简单的JavaBean持有所有的配置
 * 然后，在需要读取的地方，使用 #{smtpConfig.host} 注入
 */
@Component
public class SmtpConfig {
    @Value("${smtp.host}")
    private String host;

    @Value("${smtp.port:25}")
    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
