package com.example.sparkchaindemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BaoJingActivity extends BaseActivity implements LocationListener {
    private String TAG = "BaoJingActivity";
    private TextView mQh;
    private EditText mBJPhone, mContent;
    private CardView mInsertDz, mSendMsg, mHuJiao;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private boolean isLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_bao_jing);
        mQh = findViewById(R.id.tv_qh);
        mBJPhone = findViewById(R.id.tv_phone);
        mContent = findViewById(R.id.tv_bj_content);
        mInsertDz = findViewById(R.id.insert_dz);
        mSendMsg = findViewById(R.id.send_msg);
        mHuJiao = findViewById(R.id.call);
    }

    @Override
    protected void initData() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void initListener() {
        mQh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaoJingActivity.this, QhActivity.class);
                startActivity(intent);
            }
        });
        mInsertDz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 获取当前地址信息
                // 检查并请求位置权限
                checkLocationPermission();

            }
        });
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发送短信
                String message = mContent.getText().toString();  // 短信内容
                // 创建短信 Intent
                Uri smsToUri = Uri.parse("smsto:12110");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }
        });

        mHuJiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拨打电话
                // 获取需要拨打的电话号码
                String phoneNumber = mBJPhone.getText().toString();
                // 创建意图(Intent)对象，设置动作为拨号
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // 将电话号码作为数据传递给意图
                intent.setData(Uri.parse("tel:" + phoneNumber));
                // 启动拨号意图，跳转到拨号页面
                startActivity(intent);
            }
        });
    }

    // 检查位置权限
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            isLocationPermissionGranted = true;
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                startLocationUpdates();
            } else {
                Toast.makeText(this, "位置权限被拒绝，无法获取地址", Toast.LENGTH_SHORT).show();
                mContent.setText("位置权限被拒绝，请在设置中授予权限");
            }
        }
    }

    // 启动位置更新
    private void startLocationUpdates() {
        // 检查GPS是否开启
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            showLocationSettingsDialog();
            return;
        }

        mContent.setText("正在获取位置...");
        mInsertDz.setEnabled(false);

        try {
            // 优先使用GPS定位（高精度）
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,       // 最小更新间隔（毫秒）
                        10,         // 最小更新距离（米）
                        (LocationListener) this);
            } else if (isNetworkEnabled) {
                // 备用：使用网络定位
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000,
                        10,
                        (LocationListener) this);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            mContent.setText("安全异常：无法获取位置");
        }
    }

    // 显示位置设置对话框
    private void showLocationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("位置服务未开启")
                .setMessage("为获取精确位置，请开启GPS定位")
                .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mContent.setText("位置服务未开启，无法获取地址");
                    }
                })
                .show();
    }

    // 位置更新回调
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("Location", "获取到位置: " + location.getLatitude() + ", " + location.getLongitude());
            stopLocationUpdates(); // 获取到位置后停止更新，节省电量

            // 检查精度是否满足要求（小于100米通常可定位到小区）
            if (location.getAccuracy() < 100) {
                getAddressFromLocation(location);
            } else {
                mContent.setText("位置精度不足（" + location.getAccuracy() + "米）\n" +
                        "请移至开阔区域或等待GPS信号增强");
            }
        }
    }

    // 停止位置更新
    private void stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this);
            mInsertDz.setEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // 将经纬度转换为地址
    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                // 按省市区街道小区顺序构建地址
                if (address.getAdminArea() != null) {
                    addressText.append(address.getAdminArea()).append("省");
                }
                if (address.getLocality() != null) {
                    addressText.append(address.getLocality()).append("市");
                }
                if (address.getSubAdminArea() != null) {
                    addressText.append(address.getSubAdminArea()).append("区");
                }
                if (address.getThoroughfare() != null) {
                    addressText.append(address.getThoroughfare());
                }
                if (address.getSubThoroughfare() != null) {
                    addressText.append(address.getSubThoroughfare());
                }
                if (address.getFeatureName() != null &&
                        !address.getFeatureName().equals(address.getThoroughfare())) {
                    addressText.append(" ").append(address.getFeatureName());
                }

                // 添加精度信息
                addressText.append("\n\n精度: ").append(location.getAccuracy()).append("米");

                runOnUiThread(() -> {
                    mContent.setText("当前位置：\n" + addressText.toString());
                });
            } else {
                runOnUiThread(() -> {
                    mContent.setText("无法解析地址\n" +
                            "纬度: " + location.getLatitude() + "\n" +
                            "经度: " + location.getLongitude() + "\n" +
                            "精度: " + location.getAccuracy() + "米");
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                mContent.setText("地址解析失败\n" + e.getMessage());
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // 位置提供者状态变化时的回调
    }

    @Override
    public void onProviderEnabled(String provider) {
        // 位置提供者启用时的回调
    }

    @Override
    public void onProviderDisabled(String provider) {
        // 位置提供者禁用时的回调
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            stopLocationUpdates();
            showLocationSettingsDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}