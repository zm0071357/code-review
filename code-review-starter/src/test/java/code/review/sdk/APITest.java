package code.review.sdk;

import code.review.sdk.domain.model.DeepSeekResponse;
import com.alibaba.fastjson2.JSON;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APITest {

    @Test
    public void test_http() throws IOException {
        String post_url = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-c98ad52dc764455e9957890820ee4e1c";
        String code = "1+1";
        String json = "{\n" +
                "        \"model\": \"deepseek-chat\",\n" +
                "        \"messages\": [\n" +
                "          {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},\n" +
                "          {\"role\": \"user\", \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为：" + code + "\"} \n" +
                "        ],\n" +
                "        \"stream\": false\n" +
                "      }";

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
        DeepSeekResponse deepSeekResponse = JSON.parseObject(content.toString(), DeepSeekResponse.class);
        System.out.println(deepSeekResponse.getChoices().get(0).getMessage().getContent());
    }
}
