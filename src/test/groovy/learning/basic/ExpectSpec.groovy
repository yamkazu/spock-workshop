package learning.basic

import spock.lang.Specification

/**
 * {@code expect:}ブロックの使い方。
 */
class ExpectSpec extends Specification {

    def "expectの使い方"() {
        expect:
        Math.max(1, 2) == 2
    }
}