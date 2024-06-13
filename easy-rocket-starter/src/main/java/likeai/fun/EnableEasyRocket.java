package likeai.fun;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EasyRocketBeanSelector.class)
public @interface EnableEasyRocket {

    boolean consumer() default false;

    boolean producer() default false;

    boolean listener() default true;
}
