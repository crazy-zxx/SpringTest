package annotation.aspect;

import annotation.MetricTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 符合条件的目标方法是带有@MetricTime注解的方法，因为metric()方法参数类型是MetricTime（
 * 注意参数名是metricTime不是MetricTime），我们通过它获取性能监控的名称。
 *
 * 有了@MetricTime注解，再配合MetricAspect，任何Bean，
 * 只要方法标注了@MetricTime注解，就可以自动实现性能监控。
 *
 * 使用注解实现AOP需要先定义注解，然后使用@Around("@annotation(name)")实现装配；
 * 使用注解既简单，又能明确标识AOP装配，是使用AOP推荐的方式。
 */
@Aspect
@Component
public class MetricAspect {
    //@annotation(xxx)   <--->  metric(..., MetricTime xxx)
    @Around("@annotation(metricTime)")
    public Object metric(ProceedingJoinPoint joinPoint, MetricTime metricTime) throws Throwable {
        String name = metricTime.value();
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long t = System.currentTimeMillis() - start;
            // 写入日志或发送至JMX:
            System.err.println("[Metrics] " + name + ": " + t + "ms");
        }
    }
}
