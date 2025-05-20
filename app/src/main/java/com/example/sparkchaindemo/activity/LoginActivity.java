package com.example.sparkchaindemo.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.utils.NumsUtil;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.leancloud.LCUser;
import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.callback.LCIMClientCallback;
import cn.leancloud.sms.LCSMS;
import cn.leancloud.sms.LCSMSOption;
import cn.leancloud.types.LCNull;
import io.reactivex.rxjava3.disposables.Disposable;


public class LoginActivity extends BaseActivity {
    private EditText mMobile, mSmsCode;
    private TextView mGetSmsCode;
    private Button mLogin;
    private CheckBox mNotice;
    private String mobile, smsCode;
    private Disposable disposable;
    private CountDownTimer countDownTimer;// 计时器
    private static final long COUNT_DOWN_INTERVAL = 1000; // 每次倒计时间间隔1s
    private static final long COUNT_DOWN_PERIOD = 60 * COUNT_DOWN_INTERVAL; // 倒计总时长60s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        mMobile = findViewById(R.id.et_mobile);
        mSmsCode = findViewById(R.id.et_sms_code);
        mGetSmsCode = findViewById(R.id.tv_get_sms_code);
        mLogin = findViewById(R.id.btn_login);
        mNotice = findViewById(R.id.cb_login_notice);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mGetSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mMobile.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入手机号码");
                } else if (!NumsUtil.checkTel(mobile)) {
                    showToast("请输入有效的电话号码");
                } else {
                    // 发送验证码
                    // BmobSMS.requestSMSCode(mobile, "lxq",new QueryListener<Integer>() {
                    //     @Override
                    //     public void done(Integer smsId, BmobException e) {
                    //         if (e == null) {
                    //             showToast("验证码发送成功"+ smsId);
                    //             Log.d(TAG, "验证码发送成功"+ smsId);
                    //         }else {
                    //             showToast("验证码发送失败"+ e.getMessage());
                    //             Log.d(TAG, "验证码发送失败"+ e.getMessage());
                    //         }
                    //     }
                    // });
                    // 发送验证码
                    // sendMssage();
                    // 开启倒计时
                    startCountDownTimer();
                }
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile = mMobile.getText().toString().trim();
                smsCode = mSmsCode.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入手机号码");
                } else if (!NumsUtil.checkTel(mobile)) {
                    showToast("请输入有效的电话号码");
                } else if (TextUtils.isEmpty(smsCode)) {
                    showToast("请输入验证码");
                } else if (!mNotice.isChecked()) {
                    showToast("请阅读并同意相关协议");
                } else {
                    // 判断验证码是否正确
                    // BmobUser.signOrLoginByMobilePhone(mobile, smsCode, new LogInListener<BmobUser>() {
                    //     @Override
                    //     public void done(BmobUser bmobUser, BmobException e) {
                    //         if (e == null) {
                    //             verifyMessage();
                    //         } else {
                    //             showToast("登录失败,验证码错误"+e.getErrorCode() + "-" + e.getMessage() + "\n");
                    //             Log.d(TAG, "登录失败,验证码错误"+e.getErrorCode() + "-" + e.getMessage() + "\n");
                    //         }
                    //     }
                    // });
                    // 判断验证码是否正确
                    verifyMessage();
                }
            }
        });
    }

    private void sendMssage() {
        LCSMSOption option = new LCSMSOption();
        option.setTemplateName("聆心桥");
        option.setSignatureName("聆心桥");
        LCSMS.requestSMSCodeInBackground(mobile, option).subscribe(new io.reactivex.Observer<LCNull>() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {

            }

            @Override
            public void onNext(LCNull lcNull) {
                showToast("验证码发送成功");
            }

            @Override
            public void onError(Throwable e) {
                showToast("验证码发送失败");
                Log.d(TAG, "验证码发送失败" + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void verifyMessage() {
        LCUser.loginByMobilePhoneNumber(mobile, "111").subscribe(new io.reactivex.Observer<LCUser>() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {

            }

            public void onNext(LCUser user) {
                // 登录成功，与服务器连接
                LCIMClient client = LCIMClient.getInstance(user);
                client.open(new LCIMClientCallback() {
                    @Override
                    public void done(final LCIMClient avimClient, LCIMException e) {
                        // 执行其他逻辑
                        if (null == e) {
                            // 登录成功
                            // 将用户信息存储到本地sharedPreferences中
                            Log.d(TAG, "--------登录成功且IM登陆成功");
                        }
                    }
                });
                // 登录成功
                // 将用户信息存储到本地sharedPreferences中
                SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", user.getUsername());
                editor.putString("mobile", user.getMobilePhoneNumber());
                editor.putString("objectId", user.getObjectId());
                editor.apply();
                // 跳转到主界面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
                Log.d(TAG, "登录失败" + throwable.getMessage());
            }
            public void onComplete() {}
        });
        // LCSMS.verifySMSCodeInBackground(smsCode, mobile).subscribe(new io.reactivex.Observer<LCNull>() {
        //     @Override
        //     public void onSubscribe(io.reactivex.disposables.Disposable d) {
        //
        //     }
        //
        //     @Override
        //     public void onNext(@NonNull LCNull lcNull) {
        //         showToast("验证成功");
        //         // 跳转到主界面
        //         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //         startActivity(intent);
        //         finish();
        //     }
        //
        //     @Override
        //     public void onError(@NonNull Throwable e) {
        //         showToast("验证失败");
        //         Log.d(TAG, "验证失败" + e.getMessage());
        //     }
        //
        //     @Override
        //     public void onComplete() {
        //
        //     }
        // });
    }


    private void startCountDownTimer() {
        // 如果有计时器在运行，先取消
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // 创建新的计时器并启动
        countDownTimer = new CountDownTimer(COUNT_DOWN_PERIOD, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 计算剩余秒数，并更新按钮文本
                long secondsUntilFinished = millisUntilFinished / 1000;
                mGetSmsCode.setText(secondsUntilFinished % 60 + "秒重新获取");
            }

            @Override
            public void onFinish() {
                // 倒计时结束
                mGetSmsCode.setText("获取验证码");
                mGetSmsCode.setEnabled(true);
            }
        }.start();// 启动计时器
        // 禁止使用获取验证码，防止重复点击
        mGetSmsCode.setEnabled(false);
    }
}