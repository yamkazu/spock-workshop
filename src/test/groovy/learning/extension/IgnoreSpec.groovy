package learning.extension

import spock.lang.Ignore
import learning.EmbeddedSpec

/**
 * {@link IgnoreSpec}を設定したフィーチャメソッドは無視される
 */
class IgnoreSpec extends EmbeddedSpec {

    @Ignore
    def "このフィーチャメソッドは無視される"() {
        expect: false
    }

    @Ignore
    def "複数のフィーチャメソッドに@Ignoreを設定できる"() {
        expect: false
    }

    def "スペッククラスに付与するとフィーチャメソッドすべてが無視される"() {
        when:
        def result = runner.runWithImports """
        | @Ignore
        | class Hoge extends Specification {
        |   def foo() {
        |     expect: false
        |   }
        |
        |   def bar() {
        |     expect: false
        |   }
        | }
        """.stripMargin()

        then:
        result.runCount == 0
    }
}