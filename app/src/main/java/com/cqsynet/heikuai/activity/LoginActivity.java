package com.cqsynet.heikuai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cqsynet.heikuai.R;
import com.cqsynet.heikuai.common.AppConstants;
import com.cqsynet.heikuai.common.Globals;
import com.cqsynet.heikuai.model.LoginRequestBody;
import com.cqsynet.heikuai.model.ResponseHeader;
import com.cqsynet.heikuai.model.UserInfo;
import com.cqsynet.heikuai.model.UserInfoResponseObject;
import com.cqsynet.heikuai.network.WebServiceIf;
import com.cqsynet.heikuai.network.WebServiceIf.IResponseCallback;
import com.cqsynet.heikuai.util.Md5Util;
import com.cqsynet.heikuai.util.PhoneNumberUtil;
import com.cqsynet.heikuai.util.SharedPreferencesInfo;
import com.cqsynet.heikuai.util.ToastUtil;
import com.google.gson.Gson;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTilUsername;
    private TextInputLayout mTilPassword;
    private AutoCompleteTextView mAtvUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView() {
        mAtvUsername = findViewById(R.id.tvUsername_activity_login);
        mEtPassword = findViewById(R.id.tvPassword_activity_login);
        mTilUsername = findViewById(R.id.tilUsername_activity_login);
        mTilPassword = findViewById(R.id.tilPassword_activity_login);
        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBtnLogin = findViewById(R.id.btnLogin_activity_login);
        mBtnLogin.setOnClickListener(this);
        findViewById(R.id.btnForgotPassword_activity_login).setOnClickListener(this);
        findViewById(R.id.btnRegister_activity_login).setOnClickListener(this);

        mAtvUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    checkUsername();
                }
            }
        });

        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    checkPassword();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin_activity_login:
//                attemptLogin();
                login("18680933386", "111111");
                break;
            case R.id.btnForgotPassword_activity_login:
                break;
            case R.id.btnRegister_activity_login:
                break;
        }
    }

    /**
     * 校验用户名
     * @return
     */
    private boolean checkUsername() {
        mTilUsername.setError(null);
        String userName = mAtvUsername.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            mTilUsername.setError(getString(R.string.phonenum_empty));
        } else if (!PhoneNumberUtil.isMobileNum(userName)) {
            mTilUsername.setError(getString(R.string.invalid_phonenum));
            return false;
        }
        return true;

    }

    /**
     * 校验密码
     * @return
     */
    private boolean checkPassword() {
        mTilPassword.setError(null);
        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mTilPassword.setError(getString(R.string.password_empty));
        } else if (!PhoneNumberUtil.isPasswordValid(password)) {
            mTilPassword.setError(getString(R.string.invalid_password));
        }
        return true;

    }

    /**
     * 发起登录
     */
    private void attemptLogin() {
        if(checkUsername() && checkPassword()) {
            hideInput(mBtnLogin);
            login(mAtvUsername.getText().toString(), mEtPassword.getText().toString());
        }
    }

    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @param phoneNum 要登陆的电话号码
     * @param psw      登陆密码
     * @Description: 调用登陆接口发起登陆，并处理服务器返回信息
     * @return: void
     */
    private void login(String phoneNum, String psw) {
        mProgressDialog.setMessage(getString(R.string.logining));
        mProgressDialog.show();
        final LoginRequestBody loginRequestBody = new LoginRequestBody();
        loginRequestBody.phoneNo = phoneNum;
        loginRequestBody.password = Md5Util.MD5(psw);
        loginRequestBody.rsaPubKey = "";
        IResponseCallback loginCallbackIf = new IResponseCallback() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    Gson gson = new Gson();
                    UserInfoResponseObject responseObj = gson.fromJson(response, UserInfoResponseObject.class);
                    ResponseHeader header = responseObj.header;
                    if (header != null) {
                        if (AppConstants.RET_OK.equals(header.ret)) {
                            try {
                                UserInfo body = responseObj.body;
                                if (!TextUtils.isEmpty(body.userAccount) && !TextUtils.isEmpty(body.rsaPubKey)) {
                                    SharedPreferencesInfo.setTagString(LoginActivity.this,
                                            SharedPreferencesInfo.PHONE_NUM, loginRequestBody.phoneNo);
                                    SharedPreferencesInfo.setTagString(LoginActivity.this,
                                            SharedPreferencesInfo.ACCOUNT, body.userAccount);
                                    SharedPreferencesInfo.setTagString(LoginActivity.this,
                                            SharedPreferencesInfo.RSA_KEY, body.rsaPubKey);
                                    SharedPreferencesInfo.setTagInt(LoginActivity.this,
                                            SharedPreferencesInfo.IS_LOGIIN, 1);
                                    Globals.g_userInfo = body;
                                    SharedPreferencesInfo.setTagString(LoginActivity.this, SharedPreferencesInfo.USER_INFO, gson.toJson(body));
                                    Globals.g_tempPriSign = ""; //清空签名,重新生成
                                    Intent broadcast = new Intent(AppConstants.ACTION_SOCKET_LOGIN);
                                    sendBroadcast(broadcast);
                                    Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(home);
                                    LoginActivity.this.finish();
                                }
                            } catch (ClassCastException e) {
                                ToastUtil.showToast(LoginActivity.this, R.string.login_fail);
                            }
                        } else {
                            ToastUtil.showToast(LoginActivity.this, header.errMsg);
                        }
                    }
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onErrorResponse() {
                ToastUtil.showToast(LoginActivity.this, R.string.login_fail);
                mProgressDialog.dismiss();
            }
        };
        // 调用接口发起登陆
        WebServiceIf.login(this, loginRequestBody, loginCallbackIf);
    }
}

