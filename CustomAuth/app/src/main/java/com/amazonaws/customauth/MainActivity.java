package com.amazonaws.customauth;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoSecretHash;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.Hkdf;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.util.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String USERNAME = "bimin3"; // TODO REPLACE_ME
    public static final String PASSWORD = "1234Password!"; // TODO REPLACE_ME

    private CognitoUserPool pool;
    private String mfaCode;
    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();
        pool = new CognitoUserPool(context, new AWSConfiguration(context));
        pool.getCurrentUser().signOut();
    }

    public void customSignIn(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pool.getUser(USERNAME).getSession(new AuthenticationHandler() {
                    @Override
                    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                        Log.d(TAG, "onSuccess: " + userSession.getAccessToken());
                    }

                    @Override
                    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                        Log.d(TAG, "getAuthenticationDetails: ");

                        final HashMap<String, String> authParameters = new HashMap<>();
                        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, PASSWORD, authParameters, null);
                        authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                        authenticationContinuation.continueTask();
                    }

                    @Override
                    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                        Log.d(TAG, "getMFACode: ");
                        latch = new CountDownLatch(1);
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continuation.setMfaCode(MainActivity.this.mfaCode);
                        continuation.continueTask();
                    }

                    @Override
                    public void authenticationChallenge(ChallengeContinuation continuation) {
                        Log.d(TAG, "authenticationChallenge: " + continuation.getChallengeName());
                        continuation.setChallengeResponse(CognitoServiceConstants.CHLG_RESP_ANSWER, "5");
                        continuation.continueTask();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "onFailure: ", exception);
                    }
                });
            }
        }).start();
    }

    /**
     * Retrieves the MFA code from the UI.
     *
     * @param view
     */
    public void setMFA(View view) {
        this.mfaCode = ((EditText) findViewById(R.id.mfaCodeEditText)).getText().toString();
        if (latch != null) {
            latch.countDown();
        }
    }
}
