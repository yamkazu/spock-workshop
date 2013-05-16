package learning.datadriven

import groovy.sql.Sql
import learning.EmbeddedSpec
import spock.lang.Shared

/**
 * データパイプを使用すると外部リソースを利用した
 * データ駆動テストが可能になる
 */
class DataPipesSpec extends EmbeddedSpec {

    @Shared
    def sql

    def setupSpec() {
        setupDb()
    }

    def "データパイプの基本"() {
        expect:
        Math.max(x, y) == z

        where:
        // データ変数にデータプロバイダを接続する
        x << [1, 7, 0]
        y << [3, 4, 0]
        z << [3, 7, 0]

        // データテーブルで以下を定義した場合と同じ
        //  x | y || z
        //  1 | 3 || 3
        //  7 | 4 || 7
        //  0 | 0 || 0
    }

    def "Groovyで繰り返し可能であればデータプロバイダとして使用できる"() {
        expect:
        x + y == z

        where:
        // Collection、Stringや、Iterableが使用できる
        x << "abc"
        y << ["1", "2", "3"]
        z << new MyIterable()
    }

    static class MyIterable implements Iterable {
        Iterator iterator() {
            ["a1", "b2", "c3"].iterator()
        }
    }

    def "外部リソースをデータプロバイダとして使用する"() {
        expect:
        Math.max(x, y) == z

        where:
        // []を使用するとGroovyのマルチ代入ライクに複数のデータ変数へ接続できる
        [x, y, z] << sql.rows("SELECT a, b, c FROM maxdata")
    }

    def "使用しない値は_で捨てる"() {
        expect:
        Math.max(x, x) == x

        where:
        [x, _, _] << sql.rows("SELECT a, b, c FROM maxdata")
    }

    private setupDb() {
        sql = Sql.newInstance "jdbc:h2:mem:", "org.h2.Driver"
        sql.execute("CREATE TABLE maxdata (a INTEGER, b INTEGER, c INTEGER)")

        def metadata = sql.dataSet("maxdata")
        metadata.add(a: 3, b: 5, c: 5)
        metadata.add(a: 7, b: 0, c: 7)
        metadata.add(a: 0, b: 0, c: 0)
    }

}
