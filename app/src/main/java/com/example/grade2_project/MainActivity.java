package com.example.grade2_project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Contadores e estado
    private int countDown = 0;
    private int countUp = 0;
    private boolean isUpsideDown = false;
    private long lastTime = 0;

    // UI e Sons
    private TextView txtDown, txtUp;
    private MediaPlayer mpDown, mpUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar UI
        txtDown = findViewById(R.id.txtDown);
        txtUp = findViewById(R.id.txtUp);

        // Inicializar Sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Inicializar Sons (certifique-se de que os arquivos estão em res/raw)
        mpDown = MediaPlayer.create(this, R.raw.down_sound);
        mpUp = MediaPlayer.create(this, R.raw.up_sound);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = event.values[1]; // Eixo Vertical
            long currentTime = System.currentTimeMillis();

            // Intervalo de 600ms para evitar disparos múltiplos rápidos demais
            if ((currentTime - lastTime) > 600) {

                // Lógica para CABEÇA PARA BAIXO
                if (y < -7.0 && !isUpsideDown) {
                    isUpsideDown = true;
                    countDown++;
                    lastTime = currentTime;

                    playTrack(mpDown);
                    txtDown.setText("Cabeça para baixo: " + countDown);
                }

                // Lógica para VOLTAR AO NORMAL (CABEÇA PARA CIMA)
                else if (y > 7.0 && isUpsideDown) {
                    isUpsideDown = false;
                    countUp++;
                    lastTime = currentTime;

                    playTrack(mpUp);
                    txtUp.setText("Cabeça para cima: " + countUp);
                }
            }
        }
    }

    private void playTrack(MediaPlayer player) {
        if (player != null) {
            if (player.isPlaying()) player.pause();
            player.seekTo(0);
            player.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não necessário para esta aplicação
    }
}