package learning.basic

import learning.EmbeddedSpec
import spock.lang.Shared

/**
 * フィクスチャの構築方法
 */
class FixtureSpec extends EmbeddedSpec {

    // フィールドを使用してフィクスチャを構築できる
    // フィーチャメソッドの実行毎に初期化される
    Stack stack = new Stack()

    // フィーチャメソッド間でフィクスチャを共有したい場合は
    // @Sharedアノテーションを使用する
    @Shared
    Stack globalSatck = new Stack()

    // staticを使用しても@Sharedと同じ効果があるが、
    // 読み手に意図を伝えるためにstaticは定数宣言にのみ使用すること
    static final PI = 3.141592654

    // スペッククラスの最初に1度実行
    def setupSpec() {
    }

    // スペッククラスの最後に1度実行
    def cleanupSpec() {
    }

    // すべてのフィーチャーメソッドの前に実行
    def setup() {
        log = []
    }

    // すべてのフィーチャーメソッドの後に実行
    def cleanup() {
    }

    def "フィクスチャの初期化順序"() {
        when:
        runner.runWithImports """
        | abstract class Base extends Specification {
        |   def getLog() { learning.basic.FixtureSpec.log }
        |
        |   def baseField = new Object(){{ learning.basic.FixtureSpec.log << "baseField" }}
        |   def setup() { log << "baseSetup" }
        |   def cleanup() { log << "baseCleanup" }
        |   def setupSpec() { log << "baseSetupSpec" }
        |   def cleanupSpec() { log << "baseCleanupSpec" }
        | }
        |
        | class ChildSpec extends Base {
        |   def getLog() { learning.basic.FixtureSpec.log }
        |
        |   def childFiled = new Object(){{ learning.basic.FixtureSpec.log << "childField" }}
        |   def setup() { log << "childSetup" }
        |   def cleanup() { log << "childCleanup" }
        |   def setupSpec() { log << "childSetupSpec" }
        |   def cleanupSpec() { log << "childCleanupSpec" }
        |
        |   def feature() { expect: true }
        | }
        """.stripMargin()

        then:
        log == [
            "baseSetupSpec",
            "childSetupSpec",
            "baseField",
            "childField",
            "baseSetup",
            "childSetup",
            "childCleanup",
            "baseCleanup",
            "childCleanupSpec",
            "baseCleanupSpec",
        ]
    }

    static log
}
