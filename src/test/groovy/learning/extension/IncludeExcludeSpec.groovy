package learning.extension;

import spock.lang.Specification

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * {@code SpockConfig.groovy}を指定することで、実行するフィーチャメソッドを指定できる。
 * <p>
 * デフォルトではクラスパスの{@code SpockConfig.groovy}、
 * または{@code $HOME/.spock/SpockConfig.groovy}が使用される。
 * <p>
 * システムプロパティの{@code spock.configuration}に値を設定することで読み込むファイルを変更することも可能。
 * <p>
 * 例:
 * <pre><code>
 * -Dspock.configuration=MySpockConfig.groovy
 * </code></pre>
 */
@Server
class IncludeExcludeSpec extends Specification {

    def "server"() {
        expect:
        true
    }

    @Slow
    def "slow"() {
        expect:
        true
    }

    @Fast
    def "fast"() {
        expect:
        true
    }
}


@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Fast {}

@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Slow {}

@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Server {}
