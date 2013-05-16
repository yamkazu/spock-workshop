package learning.concurrent

import org.spockframework.runtime.SpockTimeoutError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * {@link PollingConditions}は{@link spock.util.concurrent.AsyncConditions}と
 * {@link spock.util.concurrent.BlockingVariable}を組み合わせた機能。
 * <p>
 * 現状一番使いやすいと思うがver0.7の時点では{@link org.spockframework.util.Beta}状態
 */
class PollingConditionsSpec extends Specification {

    def "PollingConditionsの基本的な使い方"() {
        setup:
        PollingConditions conditions = new PollingConditions()
        def number = 0

        when:
        Thread.start {
            number = 1
        }

        then: "eventuallyのコンディションが満たされるまで待ち合わせる"
        conditions.eventually {
            number == 1
        }
    }

    @FailsWith(SpockTimeoutError)
    def "タイムアウトになるとSpockTimeoutErrorが発生する"() {
        setup:
        PollingConditions conditions = new PollingConditions() // デフォルトではタイムアウト値が1秒
        def number = 0

        when:
        Thread.start {
            Thread.sleep(1100) // 1秒以上スリープ
            number = 1
        }

        then: "1秒待ってタイムアウト"
        conditions.eventually {
            number == 1
        }
    }

    def "タイムアウト値を設定する"() {
        setup: "タイムアウト値を2秒に設定"
        PollingConditions conditions = new PollingConditions(timeout: 2)
        def number = 0

        when:
        Thread.start {
            number = 1
        }

        then:
        conditions.eventually {
            number == 1
        }
    }

    def "withinを使ってタイムアウト値を設定する"() {
        setup:
        PollingConditions conditions = new PollingConditions()
        def number = 0

        when:
        Thread.start {
            number = 1
        }

        then: "withinでタイムアウトを2秒に設定"
        conditions.within(2000) { // withinを使うとブロックのタイムアウト値だけ変更できる
            number == 1
        }
    }

    def "PollingConditionsのデフォルト値"() {
        expect:
        with(new PollingConditions()) {
            timeout == 1
            initialDelay == 0
            delay == 0.1
            factor == 1
        }
    }
}
