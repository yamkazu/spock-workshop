package learning.basic

import org.spockframework.compiler.InvalidSpecCompileException
import learning.EmbeddedSpec

/**
 * ブロックの使い方
 */
class BlockSpec extends EmbeddedSpec {

    def "when-thenを使用した場合"() {
        setup: true
        when: true
        then: true
        cleanup: true
    }

    def "expectを使用した場合"() {
        setup: true
        expect: true
        cleanup: true
    }

    def "setup/cleanupは省略できる"() {
        when: true
        then: true
    }

    def "stimulusとresponseのセットは繰り返すことができる"() {
        when: true
        then: true
        expect: true
        when: true
        then: true
        when: true
        then: true
        expect: true
    }

    def "ただしexpectブロックは連続して使用できない"() {
        when:
        compiler.compileFeatureBody """
        | expect: true
        | expect: true
        """.stripMargin()

        then:
        InvalidSpecCompileException e = thrown()
        e.message =~ "'expect' is not allowed here"
    }

    def "and:を使用して前のブロックを継続できる"() {
        setup: true
        and: true // ここはsetupブロック
        and: true // ここはsetupブロック

        when: true
        and: true // ここはwhenブロック
        and: true // ここはwhenブロック

        then: true
        and: true // ここはthenブロック
        and: true // ここはthenブロック

        expect: true
        and: true // ここはexpectブロック
        and: true // ここはexpectブロック

        cleanup: true
        and: true // ここはcleanupブロック
        and: true // ここはcleanupブロック
    }

    def "ブロックにはドキュメントが設定できる"() {
        setup: "スタックが空"
        def stack = new Stack()

        when: "要素を一つ追加"
        stack.push("spock")

        then: "スタックが空でない"
        !stack.empty()

        and: "スタックのサイズが1"
        stack.size() == 1

        and: "先頭要素はspock"
        stack.peek() == "spock"
    }
}
