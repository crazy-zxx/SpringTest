package annotation;

import annotation.service.User;
import annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * 使用Annotation配合自动扫描能大幅简化Spring的配置，我们只需要保证：
 *     每个Bean被标注为@Component并正确使用@Autowired注入；
 *     配置类被标注为@Configuration和@ComponentScan；
 *     所有Bean均在指定包以及子包内。
 *
 * 通常来说，启动配置AppConfig位于自定义的顶层包（例如com.itranswarp.learnjava），其他Bean按类别放入子包。
 */

/**
 * @Configuration 表示它是一个配置类
 * 使用的实现类是AnnotationConfigApplicationContext，必须传入一个标注了@Configuration的类名。
 *
 * @ComponentScan 它告诉容器，自动搜索当前类所在的包以及子包，
 * 把所有标注为@Component的Bean自动创建出来，并根据@Autowired进行装配。
 */
@Configuration
@ComponentScan      //扫描该类所在的包下所有的配置类
@PropertySource("app.properties") // 表示读取classpath的app.properties
public class AutoConfig {

    /**
     * 默认情况下，当我们标记了一个@Autowired后，Spring如果没有找到对应类型的Bean，它会抛出NoSuchBeanDefinitionException异常。
     *
     * 可以给@Autowired增加一个required = false的参数
     * 这个参数告诉Spring容器，如果找到一个类型为ZoneId的Bean，就注入，如果找不到，就忽略。
     * 这种方式非常适合有定义就使用定义，没有就使用默认值的情况。
     */
    @Autowired(required = false)
    //未指定默认注入标记@Primary的
    ZoneId zoneId1 = ZoneId.systemDefault();

    @Autowired(required = false)
    @Qualifier("z")   // 指定注入名称为"z"的ZoneId
    ZoneId zoneId2 = ZoneId.systemDefault();

    @Autowired(required = false)
    @Qualifier("untest")   // 指定注入名称为"untest"的ZoneId
    ZoneId zoneId3 = ZoneId.systemDefault();

    @Autowired(required = false)
    @Qualifier("test")   // 指定注入名称为"test"的ZoneId
    ZoneId zoneId4 = ZoneId.systemDefault();

    /**
     * 使用Spring容器时，我们也可以把“文件”注入进来，方便程序读取。
     * Spring提供了一个org.springframework.core.io.Resource（注意不是javax.annotation.Resource），
     * 它可以像String、int一样使用@Value注入
     *
     * 注入Resource最常用的方式是通过classpath，即类似classpath:/logo.txt表示在classpath中搜索logo.txt文件，
     * 然后，我们直接调用Resource.getInputStream()就可以获取到输入流，避免了自己搜索文件的代码。
     * 也可以直接指定文件的路径，例如：@Value("file:/path/to/logo.txt")
     */
    @Value("classpath:/logo.txt")
    private Resource resource;

    /**
     * 在开发应用程序时，经常需要读取配置文件。最常用的配置方法是以key=value的形式写在.properties文件中。
     * Spring容器还提供了一个更简单的@PropertySource来自动读取配置文件。我们只需要在@Configuration配置类上再添加一个注解
     *
     * 注意注入的字符串语法，它的格式如下：
     *     "${app.zone}"表示读取key为app.zone的value，如果key不存在，启动将报错；
     *     "${app.zone:ZR}"表示读取key为app.zone的value，但如果key不存在，就使用默认值ZR。
     */
    @Value("${app.zone:ZR}")
    String zoneIdR;

    /**
     * 另一种注入配置的方式是先通过一个简单的JavaBean持有所有的配置
     * 然后，在需要读取的地方，使用#{smtpConfig.host}注入
     * 好处是，多个Bean都可以引用同一个Bean的某个属性。
     */
    @Value("#{smtpConfig.host}")
    private String smtpHost;

    @Value("#{smtpConfig.port}")
    private int smtpPort;


