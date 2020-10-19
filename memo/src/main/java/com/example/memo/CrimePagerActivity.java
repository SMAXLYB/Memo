package com.example.memo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.example.memo.databinding.ActivityCrimePagerBinding;

import java.util.List;

/**
 * @author smaxlyb
 * @date 2020/4/16 14:40
 * website: https://smaxlyb.cn
 */
public class CrimePagerActivity extends AppCompatActivity {
    public static final String TAG = "CrimePagerActivity";
    public static final String EXTRA_CRIME_POSITION = "crime_position";
    private ActivityCrimePagerBinding mActivityCrimePagerBinding;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context context, int position) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        mActivityCrimePagerBinding = ActivityCrimePagerBinding.inflate(LayoutInflater.from(this));
        setContentView(mActivityCrimePagerBinding.getRoot());

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();
        // 设置适配器
        mActivityCrimePagerBinding.activityCrimePagerViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "getItem: "+position);
                return CrimeFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mCrimes.get(position).getTitle();
            }
        });
        // 设置当前页面位置
        mActivityCrimePagerBinding.activityCrimePagerViewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_CRIME_POSITION, 0));
        // 设置翻页监听
        mActivityCrimePagerBinding.activityCrimePagerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: 滑动position= " + position + " positionOffset=" + positionOffset);
                //如果只有一个
                if(mCrimes.size()-1 == 0){
                    mActivityCrimePagerBinding.lastButton.setEnabled(false);
                    mActivityCrimePagerBinding.firstButton.setEnabled(false);
                    return;
                }
                // 如果是第一个位置
                if (position == 0) {
                    mActivityCrimePagerBinding.firstButton.setEnabled(false);
                    mActivityCrimePagerBinding.lastButton.setEnabled(true);
                } else if (position == mCrimes.size() - 1) {
                    // 如果是最后一个位置
                    mActivityCrimePagerBinding.lastButton.setEnabled(false);
                    mActivityCrimePagerBinding.firstButton.setEnabled(true);
                } else {
                    mActivityCrimePagerBinding.lastButton.setEnabled(true);
                    mActivityCrimePagerBinding.firstButton.setEnabled(true);
                }
            }

            @Override
            public void onPageSelected(int position) {
                CrimeLab.mCurrentIndex = position;
                Log.d(TAG, "onPageSelected: 选择position=" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 设置tab栏
        mActivityCrimePagerBinding.tabLayout.setupWithViewPager(mActivityCrimePagerBinding.activityCrimePagerViewPager);
        // 设置按钮监听
        mActivityCrimePagerBinding.firstButton.setOnClickListener(view -> {
            mActivityCrimePagerBinding.activityCrimePagerViewPager.setCurrentItem(0);
        });
        mActivityCrimePagerBinding.lastButton.setOnClickListener(view -> {
            mActivityCrimePagerBinding.activityCrimePagerViewPager.setCurrentItem(mCrimes.size()-1);
        });
    }

}
