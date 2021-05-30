/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.Data;

/**
 *
 * @author G551
 */
@Data
public class MailService {

    private String sendMsg;//信件訊息

    //寄信
    public void sendEmail(String Sender, String Recipiant, String Subject, String message, String host) throws UnsupportedEncodingException {
        // Assuming you are sending email from localhost
        // Get system properties object
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.transport.protocol", "smtp");
        // Get the default Session object.
        Session mailSession = Session.getDefaultInstance(properties);
        try {
            // Create a default MimeMessage object.
            MimeMessage mm = new MimeMessage(mailSession);
            // Set From: header field of the header.
            mm.setFrom(new InternetAddress(Sender));
            // Set To: header field of the header.
            mm.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(Recipiant));
            // Set Subject: header field
            mm.setSubject(Subject);
            // Now set the actual message
            mm.setContent("<h1>" + message + "</h1>", "text/html");
            // Send message
            try (Transport transport = mailSession.getTransport("smtp")) {
                transport.connect();
                transport.sendMessage(mm, mm.getAllRecipients());
            }
            this.sendMsg = "已寄出訂單確認信件，請至信箱確認！";
        } catch (MessagingException mex) {
            this.sendMsg = Recipiant + "訂單確認信件寄出失敗！" + mex.getMessage();
        }
    }

    //寄信
    public void sendEmail(String Sender, String Recipiant, String Subject, String message, String password, String host) throws UnsupportedEncodingException {
        // Assuming you are sending email from localhost
        // Get system properties object
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.user", Sender);
        properties.setProperty("mail.password", password);
        properties.put("mail.transport.protocol", "smtp");
        // Get the default Session object.
        Session mailSession = Session.getDefaultInstance(properties);
        try {
            // Create a default MimeMessage object.
            MimeMessage mm = new MimeMessage(mailSession);
            // Set From: header field of the header.
            mm.setFrom(new InternetAddress(Sender));
            // Set To: header field of the header.
            mm.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(Recipiant));
            // Set Subject: header field
            mm.setSubject(Subject);
            // Now set the actual message
            mm.setContent(message, "text/html;charset=Big5");
            try (Transport transport = mailSession.getTransport("smtp")) {
                transport.connect(host, Sender, password);
                transport.sendMessage(mm, mm.getAllRecipients());
            }
            this.sendMsg = "已寄出訂單確認信件，請至信箱確認！";
        } catch (MessagingException mex) {
            this.sendMsg = Recipiant + "：訂單確認信件寄出失敗！" + mex.getMessage();
        }
    }

}
