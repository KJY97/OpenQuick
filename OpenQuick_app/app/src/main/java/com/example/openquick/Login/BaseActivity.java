package com.example.openquick.Login;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;


public abstract class BaseActivity extends FragmentActivity {
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
