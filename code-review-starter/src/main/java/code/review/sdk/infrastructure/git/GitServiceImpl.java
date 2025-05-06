package code.review.sdk.infrastructure.git;

import code.review.sdk.type.utils.RandomStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class GitServiceImpl {

    // 评审通知仓库url
    private final String writeLogRepositoryUrl;

    // 密钥
    private final String githubToken;

    // 项目名
    private final String projectName;

    // 分支名
    private final String branchName;

    public GitServiceImpl(String writeLogRepositoryUrl, String githubToken, String projectName, String branchName) {
        this.writeLogRepositoryUrl = writeLogRepositoryUrl;
        this.githubToken = githubToken;
        this.projectName = projectName;
        this.branchName = branchName;
    }


    /**
     * 代码检出
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String diffCode() throws IOException, InterruptedException {
        log.info("代码检出");
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();
        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String latestCommitHash = logReader.readLine();
        logReader.close();
        logProcess.waitFor();

        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", latestCommitHash + "^", latestCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();
        StringBuilder diffCode = new StringBuilder();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));
        String line;
        while ((line = diffReader.readLine()) != null) {
            diffCode.append(line).append("\n");
        }
        diffReader.close();

        int exitCode = diffProcess.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to get diff, exit code:" + exitCode);
        }

        return diffCode.toString();

    }

    /**
     * 评审提交和推送
     * @param comment
     * @return 评审通知地址
     * @throws Exception
     */
    public String commitAndPush(String comment) throws Exception {
        log.info("评审提交和推送");
        Git git = Git.cloneRepository()
                .setURI(writeLogRepositoryUrl + ".git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();

        // 创建分支
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }

        String fileName = projectName + "-" + branchName + "-" + System.currentTimeMillis() + "-" + RandomStringUtils.randomNumeric(4) + ".md";
        File newFile = new File(dateFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(comment);
        }
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add new File").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, "")).call();
        log.info("评审提交和推送完成");
        return writeLogRepositoryUrl + "/blob/master/" + dateFolderName + "/" + fileName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBranchName() {
        return branchName;
    }

}
