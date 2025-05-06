package code.review.sdk;

import code.review.sdk.infrastructure.deepseek.dto.response.DeepSeekResponseDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APITest {

    @Test
    public void test_http() throws IOException {
        String post_url = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-c98ad52dc764455e9957890820ee4e1c";
        String code = "diff --git a/code-review-test/src/test/java/code/review/test/APITest.java b/code-review-test/src/test/java/code/review/test/APITest.javaindex 3593891..74f7222 100644--- a/code-review-test/src/test/java/code/review/test/APITest.java+++ b/code-review-test/src/test/java/code/review/test/APITest.java@@ -13,6 +13,6 @@ public class APITest {      @Test     public void test() {-        System.out.println(Integer.parseInt(\"aaaa\"));+        System.out.println(Integer.parseInt(\"aaaa1\"));     } }";
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");

        // 构建消息列表
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", "你是一个高级编程架构师，请评审以下代码变更：" + code);

        List<JSONObject> messages = new ArrayList<>();
        messages.add(systemMsg);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("stream", false);

        String json = JSON.toJSONString(requestBody);

        URL url = new URL(post_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        DeepSeekResponseDTO deepSeekResponse = JSON.parseObject(content.toString(), DeepSeekResponseDTO.class);
        System.out.println(deepSeekResponse.getChoices().get(0).getMessage().getContent());
    }

    @Test
    public void test_feishu() throws IOException {
        String writeLogUrl = "xxx";
        String web_hook = "https://open.feishu.cn/open-apis/bot/v2/hook/43bde768-f4d9-4252-8027-d329d2d5c3ea";
        String msg = "项目：".concat("xxx").concat("，\n")
                .concat("代码评审通知：").concat(writeLogUrl);
        JSONObject requestBody = new JSONObject();
        requestBody.put("msg_type", "text");
        JSONObject content = new JSONObject();
        content.put("text", msg);
        requestBody.put("content", content);
        String json = JSON.toJSONString(requestBody);

        URL url = new URL(web_hook);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            sb.append(inputLine);
        }
        in.close();
        connection.disconnect();
        System.out.println(sb.toString());
    }
}
