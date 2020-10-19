package com.example.memo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks{
    private static final String TAG = "CrimeListActivity";
    @Override
    public CrimeListFragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult接收到了消息：" + data.getStringExtra("Hello"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: 创建菜单");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCrimeSelected(int position) {
        // 如果是单面板，启动新的activity
        if(findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this,position);
            startActivity(intent);
        }else{
            // 如果是双面板，直接替换fragment
            Fragment newDetail = CrimeFragment.newInstance(position);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,newDetail).commit();
        }
    }
}
