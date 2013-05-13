package hello;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.*;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.*;

public class Main {

    public static void main(String args[]) throws Throwable {
        String mailboxDestination = "mailbox-destination";
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
        setupPollingJmsTemplate(mailboxDestination, cachingConnectionFactory);
    }

    public static void setupPollingJmsTemplate(String mailboxDestination, ConnectionFactory cachingConnectionFactory) throws Throwable {

        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        jmsTemplate.send(mailboxDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("ping!");
            }
        });

        TextMessage textMessage = (TextMessage) jmsTemplate.receive(mailboxDestination);
        String message = textMessage.getText();
        System.out.println("message : " + message);
    }

    public static void setupMessageListenerContainer(String mailboxDestination, ConnectionFactory cachingConnectionFactory) throws Throwable {

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    String txtFromMessage = ((TextMessage) message).getText();
                    System.out.println("Message received: \"" + txtFromMessage + "\"");
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        simpleMessageListenerContainer.setConcurrentConsumers(20);
        simpleMessageListenerContainer.setConnectionFactory(cachingConnectionFactory);
        simpleMessageListenerContainer.setDestinationName(mailboxDestination);
        simpleMessageListenerContainer.afterPropertiesSet();
        simpleMessageListenerContainer.start();

        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory);
        for (int i = 0; i < 20; i++) {
            final int index = i;
            jmsTemplate.send(mailboxDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage("ping! #" + (index + 1));
                }
            });
        }

    }

}