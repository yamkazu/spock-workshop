package learning.datadriven

import learning.EmbeddedSpec
import spock.lang.Unroll

/**
 * データ変数へ直接代入することもできる
 */
class DataVariableAssignmentSpec extends EmbeddedSpec {

    def "データ変数に直接代入"() {
        expect:
        Math.max(x, y) == z

        where:
        x = 3
        y = Math.random() * 100
        z = x > y ? x : y

        // データ変数の代入しかない場合は1回のイテレーション
    }

    def "組み合わせて使用する"() {
        expect:
        Math.max(x, y) == z

        where:
        x | _
        1 | _
        7 | _
        0 | _

        y << [3, 4, 0]

        // データ変数はイテレーションごとに再評価される
        z = x > y ? x : y
    }

    @Unroll
    def "#age 才は #adultLabal"() {
        expect:
        new Person(age: age).isAdult() == adult

        where:
        age | adult
        19  | false
        20  | true

        adultLabal = adult ? "成人" : "未成年"
    }

    static class Person {
        Integer age

        Boolean isAdult() {
            age >= 20
        }
    }
}