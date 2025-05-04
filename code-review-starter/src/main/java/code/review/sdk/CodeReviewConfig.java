package code.review.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        System.out.println("评审代码:" + diffCode);
    }
}
