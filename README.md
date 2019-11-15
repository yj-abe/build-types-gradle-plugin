# Build Types Gradle Plugin

Javaのプロジェクトにおいて、デバッグビルド、リリースビルド、ローカルビルド、ステージングビルドなど、
複数のビルドタイプを設定するプラグインです。  
各環境やビルドごとの定数値、ソースコード、リソースファイルの定義、配置を可能にします。

実際の使い方などは[サンプル](https://github.com/yj-abe/build-types-sample)も参照してください。

## リモートリポジトリの追加

プラグインを使用するために、プロジェクトにmaven repositoryを追加する必要があります。  
以下のように、`build.gradle`にリポジトリを追加してください。

```groovy
buildscript {
    repositories {
        mavenCentral() // プラグインが使用する依存関係のために必要
        maven {
            url 'https://github.com/yj-abe/build-types-gradle-plugin/raw/gh-pages/repository'
        }
    }
    dependencies {
        classpath 'jp.cloudace:buildtypes:0.2.0'
    }
}
```

## プラグインの適用

プラグインを使用する各プロジェクトの`build.gradle`に以下のコードスニペットを追加してください。

```groovy
apply plugin: 'jp.cloudace.buildtypes'
```

または

```groovy
plugins {
    id 'jp.cloudace.buildtypes' version '0.2.0'
}
```

## DSL

```groovy
group 'jp.cloudace.myproject'

buildTypes {
    developOn "local" // 開発時に参照するビルドタイプの指定をします
    types {
        local { // 任意の名前をつけて、ビルドタイプを定義します
            debuggable = true // デバッグフラグの定義をします。デフォルトはfalseです。
            buildConfigField "String", "ENV_NAME", "\"ローカル環境\""
        }
        develop {
            buildConfigField "String", "ENV_NAME", "\"開発環境\""
        }
        staging {
            buildConfigField "String", "ENV_NAME", "\"ステージング環境\""
        }
        product {
            buildConfigField "String", "ENV_NAME", "\"本番環境\""
        }
    }
}
```

## BuildConfigクラス

各ビルドタイプごとに`BuildConfig`という定数クラスが自動生成されます。  
BuildConfigクラスは`group`に指定されたパッケージに属するように生成されます。  
`buildConfigField`を使ってBuildConfigクラスに定数を定義することが可能です。詳細はBuildTypeを参照してください。  
ただし、以下の定数は自動的に宣言されます。

| プロパティ | 型 | 概要 |
| :--- | :---: | :--- |
| BUILD_TYPE | String | ビルドタイプ名 |
| DEBUG | boolean | デバッグモードかどうか |

上記のDSLの項に記載されている設定の場合は以下のようなBuildConfigが自動生成されます。(localの例)

```java
package jp.cloudace.myproject;

public final class BuildConfig {
    public static final String BUILD_TYPE = "local";
    public static final boolean DEBUG = true;
    public static final String ENV_NAME = "ローカル環境";
}
```

## ソースセット

ビルドタイプを定義すると、プラグインは自動的にビルドタイプと同名のソースセットを作成します。  
各ソースセットに各ビルドタイプごとのプログラムやリソースファイルを配置することが可能です。

```
src/
 ├ local/
 | ├ java/
 | | └ jp.cloudace.myproject.Hoge.java
 | ├ resources
 |   └ hoge.txt
 ├ staging/
 | ├ java/
 | | └ jp.cloudace.myproject.Hoge.java
 | ├ resources
 |   └ hoge.txt
 ├ main/
```

開発時には`developOn`で指定されたビルドタイプのBuildConfig、java、resourcesを参照しますが、各ビルドタイプごとに
ビルドする際には後述するビルド用のGradleタスクを使用して、各ビルドタイプ用のファイル群のみをコンパイルパスに通す必要があります。

## Gradleタスク

各ビルドタイプごとにそれぞれビルド用のタスクが`build${ビルドタイプ名}`の様に定義されます。  
例: `buildLocal`, `buildStaging`, etc  
これらのタスクに依存するように独自のビルド用のタスクを定義することで、各ビルドタイプごとの
プログラムやファイルを含む成果物を作成できます。  
これらのタスクは`build`タスクに依存します。

## BuildTypes extension

### プロパティ

| プロパティ | 型 | 概要 |
| :--- | :---: | :--- |
| developOn | String | 開発時に参照するビルドタイプの名前を指定します |
| types | NamedDomainObjectContainer\<BuildType\> | 定義されているビルドタイプの一覧 |

### ブロック

`buildTypes`の中では以下のブロックが使えます。

| ブロック | 概要 |
| :--- | :--- |
| types { } | このプロジェクトへのビルドタイプの設定をカプセル化します。詳細はBuildTypeを参照してください。 |

## BuildType

ビルドタイプを設定するためのDSLオブジェクトです。

### プロパティ

| プロパティ | 型 | 概要 |
| :--- | :---: | :--- |
| debuggable | boolean | このビルドタイプがデバッグモードかどうか。デフォルトはfalse。 |

### メソッド

| メソッド | 概要 |
| :--- | :--- |
| buildConfigField(typeName, varName, value) | 自動生成される`BuildConfig`クラスに定数を追加します。 |

#### メソッド詳細

```java
void buildConfigField(String typeName, String varName, String value)
```

自動生成される`BuildConfig`クラスに定数を追加します。  
定数は以下のように生成されます。  
`public static final <typeName> <varName> = <value>;`  
型名は完全修飾名で指定する必要がありますが、String、プリミティブ型、プリミティブのラッパー型ではパッケージを省略できます。  
また、Stringの場合は値をダブルクォートで囲む必要があります。

例: `buildConfigField "String", "CONST_VALUE", "\"定数\""`
