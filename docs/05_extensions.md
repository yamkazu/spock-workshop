
拡張機能
========

ビルトイン拡張機能
------------------

Spockではアノテーションベースの便利な拡張機能がビルトインされている。

### @Shared

Spockの基礎を参照

### @Unroll

データ駆動テストを参照

### @Ignore

`@Ignore`を付与すると指定したフィーチャの実行がスキップされる。

```groovy
@Ignore
def "xxx"() { ... }

@Ignore('hogehogeのため')
def "yyy"() { ... }
```

スペックに指定すると全体がスキップされる。

```groovy
@Ignore
class IgnoreSpec extends Specification { ... }
```

### @IgnoreRest

`@IgnoreRest`を付与すると付与したフィーチャメソッドだけ実行できる。

```groovy
def "このフィーチャメソッドは実行されない"() { ... }

@IgnoreRest
def "このフィーチャメソッドは実行される"() { ... }

@IgnoreRest
def "複数のフィーチャメソッドに@IgnoreRestを設定できる"() {
    expect: true
}
```

### @IgnoreIf

`@IgnoreIf`は引数のクロージャの実行結果が`ture`の場合、そのフィーチャメソッドを無視する。

```groovy
@IgnoreIf({ true })
def "trueなので無視される"() { ... }

@IgnoreIf({ false })
def "falseなので無視されない"() { ... }

@IgnoreIf({ 1 < 2 })
def "1 < 2 はtrueなので無視される"() { ... }

@IgnoreIf({ 1 > 2 })
def "1 > 2 はfalseなので実行される"() { ... }

@IgnoreIf({
    def a = 1
    def b = 1
    a + b == 2
})
def "closureをcallしているだけなので複数行書いても良い"() { ... }

// v0.7の時点で以下で動作しているが
// 次バージョンで参照方法が変わるかもしれないので注意

@IgnoreIf({ javaVersion > 1.4 })
def "javaVersionでJVMのバージョンが参照できる"() { ... }

@IgnoreIf({ env.LANG == 'C' })
def "envがSystem.getenv()のショートカットになっている"() { ... }

@IgnoreIf({ properties["os.name"] == 'Mac OS X' })
def "propertiesがSystem.getProperties()のショートカットになっている"() { ... }
```

### @FailsWith

`@FailsWith`を使うとJUnitのように発生する例外を指定できる。

```groovy
@FailsWith(IndexOutOfBoundsException)
def "FailsWithの基本"() {
    expect:
    [].get(0) // IndexOutOfBoundsExceptionがスローされる
}

@FailsWith(value = NullPointerException, reason = "ぬるぽ")
def "reasonに理由を書ける"() {
    expect:
    null.ぬるぽですよ
}
```

スペッククラスに付与することもできる。

```groovy
class FailWithOnSpec extends Specification {

    def "ぬるぽで終了する"() {
        expect:
        null.ぬるぽですよ
    }

    @FailsWith(IndexOutOfBoundsException)
    def "スペッククラスに付与しつつフィーチャメソッドでオーバライドもOK"() {
        expect:
        [].get(0)
    }
}
```

### @Timeout

`@Timeout`はフィーチャメソッドのタイムアウト値を指定できる。
このタイムアウト値を超過した場合は`org.spockframework.runtime.SpockTimeoutError`が発生する。

```groovy
@Timeout(1)
def "1秒以内に終わる"() {
    expect: Thread.sleep 500
}

@FailsWith(SpockTimeoutError)
@Timeout(1)
def "1秒以内に終わらない"() {
    expect: Thread.sleep 1100
}

@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
def "500ミリ秒以内に終わる"() {
    expect: Thread.sleep 250
}

@FailsWith(SpockTimeoutError)
@Timeout(value = 250, unit = TimeUnit.MILLISECONDS)
def "500ミリ秒以内に終わらない"() {
    expect: Thread.sleep 300
}
```

### @AutoCleanup

`@AutoCleanup`を使うと自動的に後処理を呼び出せる。

````groovy
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
```

### @Stepwise

`@Stepwise`を設定するとフィーチャメソッドを定義した順に実行される。

```groovy
def "first"() {
    expect: println "first"
}

def "second"() {
    expect: println "second"
}

def "third"() {
    expect: println "third"
}
```

途中でエラーが発生した場合は後続のフィーチャメソッドは実行されない。

```groovy
def "first"() {
    expect: println "first"
}

def "second"() {
    expect: false // ここで失敗する
}

def "third"() { // このフィーチャメソッドは呼ばれない
    expect: false
}
```

カスタム拡張機能
----------------

既存の拡張機能のコード嫁。
