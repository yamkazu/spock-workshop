package learning.basic;

import spock.lang.Specification

/**
 * {@code old}の使い方
 */
class OldValueSpec extends Specification {

    def "oldを使うとwhenブロック前の値を参照できる"() {
        setup:
        def list = [1, 2, 3]

        when:
        list << 4

        then:
        list.size() == old(list.size()) + 1
    }

    def "when-thenを続けて使用した場合"() {
        setup:
        def list = [1, 2, 3]

        when:
        list << 4

        then:
        list.size() == old(list.size()) + 1

        when:
        list << 5

        then: "直近のwhenブロック前の値になる"
        list.size() == old(list.size()) + 1
    }
}
