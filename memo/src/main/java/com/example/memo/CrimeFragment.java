package com.example.memo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.example.memo.databinding.FragmentCrimeBinding;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author smaxlyb
 * @date 2020/4/12 22:24
 * website: https://smaxlyb.cn
 */
public class CrimeFragment extends Fragment {
    // 测试用
    private static int mTotalPage = 0;
    private int mCurrentPage = ++mTotalPage;

    private int position;
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    public static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_POSITION = "crime_position";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private FragmentCrimeBinding mFragmentCrimeBinding;
    private Crime mCrime;
    private File mPhotoFile;

    public static CrimeFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CRIME_POSITION, position);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach" + mCurrentPage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_CRIME_POSITION, 0);
        mCrime = CrimeLab.get(getActivity()).getCrimes().get(position);

        Log.d(TAG, "onCreate" + mCurrentPage + " position" + position);


        //设置返回结果
        Intent data = new Intent();
        data.putExtra("Hello", "hello");
        getActivity().setResult(Activity.RESULT_OK, data);

        // 保存图片的路径
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView" + mCurrentPage);
        // 加载布局
        mFragmentCrimeBinding = FragmentCrimeBinding.inflate(inflater, container, false);
        // 设置标题
        mFragmentCrimeBinding.crimeTitle.setText(mCrime.getTitle());
        mFragmentCrimeBinding.crimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCrime.setTitle(s.toString());
            }
        });
        // 设置日期
        updateDate();
        mFragmentCrimeBinding.crimeData.setOnClickListener(view -> {
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(this, REQUEST_DATE);
            dialog.show(getActivity().getSupportFragmentManager(), DIALOG_DATE);
        });
        // 设置时间
        updateTime();
        mFragmentCrimeBinding.crimeTime.setOnClickListener(view -> {
            TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(this, REQUEST_TIME);
            dialog.show(getActivity().getSupportFragmentManager(), DIALOG_TIME);
        });
        //设置复选框
        mFragmentCrimeBinding.crimeSolved.setChecked(mCrime.isSolved());
        mFragmentCrimeBinding.crimeSolved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCrime.setSolved(isChecked);
        });
        // 设置联系按钮
        if (!TextUtils.isEmpty(mCrime.getSuspect())) {
            mFragmentCrimeBinding.crimeSuspect.setText(mCrime.getSuspect());
        }
        mFragmentCrimeBinding.crimeSuspect.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            // intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CONTACT);
                // startActivity(intent);
            }
        });
        // 设置短信按钮
        mFragmentCrimeBinding.crimeReport.setOnClickListener(view -> {
            // Intent intent = new Intent(Intent.ACTION_SEND);
            // intent.setType("text/plain");
            // intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());//内容
            // intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));//主题
            // intent = Intent.createChooser(intent, getString(R.string.send_report));// 选择器标题
            // if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //     startActivity(intent);
            // }

            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
            builder.setChooserTitle("Send via");
            builder.setType("text/plain");
            builder.setText(getCrimeReport());
            builder.setSubject(getString(R.string.crime_report_subject));
            builder.createChooserIntent();
            builder.startChooser();

        });
        // 设置电话按钮
        if (!TextUtils.isEmpty(mCrime.getPhone())) {
            mFragmentCrimeBinding.crimeCall.setText("Call: " + mCrime.getPhone());
        }
        mFragmentCrimeBinding.crimeCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mCrime.getPhone()));
            if (!TextUtils.isEmpty(mCrime.getPhone()) && intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        // 设置拍照按钮
        mFragmentCrimeBinding.crimeCamera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // 将图片的保存路径转为URI
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.neverforget.fileprovider", mPhotoFile);
                // 将URI路径当作参数
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 对可响应的应用进行授权URI写入文件
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });
        // 设置照片
        updatePhotoView();

        mFragmentCrimeBinding.crimePhoto.setOnClickListener(view -> {
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                Toast toast = Toast.makeText(getActivity(), "There are no photo here!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                PhotoViewFragment photoDialog = PhotoViewFragment.newIntent(mPhotoFile.getPath());
                photoDialog.show(getActivity().getSupportFragmentManager(), DIALOG_PHOTO);
            }
        });

        return mFragmentCrimeBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_pager, menu);
        Log.d(TAG, "onCreateOptionsMenu: 重建导航栏，position=" + position);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                deleteCrime();
                getActivity().finish();
                return true;
            case R.id.submit_crime:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCrime() {
        Log.d(TAG, "deleteCrime: 准备删除，position " + position);
        CrimeLab.get(getActivity()).getCrimes().remove(position);
        CrimeLab.get(getActivity()).deleteCrime(mCrime);
        if (mPhotoFile.exists()) {
            mPhotoFile.delete();
        }
    }

    private void updateDate() {
        mFragmentCrimeBinding.crimeData.setText(DateFormat.format("EEEE, MMMM dd, yyyy", mCrime.getDate()));
    }

    private void updateTime() {
        mFragmentCrimeBinding.crimeTime.setText(DateFormat.format("HH:mm ", mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateString = DateFormat.format("EEE, MMM dd", mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (TextUtils.isEmpty(suspect)) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data != null) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
        }

        if (requestCode == REQUEST_DATE) {
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            updateTime();
        }
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID}, null, null, null);
            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                String id = cursor.getString(1);
                mCrime = CrimeLab.get(getActivity()).getCrimes().get(position);
                mCrime.setSuspect(suspect);
                mFragmentCrimeBinding.crimeSuspect.setText(suspect);
                Cursor phone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                if (phone.moveToFirst()) {
                    String phoneNum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    mCrime.setPhone(phoneNum);
                    mFragmentCrimeBinding.crimeCall.setText("Call: " + phoneNum);
                }
                CrimeLab.get(getActivity()).updateCrime(mCrime);
            } finally {
                cursor.close();
            }
        }
        if (requestCode == REQUEST_PHOTO) {
            // 撤销权限
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.neverforget.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // 设置照片
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        // 当图片不存在
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mFragmentCrimeBinding.crimePhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaleBitmap(mPhotoFile.getPath(), getActivity());
            mFragmentCrimeBinding.crimePhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart" + mCurrentPage);
        Log.d(TAG, "onStart: 恢复界面，读取数据");
        mCrime = CrimeLab.get(getActivity()).getCrimes().get(position);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause" + mCurrentPage);
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy" + mCurrentPage);
        mFragmentCrimeBinding = null;
    }
}
