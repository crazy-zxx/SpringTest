package annotation;

import org.springframework.beans.factory.FactoryBean;

import java.time.ZoneId;

/**
 * Spring也提供了工厂模式，允许定义一个工厂，然后由工厂创建真正的Bean。
 *
 * 当一个Bean实现了FactoryBean接口后，Spring会先实例化这个工厂，然后调用getObject()创建真正的Bean。
 * getObjectType()可以指定创建的Bean的类型，因为指定类型不一定与实际类型一致，可以是接口或抽象类。
 *
 * 如果定义了一个FactoryBean，要注意Spring创建的Bean实际上是这个FactoryBean的getObject()方法返回的Bean。
 * 为了和普通Bean区分，我们通常都以XxxFactoryBean命名。
 */
public class ZoneIdFactoryBean implements FactoryBean<ZoneId> {
    String zone = "Z";

    @Override
    public ZoneId getObject() throws Exception {
        return ZoneId.of(zone);
    }

    @Override
    public Class<?> getObjectType() {
        return ZoneId.class;
    }
}
