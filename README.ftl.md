<#assign project_id="gs-messaging-jms">
This guide walks you through the process of publishing and subscribing to messages using a JMS broker.

What you'll build
-----------------

You'll build an application that uses Spring's `JmsTemplate` to post a single message and subscribes to it with a POJO using `MessageListenerAdapter`.

What you'll need
----------------

 - About 15 minutes
 - <@prereq_editor_jdk_buildtools/>

## <@how_to_complete_this_guide jump_ahead='Create a message receiver'/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>


<@create_both_builds/>

<@bootstrap_starter_pom_disclaimer/>

<a name="initial"></a>
Create a message receiver
---------------------------
Spring provides the means to publish messages to any POJO.

    <@snippet path="src/main/java/hello/Receiver.java" prefix="complete"/>

This is also known as a **message driven POJO**. As you can see in the code above, there is no need to implement any particular interface or for the method to have any particular name.

Send and receive JMS messages with Spring
----------------------------------------------
Next, wire up a sender and a receiver.

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

To wrap the `Receiver` you coded earlier, use `MessageListenerAdapter`. Then use the `setDefaultListenerMethod` to configure which method to invoke when a message comes in. Thus you avoid implementing any JMS or broker-specific interfaces.

The `SimpleMessageListenerContainer` class is an asynchronous message receiver. It uses the `MessageListenerAdapter` and the `ConnectionFactory` and is fired up when the application context starts. Another parameter is the queue name set in `mailboxDestination`. It is also set up to receive messages ina 

Spring provides a convenient template class called `JmsTemplate`. `JmsTemplate` makes it very simple to send messages to a JMS message queue. In the `main` runner method, after starting things up, you create a `MessageCreator` and use it from `jmsTemplate` to send a message.

Two beans that you don't see defined are `JmsTemplate` and `ActiveMQConnectionFactory`. These are created automatically by Spring Boot. In this case, the ActiveMQ broker runs embedded.

> **Note:** Spring's `JmsTemplate` can receive messages directly through its `receive` method, but it only works synchronously, meaning it will block. That's why Spring recommends that you use Spring's `SimpleMessageListenerContainer` with a cache-based connection factory, so you can consume messages asynchronously and with maximum connection efficiency.


<@build_an_executable_jar_subhead/>

<@build_an_executable_jar_with_both/>


<@run_the_application_with_both module="service"/>

When it runs, buried amidst all the logging, you should see these messages:

```
Sending a new mesage.
Received <ping!>
```

Summary
-------
Congratulations! You've just developed a publisher and consumer of JMS-based messages.
