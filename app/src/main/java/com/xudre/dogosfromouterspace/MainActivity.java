package com.xudre.dogosfromouterspace;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView scoreView;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        scoreView = findViewById(R.id.scoreView);
        gameView = findViewById(R.id.gameView);

        gameView.Main = this;
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameView.Paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameView.Paused = false;
    }

    public void setScore(final int score) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreView.setText(score + " pts");
            }
        });
    }
}
