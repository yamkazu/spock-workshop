
Spockのモックサポート
=====================

* 自身でモック機能を備えている
* モックの生成にはJDKの動的プロキシとかCGLIBを使っている
    * GroovyのMOPを使っていない
    * Javaのコードでも問題なく使える

モックオブジェクトの生成
------------------------

`MockingApi#Mock`メソッドで生成する。

```groovy
def subscriber = Mock(Subscriber)
```

左辺で型指定してもよい。

```groovy
Subscriber subscriber = Mock()
```

モッキング
----------

モッキングとはインタラクションを宣言する活動のこと。

```groovy
def "subscriberでメッセージが受信されるべき"() {
    when:
    publisher.send("hello")

    then:
    1 * subscriber.receive("hello") // このインタラクションを宣言する活動をモッキングという
}
```

インタラクション
----------------

インタラクションは4つのパートからなる。

```groovy
1 * subscriber.receive("hello")
|   |          |       |
|   |          |       引数制約
|   |          メソッド制約
|   対象制約
多重度
```

### 多重度

多重度は呼び出しを期待するメソッドの呼び出し回数。

```groovy
1 * subscriber.receive("hello")      // 1回呼ばれる
0 * subscriber.receive("hello")      // 0回呼ばれる
(1..3) * subscriber.receive("hello") // 1回から3回の間
(1.._) * subscriber.receive("hello") // 1回以上
(_..3) * subscriber.receive("hello") // 3回以下
_ * subscriber.receive("hello")      // 任意の呼び出し回数(0を含む)
```

### 対象制約

対象制約は呼び出しを期待するモックオブジェクト。

```groovy
1 * subscriber.receive("hello") // 'subscriber'への呼び出し
1 * _.receive("hello")          // 任意のオブジェクトへの呼び出し
```

### メソッド制約

メソッド制約は呼び出しを期待するメソッド。

```groovy
1 * subscriber.receive("hello") // 'receive'のメソッド
1 * subscriber._("hello")       // 任意のメソッド
1 * subscriber./r.*e/("hello")  // 'r'で始まり'e'で終わるメソッド
```

### 引数制約

引数制約は呼び出しを期待するメソッドの引数。

```groovy
1 * subscriber.receive("hello")           // "hello"に一致する
1 * subscriber.receive(!"hello")          // "hello"以外
1 * subscriber.receive()                  // 引数なし
1 * subscriber.receive(_)                 // 任意の引数
1 * subscriber.receive(*_)                // 任意の引数リスト
1 * subscriber.receive(!null)             // null以外
1 * subscriber.receive(_ as String)       // 任意のString
1 * subscriber.receive({ it.size() > 3 }) // 引数のsize()が3より大きい
```

## 任意な呼び出しのショートカット
 
```groovy
1 * subscriber._(*_)     // subscriberの任意のメソッド、任意の引数の呼び出し
1 * subscriber._         // 上記のショートカット
1 * _._                  // 任意のモックオブジェクトに対する任意の呼び出し
1 * _                    // 上記のショートカット
```

## インタラクションの宣言場所

以下の場所で宣言できる。

* `then:`ブロック
* `when:`ブロックの前
    * `setup:`ブロック
    * `setup`メソッド
    * フィールド

### モック作成時のインタラクション宣言

モック作成と同時にインタラクションを宣言する。

```groovy
def subscriber = Mock(Subscriber) {
    1 * receive("hello")
    1 * receive("goodbye")
}
```

対象制約がないことに注意。

### インタラクションの評価順序

* `then:`ブロックのインタラクションは最初に評価される
    * `then:`ブロックのインタラクションは`when:`ブロックの前に移動される
* あとは宣言した順序で評価される

### インタラクションブロック

以下は`MissingPropertyException`で正しく動作しない。

```groovy
when:
subscriber.receive("hello")

then:
def message = "hello"
1 * subscriber.receive(message) // このインタラクションはwhenブロックの前に移動される
```

回避するにはインタラクションを移動するかインタラクションブロックを使う。

```groovy
when:
subscriber.receive("hello")

then:
interaction {
    def message = "hello"
    1 * subscriber.receive(message)
}
```

## Strictモッキング

Spockのモックフレームワークは、Mockitoと同じようにデフォルトでLenient(緩い)なモックフレームワーク。

* 宣言していないインタラクションを許容する
* 呼び出し順序をチェックしない

Strict(厳密)にチェックするには少し工夫が必要(EasyMock、JMockといったモックフレームワークはデフォルトでStrict)。

### 宣言していないインタラクションを許容しない

```groovy
when:
publisher.publish("hello")

then:
1 * subscriber.receive("hello") // `subscriber`の`receive`メソッドが1回呼ばれる
0 * _                           // 他の呼び出しが0回であること
                                // これで宣言していないインタラクションが発生していないことを検証できる
                                // (宣言していないメソッド呼び出しが0回)
```

### 呼び出し順序をチェックする

デフォルトでは`then:`ブロック内のインタラクションが最終的に満たされれば良い。
呼び出し順序をチェックするには`then:`ブロックを分割する。

```groovy
when:
subscriber.receive("hello")
subscriber.receive("goodbye")

then:
1 * subscriber.receive("hello")

then:
1 * subscriber.receive("goodbye")
```

`and:`では分割したことにならないので注意すること(`and:`はドキュメンテーション目的だけに存在する)。

### thenのスコープ

`then:`ブロックのインタラクションは直前の`when:`ブロックにのみスコープを持つ。

```groovy
when:
subscriber.receive("hello")

then:
1 * subscriber.receive("hello")

when:
subscriber.receive("goodbye")

then:
1 * subscriber.receive("goodbye")

```

## インタラクションのグループ化

