package likeai.fun;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author likeai
 */
public class EasyRocketBeanSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String name = EnableEasyRocket.class.getName();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(name, true));
        if (isNull(attributes)) {
            return new String[]{};
        }
        List<String> imports = new ArrayList<>();
        imports.add(RocketMqConfigRegister.class.getName());
        boolean consumer = attributes.getBoolean("consumer");
        if (consumer) {
            imports.add(ConsumerRegister.class.getName());
        }
        boolean producer = attributes.getBoolean("producer");
        if (producer) {
            imports.add(ProducerRegister.class.getName());
        }
        boolean listener = attributes.getBoolean("listener");
        if (producer && listener) {
            imports.add(ProducerListenerRegister.class.getName());
        }
        return imports.toArray(new String[0]);
    }
}
