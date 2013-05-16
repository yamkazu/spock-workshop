package learning.extension

import learning.EmbeddedSpec
import spock.lang.Stepwise

/**
 * {@link Stepwise}を設定するとフィーチャメソッドを定義した順に実行される
 */
@Stepwise
class StepwiseSpec extends EmbeddedSpec {

    def "first"() {
        expect: println "first"
    }

    def "second"() {
        expect: println "second"
    }

    def "third"() {
        expect: println "third"
    }

    def "途中で失敗すると残りのフィーチャメッドは実行されない"() {
        setup:
        runner.throwFailure = false

        when:
        def result = runner.runWithImports """
        | @Stepwise
        | class Foo extends Specification {
        |   def one() { expect: println 'one' }
        |   def two() { expect: println 'two'; false } // ここで失敗する
        |   def three() { expect: println 'よばれない' }
        | }
        """.stripMargin()

        then:
        result.runCount == 2
        result.failureCount == 1
        result.ignoreCount == 1
    }
}