Getting Started: Working with JMS from Spring
=========================================

This Getting Started guide will walk you through the process of accessing relational data with Spring.
To help you get started, we've provided an initial project structure for you in GitHub:

```sh
$ git clone https://github.com/springframework-meta/gs-relational-data-access.git
```

Before we can write the REST endpoint itself, there's some initial project setup that's required. Or, you can skip straight to the [fun part]().

Selecting Dependencies
----------------------
The sample in this Getting Started Guide will leverage Spring's core data-access modules and the H2, in-memory, embedded database. 

 - "com.h2database:h2:1.3.168"
 - "org.springframework:spring-jms:3.2.2.RELEASE"
 - "org.slf4j:slf4j-log4j12:1.6.1"
 - "org.apache.activemq:activemq-pool:5.5.0"
 - "org.apache.activemq:activemq-core:5.5.0"
 - "org.codehaus.jackson:jackson-mapper-asl:1.8.2"

Click here for details on how to map these dependencies to your specific build tool.

Sending Messages over JMS using Spring
----------------------------
Spring provides a convenient template class called the `JmsTemplate`. The `JmsTemplate` makes makes it very simple to send and receive messages from a JMS message queue.

This example sets up a JMS `ConnectionFactory` (one specific to [ActiveMQ](http://activemq.apache.org)) that we then wrap in an instance of Spring's `CachingConnectionFactory`. The `CachingConnectionFactory` has saner defaults when working with `ConnectionFactory` instances that are not already backed by a threadpool.


```java
package hello;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.*;

import javax.jms.*;

public class Main {

    public static void main(String args[]) throws Throwable {
    
        String mailboxDestination = "mailbox-destination";
        
        CachingConnectionFactory cachingConnectionFactory =
              new CachingConnectionFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
              
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
}

```


With our `JmsTemplate` instance in hand it's easy to then send and receive messages. The `send()` method expects a reference to the destination (either a `String` or a `javax.jms.Destination`) to which the message should be sent, and a callback instance of type `org.springframework.jms.core.MessageCreator`. The `receive()` method blocks and waits for a message to arrive on the specified destination (also either a  `String` or a `javax.jms.Destination`).

There are alternative methods that depend on a configured mapper to marshal the payloads of the messages sent and received, `receiveAndConvert()` and `convertAndSend()`. For a non-blocking approaching featuring the JMS `MessageListenerContainer`, see this getting started guide.

Building and Running the Client
--------------------------------------
To invoke the code and see the results of the search, simply run it from the command line, like this:

```sh
$ gradle run
```
	
This will compile the `main` method and then run it.


Next Steps
----------
Congratulations! You have just developed a simple JDBC client using Spring.  

There's more to building and working with JDBC and datastores in general than is covered here. You may want to continue your exploration of Spring and REST with the following Getting Started guides:

* **Consuming REST Services on Android**
* Handling POST, PUT, and GET requests in REST endpoints
* Creating self-describing APIs with HATEOAS
* Securing a REST endpoint with HTTP Basic
* Securing a REST endpoint with OAuth
* Consuming REST APIs
* Testing REST APIs

