package aoto.com.baidubodyattribute.Json;

/**
 * author:why
 * created on: 2019/7/3 8:52
 * description:
 */
public class GestureResponse {

    String log_id;
    int result_num;
    GestureData[] gestureList;

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public GestureData[] getGestureList() {
        return gestureList;
    }

    public void setGestureList(GestureData[] gestureList) {
        this.gestureList = gestureList;
    }
}
