
package com.tomovwgti.socketio;

import io.socket.SocketIO;
import io.socket.util.SocketIOManager;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tomovwgti.json.Msg;
import com.tomovwgti.socketio.SocketIOFragment.MessageCallback;
import com.tomovwgti.socketio.SocketIOFragment.MessageCallbackPicker;

public class SocketIOActivity extends FragmentActivity implements MessageCallbackPicker,
        MessageCallback {
    static final String TAG = SocketIOActivity.class.getSimpleName();

    private static final String PREF_KEY = "IPADDRESS";
    private SocketIOManager mSocketManager;
    private SocketIO mSocket;
    private AlertDialog mAlertDialog;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

        { // SocketIOFragmentの作成と登録
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(new SocketIOFragment(), "socketio");
            transaction.commit();
        }

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPref.edit();

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.input_message);
                Msg sendMessage = new Msg();
                sendMessage.setValue(edit.getText().toString());
                try {
                    mSocket.emit("message", new JSONObject(JSON.encode(sendMessage)));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (org.json.JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                edit.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPref.getString(PREF_KEY, "").equals("")) {
            // IPアドレス確認ダイアログ
            mAlertDialog = showAlertDialog();
            mAlertDialog.show();
        } else {
            mSocket = mSocketManager.connect("http://" + mPref.getString(PREF_KEY, "") + ":3000/");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSocketManager.disconnect();
    }

    @Override
    public MessageCallback getInstance() {
        return this;
    }

    public void setSocketIOManager(SocketIOManager manager) {
        this.mSocketManager = manager;
    }

    public AlertDialog showAlertDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View entryView = factory.inflate(R.layout.dialog_entry, null);
        final EditText edit = (EditText) entryView.findViewById(R.id.ip_address);

        if (mPref.getString(PREF_KEY, "").equals("")) {
            edit.setHint("***.***.***.***");
        } else {
            edit.setText(mPref.getString(PREF_KEY, ""));
        }
        // キーハンドリング
        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Enterキーハンドリング
                if (KeyEvent.KEYCODE_ENTER == keyCode) {
                    // 押したときに改行を挿入防止処理
                    if (KeyEvent.ACTION_DOWN == event.getAction()) {
                        return true;
                    }
                    // 離したときにダイアログ上の[OK]処理を実行
                    else if (KeyEvent.ACTION_UP == event.getAction()) {
                        if (edit != null && edit.length() != 0) {
                            // ここで[OK]が押されたときと同じ処理をさせます
                            String editStr = edit.getText().toString();
                            mSocket = mSocketManager.connect("http://" + editStr + ":3000/");
                            mAlertDialog.dismiss();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        // AlertDialog作成
        return new AlertDialog.Builder(this).setTitle("Server IP Address").setView(entryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String editStr = edit.getText().toString();
                        // OKボタン押下時のハンドリング
                        mEditor.putString(PREF_KEY, editStr);
                        mEditor.commit();
                        mSocket = mSocketManager.connect("http://" + editStr + ":3000/");
                    }
                }).create();
    }

    /**
     * サーバからのSocket.IOイベント／メッセージ
     */
    @Override
    public void onDisconnect() {
        Toast.makeText(SocketIOActivity.this, "Disconnect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnect() {
        Toast.makeText(SocketIOActivity.this, "Connect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHertbeat() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMessage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onJsonMessage(Msg message) {
        mAdapter.add(message.getValue());
    }

    @Override
    public void onEvent() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAck() {
        // TODO Auto-generated method stub

    }
}
