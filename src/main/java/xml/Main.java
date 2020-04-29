package xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import xml.service.User;
import xml.service.UserService;

public class Main {

    public static void main(String[] args) {
        /**
         * Spring提供的容器又称为IoC容器
         * IoC全称Inversion of Control，直译为控制反转。IoC又称为依赖注入（DI：Dependency Injection）
         *
         * 它解决了一个最主要的问题：将组件的 创建+配置 与组件的 使用 相分离，并且，由IoC容器负责管理组件的生命周期。
         *
         * 因为IoC容器要负责实例化所有的组件，因此，有必要告诉容器如何创建组件，以及各组件的依赖关系。
         * 一种最简单的配置是通过XML文件来实现
         * 在Spring的IoC容器中，我们把所有组件统称为JavaBean，即配置一个组件就是配置一个Bean
         *
         * Spring的IoC容器是一个高度可扩展的无侵入容器。
         * 所谓无侵入，是指应用程序的组件无需实现Spring的特定接口，或者说，组件根本不知道自己在Spring的容器中运行。
         * 这种无侵入的设计有以下好处：
         *
         *     应用程序组件既可以在Spring的IoC容器中运行，也可以自己编写代码自行组装配置；
         *     测试的时候并不依赖Spring容器，可单独进行测试，大大提高了开发效率
         */

        /**
         * 创建一个Spring的IoC容器实例，然后加载配置文件，
         * 让Spring容器为我们创建并装配好配置文件中指定的所有Bean
         *
         * ApplicationContext接口是从BeanFactory接口继承而来的，
         * 并且，ApplicationContext提供了一些额外的功能，包括国际化支持、事件和通知机制等。
         * 通常情况下，我们总是使用ApplicationContext，很少会考虑使用BeanFactory。
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        UserService userService = context.getBean(UserService.class);
        User user = userService.login("bob@example.com", "password");
        System.out.println(user.getName());

        //Spring还提供另一种IoC容器叫BeanFactory，使用方式和ApplicationContext类似：
        // BeanFactory factory = new XmlBeanFactory(new ClassPathResource("spring-config.xml"));
        // MailService mailService = factory.getBean(MailService.class);




    }
}


