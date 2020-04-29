package annotation;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Spring还可以根据@Conditional决定是否创建某个Bean。
 * Spring只提供了@Conditional注解，具体判断逻辑还需要我们自己实现。
 */
@Component
@Conditional(OnSmtpEnvCondition.class) //满足OnSmtpEnvCondition的条件，才会创建这个Bean
public class EnvConditioinTest {

}


/**
 * Spring Boot提供了更多使用起来更简单的条件注解
 * 例如，如果配置文件中存在app.smtp=true，则创建MailService：
 *
 * @Component
 * @ConditionalOnProperty(name = "app.smtp", havingValue = "true")
 * public class MailService {
 *     ...
 * }
 *
 * 如果当前classpath中存在类javax.mail.Transport，则创建MailService：
 *
 * @Component
 * @ConditionalOnClass(name = "javax.mail.Transport")
 * public class MailService {
 *     ...
 */