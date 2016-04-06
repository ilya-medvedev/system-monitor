package medvedev.ilya.monitor.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
class EmailNotificationService {
    private final MailSender mailSender;
    private final String from;
    private final String to;
    private final String subject;

    @Autowired
    public EmailNotificationService(
            final MailSender mailSender,
            @Value("${notification.email.from}") final String from,
            @Value("${notification.email.to}") final String to,
            @Value("${notification.email.subject}") final String subject
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.to = to;
        this.subject = subject;
    }

    void sendNotification(final String notification) {
        final SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(notification);

        mailSender.send(msg);
    }
}
