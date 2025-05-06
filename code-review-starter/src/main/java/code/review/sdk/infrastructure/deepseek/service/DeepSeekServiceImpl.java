package code.review.sdk.infrastructure.deepseek.service;

import code.review.sdk.infrastructure.deepseek.dto.request.DeepSeekRequestDTO;
import code.review.sdk.infrastructure.deepseek.dto.response.DeepSeekResponseDTO;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class DeepSeekServiceImpl {

    // DeepSeek API 请求地址
    private final String apiHost;

    // DeepSeek ApiKey
    private final String apiKey;

    public DeepSeekServiceImpl(String apiHost, String apiKey) {
        this.apiHost = apiHost;
        this.apiKey = apiKey;
    }

    /**
     * 请求DeepSeek API
     * @param requestDTO
     * @return
     * @throws Exception
     */
    public DeepSeekResponseDTO completions(DeepSeekRequestDTO requestDTO) throws Exception {
        URL url = new URL(apiHost);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        String json = JSON.toJSONString(requestDTO);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        log.info("DeepSeek API ResponseCode:{}", responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        log.info("评审结果:{}", content.toString());
        return JSON.parseObject(content.toString(), DeepSeekResponseDTO.class);
    }

}
