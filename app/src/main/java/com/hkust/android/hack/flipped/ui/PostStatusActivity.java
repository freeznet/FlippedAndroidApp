package com.hkust.android.hack.flipped.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hkust.android.hack.flipped.BootstrapServiceProvider;
import com.hkust.android.hack.flipped.R;
import com.hkust.android.hack.flipped.util.ImageCompressionAsyncTask;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;

public class PostStatusActivity extends BootstrapActivity {

    @Inject
    protected BootstrapServiceProvider serviceProvider;

//    @InjectView(R.id.tv_catename)
//    TextView mTvCatename;
//    @InjectView(R.id.tv_locationname)
//    TextView mTvLocationname;
//    @InjectView(R.id.tv_taskcontent)
//    TextView mTvTaskcontent;


    @InjectView(R.id.bt_submit)
    Button mBtSubmit;
    EditText mEtCreditbouns;
    @InjectView(R.id.et_taskcontent)
    EditText mEtTaskcontent;

    @InjectView(R.id.iv_imageattr)
    ImageView mIvImageattr;
    @InjectView(R.id.title_bar_menu_layout)
    FrameLayout mTitleBarMenuLayout;
    @InjectView(R.id.tv_title)
    TextView mTvTitle;
    @InjectView(R.id.tv_intro)
    TextView mTvIntro;

    String location = "unknown";
    private Bitmap currentBitmap = null;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getIntent() != null && getIntent().getExtras() != null) {
//            taskItem = (Task) getIntent().getExtras().getSerializable(TASK_ITEM);
            location = getIntent().getExtras().get("location").toString();
        }

        setContentView(R.layout.activity_post_status);

        mTvTitle.setText("Footprint");

        mTvIntro.setText(location);


//        mEtTaskcontent.setHint(getString(R.string.task_content_hint));
        mEtTaskcontent.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        mIvImageattr.setVisibility(View.VISIBLE);
        mIvImageattr.setImageResource(R.drawable.ic_action_take_photo);
        mIvImageattr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        PostStatusActivity.this);
                builder.setTitle(getString(R.string.choose_image));
                builder.setPositiveButton(getString(R.string.from_camera),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "gmission_task_image");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(PostStatusActivity.this)));
                                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            }
                        });
                builder.setNegativeButton(getString(R.string.from_gallery),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
//        currentActivity = this;


        mBtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtSubmit.setEnabled(false);

                String content = "content";
                content = mEtTaskcontent.getText().toString().trim();
                try {
                    final String finalContent = content;

                    ///check in
                    mBtSubmit.setEnabled(true);
                    PostStatusActivity.this.finish();
                    Toast.makeText(PostStatusActivity.this, "发送成功", Toast.LENGTH_SHORT).show();

                } catch (NumberFormatException nfe) {
                    Toast.makeText(PostStatusActivity.this, "task_request_error", Toast.LENGTH_SHORT).show();
                    mBtSubmit.setEnabled(true);
                    return;
                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
//                    final File file = getTempFile(this);
//                    try {
//                        currentBitmap = readBitmap(Uri.fromFile(getTempFile(GMissionPostTaskRequestActivity.this)));
//                        mIvImageattr.setImageBitmap(currentBitmap);
////                        mLlAddimage.setVisibility(View.GONE);
////                        mLlImageattr.setVisibility(View.VISIBLE);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    new ImageCompressionAsyncTask(this, currentBitmap, mIvImageattr).execute(Uri.fromFile(getTempFile(PostStatusActivity.this)).toString());
                } else {
                    currentBitmap = null;
                }
                break;
            }
            case SELECT_IMAGE_ACTIVITY_REQUEST_CODE: {
                if( resultCode == RESULT_OK){
                    try {
//                        ContentResolver resolver = getContentResolver();
//                        Uri originalUri = data.getData();
//                        byte[] mContent = readStream(resolver.openInputStream(Uri
//                                .parse(originalUri.toString())));
//                        currentBitmap = BitmapFactory.decodeByteArray(mContent, 0, mContent.length);;
//                        mIvImageattr.setImageBitmap(currentBitmap);
//                        mIvImageattr.setTag(currentBitmap);
                        new ImageCompressionAsyncTask(this, currentBitmap, mIvImageattr).execute(data.getData().toString());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    currentBitmap = null;
                }
                break;
            }
        }
    }

    private File getTempFile(Context context) {
        //it will return /sdcard/image.tmp
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }




}
