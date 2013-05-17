package learning.mock

import learning.EmbeddedSpec

/**
 * スパイオブジェクトの使い方
 */
class SpySpec extends EmbeddedSpec {

    def "Spyの基本的な使い方"() {
        setup: "Spyを使用して、クラスとコンストラクタの引数を指定する"
        def person = Spy(Person, constructorArgs: ["Burt"]) // 必ず具象クラスを指定する必要がある

        when: "メソッド呼び出しは本物オブジェクトに委譲される"
        def name = person.name

        then: "インタラクションが定義できる"
        1 * person.name
        name == "Burt"
    }

    def "constructorArgsを指定しない場合はデフォルトコンストラクタが使用される"() {
        setup:
        def person = Spy(Person)

        when:
        def name = person.name

        then:
        1 * person.name
        name == "anonymous"
    }

    def "スタビングを行うと本物オブジェクトを呼ばない"() {
        setup:
        def person = Spy(Person)
        person.name >> "Burt"

        when:
        def name = person.name

        then:
        name == "Burt"
    }

    def "callRealMethodを使ってスタビングしつつ本物オブジェクトを呼び出す"() {
        setup:
        def person = Spy(Person)
        person.setName(_) >> {
            // callRealMethod()で本物オブジェクトのメソッドが呼ばれる
            // 引数は自動的に設定されることに注意(明示的に指定する必要がない)
            callRealMethod()

            // 自由にコードが書ける
            // ...
        }

        when:
        person.name = "spock"

        then:
        person.name == "spock"
    }

    def "callRealMethodWithArgsを使用すると引数のコントロールができる"() {
        setup:
        def person = Spy(Person)
        person.setName(_) >> {
            // callRealMethodWithArgsを使用するとメソッド呼び出しの引数がコントロールできる
            callRealMethodWithArgs("foo")

            // 自由にコードが書ける
            // ...
        }

        when:
        person.name = "bar"

        then:
        person.name == "foo"

    }

    static class Person {
        String name

        Person() {
            this("anonymous")
        }

        Person(String name) {
            this.name = name
        }
    }
}
