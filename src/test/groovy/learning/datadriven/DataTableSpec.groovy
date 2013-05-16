package learning.datadriven

import learning.EmbeddedSpec
import org.spockframework.compiler.InvalidSpecCompileException

/**
 * データテーブルを使用したデータ駆動テスト
 */
class DataTableSpec extends EmbeddedSpec {

    def "2つの数字の最大値"(int x, int y, int z) {
        expect:
        Math.max(x, y) == z

        where: "以下をデータテーブルという"
        x | y | z // <- 1行目はデータ変数を定義したヘッダ
        1 | 3 | 3 // <- 2行目以降がデータ列となる
        7 | 4 | 7
        0 | 0 | 0
    }

    def "さらにシンタックスを改善"(/* メソッドの引数は省略可能*/) {
        expect:
        Math.max(x, y) == z

        where: "データの区切りに||を使用できる" // 視覚以上の効果はなし
        x | y || z
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }

    def "値が一つの場合は_を使用する"() {
        expect:
        Math.max(x, x) == x

        where:
        x | _
        1 | _
        7 | _
        8 | _
        // ただし値がひつの場合はデータパイプを使用した書き方がおすすめ
        // 不要な_は綺麗ではない
    }

    def "インスタンスフィールドにはアクセスできない"() {
        when:
        compiler.compileSpecBody """
        | def instanceField
        | 
        | def foo() {
        |   expect:
        |   true
        | 
        |   where:
        |   x             | y
        |   instanceField | 1
        | }
        """.stripMargin()

        then:
        def e = thrown(InvalidSpecCompileException)
        e.message =~ "Only @Shared and static fields may be accessed from here"
    }
}
