package learning.mock

import learning.EmbeddedSpec

class StubbingSpec extends EmbeddedSpec {

    def subscriber = Mock(Subscriber)

    def "スタビングの基本"() {
        setup: "モッキングとの違いはレポンスジェネレータを>>で指定する"
        subscriber.receive(_) >> "ok"
//      |          |       |     |
//      |          |       |     レスポンスジェネレータ
//      |          |       引数制約(argument constraint)
//      |          メソッド制約(method constraint)
//      対象制約(target constraint)

        when:
        def answer = subscriber.receive("hello")

        then:
        answer == "ok"
    }

    def "固定の値を返す"() {
        setup: "固定の値を定義するには >> を使用する"
        subscriber.receive("message1") >> "ok"
        subscriber.receive("message2") >> "fail"

        expect:
        subscriber.receive("message1") == "ok"
        subscriber.receive("message2") == "fail"
    }

    def "呼び出し毎に値を変更する"() {
        setup: "シーケンシャルな値を定義するには >>> を使用する"
        subscriber.receive(_) >>> ["ok", "error", "error", "ok"]

        expect: "1回目の呼び出し"
        subscriber.receive("x") == "ok"

        and: "2、3回目の呼び出し"
        subscriber.receive("x") == "error"
        subscriber.receive("x") == "error"

        and: "4回目以降の呼び出し"
        subscriber.receive("x") == "ok"
        subscriber.receive("x") == "ok"
        subscriber.receive("x") == "ok"
        subscriber.receive("x") == "ok"
        // ...
    }

    def "動的に値を返す"() {
        when: "動的に値を返すにはクロージャを使用する"
        def subscriber1 = Mock(Subscriber)

        // クロージャの引数を1つ、かつ型の指定なしとすると、引数リストが渡される
        subscriber1.receive(_) >> { args -> args[0].size() > 3 ? "ok" : "fail" }

        then:
        subscriber1.receive("xxx") == "fail"
        subscriber1.receive("xxxx") == "ok"

        when: "型指定ありバージョン"
        def subscriber2 = Mock(Subscriber)

        // クロージャの引数を1つ以上、かつ型を指定すると、引数が自動的にマップされる
        subscriber2.receive(_) >> { String message -> message.size() > 3 ? "ok" : "fail" }

        then:
        subscriber1.receive("xxx") == "fail"
        subscriber1.receive("xxxx") == "ok"
    }

    def "例外を発生させる"() {
        setup: "クロージャを使用すれば任意のコードが実行できる"
        subscriber.receive(_) >> { throw new RuntimeException("例外をスロー") }

        when:
        subscriber.receive("hello")

        then:
        def e = thrown(RuntimeException)
        e.message == "例外をスロー"
    }

    def "固定値、シーケンシャル、動的な値の組み合わせ"() {
        setup:
        subscriber.receive(_) >>> ["ok", "fail", "ok"] >> { throw new InternalError() } >> "ok"

        expect: "初めに3回の呼び出しにok,fail,okを返す"
        subscriber.receive("hello") == "ok"
        subscriber.receive("hello") == "fail"
        subscriber.receive("hello") == "ok"

        when: "次の呼び出しに例外をスローする"
        subscriber.receive("hello")

        then:
        thrown(InternalError)

        expect: "5回目以降はokを返す"
        subscriber.receive("hello") == "ok"
        subscriber.receive("hello") == "ok"
        subscriber.receive("hello") == "ok"
        subscriber.receive("hello") == "ok"
    }

    def "モッキングとスタビングの組み合わせ"() {
        setup:
        // 必ずモッキングとスタビングは同時に行う必要がある
        // mockitoのように別々に宣言することはできない
        subscriber.receive(_) >> "ok"

        when:
        def answer = subscriber.receive("hello")

        then:
        answer == "ok"
    }
}
