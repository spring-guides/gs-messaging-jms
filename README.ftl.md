<#assign project_id="gs-messaging-jms">

Getting Started: Messaging with JMS
===================================

What you'll build
-----------------

This guide walks you through the process of publishing and subscribing to messages using a JMS broker.

What you'll need
----------------

 - About 15 minutes
 - ActiveMQ JMS broker (instructions below)
 - <@prereq_editor_jdk_buildtools/>

## <@how_to_complete_this_guide jump_ahead='Install and run ActiveMQ broker'/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>

### Create a Maven POM

    <@snippet path="pom.xml" prefix="complete"/>

<@bootstrap_starter_pom_disclaimer/>

<a name="initial"></a>
Install and run ActiveMQ broker
--------------------------------------
To publish and subscribe to messages, you need to install a JMS broker. For this guide, you will use ActiveMQ. 

> **Note:** ActiveMQ is an [AMQP](http://www.amqp.org/) broker that supports multiple protocols including [JMS](http://en.wikipedia.org/wiki/Java_Message_Service), the focus of this guide.

Visit the [ActiveMQ download page](http://activemq.apache.org/activemq-580-release.html), get the proper version, then unpack it.

Alternatively, if you use a Mac with [Homebrew](http://mxcl.github.io/homebrew/):

    brew install activemq
    
Or on Ubuntu Linux:

    sudo apt-get install activemq
    
If you download the bundle, unpack it and cd into the **bin** folder. If you use a package manager like **apt-get** or **brew**, it should already be on your path.

Launch a simple broker:

    activemq start
    
You see output something like this:

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
> **Note:** To shut down the broker, run `activemq stop`.

Now you're all set to run the rest of the code in this guide!

Create a message receiver
---------------------------
Spring provides the means to publish messages to any POJO.

    <@snippet path="src/main/java/hello/Receiver.java" prefix="complete"/>

This is also known as a **message driven POJO**. There is no requirement on the name of the method, as you will see.

Send and receive JMS messages with Spring
----------------------------------------------
Next, wire up a sender and a receiver.

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

The first key component is the `ConnectionFactory`interface. It consists of a `CachingConnectionFactory` wrapping an `ActiveMQConnectionFactory` pointed at `tcp://localhost:61616`, the default port of ActiveMQ.

To wrap the `Receiver` you coded earlier, use `MessageListenerAdapter`. Then use the `setDefaultListenerMethod` to configure which method to invoke when a message comes in. Thus you avoid implementing any JMS- or broker-specific interfaces.

The `SimpleMessageListenerContainer` class is an asynchronous message receiver. It uses the `MessageListenerAdapter` and the `ConnectionFactory` and is fired up when the application context starts. Another parameter is the queue name set in `mailboxDestination`.

Spring provides a convenient template class called `JmsTemplate`. `JmsTemplate` makes it very simple to send messages to a JMS message queue. In the `main` runner method, after starting things up, you create a `MessageCreator` and use it from `jmsTemplate` to send a message.

> **Note:** Spring's `JmsTemplate` can receive messages, but it only works synchronously, meaning it will block. That's why Spring usually recommends that you use Spring's `SimpleMessageListenerCreator` with a cache-based connection factory.



Make the application executable
-------------------------------
You can bundle the app as a runnable jar file, thanks to the maven-shade-plugin as well as Spring Bootstrap's support for embedded Tomcat.

## <@build_an_executable_jar/>


Run the service
---------------
Run your service with `java -jar` at the command line:

    java -jar target/${project_id}-complete-0.1.0.jar

When it runs, you should see these messages:

```
Sending a new mesage.
Received <ping!>
```

Summary
-------
Congratulations! You've just developed a publisher and consumer of JMS-based messages.
