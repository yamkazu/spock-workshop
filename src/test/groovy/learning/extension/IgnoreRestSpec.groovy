package learning.extension

import spock.lang.IgnoreRest
import spock.lang.Specification

/**
 * {@link IgnoreRest}を付与すると付与したフィーチャメソッドだけ実行できる
 */
class IgnoreRestSpec extends Specification {

    def "このフィーチャメソッドは実行されない"() {
        expect: false
    }

    @IgnoreRest
    def "このフィーチャメソッドは実行される"() {
        expect: true
    }

    @IgnoreRest
    def "複数のフィーチャメソッドに@IgnoreRestを設定できる"() {
        expect: true
    }
}
