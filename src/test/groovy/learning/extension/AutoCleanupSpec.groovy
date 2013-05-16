package learning.extension

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

/**
 * {@link AutoCleanup}を使うと自動的に後処理を呼び出せる
 */
class AutoCleanupSpec extends Specification {

    // 自動的にcloseが呼ばされる
    @AutoCleanup
    def closable = new MyClosable()

    // 明示的に呼び出すメソッドを指定
    @AutoCleanup("dispose")
    def disposable = new MyDisposable()

    // quietをtrueにすると例外を握りつぶす
    @AutoCleanup(quiet = true)
    def boom = new Boom()

    // @Sharedが設定されているプロパティはスペックの
    // 最後に一度だけ後処理が呼ばれる
    @Shared
    @AutoCleanup
    def closableWithShared = new MyClosable()

    def "dummy1"() { expect: println "dummy1" }

    def "dummy2"() { expect: println "dummy2" }

    def cleanup() {
        println "cleanup"
    }

    def cleanupSpec() {
        println "cleanupSpec"
    }

    static class MyClosable {
        def close() {
            println "closed"
        }
    }

    static class MyDisposable {
        def dispose() {
            println "disposed"
        }
    }

    static class Boom {
        def close() {
            println "boomed"
            throw new RuntimeException()
        }
    }
}
