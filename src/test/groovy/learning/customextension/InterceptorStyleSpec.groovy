package learning.customextension

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FeatureInfo
import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * {@link IMethodInterceptor}を使ったやり方
 */
class InterceptorStyleSpec extends Specification {

    @Say("world")
    def "hello"() {
        expect:
        println "hello"
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtensionAnnotation(SayExtension.class)
public @interface Say {
    String value()
}

class SayExtension extends AbstractAnnotationDrivenExtension<Say> {

    @Override
    void visitFeatureAnnotation(Say say, FeatureInfo feature) {
        feature.addInterceptor(new SayInterceptor(say))
    }
}

class SayInterceptor implements IMethodInterceptor {

    Say say

    SayInterceptor(Say say) {
        this.say = say
    }

    void intercept(IMethodInvocation invocation) throws Throwable {
        try {
            invocation.proceed()
        } finally {
            println say.value()
        }
    }
}
