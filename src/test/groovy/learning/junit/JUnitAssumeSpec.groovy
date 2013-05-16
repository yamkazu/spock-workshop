package learning.junit

import org.junit.Before
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assume.assumeThat

/**
 * {@link org.junit.Assume}を使って前提条件が満たされない場合は実行しない
 */
class JUnitAssumeSpec extends Specification {

    def "前提条件が満たされない場合はフィーチャメソッドが実行されない"() {
        assumeThat 1, equalTo(2)

        expect:
        false
    }
}

/**
 * {@code setup}メソッドで使用
 */
class JUnitAssumeWithSetupMethodSpec extends Specification {

    def setup() {
        assumeThat 1, equalTo(2)
    }

    def "前提条件が満たされない場合はフィーチャメソッドが実行されない"() {
        expect:
        false
    }
}

/**
 * {@link Before}アノテーションが付与されたメソッドで使用
 */
class JUnitAssumeWithBeforeMethodSpec extends Specification {

    @Before
    void before() {
        assumeThat 1, equalTo(2)
    }

    def "前提条件が満たされない場合はフィーチャメソッドが実行されない"() {
        expect:
        false
    }
}

