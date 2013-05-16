package learning.basic

import learning.EmbeddedSpec
import org.spockframework.runtime.SpockComparisonFailure

/**
 * {@code when:}、{@code then:}ブロックの使い方
 */
class WhenThenSpec extends EmbeddedSpec {

    Stack stack = new Stack()

    def "基本的な使い方"() {
        when: "whenブロックは「もし...」を表す任意の実行コードを記述する"
        stack.push("spock")

        then: "thenブロックには「ならば...」を表すコンディションを記述する"
        !stack.empty
        stack.size() == 1
        stack.peek() == "spock"
    }

    def "コンディションはBoolean式で記述する"() {
        when:
        true

        then: "Boolean式はGroovy流に評価される"
        !false
        true
        !0
        1
        ![:]
        ['one': 1]
        ![]
        [1]
        !""
        "this is true"
        new Object()
        !null
    }

    def "コンディションが満たされない場合はPowerAssertライクなエラー出力"() {
        when:
        runner.runFeatureBody """
        | when:
        | def x = 1
        | def y = 2
        | then:
        | x == y
        """.stripMargin()

        then:
        SpockComparisonFailure e = thrown()
        e.condition.rendering.trim() == """
                                        |x == y
                                        || |  |
                                        |1 |  2
                                        |  false
                                        """.stripMargin().trim()
    }

    def "トップレベルの式だけがコンディションとして認識される"() {
        when:
        def list = [1, 2, 3]

        then:
        list.each {
            it > 0 // これはコンティションとして認識されない
        }


        and: "回避方法1: トップレベルの式で評価行えるようにする"
        list.every {
            it > 0
        }

        and: "回避方法2: 明示的なアサート"
        list.each {
            assert it > 0
        }
    }

    def "コンディションのグループ化"() {
        setup:
        def stack = new Stack()

        when:
        stack.push("spock")

        then: "withを使う"
        with(stack) {
            !empty()
            size() == 1
            peek() == "spock"
        }
    }

    def "実はthenブロックを続けることができるが詳しくはモックの中で"() {
        when: true
        then: true
        then: true
        then: true
    }
}
