/*
  Copyright 2014-2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.example.openquick.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.openquick.demos.MarkerActivity;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;


public class SampleSignupActivity extends BaseActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                int result = errorResult.getErrorCode();
                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Logger.e("onSessionClosed");
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                Log.e("msg", result.toString());

                String id = String.valueOf(result.getId());
                String nickname = result.getNickname();

                String email = null;

                if (!result.getKakaoAccount().emailNeedsAgreement().getBoolean()) {
                    email = result.getKakaoAccount().getEmail();
                }

                Log.e("msg", "email " + email);

                JSONObject params = new JSONObject();

                try {
                    params.put("id", id);
                    params.put("nickname", nickname);

                    if (email != null)
                        params.put("email", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://172.16.22.239:8080/openQuick/login.jsp";

                HttpConnection connection = new HttpConnection(params.toString(), url);
                connection.start();

                try {
                    connection.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String value = connection.getResult();

                if (value.equals("Success")) {
                    redirectMainActivity();
                } else {
                    redirectLoginActivity();
                }
            }
        });
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(this, MarkerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}