    public static void main(String[] args) {

        ApplicationContext context=new AnnotationConfigApplicationContext(AutoConfig.class);
        UserService userService=context.getBean(UserService.class);
        User user=userService.login("bob@example.com", "password");
        System.out.println(user.getName());
        /**
         * 当我们把一个Bean标记为@Component后，它就会自动为我们创建一个单例（Singleton），
         * 即容器初始化时创建Bean，容器关闭前销毁Bean。
         * 在容器运行期间，我们调用getBean(Class)获取到的Bean总是同一个实例。
         */
        UserService userService1=context.getBean(UserService.class);
        System.out.println(userService);
        System.out.println(userService1);

        /**
         * 有一种Bean，我们每次调用getBean(Class)，容器都返回一个新的实例，这种Bean称为Prototype（原型），
         * 它的生命周期显然和Singleton不同。声明一个Prototype的Bean时，需要添加一个额外的@Scope注解
         */
        MyBean myBean =context.getBean(MyBean.class);
        MyBean myBean1 =context.getBean(MyBean.class);
        System.out.println(myBean);
        System.out.println(myBean1);

    }

    /**
     * 有些时候，一个Bean在注入必要的依赖后，需要进行初始化（监听消息等）。
     * 在容器关闭时，有时候还需要清理资源（关闭连接池等）。
     * 我们通常会定义一个init()方法进行初始化，定义一个shutdown()方法进行清理，
     * 然后，引入JSR-250定义的Annotation
     *
     * Spring容器会对Bean做如下初始化流程：
     *     调用构造方法创建MailService实例；
     *     根据@Autowired进行注入；
     *     调用标记有@PostConstruct的init()方法进行初始化。
     *     而销毁时，容器会首先调用标记有@PreDestroy的shutdown()方法。
     *
     * Spring只根据Annotation查找无参数方法，对方法名不作要求。
     */
    @PostConstruct
    public void init() {
        System.out.println("Init... " );

        try (var reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            System.out.println(reader.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(zoneId1);
        System.out.println(zoneId2);
        System.out.println(zoneId3);
        System.out.println(zoneId4);

        System.out.println(zoneIdR);

        System.out.println(smtpHost);
        System.out.println(smtpPort);

    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutdown...");
    }

    /**
     * 如果一个Bean不在我们自己的package管理之类，例如ZoneId，如何创建它？
     * 答案是我们自己在@Configuration类中编写一个Java方法创建并返回它，注意给方法标记一个@Bean注解
     * Spring对标记为@Bean的方法只调用一次，因此返回的Bean仍然是单例。
     */
    // 创建一个Bean:
    @Bean("z")
    ZoneId createZoneId() {
        return ZoneId.of("Z");
    }

    /**
     * 但有些时候，我们需要对一种类型的Bean创建多个实例
     * Spring会报NoUniqueBeanDefinitionException异常，意思是出现了重复的Bean定义。
     *
     * 需要给每个Bean添加不同的名字：
     * 可以用@Bean("name")指定别名，也可以用@Bean+@Qualifier("name")指定别名。
     *
     * 存在多个同类型的Bean时，注入ZoneId又会报错
     * 注入时，要指定Bean的名称
     *
     * 还有一种方法是把其中某个Bean指定为@Primary
     * 在注入时，如果没有指出Bean的名字，Spring会注入标记有@Primary的Bean。这种方式也很常用
     */
    @Bean
    @Primary        // 指定为主要Bean
    @Qualifier("utc8")
    ZoneId createZoneOfUTC8() {
        return ZoneId.of("UTC+08:00");
    }

    /**
     * 创建某个Bean时，Spring容器可以根据注解@Profile来决定是否创建。
     *
     * 在运行程序时，加上JVM参数 -Dspring.profiles.active=xxx 就可以指定以 xxx 环境启动。
     * Spring允许指定多个Profile
     */
    // 如果当前的Profile设置为test，则Spring容器会调用createZoneIdForTest()创建ZoneId，
    // 否则，调用createZoneIdForUnTest()创建ZoneId。注意到@Profile("!test")表示非test环境。
    @Bean("untest")
    @Profile("!test")
    ZoneId createZoneIdForUnTest() {
        return ZoneId.systemDefault();
    }

    @Bean("test")
    @Profile("test")
    ZoneId createZoneIdForTest() {
        return ZoneId.of("America/New_York");
    }


}
