package learning.customextension

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * {@link org.spockframework.runtime.IRunListener}を使ったやり方 
 */
@StopWatch
class ListenerStyleSpec extends Specification {

    def "1秒程度かかるフィーチャメソッド"() {
        when:
        Thread.sleep(1000)

        then:
        true
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(StopWatchExtension.class)
public @interface StopWatch {
}

class StopWatchExtension extends AbstractAnnotationDrivenExtension<StopWatch> {

    @Override
    void visitSpecAnnotation(StopWatch annotation, SpecInfo spec) {
        spec.addListener(new StopWatchListener())
    }
}

class StopWatchListener extends AbstractRunListener {

    Long start

    @Override
    void beforeFeature(FeatureInfo feature) {
        println "start $feature.name"
        start = System.currentTimeMillis()
    }

    @Override
    void afterFeature(FeatureInfo feature) {
        println "end ${System.currentTimeMillis() - start} milliseconds"
    }
}
