Socket.IO for Android
=============

Socket.IOをAndroidクライアントで動作するようにしたサンプルアプリです

注意
-------

* サーバサイドはこの[５分くらいで出来るnode.js(0.6) + socket.io(0.8x)のサンプルプログラム](http://d.hatena.ne.jp/replication/20111108/1320762287)を参考にしたもので確認しています
* 動作確認はNode.js(v0.8.16), Socket.IO(v0.9.10)
* 接続するURLに接続するポート番号を付加してください（例：192.168.0.8:3000)

構成
--------

* [Socket.IO-Client for Java](https://github.com/Gottox/socket.io-java-client)をベースにしています。
* Fragmentに対応させていますが、コードはやや複雑に・・・
* 受信イベントをリスナーで処理するようにしているので、実際に使うものだけリスナーに登録するようにすればいいでしょう
* JSONのパースにJSONICを（無理やり）使ってます

更新情報
---------

* nginx1.3.13でリバースプロキシしても動作するように修正。 @gt2k さんありがとうございます。[nginx(1.3.13) のリバースプロキシでNode.jsとSocket.IO for Android(weberknecht)をつないでみる](http://qiita.com/items/15379fe0e73572251e05)
* weberknechtを内部にソースとして持つようになった
