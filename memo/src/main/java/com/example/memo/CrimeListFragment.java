package com.example.memo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.memo.databinding.FragmentCrimeListBinding;
import com.example.memo.databinding.ListItemCrimeBinding;

import java.util.List;

public class CrimeListFragment extends Fragment {
    public static final String TAG = "CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private FragmentCrimeListBinding mFragmentCrimeListBinding;
    private CrimeAdapter mCrimeAdapter;
    private static int createTime = 0;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu: 创建菜单");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                int position = CrimeLab.get(getActivity()).addCrime(crime);
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    // 如果是单面板
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), position);
                    startActivity(intent);
                } else {
                    // 如果是双面板
                    Fragment fragment = CrimeFragment.newInstance(position-1);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,fragment).commit();
                }
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                // 重建工具栏
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle, crimeCount, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView更新数据");
        mFragmentCrimeListBinding = FragmentCrimeListBinding.inflate(inflater, container, false);
        mFragmentCrimeListBinding.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return mFragmentCrimeListBinding.getRoot();
    }

    // Holder
    private class CrimeHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        public CrimeHolder(View view) {
            super(view);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }
    }

    // Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> implements ItemTouchHelperCallBack{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder" + (++createTime));
            View v = ListItemCrimeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();

            // 根据类型选择不同布局
            // if (viewType == 0) {
            //     v = ListItemCrimeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            // } else if (viewType == 1) {
            //     v = ListItemCrimePoliceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
            // }
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder" + position);
            Crime crime = mCrimes.get(position);
            holder.mTitleTextView.setText(crime.getTitle());
            String date = DateFormat.format("EEEE, MMMM dd, yyyy", crime.getDate()).toString();
            holder.mDateTextView.setText(date);
            holder.mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(view -> {
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    // 如果是单面板
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), position);
                    startActivityForResult(intent,1);
                    // startActivity(intent);
                } else {
                    // 如果是双面板
                    Fragment fragment = CrimeFragment.newInstance(position);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,fragment).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount");
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType");
            Crime crime = mCrimes.get(position);
            return crime.getRequiredPolice();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            CrimeLab.get(getActivity()).deleteCrime( mCrimes.get(position));
            mCrimes.remove(position);
            notifyItemRemoved(position);
        }
    }

    // 更新UI
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimeLab.getCrimes());
            mFragmentCrimeListBinding.crimeRecyclerView.setAdapter(mCrimeAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelper(mCrimeAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(mFragmentCrimeListBinding.crimeRecyclerView);
        } else {
            mCrimeAdapter.setCrimes(crimeLab.getCrimes());
            mCrimeAdapter.notifyDataSetChanged();
        }
        mFragmentCrimeListBinding.noCrime.setVisibility(crimeLab.getCrimes().size() == 0 ? View.VISIBLE : View.INVISIBLE);
        updateSubtitle();
    }


    // 活动结果回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult接收到了消息：" + data.getStringExtra("Hello"));
        }
    }

    // 委托回调接口
    public interface Callbacks {
        void onCrimeSelected(int position);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume更新数据");
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mFragmentCrimeListBinding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
}
