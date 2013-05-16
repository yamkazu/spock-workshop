package learning.extension

import spock.lang.FailsWith
import spock.lang.Specification

/**
 * {@link FailsWith}を使うとJUnitのように発生する例外を指定できる
 */
class FailWithSpec extends Specification {

    @FailsWith(IndexOutOfBoundsException)
    def "FailsWithの基本"() {
        expect:
        [].get(0) // IndexOutOfBoundsExceptionがスローされる
    }

    @FailsWith(value = NullPointerException, reason = "ぬるぽ")
    def "reasonに理由を書ける"() {
        expect:
        null.ぬるぽですよ
    }
}

/**
 * スペッククラスに付与することもできる
 */
@FailsWith(NullPointerException)
class FailWithOnSpec extends Specification {

    def "ぬるぽで終了する"() {
        expect:
        null.ぬるぽですよ
    }

    @FailsWith(IndexOutOfBoundsException)
    def "スペッククラスに付与しつつフィーチャメソッドでオーバライドもOK"() {
        expect:
        [].get(0)
    }
}
