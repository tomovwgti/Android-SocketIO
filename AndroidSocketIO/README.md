Socket.IO for Android
=============

Socket.IOをAndroidクライアントで動作するようにしたサンプルアプリです

注意
-------

* サーバサイドはこの[５分くらいで出来るnode.js(0.6) + socket.io(0.8x)のサンプルプログラム](http://d.hatena.ne.jp/replication/20111108/1320762287)を参考にしたもので確認しています。
* 動作確認はNode.js(v0.6.6/v0.8.7)Socket.IO(v.0.9.0)

構成
------------

* [Socket.IO-Client for Java](https://github.com/Gottox/socket.io-java-client)をベースにしています。
* Fragmentに対応させていますが、コードはやや複雑に・・・
* 受信イベントをリスナーで処理するようにしているので、実際に使うものだけリスナーに登録するようにすればいいでしょう
* JSONのパースにJSONICを（無理やり）使ってます