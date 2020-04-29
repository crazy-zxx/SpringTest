package annotation.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Validators {
    /**
     * Spring会自动把所有类型为Validator的Bean装配为一个List注入进来，
     * 这样一来，我们每新增一个Validator类型，就自动被Spring装配到Validators中了，非常方便。
     *
     * Spring是通过扫描classpath获取到所有的Bean，而List是有序的，
     * 要指定List中Bean的顺序，可以加上@Order注解
     */
    @Autowired
    List<Validator> validators;

    public void validate(String email, String password, String name) {
        for (var validator : this.validators) {
            validator.validate(email, password, name);
        }
    }
}