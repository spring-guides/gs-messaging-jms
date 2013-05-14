package hello;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.*;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.*;

public class Main {

    public static void main(String args[]) throws Throwable {
        // setup share variables
        String mailboxDestination = "mailbox-destination";
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
        cachingConnectionFactory.afterPropertiesSet();

        // receive
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String txtFromMessage = ((TextMessage) message).getText();
                    System.out.println("Message received: \"" + txtFromMessage + "\"");
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setMessageListener(messageListener);
        simpleMessageListenerContainer.setConcurrentConsumers(5);
        simpleMessageListenerContainer.setConnectionFactory(cachingConnectionFactory);
        simpleMessageListenerContainer.setDestinationName(mailboxDestination);
        simpleMessageListenerContainer.afterPropertiesSet();
        simpleMessageListenerContainer.start();

        // send
        MessageCreator messageCreator = new
                MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("ping!");
                    }
                };
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.send(mailboxDestination, messageCreator);

    }
}