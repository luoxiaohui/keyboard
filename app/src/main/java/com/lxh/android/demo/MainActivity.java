package com.lxh.android.demo;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.lxh.android.keyboard.KeyboardClient;

/**
 * 安全键盘
 *
 * @author luoxiaohui
 * @createTime 2019/1/24 2:18 PM
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyboardClient keyboardClient = KeyboardClient.newKeyboard(this.getWindow());
        keyboardClient.showKeyboard();

        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setGravity(Gravity.TOP);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                KeyboardClient dialogKeyboard = KeyboardClient.newKeyboard(dialog.getWindow(), false);
                dialogKeyboard.showKeyboard();
                dialog.show();

            }
        });


    }


}






















