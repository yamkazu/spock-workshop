package learning.basic

import learning.EmbeddedSpec
import org.spockframework.compiler.InvalidSpecCompileException

/**
 * {@code setup:}ブロックの使い方。
 */
class SetupSpec extends EmbeddedSpec {

    def "基本的な使い方"() {
        setup:
        true
    }

    def "setupの代わりにgivenを使うこともできる"() {
        given:
        true
    }

    def "stimulusより前のコードは暗黙的にsetupブロックになる"() {
        // ここはsetupブロック
        true

        expect:
        true
    }

    def "暗黙的なsetupブロックと明示的なsetupブロックを組み合わせて使える"() {
        // まあ必要性はない
        true

        setup:
        true
    }

    def "複数のsetupブロックを宣言することはできない"() {
        when:
        compiler.compileFeatureBody """
        | setup: true
        | setup: true
        """.stripMargin()

        then:
        thrown(InvalidSpecCompileException)
    }
}
