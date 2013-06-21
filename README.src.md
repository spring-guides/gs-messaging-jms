Getting Started: Messaging with JMS
===================================

What you'll build
-----------------

This guide will walk you through the process of publishing and subscribing to messages using a JMS broker.

What you'll need
----------------

 - About 15 minutes
 - {!include#prereq-editor-jdk-buildtools}

## {!include#how-to-complete-this-guide}


<a name="scratch"></a>
Set up the project
------------------

{!include#build-system-intro}

{!include#create-directory-structure-hello}

### Create a Maven POM

    {!include:initial/pom.xml}

{!include#bootstrap-starter-pom-disclaimer}

Installing and running ActiveMQ broker
--------------------------------------
To publish and subscribe to message, you need to install a JMS broker. For this guide, you will use ActiveMQ. Visit their [download page](http://activemq.apache.org/activemq-580-release.html), get the proper version, then unpack it.

> **Note:** ActiveMQ is an [AMQP](http://www.amqp.org/) broker that supports multiple protocols including [JMS](http://en.wikipedia.org/wiki/Java_Message_Service), the focus of this guide.

If you happen to be using a Mac with [Homebrew](http://mxcl.github.io/homebrew/), you can alternatively type:

    brew install activemq
    
On Ubuntu Linux, you can try:

    sudo apt-get install activemq
    
If you downloaded the bundle, unpack it and cd into the **bin** folder. If you used a package manager like **apt-get** or **brew**, it should already be on your path.

    activemq start
    
There are other options, but this will launch a simple broker. You should see something like this:

```
$ activemq start
INFO: Using default configuration
(you can configure options in one of these file: /etc/default/activemq /Users/gturnquist/.activemqrc)

INFO: Invoke the following command to create a configuration file
/usr/local/Cellar/activemq/5.8.0/libexec/bin/activemq setup [ /etc/default/activemq | /Users/gturnquist/.activemqrc ]

INFO: Using java '/Library/Java/JavaVirtualMachines/jdk1.7.0_11.jdk/Contents/Home//bin/java'
INFO: Starting - inspect logfiles specified in logging.properties and log4j.properties to get details
INFO: pidfile created : '/usr/local/Cellar/activemq/5.8.0/libexec/data/activemq-retina.pid' (pid '7781')
```
> **Note:** To shutdown the broker, run `activemq stop`.

Now you're all set to run the rest of the code in this guide!

<a name="initial"></a>
Creating a message receiver
---------------------------
For starters, Spring provides the means to publish messages to any POJO.

{!include:complete/src/main/java/hello/Receiver.java}

This is also known as a **message driven POJO**. There is no requirement on the name of the method as you'll see further below.

Sending and receiving JMS messages with Spring
----------------------------------------------
Next, you need to wire up a sender and a receiver.

{!include:complete/src/main/java/hello/Application.java}

The first key component is the `ConnectionFactory`. It is comprised of a `CachingConnectionFactory` wrapping an `ActiveMQConnectionFactory` pointed at `tcp://localhost:61616`, the default port of ActiveMQ.

To wrap the `Receiver` you coded earlier, use the `MessageListenerAdapter`. Then use the `setDefaultListenerMethod` to configure which method to invoke when a message comes in. This empower you to avoid implementing any JMS or broker-specific interfaces.

The `SimpleMessageListenerContainer` is an asynchronous message receiver. It uses the `MessageListenerAdapter` and the `ConnectionFactory` and is fired up when the application context starts. Another parameter is the queue name set in `mailboxDestination`.

Spring provides a convenient template class called the `JmsTemplate`. The `JmsTemplate` makes it very simple to send messages to a JMS message queue. In our `main` runner method, after starting things up, we create a `MessageCreator` and use it from our `jmsTemplate` to send a message.

> **Note:** Spring's `JmsTemplate` has the ability to receive messages, but it only works synchronously, meaning it will block. That's why it's usually recommend to use Spring's `SimpleMessageListenerCreator` with a cache-based connection factory.



Make the application executable
-------------------------------
You can bundle up the app as a runnable jar file thanks to the maven-shade-plugin as well as Spring Bootstrap's support for embedded Tomcat.

### {!include#build-an-executable-jar}


Run the service
---------------
Run your service with `java -jar` at the command line:

    java -jar target/gs-consuming-jms-complete-0.1.0.jar

When it runs, you should see a couple messages:

```
Sending a new mesage.
Received <ping!>
```

Summary
-------
Congrats! You've just developed a publisher and consumer of JMS-based messages.

[zip]: https://github.com/springframework-meta/gs-consuming-jms/archive/master.zip
