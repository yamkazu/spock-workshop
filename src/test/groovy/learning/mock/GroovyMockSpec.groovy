package learning.mock

import learning.EmbeddedSpec

/**
 * Groovyモックの使い方。
 * <p>
 * 非常に強力だが、これが必要になる前に設計を疑ったほうが良い。
 * <p>
 * Groovyモックは{@link spock.lang.MockingApi#GroovyMock()}、{@link spock.lang.MockingApi#GroovyStub()}、
 * {@link spock.lang.MockingApi#GroovySpy()}メソッドで作成する。
 */
class GroovyMockSpec extends EmbeddedSpec {

    def "動的メソッドのモック"() {
        setup:
        def person = GroovyMock(Person)

        when: "動的メソッドを呼び出す"
        person.say("hello")

        then: "動的メソッドのモッキング"
        1 * person.say("hello")
    }

    def "グローバルモックとモッキング"() {
        setup:
        // 変数で受けたglobalSpyはモッキング、スタビングを定義するためだけに使用する
        // このオブジェクトをコラボレータとして差し替えたりはしない
        def globalSpy = GroovySpy(Person, global: true)

        when:
        def name1 = new Person(name: "Graeme").name
        def name2 = new Person(name: "Burt").name

        then:
        2 * globalSpy.name

        and:
        name1 == "Graeme"
        name2 == "Burt"
    }

    def "グローバルモックとスタビング"() {
        setup:
        def globalSpy = GroovySpy(Person, global: true)
        globalSpy.name >> "mock"

        expect:
        new Person().name != "mock"
    }

    def "コンストラクタのモッキング"() {
        setup:
        GroovyMock(Person, global: true)

        when:
        new Person(name: "Graeme")
        new Person(name: "Burt")

        then:
        2 * new Person(_)
    }

    def "コンストラクタのスタビング"() {
        setup:
        GroovySpy(Person, global: true)
        new Person(_) >> new Person(name: "mock")

        expect:
        new Person(name: "Graeme").name == "mock"
        new Person(name: "Burt").name == "mock"
    }

    def "staticメソッドのモッキング"() {
        setup:
        GroovyMock(Math, global: true)

        when:
        Math.max(1, 2)
        Math.max(3, 4)

        then:
        2 * Math.max(_, _)
        0 * _
    }

    def "staticメソッドのスタビング"() {
        setup:
        GroovyMock(Math, global: true)
        Math.max(_, _) >> 100

        expect:
        Math.max(1, 2) == 100
        Math.max(3, 4) == 100
    }

    static class Person {
        String name
    }
}