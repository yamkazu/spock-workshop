package learning.mop

import spock.lang.Specification
import spock.util.mop.Use

class UseSpec extends Specification {

    @Use(StringExtension)
    def "拡張モジュールを使う"() {
        expect:
        "groovy".duplicate() == "groovygroovy"
    }

    @Use(IntegerCategory)
    def "カテゴリークラスを使う"() {
        expect:
        7.square() == 49
    }

    @Use([StringExtension, IntegerCategory])
    def "複数指定する"() {
        expect:
        "groovy".duplicate() == "groovygroovy"
        7.square() == 49
    }
}

class StringExtension {
    static String duplicate(String self) { self * 2 }
}

@Category(Integer)
class IntegerCategory {
    Integer square() { this * this }
}
