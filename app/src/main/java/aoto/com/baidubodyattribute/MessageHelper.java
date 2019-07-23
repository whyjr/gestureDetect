package aoto.com.baidubodyattribute;

import android.os.Handler;
import android.os.Message;

/**
 * author:why
 * created on: 2019/6/25 19:44
 * description:
 */
public class MessageHelper {

    public static void sendMsg(Handler handler,int taskId){
        Message message=Message.obtain();
        message.what=taskId;
        handler.sendMessage(message);
    }
}
