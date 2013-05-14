#Getting Started: Working with JMS from Spring


[![Build Status](https://drone.io/github.com/springframework-meta/gs-rest-service/status.png)](https://drone.io/github.com/springframework-meta/gs-rest-service/latest)

Introduction
------------

### What You'll Build

This guide will take you through creating a simple JMS producer and consumer with Spring. We'll build a service sends and receives JMS messages.


### What You'll Need

 - About 15 minutes
 - A favorite text editor or IDE
 - [JDK 7](http://docs.oracle.com/javase/7/docs/webnotes/install/index.html) or better
 - Your choice of Maven (3.0+) or Gradle (1.5+)

### How to Complete this Guide

Like all Spring's [Getting Started guides](/getting-started), you can choose to start from scratch and complete each step, or you can jump past basic setup steps that may already be familiar to you. Either way, you'll end up with working code.

To **start from scratch**, just move on to the next section and start [setting up the project](#scratch).

If you'd like to **skip the basics**, then do the following:

 - [download][zip] and unzip the source repository for this guide—or clone it using [git](/understanding/git):
`git clone https://github.com/springframework-meta/gs-consuming-jms.git`
 - cd into `gs-consuming-jms.git/initial`
 - jump ahead to [creating a representation class](#initial).

And **when you're finished**, you can check your results against the the code in `gs-rest-service/complete`.


<a name="scratch"></a>
Setting up the project
----------------------
First you'll need to set up a basic build script. You can use any build system you like when building apps with Spring, but we've included what you'll need to work with [Gradle](http://gradle.org) here. If you're not familiar with either of these, you can refer to our [Getting Started with Maven](../gs-maven/README.md) or [Getting Started with Gradle](../gs-gradle/README.md) guides.

### Maven

Create a Maven POM that looks like this:

`pom.xml`
```xml
...
```

### Gradle

TODO: paste complete build.gradle.

Add the following within the `dependencies { }` section of your build.gradle file:

`build.gradle`
```groovy
 compile "com.h2database:h2:1.3.168"
 compile "org.springframework:spring-jms:3.2.2.RELEASE"
 compile "org.slf4j:slf4j-log4j12:1.6.1"
 compile "org.apache.activemq:activemq-pool:5.5.0"
 compile "org.apache.activemq:activemq-core:5.5.0"
 compile "org.codehaus.jackson:jackson-mapper-asl:1.8.2"
```

Making a Connection(Factory)
----------------------------
To work with JMS, you need to setup a JMS `ConnectionFactory`. Here, we setup a JMS `ConnectionFactory` (one specific to [ActiveMQ](http://activemq.apache.org)) that we then wrap in an instance of Spring's `CachingConnectionFactory`. The `CachingConnectionFactory` has saner defaults when working with `ConnectionFactory` instances that are not already backed by a threadpool (as is typically the case with `ConnectionFactory` instances obtained from within an application server. 

```java
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
        // ...

    }
}
```

Now, let's setup Spring's `SimpleMessageListenerContainer`, which listens for new messages on a JMS `Destination` and then - in a background thread that's managed by a thread pool - consumes the messages as fast as they're available and hands them off to a component - a `MessageListener` - that we specify. 

```
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


    }
}
```

Once the `SimpleMessageListenerContainer` is `start()`'d and waiting for new messages, it's easy to then send messages using Spring's `JmsTemplate`. 

```
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
```

Note that we start the container first because otherwise there's a chance that the container won't be available when the message is sent (a race condition) and thus won't receive the message, invalidating our experiment. 

Building and Running the Client
--------------------------------------
To invoke the code and see the results of the search, simply run it from the command line, like this:

```sh
$ gradle run
```
	
This will compile the `main` method and then run it.


Next Steps
----------
Congratulations! You have successfully sent, and received, your first JMS message with Spring. 
* ...
