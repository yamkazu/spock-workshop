package learning.mop

import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

/**
 * {@link ConfineMetaClassChanges}を使うと{@code metaClass}の状態を
 * リストアできる。
 * <p>
 * フィーチャメソッドに{@link ConfineMetaClassChanges}を付与すると、
 * {@code setup}メソッドの後の{@code metaClass}状態に
 * {@code cleanup}メソッドの前でリストアされる。
 */
class ConfineMetaClassChangesSpec extends Specification {

    def setup() {
        assert !String.metaClass.methods.find { it.name == "duplicate" }
        assert !Integer.metaClass.methods.find { it.name == "square" }
    }

    @ConfineMetaClassChanges([String, Integer])
    def "メタクラスを操作する"() {
        setup:
        String.metaClass.duplicate = { -> delegate * 2 }
        Integer.metaClass.square = { -> delegate * delegate }

        expect:
        "hello".duplicate() == "hellohello"
        2.square() == 4
    }

    def cleanup() {
        assert !String.metaClass.methods.find { it.name == "duplicate" }
        assert !Integer.metaClass.methods.find { it.name == "square" }
    }
}

/**
 * スペッククラスに{@link ConfineMetaClassChanges}を付与すると、
 * {@code setupSpec}メソッドの前の{@code metaClass}状態に
 * {@code cleanupSpec}メソッドの後でリストアされる。
 */
@ConfineMetaClassChanges(String)
class ConfineMetaClassChangesWithClassSpec extends Specification {

    def setupSpec() {
        assert !String.metaClass.methods.find { it.name == "duplicate" }
    }

    def setup() {
        assert !String.metaClass.methods.find { it.name == "duplicate" }
    }

    def "メタクラスを操作する"() {
        setup:
        String.metaClass.duplicate = {-> delegate * 2 }

        expect:
        "hello".duplicate() == "hellohello"
    }

    def cleanup() {
        assert String.metaClass.methods.find { it.name == "duplicate" }
    }

    def cleanupSpec() {
        assert String.metaClass.methods.find { it.name == "duplicate" }

        // この後消える
    }
}
