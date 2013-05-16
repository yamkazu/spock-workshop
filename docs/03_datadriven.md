
データ駆動テスト
================

データ駆動テストとは
--------------------

テストのロジックとデータが分離されているテスト。

ロジックとデータが分離されていることにより以下が実現される。

* 同じテストロジックを複数書くとか不毛なことしなくてよい
* データの自動生成や外部データを読み込みが可能
* それぞれ簡単に変更できる


データテーブル
--------------

データ駆動テストをやるには、データテーブルを使うのが一番簡単。

```groovy
class Math extends Specification {
    def "2つの数値のうち大きいほう"(int a, int b, int c) { // このメソッドの引数をデータ変数という
        expect:
        Math.max(a, b) == c

        where:
        a | b | c  // ヘッダに対応するデータ変数を指定
        1 | 3 | 3  // 2行目以降がデータ
        7 | 4 | 4
        0 | 0 | 0
    }
}
```

### シンタックスの改善

以下のようにも書ける。

```groovy
def "シンタックスの改善"(/* メソッドの引数は省略可能*/) {
    expect:
    Math.max(x, y) == z

    where: "データの区切りに||を使用できる"
    x | y || z
    1 | 3 || 3
    7 | 4 || 7
    0 | 0 || 0
}
```

### 値がひとつの場合

`_`を使用する。

```groovy
def "値が一つの場合"() {
    expect:
    Math.max(x, x) == x

    where:
    x | _
    1 | _
    7 | _
    8 | _
    // ただし、この書き方よりはデータパイプを使用した書き方のほうがおすすめ
    // 不要な_は綺麗ではない
}
```

### 注意点

* `setup`メソッド、`cleanup`メソッドは繰り返しごとに呼ばれる
* データテーブルから、ローカル変数やインタンスフィールドにアクセスできない
* `@Share`または`static`フィールドにはアクセスできる

@Unroll
-------

どの繰り返しで失敗したかわからない場合は`@Unroll`を使う。

```groovy
@Unroll
def "2つの数値のうち大きいほう"() {
    expect:
    Math.max(x, y) == z

    where:
    x | y || z
    1 | 3 || 3
    7 | 4 || 4
    0 | 0 || 0
}
```

`@Unroll`を設定すると繰り返しごとにレポートを生成する。

```
2つの数値のうち大きいほう[0]   PASSED
2つの数値のうち大きいほう[1]   FAILED

Math.max(x, y) == z
     |   |  |  |  |
     7   7  4  |  4
               false

2つの数値のうち大きいほう[2]   PASSED
```

メソッド名でプレースホルダを使える。

```groovy
@Unroll
def "#x と #y で大きい方は #z"() {
    expect:
    Math.max(x, y) == z

    where:
    x | y || z
    1 | 3 || 3
    7 | 4 || 4
    0 | 0 || 0
}
```

以下のように出力される。

```
1 と 3 で大きい方は 3     PASSED
7 と 4 で大きい方は 4     FAILED

Math.max(x, y) == z
     |   |  |  |  |
     7   7  4  |  4
               false

0 と 0 で大きい方は 0     PASSED
```

アノテーションに指定してもよい。

```groovy
@Unroll("#x と #y で大きい方は #z")
def "アノテーションに指定してもよい"() {
    expect:
    Math.max(x, y) == z

    where:
    x | y || z
    1 | 3 || 3
    7 | 4 || 4
    0 | 0 || 0
}
```

プレースホルダに使えるのは以下のみ。

* プロパティ
* 引数なしのメソッド呼び出し

```groovy
@Unroll
def "#person.name.toUpperCase() の年齢は #person.age"() { ... }
```

以下のようには使えない。

```groovy
@Unroll
def "#person.name.split(' ')[1]" { ... }

@Unroll
def "#person.age / 2" { ... }
```

注意点として`@Unroll`を使うと劇的にテスト数が増える。
これは、場合によっては好ましくない。


データパイプ
------------

データテーブルはデータパイプのシンタックスシュガー。

```groovy
def "2つの数値のうち大きいほう"() {
    expect:
    Math.max(x, y) == z

    where:
    x | y || z
    1 | 3 || 3
    7 | 4 || 7
    0 | 0 || 0
}
```

をデータパイプで書き直すと以下になる。

```groovy
def "2つの数値のうち大きいほう"() {
    expect:
    Math.max(x, y) == z

    where: "データパイプは<<を使う"
    x << [1, 7, 0] // <<の右をデータプロバイダという
    y << [3, 4, 0]
    z << [3, 7, 0]
}
```

データ変数にデータプロバイダを接続すると覚えると`<<`が覚えやすい。


### データパイプの良いところ

* 値がひとつの場合はデータパイプのほうが使いやすい
* Groovyで繰り返しが可能なオブジェクトであればなんでも使える
    * 外部リソースの読み込みなど

### 値がひとつの場合

```groovy
def "値が一つの場合"() {
    expect:
    Math.max(x, x) == x

    where:
    x << [1, 7, 8]

    // データテーブルで書くと以下
    // x | _
    // 1 | _
    // 7 | _
    // 8 | _
}
```

### データプロバイダに使用できるオブジェクト

Groovyで繰り返しが可能なオブジェクトならなんでも使える。

```groovy
def "Groovyで繰り返し可能なオブジェクトがデータプロバイダとして使用できる"() {
    expect:
    x + y == z

    where:
    // 例えばCollection、Stringや、Iterable
    x << "abc"
    y << ["1", "2", "3"]
    z << new MyIterable()
}
class MyIterable implements Iterable {
    Iterator iterator() {
        ["a1", "b2", "c3"].iterator()
    }
}
```

外部リソースをデータプロバイダにする。

```groovy
def "データベースから読み込む"() {
    expect:
    Math.max(x, y) == z

    cleanup:
    sql.close()

    where:
    // []を使用するとGroovyのマルチ代入ライクに複数のデータ変数へ接続できる
    [x, y, z] << sql.rows("SELECT a, b, c FROM maxdata")
}
```

複数のデータ変数へ接続するさいに、いらない値は`_`で捨てる。

```groovy
[x, _, z] << sql.rows("SELECT a, b, c FROM maxdata")
```

データ変数への直接代入
----------------------

```groovy
def "データ変数に直接代入できる"() {
    expect:
    Math.max(x, y) == z

    where:
    x = 3
    y = Math.random() * 100
    z = x > y ? x : y
}
```

データテーブル、データパイプ、代入の組み合わせ
----------------------------------------------

```groovy
def "組み合わせて使用する"() {
    expect:
    Math.max(x, y) == z

    where:
    x | _
    1 | _
    7 | _
    0 | _

    y << [3, 4, 0]

    // 代入は繰り返しごとに再評価される
    z = x > y ? x : y
}
```

### プレースホルダとして代入を活用する

```groovy
@Unroll
def "#age 才は #adultLabel"() {
    expect:
    new Person(age: age).isAdult() == adult

    where:
    age | adult
    19  | false
    20  | true

    adultLabel = adult ? "成人" : "未成年"
}
```
