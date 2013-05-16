
Spockとは
=========

* JUnit互換で動作するJava/Groovy対応のテストツール
    * IDE、ビルドツール、CIツール、と互換がある
* 効率的なデータ駆動テスト
* パワフルなモックサポート
* BDDツール
    * 表現力の高いDSLで可読性が高い


使用プロジェクト
----------------

* Asgard
* Geb
* GPars
* Gradle
* Grails
* Grails Plugin Collective
* Grasp
* Griffon
* Spock (eating our own dog food)
* Apache Tapestry

[参考: WhoIsUsingSpock](https://code.google.com/p/spock/wiki/WhoIsUsingSpock)

リソース
--------

* [GitHub](https://github.com/spockframework/spock)
* [リファレンス](https://spock-framework.readthedocs.org/ja/latest/)
* [Wiki(旧ドキュメント)](https://code.google.com/p/spock/w/list)

日本語リソース
--------------

* [Groovy/G*界隈でBoostする為に有用なリンクを集めてみた(Spock編) - Shinya’s Daily Report](http://d.hatena.ne.jp/absj31/20120419/1334849503)
* [spockを使う - Grails goes on](http://grailsgoeson.metabolics.co.jp/2009/12/spock.html)

IDEの準備
----------

JUnit互換なので基本特別な準備はない。

### IDEAのプラグイン

* [Spock Framework Enhancements](http://plugins.jetbrains.com/plugin?pluginId=7114)

### IDEAのテンプレート設定

* [Spcokのショートカットキーで補完する設定 - みちしるべ](http://d.hatena.ne.jp/orangeclover/20120718/1342618459)
* [IntelliJ IDEAのTemplate機能を使ってSpockのテストをもっと快適にしてみる - Shinya’s Daily Report](http://d.hatena.ne.jp/absj31/20130318/1363619322)
* [わたしの設定](https://gist.github.com/yamkazu/5589787#file-gistfile1-md)

### Macの日本語問題

[参考: Javaのシステムプロパティの設定と文字エンコーディングについて改めて見直してみた - PiyoPiyoDucky](http://piyopiyoducky.net/blog/2013/04/13/java-system-properties-setting-and-character-encoding/)

`/etc/launchd.conf`に以下のように設定するのがおすすめ(※要再起動)。
```
setenv LANG ja_JP.UTF-8
```

インストール
------------

### build.gradleの例

```groovy
// Groovyのバージョンによって使用するアーティファクトが異なる
// 参考: https://code.google.com/p/spock/wiki/SpockVersionsAndDependencies
testCompile 'org.spockframework:spock-core:0.7-groovy-2.0'

// モック機能を使う場合(正確にはクラスをモックする場合)は
// cglib-nodep-2.2以上、objenesis-1.2以上が必要
testRuntime 'cglib:cglib-nodep:2.2'
testRuntime 'org.objenesis:objenesis:1.2'
```

### Gradleを使わず直接IDEAでやる方法

* [Groovy製BDDフレームワーク『Spock』開発環境構築手順(※IntelliJ IDEA 12/Groovy 2.0/Spock 0.7) - Shinya’s Daily Report](http://d.hatena.ne.jp/absj31/20130317/1363510514)
* [IntelliJっぽいライブラリの追加方法 - marsのメモ](http://d.hatena.ne.jp/masanobuimai/20130317/1363528353)
