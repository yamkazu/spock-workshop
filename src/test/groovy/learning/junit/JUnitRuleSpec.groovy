package learning.junit

import org.junit.ClassRule
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import spock.lang.Shared
import spock.lang.Specification

/**
 * {@link Rule}、{@link ClassRule}が使用できる。
 * <p>
 * Spockの独自機能で明示的に{@code new}しなくともデフォルトコンストラクタで
 * インスタンスが生成される。
 * <p>
 * コンストラクタの引数が必要な場合は、明示的に{@code new}する必要がある。
 */
class JUnitRuleSpec extends Specification {

    @Rule
    MyMethodRule myMethodRule1 // デフォルトコンストラクタで自動的にnewされる
    @Rule
    MyMethodRule myMethodRule2 = new MyMethodRule("MyMethodRule2") // 明示的にコンストラクタを指定して生成

    @ClassRule
    @Shared // @ClassRuleはstatic or @Sharedのフィールドのみに適用可能
    MyClassRule myClassRule1
    @ClassRule
    @Shared
    MyClassRule myClassRule2 = new MyClassRule("MyClassRule2")

    def "dummy1"() {
        expect:
        true
    }

    def "dummy2"() {
        expect:
        true
    }

    static class MyMethodRule implements TestRule {

        String message = "MyMethodRule1"

        MyMethodRule() {
        }

        MyMethodRule(String message) {
            this.message = message
        }

        @Override
        Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                void evaluate() {
                    println message
                    base.evaluate()
                }
            }
        }
    }

    static class MyClassRule implements TestRule {

        String message = "MyClassRule1"

        MyClassRule() {
        }

        MyClassRule(String message) {
            this.message = message
        }

        @Override
        Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                void evaluate() {
                    println message
                    base.evaluate()
                }
            }
        }
    }
}