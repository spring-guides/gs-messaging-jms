package hello;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
public class Application {
    
    static String mailboxDestination = "mailbox-destination";
    
    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(
                new ActiveMQConnectionFactory("tcp://localhost:61616"));
    }
    
    @Bean
    MessageListenerAdapter receiver() {
        return new MessageListenerAdapter(new Receiver()) {{
            setDefaultListenerMethod("receiveMessage");
        }};
    }
    
    @Bean
    SimpleMessageListenerContainer container(final MessageListenerAdapter messageListener,
            final ConnectionFactory connectionFactory) {
        return new SimpleMessageListenerContainer() {{
            setMessageListener(messageListener);
            setConnectionFactory(connectionFactory);
            setDestinationName(mailboxDestination);
        }};
    }
    
    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
    
    public static void main(String args[]) throws Throwable {
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(Application.class);
        
        MessageCreator messageCreator = new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("ping!");
                    }
                };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new mesage.");
        jmsTemplate.send(mailboxDestination, messageCreator);
        
        context.close();
    }
}