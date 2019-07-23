package aoto.com.baidubodyattribute;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import aoto.com.baidubodyattribute.Json.GestureData;
import aoto.com.baidubodyattribute.Listeners.GestureDetectListener;
import aoto.com.baidubodyattribute.SelfView.CameraPreviewView;

/**
 * @author why
 * @date 2019-6-21 16:44:23
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityWhy";
    private TextView result;
    private TextView residueTimeView;
    private CameraPreviewView previewView;

    private EditText app_id_view;
    private EditText api_key_view;
    private EditText secret_key_view;

    private int residueCount = 0;
    private boolean isOpen = false;
    private boolean stepOver = true;
    private SPManager spManager;

    @SuppressWarnings("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    result.setText("测试结果：");
                    break;
                case 2:
                    result.setText(msg.obj.toString());
                    residueTimeView.setText("今天剩余测试次数：" + String.valueOf(residueCount));
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        result = findViewById(R.id.result);
        residueTimeView = findViewById(R.id.test_time_residue);
        previewView = findViewById(R.id.camera_preview);
        app_id_view = findViewById(R.id.app_id_view);
        api_key_view = findViewById(R.id.api_key_view);
        secret_key_view = findViewById(R.id.secret_key_view);

        spManager = SPManager.getInstance(this);

        //TODO check permission firstly
        if (checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            BaiduClientManager.init();
            startCamera();
            //TODO check the data store in the SP
            //           if(checkLocalData()){
            //BaiduClientManager.init();
//                BaiduClientManager.initWithParams(spManager.getString(SPManager.SPValueKey.APP_ID,"")
//                        ,spManager.getString(SPManager.SPValueKey.API_KEY,""),
//                        spManager.getString(SPManager.SPValueKey.SECRET_KEY,""));
//                hideView();
            //        }
            //          else {
//toast("请填写申请的数据");
            //               showView();
//            }
        }


        if (getDate().equals(spManager.getString(SPManager.SPValueKey.DATE_KEY, ""))) {
            residueCount = Integer.parseInt(spManager.getString(SPManager.SPValueKey.COUNT_KEY, "50000"));
            residueTimeView.setText("今天剩余测试次数：" + residueCount);
        } else {
            residueCount = 50000;
            spManager.putString(SPManager.SPValueKey.DATE_KEY, getDate());
            residueTimeView.setText("今天剩余测试次数：" + residueCount);
        }
    }

    /**
     * 校验本地APP 数据
     */
    private boolean checkLocalData() {
        return spManager.getBoolean(SPManager.SPValueKey.CHECK_RESULT, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startCamera();
    }

    /**
     * 开始测试
     *
     * @param view
     */
    public void start(View view) {
        stepOver = true;
        isOpen = true;
    }

    /**
     * 关闭测试
     *
     * @param view
     */
    public void stop(View view) {
        stepOver = false;
        isOpen = false;
        MessageHelper.sendMsg(handler, 1);
    }

    /**
     * 开启摄像头
     */
    private void startCamera() {
        Camera camera = Camera.open();
        previewView.setCamera(camera);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //TODO send the data to BD server for gesture detect
                if (stepOver && isOpen) {
                    // Log.e(TAG, "onPreviewFrame: " + data.length);
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, 640, 480, null);
                    if (image != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, 640, 480), 80, stream);
                        startDetectGesture(stream.toByteArray());
                        stepOver = false;
                    } else {
                        Log.e(TAG, "onPreviewFrame: 转换失败");
                    }
                }
            }
        });
    }

    /**
     * 开始检测手势
     */
    private void startDetectGesture(String filePath) {
        BaiduClientManager.gestureDetect(filePath, new GestureDetectListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Message message = Message.obtain();
                try {
                    message.obj = jsonObject.toString(2);
                    message.what = 2;
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    stepOver = true;
                }
                handler.sendMessage(message);
            }

            @Override
            public void onFail() {

            }
        });
    }


    private void startDetectGesture(byte[] bytes) {
        //Log.e(TAG, "startDetectGesture: " + bytes.length);
        BaiduClientManager.gestureDetect(bytes, new GestureDetectListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Message message = Message.obtain();
                try {
                    message.obj = jsonObject.toString(2);//输出的是result数据
                    message.what = 2;
                    JSONArray array = (JSONArray) jsonObject.get("result");
                    if (array != null && array.length() > 0) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = (JSONObject) array.get(i);
                            builder.append(json.getString("classname") + ",");
                        }
                        Log.e(TAG, "识别到手势：" + builder.toString());
                    }
                    if (message.obj.toString().contains("error")) {
                        stepOver = false;
                    } else {
                        stepOver = true;
                    }
                    residueCount--;
                    spManager.putString(SPManager.SPValueKey.COUNT_KEY, String.valueOf(residueCount));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(message);
            }

            @Override
            public void onFail() {

            }
        });
    }


    private byte[] bitmap2Bytes(String filePath) {
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }


    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }

    /**
     * 隐藏View
     */
    private void hideView() {
        app_id_view.setVisibility(View.INVISIBLE);
        api_key_view.setVisibility(View.INVISIBLE);
        secret_key_view.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示View
     */
    private void showView() {
        app_id_view.setVisibility(View.VISIBLE);
        api_key_view.setVisibility(View.VISIBLE);
        secret_key_view.setVisibility(View.VISIBLE);
    }


    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "你授予了相机权限", Toast.LENGTH_SHORT).show();
                    BaiduClientManager.init();
                    startCamera();
                } else {
                    Toast.makeText(this, "你拒绝了相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
