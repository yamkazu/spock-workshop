package learning.basic

import learning.EmbeddedSpec

/**
 * 例外のコンディションの使い方
 */
class ExceptionSpec extends EmbeddedSpec {

    def "例外コンディションの基本"() {
        when:
        "".charAt(0)

        then:
        thrown(IndexOutOfBoundsException)
    }

    def "メッセージなどを検証したい場合は一度変数に落とす"() {
        when:
        "".charAt(0)

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "String index out of range: 0"
    }

    def "IDEのサポートを受けやすいよう左辺で例外の型を宣言できる"() {
        when:
        "".charAt(0)

        then:
        IndexOutOfBoundsException e = thrown()
        e.message == "String index out of range: 0"
    }

    def "特定の例外が発生しないというコンディションを記述できる"() {
        when:
        "a".charAt(0)

        then:
        notThrown(IndexOutOfBoundsException)
    }

    def "例外が発生しないというコンディションを記述できる"() {
        when:
        "a".charAt(0)

        then:
        noExceptionThrown()
    }
}
