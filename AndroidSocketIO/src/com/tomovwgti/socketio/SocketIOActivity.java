
package com.tomovwgti.socketio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
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
    private AlertDialog mAlertDialog;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 起動時にキーボードが開かないように
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.main);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

        { // SocketIOFragmentの作成と登録
            FragmentManager manager = getSupportFragmentManager();
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
                ((SocketIOFragment) getFragment()).emit(sendMessage);
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
            ((SocketIOFragment) getFragment()).connectSocketIO(mPref.getString(PREF_KEY, ""));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((SocketIOFragment) getFragment()).disconnectSocketIO();
    }

    @Override
    public MessageCallback getInstance() {
        return this;
    }

    private Fragment getFragment() {
        return getSupportFragmentManager().findFragmentByTag("socketio");
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
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                                    "socketio");
                            ((SocketIOFragment) fragment).connectSocketIO(editStr);
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
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                                "socketio");
                        ((SocketIOFragment) fragment).connectSocketIO(editStr);
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
        Toast.makeText(SocketIOActivity.this, "Socket.IO Error!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAck() {
        // TODO Auto-generated method stub

    }
}
