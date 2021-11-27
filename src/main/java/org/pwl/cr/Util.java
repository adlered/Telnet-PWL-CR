package org.pwl.cr;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static String login(String username, String password) {
        HttpResponse response = HttpRequest.post("https://pwl.icu/api/getKey")
                .bodyText(
                        new JSONObject().put("nameOrEmail", username).put("userPassword", getMD5Str(password)).toString()
                )
                .contentTypeJson()
                .connectionTimeout(10000)
                .timeout(30000)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36")
                .send();
        response.close();
        response.charset("UTF-8");
        JSONObject result = new JSONObject(response.bodyText());
        if (result.optInt("code") == 0) {
            return result.optString("Key");
        } else {
            return "";
        }
    }

    private static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String getHelpText() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("=======================================\r\n");
        stringBuffer.append("= HELP\r\n");
        stringBuffer.append("=======================================\r\n");
        stringBuffer.append("- 发送消息：直接输入文本并回车\r\n");
        stringBuffer.append("- 获取帮助信息：/help\r\n");
        stringBuffer.append("=======================================\r\n");
        return stringBuffer.toString();
    }
}
