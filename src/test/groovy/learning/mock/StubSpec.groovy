package learning.mock

import learning.EmbeddedSpec
import org.spockframework.mock.MockDetector
import spock.lang.Unroll

/**
 * スタブオブジェクトの使い方
 */
class StubSpec extends EmbeddedSpec {

    def "Stubの基本"() {
        when:
        def subscriber = Stub(Subscriber)

        // Stubではスタビングのみ可能で、モッキングは使用できない
        // これは機能を制限することで、このモックの役割を明確にする
        subscriber.receive("hello") >> "ok"

        then:
        subscriber.receive("hello") == "ok"

        // Stubは可能な限りリアルオブジェクトをデフォルト値として返す
        subscriber.receive("bye") == ""
    }

    @Unroll("#methodの値は、mockの場合は#mockExpected、stubの場合は#stubExpected")
    def "MockとStubのデフォルト値の違い"() {
        // Stubは
        //  - プリミティブ型は、プリミティブ型のデフォルト値
        //  - 非プリミティブ数値（BigDecimalのような）の場合は、ゼロ
        //  - 数字以外の値は空やダミーオブジェクト
        // を返す
        given:
        def mock = Mock(TestInterface)
        def stub = Stub(TestInterface)

        expect:
        mock[method] == mockExpected
        stub[method] == stubExpected

        where:
        method    | mockExpected | stubExpected
        'byte'    | 0            | 0
        'short'   | 0            | 0
        'int'     | 0            | 0
        'long'    | 0            | 0
        'float'   | 0            | 0
        'double'  | 0            | 0
        'boolean' | false        | false
        'char'    | 0            | 0

        and:
        'byteWrapper'    | null | 0
        'shortWrapper'   | null | 0
        'intWrapper'     | null | 0
        'longWrapper'    | null | 0
        'floatWrapper'   | null | 0
        'doubleWrapper'  | null | 0
        'booleanWrapper' | null | false
        'charWrapper'    | null | 0

        and:
        'bigInteger' | null | BigInteger.ZERO
        'bigDecimal' | null | BigDecimal.ZERO

        and:
        'charSequence' | null | ""
        'string'       | null | ""
        'GString'      | null | ""

        and:
        'primitiveArray' | null | [] as int[]
        'interfaceArray' | null | [] as IPerson[]
        'classArray'     | null | [] as Person[]

        and:
        'iterable'   | null | []
        'collection' | null | []
        'queue'      | null | []
        'list'       | null | []
        'set'        | null | [] as Set
        'map'        | null | [:]
        'sortedSet'  | null | [] as Set
        'sortedMap'  | null | [:]
    }

    def "インタフェースが戻り値のメソッドの場合はそのインタフェースのstubを返す"() {
        given:
        def mock = Mock(TestInterface)
        def stub = Stub(TestInterface)

        expect: "mockの場合はnull"
        mock.unknownInterface == null

        and: "stubの場合はそのインタフェースのstubを返す"
        with(stub.unknownInterface) {
            new MockDetector().isMock(it)
            name == ""
            age == 0
            children == []
        }
    }

    def "デフォルトコンストラクタがあるクラスが戻り値の場合はそのクラスのインスタンスを返す"() {
        given:
        def mock = Mock(TestInterface)
        def stub = Stub(TestInterface)

        expect: "mockの場合はnull"
        mock.unknownClassWithDefaultCtor == null

        and: "stubの場合はそのクラスの本物(モックではない)のインスタンスを返す"
        with(stub.unknownClassWithDefaultCtor) {
            !new MockDetector().isMock(it)
            name == "default"
            age == 0
            children == null
        }
    }

    def "デフォルトコンストラクタがないクラスが戻り値の場合はそのクラスのstubを返す"() {
        given:
        def mock = Mock(TestInterface)
        def stub = Stub(TestInterface)

        expect: "mockの場合はnull"
        mock.unknownClassWithoutDefaultCtor == null

        and: "stubの場合はそのクラスのstubを返す"
        with(stub.unknownClassWithoutDefaultCtor) {
            new MockDetector().isMock(it)
            name == ""
            age == 0
            children == []
        }
    }

    static interface TestInterface {
        byte getByte()

        short getShort()

        int getInt()

        long getLong()

        float getFloat()

        double getDouble()

        boolean getBoolean()

        char getChar()

        Byte getByteWrapper()

        Short getShortWrapper()

        Integer getIntWrapper()

        Long getLongWrapper()

        Float getFloatWrapper()

        Double getDoubleWrapper()

        Boolean getBooleanWrapper()

        Character getCharWrapper()

        BigInteger getBigInteger()

        BigDecimal getBigDecimal()

        CharSequence getCharSequence()

        String getString()

        GString getGString()

        int[] getPrimitiveArray()

        IPerson[] getInterfaceArray()

        Person[] getClassArray()

        Iterable getIterable()

        Collection getCollection()

        Queue getQueue()

        List getList()

        Set getSet()

        Map getMap()

        SortedSet getSortedSet()

        SortedMap getSortedMap()

        IPerson getUnknownInterface()

        Person getUnknownClassWithDefaultCtor()

        ImmutablePerson getUnknownClassWithoutDefaultCtor()
    }

    static interface IPerson {
        String getName()

        int getAge()

        List<String> getChildren()
    }

    static class Person implements IPerson {
        String name = "default"
        int age
        List<String> children
    }

    static class ImmutablePerson extends Person {
        ImmutablePerson(String name, int age, List<String> children) {
            this.name = name
            this.age = age
            this.children = children
        }
    }
}
