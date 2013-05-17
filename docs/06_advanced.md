
高度な話題
==========

old
---

oldを使うとwhenブロック前の値を参照できる。

```groovy
setup:
def list = [1, 2, 3]

when:
list << 4

then:
list.size() == old(list.size()) + 1
```

JUnit連携
---------

### HamcrestのMatcherを使う

```groovy
def "thatの使い方"() {
    def x = 10

    expect:
    that x, is(10)
    that x, equalTo(10)
}

def "thatのエイリアスのexpect"() {
    def x = 10

    expect:
    expect x, is(10)
}

def "that、expectを記述せずに使用することもできる"() {
    def x = 10

    expect:
    x is(10)
    x is(not(9))
}
```

[カスタムのMatcherを使うのも簡単](http://mrhaki.blogspot.jp/2013/05/spocklight-using-custom-hamcrest-matcher.html)

### Ruleを使う

Spockの独自機能で明示的に`new`しなくともデフォルトコンストラクタでインスタンスが生成される。
コンストラクタの引数が必要な場合は、明示的に`new`する必要がある。

```groovy
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
```

### JUnitのフィクスチャアノテーションを使う

```groovy
@Before
void "before"() { ... }

@After
void "cleanup"() { ... }

@BeforeClass
static void "beforeClass"() { ... }

@AfterClass
static void "afterClass"() { ... }
```

### JUnitのAssumeを使う

`@Assume`を使って前提条件が満たされない場合にフィーチャメソッドを実行しない。

```groovy
def "前提条件が満たされない場合はフィーチャメソッドが実行されない"() {
    assumeThat 1, equalTo(2)

    expect:
    false
}
```

MOP関連のサポート
-------------

### @Use

`@Use`をフィーチャメソッドに付与すると、フィーチャメソッド内がuseブロックのスコープとなる。

```groovy
class UseSpec extends Specification {

    @Use(StringExtension)
    def "拡張モジュールを使う"() {
        expect:
        "groovy".duplicate() == "groovygroovy"
    }

    @Use(IntegerCategory)
    def "カテゴリークラスを使う"() {
        expect:
        7.square() == 49
    }

    @Use([StringExtension, IntegerCategory])
    def "複数指定する"() {
        expect:
        "groovy".duplicate() == "groovygroovy"
        7.square() == 49
    }
}

class StringExtension {
    static String duplicate(String self) { self * 2 }
}

@Category(Integer)
class IntegerCategory {
    Integer square() { this * this }
}
```

### @ConfineMetaClassChanges

`@ConfineMetaClassChanges`を使うと`metaClass`の状態をリストアできる。
フィーチャメソッドに`@ConfineMetaClassChanges`を付与すると`setup`メソッドの後の`metaClass`状態に`cleanup`メソッドの前でリストアされる。

```groovy
@ConfineMetaClassChanges([String, Integer])
def "メタクラスを操作する"() {
    setup: "この操作はロールバックされる"
    String.metaClass.duplicate = { -> delegate * 2 }
    Integer.metaClass.square = { -> delegate * delegate }

    expect:
    "hello".duplicate() == "hellohello"
    2.square() == 4
}
```

スペッククラスに`@ConfineMetaClassChanges`を付与すると、`setupSpec`メソッドの前の`metaClass`状態に`cleanupSpec`メソッドの後でリストアされる。

```groovy
// このスペッククラス内でのStringに対するmetaClassの操作は
// このスペックが終了するときにロールバックされる
@ConfineMetaClassChanges(String)
class ConfineMetaClassChangesWithClassSpec extends Specification {
    ...
}
```

任意のフィーチャメソッドを実行する
----------------------------------

[参考: 任意のフィーチャーメソッドだけを実行する - bluepapa32's Java Blog](http://d.hatena.ne.jp/bluepapa32/20111206/1323182112)

まずはアノテーションを用意する。

```groovy
@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Fast {}

@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Slow {}

@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
public @interface Server {}
```

アノテーションをスペッククラスやフィーチャメソッドに付与する。

```groovy
@Server
class IncludeExcludeSpec extends Specification {

    def "server"() { ... }

    @Slow
    def "slow"() { ... }

    @Fast
    def "fast"() { ... }
}
```

設定ファイルを準備する。

```groovy
// Firstが付与されているものだけを実行
runner {
    include Fast
}
```

このファイルを以下のいずれかで読み込ませる。

* システムプロパティ`spock.configuration`で指定
    * 例: `-Dspock.configuration=MySpockConfig.groovy`
* クラスパス上の`SpockConfig.groovy`に配置
* `$HOME/.spock/SpockConfig.groovy`に配置

IDE上でクラスパスに`SpockConfig.groovy`を配置する場合はコンパイルされないように工夫が必要。

* [Intellijの場合](http://blog.livedoor.jp/ryu22e/archives/65744560.html)
* [Eclipseの場合](http://d.hatena.ne.jp/bluepapa32/20111206/1323182112)

### Slowが付与さているものだけ除外する場合

```goorvy
runner {
    exclude Slow
}
```

### Serverが付与されているものの中でFastとSlowを除外する場合

```groovy
runner {
    include Server
    exclude Fast, Slow
}
```

### スペック(ベースでもOK)クラスを指定する場合

```groovy
runner {
    include IncludeExcludeSpec
}
```

これで特定のクラスを継承したスペッククラスを対象とする、または除外するといったことが可能になる。

非同期処理のテスト
------------------

`AsyncConditions`、`BlockingVariable(s)`などがあるが、ver0.7で追加されたポーリングコンディションを実現する`PollingConditions`が使いやすい。

```groovy
def "PollingConditionsの基本的な使い方"() {
    setup:
    PollingConditions conditions = new PollingConditions()
    def number = 0

    when:
    Thread.start {
        number = 1
    }

    then: "eventuallyのコンディションが満たされるまで待ち合わせる"
    conditions.eventually {
        number == 1
    }
}

@FailsWith(SpockTimeoutError)
def "タイムアウトになるとSpockTimeoutErrorが発生する"() {
    setup:
    PollingConditions conditions = new PollingConditions() // デフォルトではタイムアウト値が1秒
    def number = 0

    when:
    Thread.start {
        Thread.sleep(1100) // 1秒以上スリープ
        number = 1
    }

    then: "1秒待ってタイムアウト"
    conditions.eventually {
        number == 1
    }
}

def "タイムアウト値を設定する"() {
    setup: "タイムアウト値を2秒に設定"
    PollingConditions conditions = new PollingConditions(timeout: 2)
    def number = 0

    when:
    Thread.start {
        number = 1
    }

    then:
    conditions.eventually {
        number == 1
    }
}

def "withinを使ってタイムアウト値を設定する"() {
    setup:
    PollingConditions conditions = new PollingConditions()
    def number = 0

    when:
    Thread.start {
        number = 1
    }

    then: "withinでタイムアウトを2秒に設定"
    conditions.within(2000) { // withinを使うとブロックのタイムアウト値だけ変更できる
        number == 1
    }
}
```

ただし、ver0.7の時点では`PollingConditions`は`@Beta`扱いのため、今後変更される可能性もある。
