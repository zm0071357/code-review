package code.review.sdk.domain.service.impl;

import code.review.sdk.domain.model.Model;
import code.review.sdk.domain.service.AbstractCodeViewService;
import code.review.sdk.infrastructure.deepseek.dto.request.DeepSeekRequestDTO;
import code.review.sdk.infrastructure.deepseek.dto.request.Message;
import code.review.sdk.infrastructure.deepseek.dto.response.DeepSeekResponseDTO;
import code.review.sdk.infrastructure.deepseek.service.DeepSeekServiceImpl;
import code.review.sdk.infrastructure.feishu.dto.Content;
import code.review.sdk.infrastructure.feishu.dto.FeiShuRequestDTO;
import code.review.sdk.infrastructure.feishu.service.FeiShuServiceImpl;
import code.review.sdk.infrastructure.git.GitServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CodeViewServiceImpl extends AbstractCodeViewService {
    public CodeViewServiceImpl(GitServiceImpl gitServiceImpl, DeepSeekServiceImpl deepSeekServiceImpl, FeiShuServiceImpl feiShuServiceImpl) {
        super(gitServiceImpl, deepSeekServiceImpl, feiShuServiceImpl);
    }

    @Override
    protected String getDiffCode() throws IOException, InterruptedException {
        return gitServiceImpl.diffCode();
    }

    @Override
    protected String codeReview(String diffCode) throws Exception {
        DeepSeekRequestDTO deepSeekRequestDTO = new DeepSeekRequestDTO();
        deepSeekRequestDTO.setModel(Model.DEEPSEEK_CHAT.getCode());
        deepSeekRequestDTO.setStream(false);
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder().role("user").content("你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:").build());
        messages.add(Message.builder().role("user").content(diffCode).build());
        DeepSeekResponseDTO responseDTO = deepSeekServiceImpl.completions(deepSeekRequestDTO);
        return responseDTO.getChoices().get(0).getMessage().getContent();
    }

    @Override
    protected String recordCodeReview(String comment) throws Exception {
        return gitServiceImpl.commitAndPush(comment);
    }

    @Override
    protected void pushMessage(String logUrl) throws Exception {
        String msg = "项目：".concat(gitServiceImpl.getProjectName()).concat("，\n")
                .concat("分支：").concat(gitServiceImpl.getBranchName()).concat("，\n")
                .concat("代码评审通知：").concat(logUrl);
        FeiShuRequestDTO requestDTO = new FeiShuRequestDTO();
        requestDTO.setMsg_type("text");
        requestDTO.setContent(Content.builder().text(msg).build());
        feiShuServiceImpl.pushMessage(requestDTO);
    }
}
