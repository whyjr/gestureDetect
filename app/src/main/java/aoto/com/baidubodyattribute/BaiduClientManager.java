package aoto.com.baidubodyattribute;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import aoto.com.baidubodyattribute.Listeners.GestureDetectListener;

/**
 * author:why
 * created on: 2019/6/21 16:56
 * description:
 */
public class BaiduClientManager {

    //
    public static final String APP_ID = "##";
    public static final String API_KEY = "##";
    public static final String SECRET_KEY = "##";

    private static ThreadPoolExecutor executor;
    private static AipBodyAnalysis client = null;

    /**
     * for why
     */
    public static void init() {
        if (client == null) {
            client = new AipBodyAnalysis(APP_ID, API_KEY, SECRET_KEY);
        }
        if (executor == null) {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        }
    }


    /**
     *  for other people
     * @param app_id
     * @param api_key
     * @param secret_key
     */
    public static void initWithParams(String app_id,String api_key,String secret_key){
        if (client == null) {
            client = new AipBodyAnalysis(app_id, api_key, secret_key);
        }
        if (executor == null) {
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        }
    }

    /**
     * 手势识别
     *
     * @param imagePath
     * @return
     */
    public static synchronized void gestureDetect(final String imagePath, final GestureDetectListener listener) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = client.gesture(imagePath, new HashMap<String, String>());
                listener.onSuccess(json);
            }
        });
    }

    public static synchronized void gestureDetect(final byte[] bytes, final GestureDetectListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = client.gesture(bytes, new HashMap<String, String>());
                listener.onSuccess(json);
            }
        });
    }
}
