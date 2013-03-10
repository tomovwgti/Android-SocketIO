
package com.tomovwgti.socketio;

import io.socket.SocketIO;
import io.socket.util.SocketIOManager;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tomovwgti.json.Msg;

public class SocketIOFragment extends Fragment {
    static final String TAG = SocketIOFragment.class.getSimpleName();

    private MessageCallback mCallback;
    private SocketIOManager mSocketManager;
    private SocketIO mSocket;

    public static interface MessageCallbackPicker {
        public MessageCallback getInstance();
    }

    public static interface MessageCallback {
        /** 切断されたとき */
        public void onDisconnect();

        /** 接続したとき */
        public void onConnect();

        /** HERTBEATを受信したとき */
        public void onHertbeat();

        /** メッセージを受信したとき */
        public void onMessage();

        /** JSONメッセージを受信したとき */
        public void onJsonMessage(Msg message);

        /** イベントを受信したとき */
        public void onEvent();

        /** エラーを受信したとき */
        public void onError();

        /** Ackメッセージを受信したとき */
        public void onAck();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketIOManager.SOCKETIO_DISCONNECT:
                    Log.i(TAG, "SOCKETIO_DISCONNECT");
                    mCallback.onDisconnect();
                    break;
                case SocketIOManager.SOCKETIO_CONNECT:
                    Log.i(TAG, "SOCKETIO_CONNECT");
                    mCallback.onConnect();
                    break;
                case SocketIOManager.SOCKETIO_HERTBEAT:
                    Log.i(TAG, "SOCKETIO_HERTBEAT");
                    mCallback.onHertbeat();
                    break;
                case SocketIOManager.SOCKETIO_MESSAGE:
                    Log.i(TAG, "SOCKETIO_MESSAGE");
                    mCallback.onMessage();
                    break;
                case SocketIOManager.SOCKETIO_JSON_MESSAGE:
                    Log.i(TAG, "SOCKETIO_JSON_MESSAGE");
                    Msg message = JSON.decode((String) (msg.obj), Msg.class);
                    mCallback.onJsonMessage(message);
                    break;
                case SocketIOManager.SOCKETIO_EVENT:
                    Log.i(TAG, "SOCKETIO_EVENT");
                    mCallback.onEvent();
                    break;
                case SocketIOManager.SOCKETIO_ERROR:
                    Log.i(TAG, "SOCKETIO_ERROR");
                    mCallback.onError();
                    break;
                case SocketIOManager.SOCKETIO_ACK:
                    Log.i(TAG, "SOCKETIO_ACK");
                    mCallback.onAck();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mSocketManager = new SocketIOManager(mHandler);

        MessageCallbackPicker picker = (MessageCallbackPicker) activity;
        mCallback = picker.getInstance();
    }

    /**
     * Socket.IOでの接続
     * 
     * @param ipAddress
     */
    public void connectSocketIO(String ipAddress) {
        mSocket = mSocketManager.connect("http://" + ipAddress);
    }

    /**
     * Socket.IOの切断
     */
    public void disconnectSocketIO() {
        mSocketManager.disconnect();
    }

    /**
     * Socket.IOを使って送信
     */
    public void emit(Msg message) {
        try {
            mSocket.emit("message", new JSONObject(JSON.encode(message)));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * メニューを生成する
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.address_setting, menu);
    }

    /**
     * メニューが選択された場合の動作
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ip_settings:
                mSocketManager.disconnect();
                ((SocketIOActivity) getActivity()).showAlertDialog().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
