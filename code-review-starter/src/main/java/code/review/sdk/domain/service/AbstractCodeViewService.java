package code.review.sdk.domain.service;

import code.review.sdk.infrastructure.deepseek.service.DeepSeekServiceImpl;
import code.review.sdk.infrastructure.feishu.service.FeiShuServiceImpl;
import code.review.sdk.infrastructure.git.GitServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractCodeViewService implements CodeViewService {

    protected final GitServiceImpl gitServiceImpl;
    protected final DeepSeekServiceImpl deepSeekServiceImpl;
    protected final FeiShuServiceImpl feiShuServiceImpl;

    protected AbstractCodeViewService(GitServiceImpl gitServiceImpl, DeepSeekServiceImpl deepSeekServiceImpl, FeiShuServiceImpl feiShuServiceImpl) {
        this.gitServiceImpl = gitServiceImpl;
        this.deepSeekServiceImpl = deepSeekServiceImpl;
        this.feiShuServiceImpl = feiShuServiceImpl;
    }

    @Override
    public void exec() {
        try {
            // 获取提交代码
            String diffCode = getDiffCode();
            // 评审代码
            String comment = codeReview(diffCode);
            // 记录评审结果到文件
            String logUrl = recordCodeReview(comment);
            // 发送评审通知到飞书
            pushMessage(logUrl);
        } catch (Exception e) {
            log.info("code-review error", e);
        }
    }

    protected abstract String getDiffCode() throws IOException, InterruptedException;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String recordCodeReview(String comment) throws Exception;

    protected abstract void pushMessage(String logUrl) throws Exception;

}
