package hello;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.ObjectMessage;

@Component
public class Receiver {

    @JmsListener(destination = "mailbox", containerFactory = "myFactory")
    public void receiveMessage(Message email) {
        if (email instanceof Email) {
            System.out.println("Received <" + email + ">");
        }
        else if(email instanceof ObjectMessage)
            System.out.println("Blubb");
        else
            System.out.println("tekkk");
    }

}
