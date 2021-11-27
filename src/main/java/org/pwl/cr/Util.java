package org.pwl.cr;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pwl.telnet.RunningConfig;
import org.pwl.telnet.Trans;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void getMessages(String apiKey, int page, Socket socket, RunningConfig runningConfig) {
        HttpResponse response = HttpRequest.get("https://pwl.icu/chat-room/more?apiKey=" + apiKey + "&page=" + page)
                .connectionTimeout(10000)
                .timeout(30000)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36")
                .send();
        response.close();
        response.charset("UTF-8");
        JSONObject result1 = new JSONObject(response.bodyText());
        JSONArray result2 = result1.optJSONArray("data");
        for (int i = result2.length() - 1; i >= 0; i--) {
            JSONObject message = (JSONObject) result2.get(i);
            renderMessage(
                    message.optString("oId"),
                    message.optString("userName"),
                    message.optString("userNickname"),
                    message.optString("time"),
                    message.optString("content"),
                    socket,
                    runningConfig
            );
        }
    }

    public static void renderMessage(String oId, String userName, String userNickname, String time, String content, Socket socket, RunningConfig runningConfig) {
        content = content.replaceAll("<br>", "\r\n");
        if (content.contains("<img")) {
            Set<String> pics = new HashSet<>();
            String img = "";
            Pattern p_image;
            Matcher m_image;
            String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
            p_image = Pattern.compile
                    (regEx_img, Pattern.CASE_INSENSITIVE);
            m_image = p_image.matcher(content);
            while (m_image.find()) {
                // 得到<img />数据
                img = m_image.group();
                // 匹配<img>中的src数据
                Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                while (m.find()) {
                    String picURL = m.group(1);
                    content += "[图片附件 " + picURL + "]";
                    pics.add(picURL);
                }
            }
        }
        String htmlRegex="<[^>]+>";
        content = content.replaceAll(htmlRegex, "");
        String name = "";
        if (!userNickname.isEmpty()) {
            name = userNickname + "(" + userName + ")";
        } else {
            name = userName;
        }
        Trans.send(socket, runningConfig.encode, "[" + time + " " + name + "] " + content + "\r\n");
    }
}
