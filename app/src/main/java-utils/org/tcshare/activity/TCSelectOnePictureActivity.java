package org.tcshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.tcshare.utils.GetImagePath;

import java.io.File;

/**
 * Created by yuxiaohei on 2018/4/24.
 */

public class TCSelectOnePictureActivity extends Activity {
    private static final int    CHOOSE_PHOTO_0  = 2000;
    private static final int    TAKE_PHOTO_0    = 3000;
    public static final  String RESULT_RECEIVER = "result_receiver";
    public static final  String SELECT_PICTURE  = "select_pic";
    public static final  String ACT_TYPE        = "action_type";
    private ResultReceiver resultReceiver;
    private File cacheDir = new File(Environment.getExternalStorageDirectory(), "tcache");
    private File imageFile    = new File(cacheDir, "tempImage.jpg");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        if (getIntent() != null) {
            resultReceiver = getIntent().getParcelableExtra(RESULT_RECEIVER);
            int actType = getIntent().getIntExtra(ACT_TYPE, 0);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            switch (actType) {
                case 0:
                    intent.setAction("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, TAKE_PHOTO_0);
                    break;
                case 1:
                    intent.setAction("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOOSE_PHOTO_0);
                    break;
            }
        } else {
            finish();
        }
    }


    private Uri getImageUri() {
        cacheDir.mkdirs();
        imageFile.delete();

        Uri imageUri;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);
        }

        return imageUri;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = new Bundle();
        bundle.putString(SELECT_PICTURE, "");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_0:
                    bundle.putString(SELECT_PICTURE, imageFile.getPath());
                    break;
                case CHOOSE_PHOTO_0:
                    bundle.putString(SELECT_PICTURE, GetImagePath.getPath(this, data.getData()));
                    break;
                default:
                    break;
            }
        }
        resultReceiver.send(resultCode, bundle);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
