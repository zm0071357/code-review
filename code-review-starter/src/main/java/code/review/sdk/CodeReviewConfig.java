package code.review.sdk;

import code.review.sdk.domain.service.impl.CodeViewServiceImpl;
import code.review.sdk.infrastructure.deepseek.dto.response.DeepSeekResponseDTO;
import code.review.sdk.infrastructure.deepseek.service.DeepSeekServiceImpl;
import code.review.sdk.infrastructure.feishu.service.FeiShuServiceImpl;
import code.review.sdk.infrastructure.git.GitServiceImpl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 配置入口
 */
@Slf4j
public class CodeReviewConfig {

    // 飞书配置
    private String web_hook;
    // DeepSeek 配置
    private String api_host;
    private String api_key;

    // GitHub 配置
    private String write_log_repository_url;
    private String github_token;

    // 工程配置 - 自动获取项目名、分支名、作者
    private String project_name;
    private String branch_name;
    private String author_name;

    public static void main(String[] args) {
        FeiShuServiceImpl feiShuServiceImpl = new FeiShuServiceImpl(getEnv("WEB_HOOK"));
        GitServiceImpl gitServiceImpl = new GitServiceImpl(
                getEnv("WRITE_LOG_REPOSITORY_URL"),
                getEnv("GITHUB_TOKEN"),
                getEnv("PROJECT_NAME"),
                getEnv("BRANCH_NAME"));
        DeepSeekServiceImpl deepSeekServiceImpl = new DeepSeekServiceImpl(
                getEnv("API_HOST"),
                getEnv("API_KEY"));

        CodeViewServiceImpl codeViewServiceImpl = new CodeViewServiceImpl(gitServiceImpl, deepSeekServiceImpl, feiShuServiceImpl);
        codeViewServiceImpl.exec();
        log.info("code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }


}
