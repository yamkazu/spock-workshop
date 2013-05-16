
Spockの基礎
===========

スペッククラス
--------------

スペッククラスはJUnitのテストクラスに相当する。

```groovy
class HelloSpec extends Specification {
    ...
}
```

* 慣例的にクラス名のサフィックスを`Spec`にする
* `spock.lang.Specification`を継承する


フィーチャメソッド
------------------

フィーチャメソッドはJUnitのテストメソッドに相当する。

```groovy
class HelloSpec extends Specification {

    // 慣例的に文字列を使用
    def "これがフィーチャメソッド"() { ... }

}
```

フェーズとブロック
------------------

フィーチャメソッドの中身はブロックで構成される。
ブロックはフェーズに合わせて使用する。

### フェーズ

フィーチャメソッドは4つのフェーズから構成される。

```groovy
def "何かのフィーチャメソッド"() {
    // Setup(省略可能)
    ...
    // Stimulus
    ...
    // Response
    ...
    // Cleanup(省略可能)
    ...
}
```

### ブロック

フェーズに合わせてブロックが定義されている。

| ブロック    | フェーズ          |
| ----------- | ----------------- |
| setup/given | Setup             |
| when        | Stimulus          |
| then        | Response          |
| expect      | Stimulus+Response |
| cleanup     | Cleanup           |
| and         | -                 |
| where       | -                 |

