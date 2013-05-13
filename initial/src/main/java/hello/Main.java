package hello;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.*;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.*;

public class Main {

    public static void main(String args[]) throws Throwable {
        
		String mailboxDestination = "mailbox-destination";
        
		ConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
		
		// let's use the ConnectionFactory
		
    }
}