package hello;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

@Component
public class Receiver {
    //@JmsListener(destination = "mailbox", containerFactory = "myFactory")
    public void receiveMessage(Message email) throws JMSException {
        if (email instanceof Email) {
            System.out.println("Received <" + email + ">");
        }
        else if(email instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) email;
            Email testEmail = (Email)objectMessage.getObject();
            System.out.println("Received Object Message <" + testEmail + ">");
        }
        else
            System.out.println("Else Case"+email);
    }
}


