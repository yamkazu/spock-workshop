package learning.concurrent

import org.spockframework.runtime.SpockTimeoutError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.util.concurrent.BlockingVariables

/**
 * {@link BlockingVariables}は
 * {@link spock.util.concurrent.BlockingVariable}と異なり
 * 複数の値を扱える。
 */
class BlockingVariablesSpec extends Specification {

    def "BlockingVariablesを使うと複数の値に対し待ち合わせできる"() {
        setup:
        def variables = new BlockingVariables()

        when:
        Thread.start {
            variables.baz = 3
            variables.bar = 2
        }
        Thread.start {
            variables.foo = 1
        }

        then:
        variables.foo == 1
        variables.bar == 2
        variables.baz == 3
    }

    @FailsWith(SpockTimeoutError)
    def "タイムアウトになるとSpockTimeoutErrorが発生する"() {
        setup:
        def variables = new BlockingVariables() // デフォルトではタイムアウト値が1秒

        when:
        Thread.start {
            Thread.sleep(1100) // 1秒以上待ち合わせ
            variables.foo = "hello"
        }

        then: "1秒待ってタイムアウト"
        variables.foo == "hello"
    }

    def "明示的にタイムアウト値を設定する"() {
        setup: "タイムアウト値を2秒に設定"
        def variables = new BlockingVariables(2)

        when:
        Thread.start {
            variables.foo = "hello"
        }

        then:
        variables.foo == "hello"
    }
}