コンディションのグループ化と同様に、インタラクションも`with`でグループ化できる。

```groovy
with(subscriber) {
    1 * receive("hello")
    1 * receive("goodbye")
}
```

対象制約がないことに注意すること。

## スタビング

スタビングとは呼び出しに対するレスポンスを宣言する活動のこと。

```groovy
subscriber.receive(_) >> "ok"
```

多重度の代わりに、引数制約の右側にレスポンスジェネレータを指定する。

```groovy
subscriber.receive(_) >> "ok"
|          |       |     |
|          |       |     レスポンスジェネレータ
|          |       引数制約
|          メソッド制約
対象制約
```

### 固定値の返却

固定値を返すには`>>`を使用する。

```groovy
subscriber.receive(_) >> "ok"
```

型宣言を外れる戻り値は指定できない。

### 複数回の呼び出しに返す値を変える

複数回の呼び出しに返す値を変えるには`>>>`を使用する。

```groovy
subscriber.receive(_) >>> ["ok", "error", "error", "ok"]
```

### 動的に値を返す

動的に値を返すには`>>`演算子とクロージャを使用する。

クロージャの引数を型指定なしの1つとした場合は引数のリストが渡される。

```groovy
subscriber.receive(_) >> { args -> args[0].size() > 3 ? "ok" : "fail" }
```

クロージャの引数を型指定ありで1つ以上定義した場合は実際のメソッドの引数がクロージャの引数にマップされる。

```groovy
subscriber.receive(_) >> { String message -> message.size() > 3 ? "ok" : "fail" }
```

任意の副作用を実行することもできる。

```groovy
subscriber.receive(_) >> { throw new InternalError("ouch") }
```

### レスポンスのチェーン

メソッドのレスポンスをチェーンすることができる。

```groovy
subscriber.receive(_) >>> ["ok", "fail", "ok"] >> { throw new InternalError() } >> "ok"
```

## モッキングとスタビングの組み合わせ

```groovy
1 * subscriber.receive("message") >> "ok"
```

必ず1つのインタラクションとして宣言する必要がある。

```groovy
setup:
subscriber.receive("message") >> "ok"

when:
publisher.send("message")

then:
1 * subscriber.receive("message")
```

上記は2つのインタラクションとして認識される。

## Stubオブジェクト

スタビング専用のMockオブジェクト。

```groovy
def subscriber = Stub(Subscriber)
```

* インタラクションを宣言できない
* デフォルト値が異なる
    * プリミティブ型は、プリミティブ型のデフォルト値
    * 非プリミティブ数値（BigDecimalのような）の場合は、ゼロを返す
    * 数字以外の値は"空"や"ダミー"オブジェクトを返す

## Spyオブジェクト

パーシャルモックを作れる。

```groovy
def subscriber = Spy(SubscriberImpl, constructorArgs: ["Fred"])
def subscriber = Spy(SubscriberImpl) // コンストラクタの引数を省略した場合はデフォルトコンストラクタが使用される
```

本物メソッドに対するモッキングが可能。

```groovy
1 * subscriber.receive(_)
```

スタビングすると本物のメソッドが呼び出されなくなる。

```groovy
subscriber.receive(_) >> "ok"
```

### スタビングしつつ本物のメソッドを呼び出す

`callRealMethod`を使用するとクロージャの引数が自動的に`callRealMethod`の引数となる。

```groovy
subscriber.receive(_) >> { String message ->
    callRealMethod() // 自分で引数を設定しない
                     // 自動的に引数が設定される

    message.size() > 3 ? "ok" : "fail"
}
```

`callRealMethodWithArgs`を使用すると自分で引数を設定できる。

```groovy
subscriber.receive(_) >> { String message ->
    callRealMethodWithArgs("hello") // 自分で引数を指定して本物メソッドを呼び出す

    message.size() > 3 ? "ok" : "fail"
}
```

## GroovyMock(Stub/Spy)

GroovyMockは以下のモッキング(orスタビング)が可能。

* Groovyのダイナミックメソッド
* グローバルモック
* コンストラクタ
* finalやstaticメソッド

パワフルだが最終手段。まずは設計に問題がないことを疑う。

`MockingApi.GroovyMock()`、`MockingApi.GroovyStub()`、`MockingApi.GroovySpy()`を使う。

### 動的メソッドのモッキング

```groovy
def subscriber = GroovyMock(Subscriber)

1 * subscriber.someDynamicMethod("hello")
```

### グローバルモック

`global: true`を指定すると全てのインスタンスへ影響を与えることができる。

```groovy
setup:
// 変数で受けたglobalSpyはモッキング、スタビングを定義するためだけに使用する
// このオブジェクトをコラボレータとして差し替えたりはしない
def globalSpy = GroovySpy(Person, global: true)

when:
new Person(name: "Graeme").name
new Person(name: "Burt").name

then:
2 * globalSpy.name
```

### コンストラクタのモッキング

#### モッキングの例

```groovy
setup:
GroovyMock(Person, global: true)

when:
new Person(name: "Graeme")
new Person(name: "Burt")

then:
2 * new Person(_)
```

#### スタビングの例

```groovy
setup:
GroovySpy(Person, global: true)
new Person(_) >> new Person(name: "mock")

expect:
new Person(name: "Graeme").name == "mock"
new Person(name: "Burt").name == "mock"
```

### Staticメソッドのモッキング

#### モッキングの例

```groovy
setup:
GroovyMock(Math, global: true)

when:
Math.max(1, 2)
Math.max(3, 4)

then:
2 * Math.max(_, _)
0 * _
```

#### スタビングの例

```groovy
setup:
GroovyMock(Math, global: true)
Math.max(_, _) >> 100

expect:
Math.max(1, 2) == 100
Math.max(3, 4) == 100
```
