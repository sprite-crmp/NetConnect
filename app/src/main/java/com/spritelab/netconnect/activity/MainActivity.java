package com.spritelab.netconnect.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spritelab.netconnect.R;
import com.spritelab.netconnect.adapter.InformationAdapter;
import com.spritelab.netconnect.adapter.ServiceAdapter;
import com.spritelab.netconnect.model.InformationModel;
import com.spritelab.netconnect.model.ServiceModel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MainActivity extends AppCompatActivity {

    private RecyclerView urlListRec, infoListRec;
    private ServiceAdapter serviceAdapter;
    private InformationAdapter infoAdapter;
    private TextView textLogs;
    private TextView reBtn;
    private List<ServiceModel> urlList = new ArrayList<>();
    private List<InformationModel> infoList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 123;
    private int finishChecks;
    private int errorChecks;
    private final int totalChecks = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLogs = findViewById(R.id.textLogs);
        reBtn = findViewById(R.id.reBtn);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        reBtn.setScaleX(0f);
        reBtn.setScaleY(0f);
        reBtn.setOnClickListener(v -> {
            v.animate()
                    .setDuration(150)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .withEndAction(() -> {
                        v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .start();
                    })
                    .start();

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        requestLocationPermission();
        setupUrlRecyclerView();
        setupInfoRecyclerView();
    }

    private void fabricUrlItem() {
        checkConnect("https://vk.ru/");
        checkConnect("https://yandex.ru/");
        checkConnect("https://www.youtube.com");
        checkConnect("https://colorscheme.ru/html-colors.html");
    }

    private void fabricInfoItem() {
        AtomicLong ping = new AtomicLong();
        AtomicLong dns = new AtomicLong();
        int imgOperator = 0;

        if ("Megafon".equalsIgnoreCase(getOperator())) imgOperator = R.drawable.megafon;
        if ("Beeline".equalsIgnoreCase(getOperator())) imgOperator = R.drawable.beeline;
        if ("Mts".equalsIgnoreCase(getOperator())) imgOperator = R.drawable.mts;
        if ("Yota".equalsIgnoreCase(getOperator())) imgOperator = R.drawable.yota;

        getPingTime("https://google.com", pingTime -> {
            ping.set(pingTime);
        });

        getDnsResolutionTime("google.com", dnsTime -> {
            dns.set(dnsTime);
        });

        InformationModel[] items = {
                new InformationModel("Оператор: " + getOperator(), imgOperator),
                new InformationModel("Internet сигнал: " + getMobileSignalLevel() + " / 3", 0),
                new InformationModel("Wi-Fi сигнал: " + getWifiSignalLevel() + " / 3", 0)
        };

        for (int i = 0; i < items.length; i++) {
            final int index = i;
            new CountDownTimer(500 * (i + 1), 500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    infoList.add(items[index]);
                    infoAdapter.notifyItemInserted(infoList.size() - 1);

                    if (index == items.length - 1) {
                        getPingTime("https://vk.com", pingTime -> {
                            InformationModel pingItem = new InformationModel("Ping: " + pingTime + "ms", 0);
                            infoList.add(pingItem);
                            infoAdapter.notifyItemInserted(infoList.size() - 1);
                        });

                        getDnsResolutionTime("vk.com", dnsTime -> {
                            if (dnsTime == 0) return;
                            InformationModel dnsItem = new InformationModel("DNS-резолвинг: " + dnsTime + "ms", 0);
                            infoList.add(dnsItem);
                            infoAdapter.notifyItemInserted(infoList.size() - 1);
                        });

                        reBtn.animate()
                                .setDuration(350)
                                .scaleX(1f)
                                .scaleY(1f)
                                .start();
                    }
                }
            }.start();
        }
    }

    private void setupInfoRecyclerView() {
        infoListRec = findViewById(R.id.InfoListRec);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        infoListRec.setLayoutManager(layoutManager);

        infoAdapter = new InformationAdapter(infoList);
        infoListRec.setAdapter(infoAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupUrlRecyclerView() {
        urlListRec = findViewById(R.id.UrlListRec);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        urlListRec.setLayoutManager(layoutManager);

        serviceAdapter = new ServiceAdapter(urlList);
        urlListRec.setAdapter(serviceAdapter);

        fabricUrlItem();
    }

    private void checkConnect(String urlToCheck) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlToCheck);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                    int responseCode = connection.getResponseCode();

                    boolean isConnected = responseCode == HttpURLConnection.HTTP_OK;
                    ServiceModel newItem = new ServiceModel(urlToCheck, isConnected);

                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            urlList.add(newItem);
                            serviceAdapter.notifyItemInserted(urlList.size() - 1);

                            finishChecks++;
                            if (finishChecks == totalChecks) {
                                if (errorChecks != 0) {
                                    textLogs.setText("Проверка завершена. Неудачно " + errorChecks + "/" + totalChecks);
                                } else {
                                    textLogs.setText("Проверка завершена. Все запросы удачны.");
                                }
                                fabricInfoItem();
                            } else {
                                textLogs.setText("Проверка соединения " + finishChecks + "/" + totalChecks);
                            }
                        }
                    });

                    connection.disconnect();

                    Log.d("MainActivity", (isConnected ? "Successfully connected to: " : "Failed to connect to: ") + urlToCheck);

                } catch (IOException e) {
                    ServiceModel newItem = new ServiceModel(urlToCheck, false);

                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            urlList.add(newItem);
                            serviceAdapter.notifyItemInserted(urlList.size() - 1);

                            finishChecks++;
                            errorChecks++;
                            if (finishChecks == totalChecks) {
                                if (errorChecks != 0) {
                                    textLogs.setText("Проверка завершена. Неудачно " + errorChecks + "/" + totalChecks);
                                } else {
                                    textLogs.setText("Проверка завершена. Все запросы удачны.");
                                }
                                fabricInfoItem();
                            } else {
                                textLogs.setText("Проверка соединения " + finishChecks + "/" + totalChecks);
                            }
                        }
                    });

                    Log.e("MainActivity", "Failed to connect to " + urlToCheck + ": " + e.getMessage());
                }
            }
        }).start();
    }

    private String getOperator() {
        final TelephonyManager telephonyManager = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getNetworkOperatorName();
        return operator;
    }

    private int getMobileSignalLevel() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        if (cellInfoList != null && !cellInfoList.isEmpty()) {
            CellInfo cellInfo = cellInfoList.get(0);
            if (cellInfo instanceof CellInfoGsm) {
                return ((CellInfoGsm) cellInfo).getCellSignalStrength().getLevel();
            } else if (cellInfo instanceof CellInfoLte) {
                return ((CellInfoLte) cellInfo).getCellSignalStrength().getLevel();
            } else if (cellInfo instanceof CellInfoWcdma) {
                return ((CellInfoWcdma) cellInfo).getCellSignalStrength().getLevel();
            }
        }
        return -1;
    }

    private int getWifiSignalLevel() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
    }

    private void getPingTime(String url, PingCallback callback) {
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("HEAD");
                connection.connect();
                long endTime = System.currentTimeMillis();
                connection.disconnect();

                long pingTime = endTime - startTime;
                runOnUiThread(() -> callback.onResult(pingTime));
            } catch (IOException e) {
                runOnUiThread(() -> callback.onResult(-1L));
            }
        }).start();
    }

    private void getDnsResolutionTime(String host, DnsCallback callback) {
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                InetAddress.getByName(host);
                long endTime = System.currentTimeMillis();

                long dnsTime = endTime - startTime;
                runOnUiThread(() -> callback.onResult(dnsTime));
            } catch (IOException e) {
                runOnUiThread(() -> callback.onResult(-1L));
            }
        }).start();
    }

    interface PingCallback {
        void onResult(long pingTime);
    }

    interface DnsCallback {
        void onResult(long dnsTime);
    }

    private void requestLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено
            }
        }
    }

}