package annotation;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnSmtpEnvCondition implements Condition {
    /**
     * Spring只提供了@Conditional注解，具体判断逻辑还需要我们自己实现。
     */
    //OnSmtpEnvCondition的条件是存在环境变量smtp，值为true
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return "true".equalsIgnoreCase(System.getenv("smtp"));
    }
}
