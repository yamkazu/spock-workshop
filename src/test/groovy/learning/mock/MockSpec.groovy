package learning.mock

import learning.EmbeddedSpec

/**
 * モックの基本的な使い方
 */
class MockSpec extends EmbeddedSpec {

    def publisher = new Publisher()

    def "モックの基本的な使い方"() {
        setup: "Mockメソッドで作成する"
        def subscriber1 = Mock(Subscriber)

        and: "左辺で型指定をすることも可能"
        Subscriber subscriber2 = Mock()

        and: "仕様対象にモックを設定する"
        publisher.subscribers << subscriber1 << subscriber2

        when:
        publisher.send("hello")

        then: "subscriberで1回helloを受信すること"
        1 * subscriber1.receive("hello") // <- コンディションという
        1 * subscriber2.receive("hello")
    }

    def "モックのデフォルト動作"() {
        when:
        def mock = Mock(Mockable)

        then: "booleanはfalse"
        mock.getBoolean() == false

        and: "数値のプリミティブ型は0"
        mock.getByte() == 0
        mock.getShort() == 0
        mock.getInt() == 0
        mock.getLong() == 0
        mock.getFloat() == 0f
        mock.getDouble() == 0d

        and: "オブジェクトはnull"
        mock.getObject() == null
        mock.getVoid() == null
        mock.getDynamic() == null
    }

    interface Mockable {
        boolean getBoolean()

        byte getByte()

        short getShort()

        int getInt()

        long getLong()

        float getFloat()

        double getDouble()

        Object getObject()

        void getVoid()

        def getDynamic()
    }
}