package com.sbank.admin.common.service;

import com.sbank.admin.common.annotation.NoLogging;
import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.util.CommonUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**--------------------------------------------------------------------
 * ■메일 전송 서비스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class SendMailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${temp.file.path}")
    private String strTempFilePath;

    /**--------------------------------------------------------------------
     * ■메일 발송 ■payletter ■2019-03-21
     --------------------------------------------------------------------**/
    @Async
    @NoLogging
    public void sendMail(String to, String from, String subject, String template, Context mailBodyContext) {
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper  = new MimeMessageHelper(mimeMessage, true, GlobalConstants.SYSTEM_ENCODING);
                String mailContent                   = templateEngine.process(template, mailBodyContext);

                mimeMessageHelper.setTo(new InternetAddress(to));
                mimeMessageHelper.setFrom(new InternetAddress(from));
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(mailContent, true);
            }
        };

        try {
            javaMailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**--------------------------------------------------------------------
     * ■메일 발송 - 첨부파일 추가 ■payletter ■2020-02-19
     --------------------------------------------------------------------**/
    @NoLogging
    public void sendMail(String to, String from, String subject, String template, Context mailBodyContext, String fileName, String filePath) {
        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper  = new MimeMessageHelper(mimeMessage, true, GlobalConstants.SYSTEM_ENCODING);
                String mailContent                   = templateEngine.process(template, mailBodyContext);

                mimeMessageHelper.setTo(new InternetAddress(to));
                mimeMessageHelper.setFrom(new InternetAddress(from));
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(mailContent, true);

                if(!CommonUtil.isNullOrEmpty(filePath)) {
                    FileSystemResource fsr = new FileSystemResource(filePath);
                    mimeMessageHelper.addAttachment(fileName, fsr);
                }
            }
        };

        try {
            javaMailSender.send(mimeMessagePreparator);
        } catch (MailException e) {
            throw new GlobalException("common.msg.failSendMail"); // 메일 발송에 실패하였습니다.
        }
    }
}
