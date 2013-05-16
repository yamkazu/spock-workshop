package learning.mock

import learning.EmbeddedSpec

/**
 * モッキングする
 */
class MockingSpec extends EmbeddedSpec {

    def publisher = new Publisher()
    def subscriber = Mock(Subscriber)

    def setup() {
        publisher.subscribers << subscriber
    }

    def "インタラクションの各パート"() {
        when:
        publisher.send("hello")

        then: "インタラクションは 多重度、対象制約、メソッド制約、引数制約 の4つのパートからなる"
        1 * subscriber.receive("hello")
//      |   |          |       |
//      |   |          |       引数制約(argument constraint)
//      |   |          メソッド制約(method constraint)
//      |   対象制約(target constraint)
//      多重度(cardinality)
    }

    def "多重度の色々"() {
        when:
        5.times { publisher.send("hello") }

        then:
        // 多重度はメソッドの呼び出し回数を表す
        // これは固定の数値、またはGroovyの範囲(Range)が使用できる
        1 * subscriber.receive("hello")      // 1回呼ばれる
        0 * subscriber.receive("hello")      // 0回呼ばれる
        (1..3) * subscriber.receive("hello") // 1回から3回の間
        (1.._) * subscriber.receive("hello") // 1回以上
        (_..3) * subscriber.receive("hello") // 3回以下
        _ * subscriber.receive("hello")      // 任意の呼び出し回数(0を含む)
    }

    def "対象制約の色々"() {
        when:
        2.times { publisher.send("hello") }

        then:
        // 対象制約は呼び出し対象となるモックオブジェクトを表す
        1 * subscriber.receive("hello") // 'subscriber'への呼び出し
        1 * _.receive("hello")          // 任意のモックオブジェクトへの呼び出し
    }

    def "メソッド制約の色々"() {
        when:
        3.times { publisher.send("hello") }

        then:
        // メソッド制約は呼び出し対象となるメソッドを表す
        // 対象制約とほとんど同じだが/.../で正規表現による指定が可能
        1 * subscriber.receive("hello") // 'receive'のメソッド
        1 * subscriber._("hello")       // 任意のメソッド
        1 * subscriber./r.*e/("hello")  // 'r'で始まり'e'で終わるメソッド
    }

    def "引数制約の色々"() {
        when:
        6.times { publisher.send("hello") }
        publisher.send("not hello")

        then:
        1 * subscriber.receive("hello")           // "hello"に一致する
        1 * subscriber.receive(!"hello")          // "hello"以外
        0 * subscriber.receive()                  // 引数なし
        1 * subscriber.receive(_)                 // 任意の引数
        1 * subscriber.receive(* _)               // 任意の引数リスト
        1 * subscriber.receive(!null)             // null以外
        1 * subscriber.receive(_ as String)       // 任意のString
        1 * subscriber.receive({ it.size() > 3 }) // 引数のsize()が3以上
    }

    def "ワイルドカード的なインタラクションのショートカット"() {
        when:
        4.times { publisher.send("hello") }

        then:
        1 * subscriber._(* _)    // subscriberの任意のメソッド、任意の引数の呼び出し
        1 * subscriber._         // 上記のショートカット
        1 * _._                  // 任意のモックオブジェクトに対する任意の呼び出し
        1 * _                    // 上記のショートカット
    }

    def "インタラクションの宣言場所"() {
        setup: "whenブロックの前であればインタラクションを宣言できる"
        1 * subscriber.receive("hello")

        when:
        publisher.send("hello")

        then:
        noExceptionThrown()
    }

    def "モック生成時のインタクション宣言"() {
        setup:
        def subscriber1 = Mock(Subscriber) {
            1 * /*対象制約は含まれない*/ receive("hello") // <- クロージャからsubscriber1が参照できないため 
        }
        // 型宣言バージョン
        Subscriber subscriber2 = Mock() {
            1 * receive("hello")
        }

        when:
        subscriber1.receive("hello")
        subscriber2.receive("hello")

        then:
        noExceptionThrown()
    }

    def "Strictモッキング"() {
        when:
        publisher.send("hello")

        then:
        1 * subscriber.receive("hello") // `subscriber`の`receive`メソッドが1回呼ばれる

        // 最後に0 * _を宣言する点がポイントで、
        // これで宣言していないインタラクションが発生していないことを検証できる
        // (宣言していないメソッド呼び出しが0回)
        0 * _
    }

    def "呼び出し順序の確認"() {
        // Spockはthen:ブロック内で定義されたインタラクションが
        // 最終的に満たされていれれば正常にインタラクションが行われたものとして扱う
        when:
        1.times { publisher.send("goodbye") }
        2.times { publisher.send("hello") }

        then: "この中は最終的にすべてのインタラクションが満たされていれば呼び出し順序は関係ない"
        2 * subscriber.receive("hello")
        1 * subscriber.receive("goodbye")

        when:
        2.times { publisher.send("hello") }
        1.times { publisher.send("goodbye") }

        then: "呼び出し順序の確認を行うにはthenブロックを分割する"
        2 * subscriber.receive("hello")

        then:
        1 * subscriber.receive("goodbye")
    }


    def "インタラクションのグループ化"() {
        when:
        1.times { publisher.send("goodbye") }
        2.times { publisher.send("hello") }

        then:
        with(subscriber) {
            2 * receive("hello")
            1 * receive("goodbye")
        }
    }

    def "インタラクションはwhenブロックに移動される"() {
        // インタラクションは実行時にはwhenブロックの前で評価される
        // そのため、以下は
        //
        //   when:
        //   publisher.send("hello")
        //
        //   then:
        //   def message = "hello"
        //   1 * subscriber.receive(message)
        //
        // 実行時には以下になる
        // 
        //   1 * subscriber.receive(message)
        //  
        //   when:
        //   publisher.send("hello")
        //  
        //   then:
        //   def message = "hello"
        //
        // このため、thenブロックで宣言した変数をインタラクションで参照すると
        // MissingPropertyが発生する
        when: "これを回避するにはinteractionブロックを使用する"
        publisher.send("hello")

        then: "interactionを使用するとブロックごとwhenブロックの前に移動する"
        interaction {
            def message = "hello"
            1 * subscriber.receive(message)
        }
    }
}
