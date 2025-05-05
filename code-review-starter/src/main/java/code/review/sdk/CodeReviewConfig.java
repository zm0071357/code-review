package code.review.sdk;

import code.review.sdk.domain.model.DeepSeekResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 配置入口
 */
public class CodeReviewConfig {

    public static void main(String[] args) throws Exception {
        System.out.println("DeepSeek代码评审执行");
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("token is null");
        }
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
        // 写入日志
        writeLog(token, log);
    }

    private static String codeReview(String diffCode) throws IOException {
        String post_url = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-c98ad52dc764455e9957890820ee4e1c";
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");

        // 构建消息列表
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", "你是一个高级编程架构师，请评审以下代码变更：" + diffCode);

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
        System.out.println("评审结果: " + content.toString());
        DeepSeekResponse deepSeekResponse = JSON.parseObject(content.toString(), DeepSeekResponse.class);
        return deepSeekResponse.getChoices().get(0).getMessage().getContent();
    }

    private static String writeLog(String token, String log) throws Exception {
        String url = "https://github.com/zm0071357/code-review-log";
        Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }
        String fileName = generateRandomString(12) + ".md";
        File newFile = new File(dateFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(log);
        }
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add new File").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""));
        return url + "/blob/master/" + dateFolderName + "/" + fileName;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
