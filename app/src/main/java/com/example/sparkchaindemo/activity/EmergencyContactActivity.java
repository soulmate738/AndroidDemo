package com.example.sparkchaindemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.ContactAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmergencyContactActivity extends BaseActivity implements LocationListener, ContactAdapter.OnClickListener {
    private List<Contact> contactList = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private static final String TAG = "EmergencyContactActivity";
    private String location;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private boolean isLocationPermissionGranted = false;
    private int currentPosition; // 记录当前点击的item位置
    private Button btnBack, btnAdd;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog; // 加载对话框

    // SharedPreferences相关
    private SharedPreferences sharedPreferences;
    private static final String CONTACTS_KEY = "contacts";
    private Gson gson = new Gson(); // JSON解析工具

    // 在类顶部添加
    private static final String PHONE_NUMBER_PATTERN =
            "^((\\+86)|(86))?(1[3-9]\\d{9})$"; // 匹配中国大陆手机号正则表达式

    // 在适当位置添加验证方法（建议在showAddContactDialog附近）
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_NUMBER_PATTERN);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();

        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        loadContactsFromSharedPreferences(); // 启动时加载数据
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_emergency_contact);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void initData() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    protected void initListener() {
        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());

        // 添加按钮点击事件
        btnAdd.setOnClickListener(v -> showAddContactDialog());
    }

    // 显示添加联系人对话框
    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加紧急联系人");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_comtact, null);
        final EditText etName = view.findViewById(R.id.et_name);
        final EditText etPhone = view.findViewById(R.id.et_phone);
        builder.setView(view);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches(PHONE_NUMBER_PATTERN)) {
                Toast.makeText(this, "请输入有效的手机号（如13812345678）", Toast.LENGTH_SHORT).show();
                return;
            }


            // 添加联系人到列表
            Contact newContact = new Contact(name, phone);
            int position = contactList.size();
            contactList.add(newContact);

            // 保存到SharedPreferences
            saveContactsToSharedPreferences();

            // 刷新视图
            contactAdapter.notifyItemInserted(position);
            recyclerView.scrollToPosition(position);

            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 显示编辑联系人对话框
    private void showEditContactDialog(final int position, final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("编辑紧急联系人");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_comtact, null);
        final EditText etName = view.findViewById(R.id.et_name);
        final EditText etPhone = view.findViewById(R.id.et_phone);
        etName.setText(contact.getName());
        etPhone.setText(contact.getPhone());
        builder.setView(view);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches(PHONE_NUMBER_PATTERN)) {
                Toast.makeText(this, "请输入有效的手机号（如13812345678）", Toast.LENGTH_SHORT).show();
                return;
            }
            contact.setName(name);
            contact.setPhone(phone);
            contactAdapter.notifyItemChanged(position);
            saveContactsToSharedPreferences(); // 保存数据
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onEditClick(int position) {
        showEditContactDialog(position, contactList.get(position));
    }

    @Override
    public void onDeleteClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这个联系人吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    contactList.remove(position);
                    contactAdapter.notifyItemRemoved(position);
                    saveContactsToSharedPreferences(); // 保存数据
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onSendClick(int position) {
        currentPosition = position; // 记录当前点击的位置
        checkLocationPermission();
    }

    // 检查位置权限
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            isLocationPermissionGranted = true;
            showLoadingDialog(); // 显示加载对话框
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                showLoadingDialog(); // 显示加载对话框
                startLocationUpdates();
            } else {
                Toast.makeText(this, "位置权限被拒绝，无法获取地址", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 启动位置更新
    private void startLocationUpdates() {
        if (locationManager != null) {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                dismissLoadingDialog(); // 隐藏加载对话框
                showLocationSettingsDialog();
                return;
            }

            try {
                if (isGpsEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                dismissLoadingDialog(); // 隐藏加载对话框
                Toast.makeText(this, "无法获取位置信息", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 显示位置设置对话框
    private void showLocationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("位置服务未开启")
                .setMessage("为获取精确位置，请开启GPS定位")
                .setPositiveButton("前往设置", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("取消", null)
                .show();
    }

    // 位置更新回调
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("Location", "获取到位置: " + location.getLatitude() + ", " + location.getLongitude());
            stopLocationUpdates();
            getAddressFromLocation(location);
        }
    }

    // 停止位置更新
    private void stopLocationUpdates() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    // 将经纬度转换为地址
    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressText = new StringBuilder();

                if (address.getAdminArea() != null) addressText.append(address.getAdminArea());
                if (address.getLocality() != null) addressText.append(address.getLocality());
                if (address.getSubAdminArea() != null) addressText.append(address.getSubAdminArea());
                if (address.getThoroughfare() != null) addressText.append(address.getThoroughfare());
                if (address.getSubThoroughfare() != null) addressText.append(address.getSubThoroughfare());
                if (address.getFeatureName() != null && !address.getFeatureName().equals(address.getThoroughfare()))
                    addressText.append(" ").append(address.getFeatureName());

                dismissLoadingDialog(); // 隐藏加载对话框
                sendSmsWithLocation(addressText.toString(), currentPosition);
            } else {
                dismissLoadingDialog(); // 隐藏加载对话框
                Toast.makeText(this, "无法解析地址", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            dismissLoadingDialog(); // 隐藏加载对话框
            Toast.makeText(this, "获取地址失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 发送包含位置信息的短信给指定位置的联系人
    private void sendSmsWithLocation(String locationAddress, int position) {
        Contact contact = contactList.get(position);
        Uri smsToUri = Uri.parse("smsto:" + contact.getPhone().trim());
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", "我的位置信息: " + locationAddress);
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            dismissLoadingDialog(); // 隐藏加载对话框
            stopLocationUpdates();
            showLocationSettingsDialog();
        }
    }

    @Override
    public void onCallClick(int position) {
        // 拨打电话
        String phoneNumber = contactList.get(position).getPhone().trim();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // 显示加载对话框
    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在获取位置信息...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    // 隐藏加载对话框
    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // 从SharedPreferences加载联系人数据
    @SuppressLint("NotifyDataSetChanged")
    private void loadContactsFromSharedPreferences() {
        contactList.clear(); // 清空原有数据
        String contactsJson = sharedPreferences.getString(CONTACTS_KEY, null);

        if (contactsJson != null) {
            try {
                // 使用Gson解析JSON数据
                Type listType = new TypeToken<List<Contact>>() {}.getType();
                List<Contact> loadedContacts = gson.fromJson(contactsJson, listType);

                if (loadedContacts != null) {
                    contactList.addAll(loadedContacts);
                    Log.d(TAG, "从SharedPreferences加载了" + loadedContacts.size() + "个联系人");
                }
            } catch (Exception e) {
                Log.e(TAG, "解析联系人数据失败: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "加载联系人数据失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "SharedPreferences中没有保存的联系人数据");
        }

        contactAdapter.notifyDataSetChanged(); // 更新列表显示
    }

    // 将联系人数据保存到SharedPreferences
    private void saveContactsToSharedPreferences() {
        try {
            // 将联系人列表转换为JSON字符串
            String contactsJson = gson.toJson(contactList);

            // 保存到SharedPreferences
            sharedPreferences.edit()
                    .putString(CONTACTS_KEY, contactsJson)
                    .apply();

            Log.d(TAG, "已保存" + contactList.size() + "个联系人到SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "保存联系人数据失败: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "保存联系人数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 确保每次返回页面时都刷新数据
        loadContactsFromSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        dismissLoadingDialog();
    }

    // 联系人数据类 - 实现Parcelable接口以便在Intent中传递
    public static class Contact {
        private String name;
        private String phone;

        public Contact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}