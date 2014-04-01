Socket.IO Node.jsサーバ
=============

Socket.IOの動作サンプルのサーバサイド

注意
-------

* サーバサイドはこの[５分くらいで出来るnode.js(0.6) + socket.io(0.8x)のサンプルプログラム](http://d.hatena.ne.jp/replication/20111108/1320762287)を参考にしてください。下記に異なる部所について補足してあります。
* 動作確認はMacでNode.js(v0.11.9)Socket.IO(v.0.9.16)

構成
------------

* express 3.4.8
* Socket.IO v0.11.9

インストールの補足
------------

* MacではNode.js v0.8以降をインストールする場合にXcodeが必要らしいです
* express3系でejsを使う時の注意

		$ express -e NodeServer
		
	としないとJadeになってしまう
* git cloneして下記のコマンドで必要なパッケージはインストールされる

		$ npm install

コードの補足
------------

* express3からlayout.ejsが無くなり、index.ejsに記述するようになっています
* express3でSocket.IOを動かすためには、少し[修正が必要](http://blog.craftgear.net/4f9706929fde17f20f000001/title/express3%E3%81%A8Socket.IO)でした