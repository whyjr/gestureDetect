package aoto.com.baidubodyattribute.Listeners;

import org.json.JSONObject;

/**
 * author:why
 * created on: 2019/6/21 17:24
 * description:
 */
public interface GestureDetectListener {
    void onSuccess(JSONObject jsonObject);
    void onFail();
}
