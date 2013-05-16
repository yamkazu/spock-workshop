package learning.concurrent

import org.spockframework.runtime.SpockTimeoutError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

/**
 * {@link BlockingVariable}を使用すると、
 * 値が設定されるまで{@link BlockingVariable#get}で待ち合わせできる。
 */
class BlockingVariableSpec extends Specification {

    def "基本的な使い方"() {
        setup:
        BlockingVariable variable = new BlockingVariable()

        when:
        Thread.start {
            variable.set("hello")
        }

        then: "値が設定されるまでgetメソッドが待ち合わせる"
        variable.get() == "hello"
    }

    @FailsWith(SpockTimeoutError)
    def "タイムアウトになるとSpockTimeoutErrorが発生する"() {
        setup:
        BlockingVariable variable = new BlockingVariable() // デフォルトではタイムアウト値が1秒

        when:
        Thread.start {
            Thread.sleep(1100) // 1秒以上スリープ
            variable.set("hello")
        }

        then: "1秒待ってタイムアウト"
        variable.get() == "hello"
    }

    def "明示的にタイムアウト値を設定する"() {
        setup: "タイムアウト値を2秒に設定"
        BlockingVariable variable = new BlockingVariable(2)

        when:
        Thread.start {
            variable.set("hello")
        }

        then:
        variable.get() == "hello"
    }
}
