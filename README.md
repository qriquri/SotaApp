# SotaApp

## プログラムの実行
### 1. 前準備
まず`TeraTerm`等でSotaにSSH接続している必要がある
### 2. ディレクトリ移動
```
cd SotaApp/bin
```
### 3. 時刻合わせ
Sota起動時になぜか時刻がずれている。このままだとSotaクラウドでの音声認識ができないので時刻をあわせるひつようがある。形式は以下の通り
```
timedatectl set-time"20XX-YY-ZZ HH:MM:00"
```
例
```
timedatectl set-time"2023-02-27 17:36:00"
```
### 4.プログラム実行
基本的に以下の形式でプログラムを実行する
```
java_run.sh jp/hayamiti/<プログラム名(拡張子は付けない)>
```

### メインプログラム
```
java_run.sh jp/hayamiti/TestApp4
```

## 注意事項
* Sotaへのプログラムの送信はエクリプス上でsend.xmlを右クリックして`ant build`を選択する
* 追加のライブラリのインストールは[このページを参考にする](https://www.mlab.im.dendai.ac.jp/~yamada/java/ext/)