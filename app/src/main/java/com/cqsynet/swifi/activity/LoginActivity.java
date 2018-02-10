package com.cqsynet.swifi.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cqsynet.swifi.R;
import com.cqsynet.swifi.util.PhoneNumberUtil;

/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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
                attemptLogin();
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
        }
    }

    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

