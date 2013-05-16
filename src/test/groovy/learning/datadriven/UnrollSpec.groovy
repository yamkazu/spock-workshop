package learning.datadriven

import learning.EmbeddedSpec
import spock.lang.Unroll

/**
 * {@link Unroll}を使うと、それぞれのイテレーションを別々の
 * レポートとして出力できる
 */
class UnrollSpec extends EmbeddedSpec {

    @Unroll
    def "Unrollの基本的な使い方"() {
        setup:
        def person = new Person(name: name, age: age)

        expect:
        person.isValid() == valid

        where:
        name   | age  | valid
        "Jeff" | null | false
        "Jeff" | -1   | false
        "Jeff" | 0    | true
        "Jeff" | 30   | true
        null   | 45   | false
        null   | -1   | false
    }

    @Unroll
    def "名前が #name で年齢が #age の場合 #valid"() {
        setup:
        def person = new Person(name: name, age: age)

        expect:
        person.isValid() == valid

        where:
        name   | age  | valid
        "Jeff" | null | false
        "Jeff" | -1   | false
        "Jeff" | 0    | true
        "Jeff" | 30   | true
        null   | 45   | false
        null   | -1   | false
    }

    @Unroll("名前が #name で年齢が #age の場合 #valid")
    def "アノテーションの中でも宣言できる"() {
        setup:
        def person = new Person(name: name, age: age)

        expect:
        person.isValid() == valid

        where:
        name   | age  | valid
        "Jeff" | null | false
        "Jeff" | -1   | false
        "Jeff" | 0    | true
        "Jeff" | 30   | true
        null   | 45   | false
        null   | -1   | false
    }

    @Unroll("#person.name.toUpperCase() の年齢は #person.age")
    def "プレースホルダには引数なしのメソッド呼び出しとプロパティアクセスが使用できる"() {
        expect:
        person.isValid()

        where:
        person                              | _
        new Person(name: "Jeff", age: 23)   | _
        new Person(name: "Burt", age: 43)   | _
        new Person(name: "Graeme", age: 32) | _
    }

    static class Person {
        String name
        Integer age

        Boolean isValid() {
            name && age >= 0
        }
    }
}
