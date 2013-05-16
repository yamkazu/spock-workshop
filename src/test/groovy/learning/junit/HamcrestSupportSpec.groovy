package learning.junit

import spock.lang.Specification

import static org.hamcrest.CoreMatchers.*
import static spock.util.matcher.HamcrestSupport.expect
import static spock.util.matcher.HamcrestSupport.that

/**
 * {@link spock.util.matcher.HamcrestSupport#that(java.lang.Object, org.hamcrest.Matcher)} 、
 * {@link spock.util.matcher.HamcrestSupport#expect(java.lang.Object, org.hamcrest.Matcher)}
 * を使ってHamcrestのmatcherを使用できる
 */
class HamcrestSupportSpec extends Specification {

    def "thatの使い方"() {
        def x = 10

        expect:
        that x, is(10)
        that x, equalTo(10)
    }

    def "thatのエイリアスのexpect"() {
        def x = 10

        expect:
        expect x, is(10)
    }

    def "that、expectを記述せずに使用することもできる"() {
        def x = 10

        expect:
        x is(10)
        x is(not(9))
    }
}
