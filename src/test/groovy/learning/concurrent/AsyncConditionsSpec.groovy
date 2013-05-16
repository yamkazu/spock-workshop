package learning.concurrent

import org.spockframework.runtime.SpockTimeoutError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

/**
 * {@link AsyncConditions}を使うと、指定した回数
 * {@link AsyncConditions#evaluate(java.lang.Runnable)}が呼び出されるまで
 * {@link AsyncConditions#await()}で待ち合わせできる。
 */
class AsyncConditionsSpec extends Specification {

    def "AsyncConditionsの基本的な使い方"() {
        setup:
        // デフォルトコンストラクタでAsyncConditionsを生成すると
        // evaluateが1回実行されるのをawait()で待ち合わせる
        def conditions = new AsyncConditions()

        when:
        Thread.start {
            conditions.evaluate {
                assert true
            }
        }

        then: "evaluateが1回実行されるのを待ち合わせ"
        conditions.await() // デフォルトでタイムアウトは1秒 
    }

    @FailsWith(SpockTimeoutError)
    def "タイムアウトになるとSpockTimeoutErrorが発生する"() {
        setup:
        def conditions = new AsyncConditions()

        when:
        Thread.start {
            conditions.evaluate {
                Thread.sleep(1100) // 1秒以上スリープ
                assert true
            }
        }

        then:
        conditions.await() // 1秒待ってタイムアウト
    }

    def "awaitにタイムアウト値を設定する"() {
        setup:
        def conditions = new AsyncConditions()

        when:
        Thread.start {
            conditions.evaluate {
                assert true
            }
        }

        then: "タイムアウト値を2秒に設定して待ち合わせ"
        conditions.await(2)
    }

    def "evaluateの数を指定する"() {
        setup: "evaluateが3回実行されるのをawait()で待ち合わせる"
        def conditions = new AsyncConditions(3) // 明示的に回数を指定

        when:
        Thread.start {
            conditions.evaluate {
                assert true
            }
            conditions.evaluate {
                assert true
            }
        }

        and:
        Thread.start {
            conditions.evaluate {
                assert true
            }
        }

        then:
        conditions.await()
    }
}
