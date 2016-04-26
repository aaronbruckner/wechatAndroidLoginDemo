package com.aaronbruckner.wechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aaronbruckner.wechat.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Login demonstration to generate an authorization code through WeChat.
 */
public class WeChatLoginActivity extends AppCompatActivity {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_login);

        api = WXAPIFactory.createWXAPI(this, WXEntryActivity.API_ID , false);
    }

    public void onResume(){
        super.onResume();
        if(WXEntryActivity.token != null){
            Toast.makeText(this, "Token: " + WXEntryActivity.token, Toast.LENGTH_LONG).show();
            WXEntryActivity.token = null;
        }
    }

    /**
     * Register first. This notifies WeChat about your application.
     * @param view
     */
    public void onClickRegisterButton(View view) {
        boolean success = api.registerApp(WXEntryActivity.API_ID);
        Toast.makeText(this, "Registration Success: " + success, Toast.LENGTH_LONG).show();
    }

    /**
     * Send an authorization request to WeChat. If successful, you will be moved to the WeChat
     * app and you should see the application details you registered through the portal. When
     * you grant access, WeChat should close and sent a response to WXEntryActivity.
     * @param view
     */
    public void onClickLoginButton(View view) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        api.sendReq(req);
    }
}
