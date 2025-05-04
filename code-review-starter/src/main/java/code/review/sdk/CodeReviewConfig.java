package code.review.sdk;

import code.review.sdk.domain.model.DeepSeekResponse;
import com.alibaba.fastjson2.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 配置入口
 */
public class CodeReviewConfig {

    public static void main(String[] args) throws Exception {
        // 代码检出
        System.out.println("代码检出");
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exited with code:" + exitCode);
        System.out.println("评审代码:" + diffCode.toString());

        // DeepSeek代码评审
        String log = codeReview(diffCode.toString());
    }

    private static String codeReview(String diffCode) throws IOException {
        String post_url = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-c98ad52dc764455e9957890820ee4e1c";
        String json = "{\n" +
                "        \"model\": \"deepseek-chat\",\n" +
                "        \"messages\": [\n" +
                "          {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},\n" +
                "          {\"role\": \"user\", \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为：" + diffCode + "\"} \n" +
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
        System.out.println("评审结果: " + content.toString());
        DeepSeekResponse deepSeekResponse = JSON.parseObject(content.toString(), DeepSeekResponse.class);
        return deepSeekResponse.getChoices().get(0).getMessage().getContent();
    }
}