[本家のwikiの図が分かりやすい](https://code.google.com/p/spock/wiki/SpockBasics)

#### 基本的な使い方

```groovy
def "when-thenパターン"() {
    setup: ...    // Setup
    when: ...     // Stimulus
    then: ...     // Response
    cleanup: ...  // Cleanup
}

def "expectパターン"() {
    setup: ...    // Setup
    expect: ...   // Stimulus+Response
    cleanup: ...  // Cleanup
}

def "setup/cleanupは省略できる"() {
    when: ...     // Stimulus
    then: ...     // Response
}

def "setup/cleanupは省略できる"() {
    expect: ...   // Stimulus+Response
}
```

#### 特殊な使い方

```groovy
// StimulusとResponseのセットは繰り返して使用できる
def "StimulusとResponseの繰り返し"() {
    when: ...     // Stimulus
    then: ...     // Response
    expect: ...   // Stimulus+Response
    when: ...     // Stimulus
    then: ...     // Response
    when: ...     // Stimulus
    then: ...     // Response
    expect: ...   // Stimulus+Response
}

// 特殊パターンでthenを繰り返して使用できる
// 詳細はモックサポートで説明
def "thenの繰り返し"() {
    when: ...     // Stimulus
    then: ...     // Response
    then: ...     // Response
    then: ...     // Response
}

// ただしexpectを繰り返すことはできない
// 以下はNG
def "expectの繰り返しはNG"() {
    expect: ...   // Stimulus+Response
    expect: ...   // Stimulus+Response
}
```

### ブロックとドキュメント

各ブロックにドキュメントを設定できる。

```groovy
setup: "ユーザテーブルにレコードが1件"
...
when: "ユーザの一覧を取得"
...
then: "1件取得できている"
...
```

#### and

`and:`を使用すると直前のブロックを連続して使用できる。

```groovy
setup: ...
and: ...   // ここはsetupブロック
and: ...   // ここはsetupブロック

when: ...
and: ...   // ここはwhenブロック
and: ...   // ここはwhenブロック

then: ...
and: ...   // ここはandブロック
and: ...   // ここはandブロック

expect: ...
and: ...   // ここはexpectブロック
and: ...   // ここはexpectブロック
```

`and:`の目的はドキュメンテーション。

```groovy
setup: "データベースの接続を開く"
...
and: "ユーザテーブルに須藤くんを追加"
...
when: "ユーザの一覧を取得"
...
then: "取得件数が1件"
...
and: "取得したユーザの名前が須藤くん"
...
```

各ブロックの詳細
----------------

### setup

`setup:`はフィーチャメソッドの前提条件を記述する。

```groovy
setup:
def stack = new Stack()
def elem = "push me"
when: ...
```

省略すると最初のStimulusブロックまでが暗黙的に`setup:`ブロックになる。

```groovy
def stack = new Stack()
def elem = "push me"
// ↑ここまで自動的にsetupブロックとなる
when: ...
```

`given:`を使用すると[BDD](http://d.hatena.ne.jp/digitalsoul/20090819/1250686015)のgiven-when-thenテンプレートに合わせて記述できる。

```groovy
given: "口座の残高が0"
...
when: "1000円入金"
...
then: "残高が1000円"
...
```

### when-then

`when:`ブロックは任意の実行コードを記述する。

```groovy
when:
stack.push(elem)
```

`then:`ブロックには以下を含めることができる。

* 変数宣言
* コンディション
* 例外コンディション
* インタラクション

#### 変数宣言

```groovy
then:
def stack = new Stack()
```

#### コンディション

コンディションは、期待する状態を[ブール式](http://groovy.codehaus.org/Groovy+Truth)で記述する。

```groovy
then:
!stack.empty
stack.size() == 1
stack.peek() == elem
```

トップレベル以外の式はコンディションとして認識されない。

```groovy
then:
[1, 2, 3].each {
    it > 1 // ネストしているとコンディションとして認識されない
}
```

##### 回避案

```groovy
then:
// トップレベルのコンディションで記述する
[1, 2, 3].any { it > 1 }

// 明示的なassert、この方法はthenブロック以外でも有効
[1, 2, 3].each { assert it > 1 }
```

コンディションが満たされなかった場合はPowerAssertライクに出力される。

```
Condition not satisfied:

stack.size() == 2
|     |      |
|     1      false
[push me]
```

そもそも、GroovyのPowerAssertはSpockから取り込まれた。

> With Power Asserts, initially developed in the Spock Framework, the output of the assert is now much nicer and provides a visual representation of the value of each sub-expressions of the expression being asserted.
>
> [via Groovy 1.7 release notes](http://docs.codehaus.org/display/GROOVY/Groovy+1.7+release+notes)

#### 例外コンディション

例外コンディションは`thrown`を使う。

```groovy
when:
stack.pop()

then:
thrown(EmptyStackException)
```

メッセージを検証する場合は変数に受ける。

```groovy
then:
def e = thrown(EmptyStackException)
e.message == "error message"
```

左辺で型を指定することもできる。

```groovy
then:
EmptyStackException e = thrown()
e.message == "error message"
```

例外が発生しないというコンディションを記述するには`notThrown`を使う。

```groovy
then:
notThrown(EmptyStackException)
```

特定の例外を指定しない`noExceptionThrown`を使うこともできる。

```groovy
then:
noExceptionThrown()
```

#### インタラクション

モックサポートで詳しく説明する。

### expect

`expect:`には以下を含むことができる。

* 変数宣言
* コンディション

関数的なメソッドをテストするには`expect:`が便利。

```groovy
expect:
Math.max(1, 2) == 2
```

### cleanup

`cleanup:`はフィクスチャのお掃除ブロック。

```groovy
setup:
def file = new File("/some/path")
file.createNewFile()

// ...

cleanup:
file?.delete()
```

`try-finally`のように必ず`cleanup:`が呼ばれる。

### where

`where:`はデータ駆動テストで説明する。


フィクスチャ
------------

### フィクスチャメソッド

JUnitと同じようなフィクスチャメソッドが使える。

```groovy
class HelloSpec extends Specification {

    // すべてのフィーチャメソッドの前に実行
    def setup() { ... }

    // すべてのフィーチャメソッドの後に実行
    def cleanup() { ... }

    // スペッククラスの最初に1度実行
    def setupSpec() { ... }

    // スペッククラスの最後に1度実行
    def cleanupSpec() { ... }
}
```

### フィールド

フィールド宣言で初期化するとフィーチャメソッドの実行ごとに初期化される。

```groovy
class HelloSpec extends Specification {

    // フィールド宣言と同時に初期化すると
    // setupメソッドの最初に初期化するのと同じ効果がある
    // つまりフィーチャメソッドの実行ごとに初期化される
    def stack = new Stack()
    ...
}
```

フィーチャメソッド間で値を共有したい場合は`@Shared`を使う。

```groovy
class HelloSpec extends Specification {

    @Shared
    def stack = new Stack()

    // @Sharedはstaticフィールドの動作と変わりがないが
    // フィーチャメソッド間で値を共有する場合は
    // 意図を明確にするために@Sharedを使用すべき
    // 逆に定数を宣言するときはstaticフィールドを使用すべき
    static final PI = 3.141592654
    ...
}
```

### 初期化順序

独自にベースとなるSpecificationクラスを作成している場合は初期化順序に注意。

```groovy
abstract class Base extends Specification {
    def base1 = "base1"
    def base2

    def setup() { base2 = "base2" }
}

class DerivedSpec extends Base {
    def derived1 = "derived1"
    def derived2

    def setup() { derived2 = "derived2" }
}
```

上記は`base1`、`derived1`、`base2`、`derived2`の順で初期化される。
