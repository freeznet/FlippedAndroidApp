package com.hkust.android.hack.flipped.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;
import com.hkust.android.hack.flipped.R;
import com.hkust.android.hack.flipped.core.Constants;
import com.hkust.android.hack.flipped.core.User;
import com.hkust.android.hack.flipped.ui.TextWatcherAdapter;
import com.hkust.android.hack.flipped.util.Ln;
import com.hkust.android.hack.flipped.util.SafeAsyncTask;
import com.hkust.android.hack.flipped.util.Strings;

import butterknife.InjectView;
import butterknife.Views;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.github.kevinsawicki.http.HttpRequest.post;

/**
 * Created by rui on 14-4-26.
 */
public class BootstrapAccountRegisterActivity extends ActionBarAccountAuthenticatorActivity {

    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_EMAIL = "email";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    @InjectView(R.id.et_email)
    AutoCompleteTextView mEtEmail;
    @InjectView(R.id.et_password)
    EditText mEtPassword;
    @InjectView(R.id.et_password2)
    EditText mEtPassword2;
    @InjectView(R.id.b_signin)
    Button mBSignin;
    @InjectView(R.id.et_name)
    EditText mEtName;
    @InjectView(R.id.ib_register)
    ImageButton mIbBack;
    private SafeAsyncTask<Boolean> authenticationTask;

    private TextWatcher watcher = validationTextWatcher();

    private AccountManager accountManager;

    String username;
    String pass;
    String emailadd;
    private String token = null;

    private int accountid = -1;

    private String authToken;
    private String authTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.register_activity);

        setTitle("Sign Up");
        Views.inject(this);

        accountManager = AccountManager.get(this);

//        accountID.addTextChangedListener(watcher);
        mEtEmail.addTextChangedListener(watcher);
        mEtPassword.addTextChangedListener(watcher);
        mEtPassword2.addTextChangedListener(watcher);

        mEtEmail.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mEtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == IME_ACTION_DONE && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mEtPassword.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == IME_ACTION_DONE && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mEtPassword2.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mEtPassword2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == IME_ACTION_DONE && mBSignin.isEnabled()) {
                    handleReg(mBSignin);
                    return true;
                }
                return false;
            }
        });

        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        String myemail = mEtEmail.getText().toString();
        boolean populated = populated(mEtEmail) && populated(mEtPassword) && populated(mEtPassword2) && mEtPassword.getText().toString().equals(mEtPassword2.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(myemail).matches();
        mBSignin.setEnabled(populated);
//        if (!mEtPassword.getText().toString().equals(mEtPassword2.getText().toString())) {
//            mTvNotice.setText(getString(R.string.register_check_password));
//        } else {
//            if (myemail.length() == 0 || Patterns.EMAIL_ADDRESS.matcher(myemail).matches())
//                mTvNotice.setText("");
//            else
//                mTvNotice.setText(getString(R.string.register_check_email));
//        }
    }

    private boolean populated(EditText editText) {
        return editText.length() >= 6;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.message_signing_up));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (authenticationTask != null) {
                    authenticationTask.cancel(true);
                }
            }
        });
        return dialog;
    }

    public void handleReg(View view) {
        if (authenticationTask != null)
            return;

        username = mEtName.getText().toString();
        emailadd = mEtEmail.getText().toString();
        pass = mEtPassword.getText().toString();
        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                final String query = String.format("data={\"username\":\"%s\", \"password\":\"%s\", \"email\":\"%s\"}", username, pass, emailadd);

                HttpRequest request = post(Constants.Http.URL_REG).contentType("application/json").send(query);

                Ln.d("Authentication response=%s", request.code());

                if (request.ok()) {
                    final User model = new Gson().fromJson(Strings.toString(request.buffer()), User.class);
//                    model.setEmail(emailadd);
//                    token = model.getToken();
//                    accountid = model.getId();
//                    Storage.user = model;
//                    Storage.writeUser();
                    Ln.d("token = %s %d", token, accountid);
                }

                return request.ok();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Throwable cause = e.getCause() != null ? e.getCause() : e;

                String message;
                // A 404 is returned as an Exception with this message
                if ("Received authentication challenge is null".equals(cause
                        .getMessage()))
                    message = "Sign Up Error.";
                else
                    message = cause.getMessage();

                Toaster.showLong(BootstrapAccountRegisterActivity.this, message);
            }

            @Override
            public void onSuccess(Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    protected void finishLogin() {
        final Account account = new Account(emailadd, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        accountManager.addAccountExplicitly(account, pass, null);


        authToken = String.valueOf(accountid) + ":" + token;

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, emailadd);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }

        accountManager.setAuthToken(account, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE, authToken);

        intent.putExtra(KEY_BOOLEAN_RESULT, accountid);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        finish();
    }

    public void onAuthenticationResult(boolean result) {
        if (result)
            finishLogin();
        else {
            Toaster.showLong(BootstrapAccountRegisterActivity.this, "Sign Up Error...");
        }
    }


}
