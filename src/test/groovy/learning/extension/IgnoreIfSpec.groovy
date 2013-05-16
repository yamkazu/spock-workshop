package learning.extension

import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * {@link spock.lang.IgnoreIf}は引数のクロージャの実行結果が
 * {@code ture}の場合、そのフィーチャメソッドを無視する
 *
 * <ul>
 *     <li>env - System.getenv()のショートカット
 *     <li>properties - System.getProperties()のショートカット
 *     <li>javaVersion - Javaのバージョン
 * </ul>
 */
class IgnoreIfSpec extends Specification {

    @IgnoreIf({ true })
    def "trueなので無視される"() {
        expect: false
    }

    @IgnoreIf({ false })
    def "falseなので無視されない"() {
        expect: true
    }

    @IgnoreIf({ 1 < 2 })
    def "1 < 2 はtrueなので無視される"() {
        expect: false
    }

    @IgnoreIf({ 1 > 2 })
    def "1 > 2 はfalseなので実行される"() {
        expect: true
    }

    @IgnoreIf({
        def a = 1
        def b = 1
        a + b == 2
    })
    def "closureをcallしているだけなので複数行書いても良い"() {
        expect: false
    }

    // v0.7の時点で以下で動作しているが
    // 次バージョンで参照方法が変わるかもしれないので注意

    @IgnoreIf({ javaVersion > 1.4 })
    def "javaVersionでJVMのバージョンが参照できる"() {
        expect: true
    }

    @IgnoreIf({ env.LANG == 'C' })
    def "envがSystem.getenv()のショートカットになっている"() {
        expect: true
    }

    @IgnoreIf({ properties["os.name"] == 'Mac OS X' })
    def "propertiesがSystem.getProperties()のショートカットになっている"() {
        expect: true
    }
}
