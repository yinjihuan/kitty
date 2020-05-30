package com.cxytiandi.kitty.common.alarm;

import com.cxytiandi.kitty.common.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

public class DingDingMessageUtil {

    public static void sendTextMessage(String accessToken, String secret, String msg) {
        try {
            Message message = new Message();
            message.setMsgtype("text");
            message.setText(new MessageInfo(msg));
            Long timestamp = System.currentTimeMillis();
            String sign = getSign(secret, timestamp);
            String url = "https://oapi.dingtalk.com/robot/send?access_token=" + accessToken
                    + "&timestamp="+timestamp
                    + "&sign="+sign;
            post(url, JsonUtils.toJson(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String url, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            conn.connect();
            OutputStream out = conn.getOutputStream();
            byte[] data = jsonBody.getBytes();
            out.write(data);
            out.flush();
            out.close();
            InputStream in = conn.getInputStream();
            byte[] data1 = new byte[in.available()];
            in.read(data1);
            System.out.println(new String(data1));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String getSign(String secret, Long timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.getEncoder().encode(signData)),"UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("钉钉发送消息签名失败");
        }
    }
}

@Data
class Message {
    private String msgtype;
    private MessageInfo text;
}

@Data
@AllArgsConstructor
class MessageInfo {
    private String content;
}