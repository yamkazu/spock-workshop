package learning.extension

import org.spockframework.runtime.SpockTimeoutError
import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Timeout

import java.util.concurrent.TimeUnit

/**
 * {@link Timeout}はフィーチャメソッドのタイムアウト値を指定できる。
 * <p>
 * このタイムアウト値を超過した場合は{@link org.spockframework.runtime.SpockTimeoutError}が発生する。
 */
class TimeoutSpec extends Specification {

    @Timeout(1)
    def "1秒以内に終わる"() {
        expect: Thread.sleep 500
    }

    @FailsWith(SpockTimeoutError)
    @Timeout(1)
    def "1秒以内に終わらない"() {
        expect: Thread.sleep 1100
    }

    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    def "500ミリ秒以内に終わる"() {
        expect: Thread.sleep 250
    }

    @FailsWith(SpockTimeoutError)
    @Timeout(value = 250, unit = TimeUnit.MILLISECONDS)
    def "500ミリ秒以内に終わらない"() {
        expect: Thread.sleep 300
    }
}
