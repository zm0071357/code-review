package code.review.sdk.infrastructure.feishu.service;

import code.review.sdk.infrastructure.feishu.dto.FeiShuRequestDTO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeiShuServiceImpl {

    // 飞书机器人url
    private final String web_hook;

    public FeiShuServiceImpl(String webHook) {
        web_hook = webHook;
    }

    /**
     * 评审通知发送至飞书
     * @throws Exception
     */
    public void pushMessage(FeiShuRequestDTO requestDTO) throws Exception {
        String json = JSON.toJSONString(requestDTO);
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
    }

}
