# 1. Understanding the Notification Engine

## 1.1. Main purpose of the Notification Engine

The Notification Engine is designed to be the application that centralizes (e-mail) notifications in your information system.

Today you probably have many different applications, and each of them probably sends various (e-mail) notifications to users.

Each application has the responsibility of :
- creating the notifications
- determining who needs to receive each notification
- physically send the notifications

The Notification Engine is designed to unify this :
- client applications push notifications to the Notification Engine
- the Notification Engine determines who is concerned by these notifications and then needs to receive them
- The Notification Engine physically builds the messages (e-mails)
- The Notification Engine physically sends the messages (e-mails)

The Notification Engine comes with a few built-in components for all these tasks.
Actually it is designed to be extended easily, to fit your company's needs.

Technically, the Notification Engine is written as a classic JavaEE application.
It uses well known frameworks and tools such as Spring or Maven to make its adoption easy in any company.
For its internal persistance, it uses MongoDB.

## 1.2. Key Concepts

### 1.2.1. Topics

Each Notification in the system will be about a Topic.
A Topic can be seen a pure String that tells what the Notification is about.

For example, a notification can be about Football, Java Programming, Hip-Hop...

What's interesting is that Topics can be organized into a hierarchical structure : 
- Sports 
- Sports.Football
- Sports.BasketBall
- Sports.Football.France
- Sports.Football.France.StEtienne
- Sports.Football.France.Marseille
- Sports.Football.France.ParisSG
- Sports.Football.Spain
- Sports.Football.UK

The idea behind that is that if a Notification is sent about the topic Sports.Football.France.StEtienne, all users who subscribed to Sports, Sports.Football, Sports.Football.France and Sports.Football.France.StEtienne will receive it.
At the opposite, users who subscribed to Sports.Football.Spain or Sports.Football.France.Marseille will not receive it.

### 1.2.2. Raw Notifications

When a notification is created, it does not hold any information about who will receive it.
It just holds :
- its Topic
- its Context

We explained what is a Topic in last chapter. The system will use the Topic of the Raw Notification to determine who needs to receive it.

The Context of a Raw Notification is just a Map, i.e. a list of pairs Key - Value.

For example :
```JSON
{
"Subject" : "Tevez is the new Juve striker", 
"Content" : "Argentine player has signed a 4-years contract with the Italy last-year champion.",
"Date" : "01/06/2013"
}
```
could be the context of a Raw Notification with Topic Sports.Football.Italy.Juventus

The Context will be used for building the notification messages. 
A common usage can be to build a mail content by populating a template from this context. 

### 1.2.3. Subscriptions

A subscription is the way the Notification Engine models the fact that a recipient has subscribed to a Topic.

For example, let's say that :
- supermec@mail.fr has subscribed to Sports.Football.France
- greatguy@mail.uk has subscribed to Sports.Football.UK
- tio@mail.es has subscribed to Sports.Football.Spain

### 1.2.4. Decorated Notifications

When the Notification Engine process Raw Notifications, for each Raw Notification it determines, depending on Subscriptions for its Topic, who are the recipients that need to receive it.

Then, it creates Decorated Notifications, that are the association of :
- a Raw Notification
- a Recipient

Let's imagine that there are a total of 1000 subscriptions for 
- Topic Sports.Football.Italy.Juventus 
- Sports.Football.Italy 
- Sports.Football
- Sports

Then, the Raw Notification about the arrival of Tevez in the Juventus will trigger the creation of 1000 Decorated Notifications, one for each recipient that has subscribed to one of these Topics.

### 1.2.5. Physical Notifications

Once the decorated notification has been sent, the notification created is stored as a physical notification. This is the notification in the format it will be read by the end-user. 
For instance, if the notification is a mail, the physical notification will be storing the subject and the content of the mail. It also has the attributes of the decorated notification (topic, recipient, send date).

## 1.3. Notification Engine Components

### 1.3.1. Selectors

In the Notification Engine, Selector components are responsible of :
- for a given Topic : 
- retrieving not processed Raw Notifications
- for each of retrieved Raw Notifications : 
- retrieving concerned Subscriptions 
- for each of retrieved Subscriptions :
- creating a Decorated Notification linked to the Raw Notification and the Subscription recipient
- marking it as processed

### 1.3.2. Notificators

In the Notification Engine, Notificator components are responsible of :
- for a given Topic : 
- retrieving not sent Decorated Notifications
- for each of retrieved Decorated Notifications : 
- sending it
- marking it as sent

### 1.3.3. Channels

Channels are the base items when configuring the Notification Engine.

Configuring the Notification Engine is actually defining the Channels that it will instantiate.

A Channel is defined by :
- its identifier
- its Topic
- its Selector
- its Notificator

Furthermore, the Notification Engine allows to set additionnal options, such as execution periods, mail templates, to complete configuration of components.

With no surprise, the configuration file of the Notification Engine will contain the definition of its Channels. 

# 2. Installing the Notification Engine

## 2.1. JavaEE Web application

### 2.1.1 JDK version

The Notification Engine is a classical JavaEE 6 web application.

It needs Java7.

### 2.1.2 Maven as a build tool

Maven 3 is used as the build tool.

The project is declared with type war.

## 2.2. Application Server

The Notification Engine has been validated with Glassfish 3, but it should run on any JavaEE application Server, including Tomcat since it does not use EJB components.

Before building the war, just edit the localsettings.properties file under src/main/resources.
See section "Local settings" for details on how to set local settings.

To build the war just run :

```
mvn package -DskipTests
```

See section "Unit tests" if you are reluctant with skipping tests.

Then you will get the war in your target directory.
You will just have to deploy it classically into your application server.

## 2.3. Notification Engine Database

The Notification Engine needs an internal persistance system, for storing Raw and Decorated Notifications.

It can also store Subscriptions in this persistance system.

MongoDB has been choosen for this internal persistance.

The Notification Engine has been validated with MongoDB 2.4.4.

By default, the Notification Engine expects a MongoDB instance running on localhost, on port 27017.
This instance should contain a database named "notificationengine", with collections named "rawnotifications" and "decoratednotifications".

# 3. Using the Notification Engine

## 3.1. Local settings

The file localsettings.properties file (under src/main/resources) is where you must set your local installation settings.

```
config.directory=D:/temp/notificationengine/config
templates.directory=D:/temp/notificationengine/templates
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=xxxxx
smtp.password=xxxxx
smtp.timeout=8500
smtp.starttls.enable=true
smtp.auth=true
mail.from=xxxxx
mail.subject=Notification Engine Mail
mongo.replica.mode=false
mongo.url=localhost:27017
mongo.database=notificationengine
administrator.address=xxxxx
```

- config.directory is the directory that will contain the configuration file (named configuration.json) of the Notification Engine.

- templates.directory is the directory that will contain the template files used by the Notificator components.

- smtp.* are properties that define the smtp settings that the system will use for sending mails.
All these properties are self explaining.

- mail.from is the default from address that will be set by the system for all sent emails.

- mail.subject is the default subject that will be set by the system for all sent emails.

- mongo.* are properties that define the mongoDb settings that the system will use for its internal persistance.
Note that you can define here a replicaSet, you just have to set the mongo.replica.mode property to true, and the mongo.url property to a comma separated list of host:port (localhost:27017,localhost:28017:localhost:29017 for example).

- administrator.address is the address where all emails will be sent if you choose the AdministratorSelector.

## 3.2. Configuration

As explained before, for the Notification Engine, a Configuration is the definition of the Channels that the Engine will instantiate.

This definition must be done in a file named configuration.json in the configuration directory specified in the localsettings.properties file.

You can find multiple of configuration files in config directory.

As you can see, the configuration is done in JSON :

Example 1
```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
},
{
"id" : "helpdeskChannel",
"topic" : "helpdesk",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorTaskPeriod" : "5000",
"selectorTaskPeriod" : "5000",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

Example 2
```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "mongoDbSelector",
"notificatorType" : "multipleMailByRecipient",
"mailTemplate" : "facturationMailTemplate"
},
{
"id" : "helpdeskChannel",
"topic" : "helpdesk",
"selectorType" : "mongoDbSelector",
"notificatorType" : "singleMailByRecipient",
"mailTemplate" : "helpdeskMailTemplate"
}
]
}
```

Example 3
```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "mongoDbSelector",
"notificatorType" : "singleMultiTopicMailByRecipient",
"mailTemplate" : "commonMailTemplate"
},
{
"id" : "helpdeskChannel",
"topic" : "helpdesk",
"selectorType" : "mongoDbSelector",
"selectorTaskPeriod" : "5000",
"notificatorType" : "singleMultiTopicMailByRecipient",
"notificatorTaskPeriod" : "5000",
"mailTemplate" : "commonMailTemplate"
}
]
}
```

As you can see, a Configuration is an array of Channels.

Each Channel has :
- an identifier
- a Topic
- a Selector
- the Selector can be of a known type, or of a custom type - in this case the Configuration must specify its full name class
- the Selector can have a specific execution period, expressed in ms with property selectorTaskPeriod (if it has not, if will be a 20s default period)
- a Notificator
- the Notificator can be of a known type, or of a custom type - in this case the Configuration must specify its full name class
- the Notificator can have a specific execution period, expressed in ms with property notificatorTaskPeriod (if it has not, if will be a 20s default period)
- any other needed options (just like mailTemplate in our examples) that will be accessible through a map at runtime

As an option, you can indicate if the channel is urgentEnabled. That means that you can configure a channel to have some urgent notifications that have to be sent right away and others that can wait a little time before being sent. 
This option, at the moment, is only working with email notificators and you have to provide a urgentMailTemplate option in the configuration. 

Example of a channel with the urgent option : 

```JSON
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "mongoDbSelector",
"notificatorType" : "singleMultiTopicMailByRecipient",
"mailTemplate" : "commonMailTemplate",
"isHtmlTemplate": "true",
"notificatorTaskPeriod" : "600000",
"urgentEnabled": "true",
"urgentMailTemplate": "facturationMailTemplate",
"isUrgentHtmlTemplate": "true"
}
```

To indicate that a raw notification is urgent, all you have to do is to add a field urgent set to true in the context of the notification like this one : 
```JSON
{
"topic": "football",
"context": {
"subject" : "Tevez is the new Juve striker", 
"content" : "Argentine player has signed a 4-years contract with the Italy last-year champion.",
"date" : "01/06/2013",
"urgent" : true
}
}
```

In order to secure the application, we also add configuration to set type of athenticator that will be in use.

```JSON
"authenticationType": "mongoAuthenticator",
"customAuthenticatorClass" : "customAuthenticator"
```

The first line is the type of authenticator in use. For the moment, the Notification Engine has only the mongoAuthenticator built in. This authenticator stores users with a username and a password in a mongo collection. 

But, as other configurations, you can implement your own authenticator based on the Authenticator interface. In order to extends the NotificationEngine with your own authenticator, you have to specify the class name of your implementation of the Authenticator interface.

### 3.2.1. Built-in Selectors

All selectors implement the ISelector interface, that just declares a process method.

```JAVA
public interface ISelector {

public void process();
}
```

A base class implementation Selector has been provided that does all the boilerplate code, and leaves the retrieveSubscriptionsForTopic method abstract.

Boilerplate code consists in :
- for a given Topic : 
- retrieving not processed Raw Notifications
- for each of retrieved Raw Notifications : 
- retrieving concerned Subscriptions (left abstract)
- for each of retrieved Subscriptions :
- creating a Decorated Notification linked to the Raw Notification and the Subscription recipient
- marking it as processed

```JAVA
public abstract class Selector implements ISelector {

// Not shown : boilerplate code

abstract protected Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic);
}
```

All concrete implementations of Selector will provide a specific way for retrieving Subscriptions for a given Topic.

#### 3.2.1.1 AdministratorSelector

This is the most simple Selector : for any Topic it retrieves just one Recipient, the Administrator email set in localsettings.properties.

To register this selector in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

#### 3.2.1.2 MongoDbSelector

This is a more advanced Selector. It retrieves Subscriptions in the MongoDB database used by the Notification Engine for internal persistance.

It looks for Subscriptions in a collection named subscriptions, and with this format.

```JSON
{
"_id" : ObjectId("51f7c7e04531027fab736425"),
"topic" : {
"name" : "facturation.societe1"
},
"recipient" : {
"address" : "xxxx@zzzz.com"
}
}
```

Once registered, this Selector also activates a controller that will allow to create Subscriptions by HTTP PUT requests :

If content

```JSON
{
"topic" : "facturation",
"recipient" : "xxxx@yyyy.com",
"displayName": "John Doe"
}
```

is sent to URL http://host:port/notificationengine/subscription.do with method PUT and header Content-Type set to application/json, then such subscription will be persisted.

You can retrieve this example in client directory.

To register this selector in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "mongoDbSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

#### 3.2.1.3. HoldInNotification Selector

This selector guess who are the subscribers from the context of the raw notification. 
In order to do that, you have to add a field called ```recipients``` in the context. The value associated to this key has to be an array of objects representing recipients. According to that, the raw notification should have this format: 

```JSON
{
"topic": "football",
"context": {
"subject" : "Tevez is the new Juve striker", 
"content" : "Argentine player has signed a 4-years contract with the Italy last-year champion.",
"date" : "01/06/2013",
"recipients" : [
{
"email"	 : "john@doe.com",
"displayName"	: "John Doe"
},
{
"email"	 : "super@guy.com",
"displayName"	: "Super Guy"
}
]
}
}
```

To enable this kind of selector, the selector type in the ```configuration.json``` has to be ```holdInNotificationSelector```.

### 3.2.2. Built-in Notificators

All notificators implement the INotificator interface, that just declares a process method.

```JAVA
public interface INotificator {

public void process();
public void setUrgentEnabled(Boolean urgentEnabled);
}
```

A base class implementation Notificator has been provided that does all the boilerplate code, and leaves the processNotSentDecoratedNotifications method abstract.

Boilerplate code consists in :
- for a given Topic : 
- retrieving not sent Decorated Notifications
- for each of retrieved Decorated Notifications : 
- sending it (left abstract)
- marking it as sent

```JAVA
public abstract class Notificator implements INotificator {

// Not shown : boilerplate code

protected abstract void processNotSentDecoratedNotifications(
Collection<DecoratedNotification> notSentDecoratedNotifications);
}
```

All concrete implementations of Notificator will provide a specific way for processing not sent Decorated Notifications.

#### 3.2.2.1 LoggerNotificator

This is the most simple Notificator : it just logs with a level INFO any Decorated Notification.

To register this notificator in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

#### 3.2.2.2 MultipleMailByRecipientNotificator

This is quite a simple Notificator : it just send an email for any Decorated Notification.

To register this notificator in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "multipleMailByRecipient",
"mailTemplate" : "facturationMailTemplate",
"isHtmlTemplate": "true"
}
]
}
```

As you can see, this notificator needs an option "mailTemplate".
In our example, the built-in Template Engine will look for a template file named facturationMailTemplate.template in the template directory specified in localsettings.properties.

We also added on option to indicate if the template is written in HTML or not. If it is the case, you can add HTML tags in the template and add some resources. If you want to add static resources, you can set the folder in the ```localsettings.properties```file and then you have to set the source of the resource in the template like this:
```HTML
<img src="http://serveraddress/resources/my-image.jpeg" alt="My Image"/>
```

For building the mail content, this notificator will merge this template with a Context containing :
- all the entries of the Raw Notification Context
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{displayName}}

This mail has been sent by Facturation application.

{{message}}

Best regards,

Facturation Team
```

And here is an example of an HTML template fort this notificator : 
```
<p>Dear {{displayName}}</p>

<p>This mail has been sent by Facturation application.</p>

{{{content}}}

<p>Best regards,</p>

<img src="http://serveraddress/resources/my-image.jpeg" alt="My Image"/>

<p>Facturation Team</p>
```

Please note that if your ```content``` is written in HTML, you have to use the annotation with three curly braces in order to have the HTML unescaped (it comes from the Mustache documentation)

#### 3.2.2.3 SingleMailByRecipientNotificator

This is quite a more advanced Notificator : for a given topic, when it has found not sent Decorated Notifications, it will just send 1 mail by recipient, grouping the not sent Notifications.

To register this notificator in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "helpdesk",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "singleMailByRecipient",
"mailTemplate" : "helpdeskMailTemplate"
}
]
}
```

As you can see, this notificator needs an option "mailTemplate".
In our example, the built-in Template Engine will look for a template file named helpdeskMailTemplate.template in the template directory specified in localsettings.properties.

If you want to write HTML templates, you just have to do the same as explained in the previous part (3.2.2.2 MultipleMailByRecipientNotificator) 

For building the mail content, this notificator will merge this template with a Context containing :
- an entry named "notificationsByRecipient" which is the list of the Contexts of the grouped RawNotifications
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{displayName}}

This mail has been sent by Helpdesk application.

Please take into account these actions from Helpdesk :

{{#notificationsByRecipient}}
> {{message}}
{{/notificationsByRecipient}}

Best regards,

Helpdesk Team
```

#### 3.2.2.4 SingleMultiTopicMailByRecipientNotificator

This is the most advanced Notificator : when it has found not sent Decorated Notifications, it will just send 1 mail by recipient, grouping the not sent Notifications of multiple Topics.

To register this notificator in a Channel, here is an example :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "mongoDbSelector",
"notificatorType" : "singleMultiTopicMailByRecipient",
"mailTemplate" : "commonMailTemplate"
},
{
"id" : "helpdeskChannel",
"topic" : "helpdesk",
"selectorType" : "mongoDbSelector",	
"selectorTaskPeriod" : "5000",
"notificatorType" : "singleMultiTopicMailByRecipient",
"notificatorTaskPeriod" : "5000",
"mailTemplate" : "commonMailTemplate"
}
]
}
```

As you can see, this notificator needs an option "mailTemplate".
In our example, the built-in Template Engine will look for a template file named commonMailTemplate.template in the template directory specified in localsettings.properties.

In order to send an HTML mail, you have to follow what is indicated in part 3.2.2.2 MultipleMailByRecipientNotificator. 

For building the mail content, this notificator will merge this template with a Context containing :
- an entry named "topics" which is a list of Contexts containing :
- an entry "topic" with the name of the Topic
- an entry "notificationsForTopic" which is the list of the Contexts of the grouped RawNotifications of this Topic for this recipient
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{displayName}}

This mail has been sent by Notification application.

{{#topics}}

{{topic}}

{{#notificationsForTopic}}

> {{message}}

{{/notificationsForTopic}}

{{/topics}}

Best regards,

Notification Team
```

### 3.2.3. Built-in Components

#### 3.2.3.1. Mailer

The system ships a Mailer for sending the emails.

The Mailer is based on Spring mail module.

The Mailer configuration is done in localsettings.properties :

```
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=xxxxx
smtp.password=xxxxx
smtp.timeout=8500
smtp.starttls.enable=true
smtp.auth=true
```

If you need the Mailer in a custom notificator, you can get it in Spring context.
We provide a SpingUtils utility class for getting beans.

Here is the way of getting the Mailer :

```JAVA
Mailer mailer = (Mailer)SpringUtils.getBean(Constants.MAILER);
```

Then you just have to ask the mailer to send a content to a given address with its method sendMail :

```JAVA
// sent a mail to the recipient
mailer.sendMail(decoratedNotification.getRecipient().getAddress(), notificationText);
```

#### 3.2.3.2. Template Engine

The system ships a Template Engine for building the email content.

The Template Engine is based on Mustache, and all templates must follow Mustache syntax.

The Template Engine looks for templates files in the template directory specified in localsettings.properties.
It looks for files named with the value of option "mailTemplate" of the configuration, with the extension ".template".

If you need the Template Engine in a custom notificator, you can get it in Spring context.
We provide a SpingUtils utility class for getting beans.

Here is the way of getting the Template Engine :

```JAVA
TemplateEngine templateEngine = (TemplateEngine)SpringUtils.getBean(Constants.TEMPLATE_ENGINE); 
```

### 3.2.4. Built-in Authenticators

At the moment, we only have one authenticator built in: the MongoAuthenticator.

This authenticator stores in a MongoDB collection all users registered with a username and an encrypted password. This authenticator is based on the interface ```Authenticator```.

## 3.3. Raw Notifications Push API

To enable creation of Raw Notifications, the Notification Engine proposes a REST API.

If content

```JSON
{
"topic" : "facturation.societe1",
"context" : {
"salutation" : "Hola chicos",
"message" : "Hay que pagar ahora"
}
}
```

is sent to URL http://host:port/notificationengine/rawNotification.do with method POST and header Content-Type set to application/json, then a Raw Notification with such Topic and Context will be persisted.

It will be in the MongoDB database, in rawnotifications collection, with that format :

```JSON
{
"_id" : ObjectId("51f7c7e04531027fab736421"),
"processed" : false,
"topic" : {
"name" : "facturation.societe1"
},
"context" : {
"salutation" : "Hola chicos",
"message" : "Hay que pagar ahora."
}
}
```

You can also send a rawNotification with files attachments. In order to do that, you just have to do a POST on this URL : ```http://host:port/notificationengine/rawNotificationWithAttach.do```. The elements to post are a JSON named json which has the same format as shown before and files, each one named files[]. The Content-Type in the header should be multipart/form-data. 


You can retrieve this example in client directory.

## 3.4. Metrics available

Some metrics are available through a REST API. These can be used for a front-end that summarize the activity of the NotificationEngine. 

### 3.4.1. Subscription metrics

It is possible to get two different metrics for subscriptions : 
- get count of all subscriptions with the url ```countAllSubscriptions.do```
- get count of all subscriptions for a topic with the url ```countAllSubscriptionsForTopic.do?topic=topicName```

The response would look like this in the first case : 
```JSON
{
"count": 6
}
```

And like this in the second case : 
```JSON
{
"count": 3,
"topic": {
"name": "topicName"
}
}
```

### 3.4.2. RawNotification metrics

The NotificationEngine allows to get 6 different metrics for raw notifications : 
- get count of all raw notifications created with the url ```countAllRawNotifications.do```. It responds the following JSON : 
```JSON
{
"count": 42
}
```

- get count of all raw notifications for a specific topic (```countRawNotificationsForTopic.do?topic=topicName```) that responds : 
```JSON
{
"count": 3,
"topic": {
"name": "topicName"
}
}
```

- get count of all raw notifications not processed (```countNotProcessedRawNotifications.do```). It gives this JSON : 
```JSON
{
"count": 42
}
```

- get count of all not processed raw notifications for a topic (```countNotProcessedRawNotificationsForTopic.do?topic=topicName```). As a response, one gets : 
```JSON
{
"count": 3,
"topic": {
"name": "topicName"
}
}
```

- get count of all raw notifications created for last x days (```countCreatedRawNotificationsForLastDays.do?days=5```). The response is like : 
```JSON
[
{
"count": 4,
"date": "2013-08-09"
},
{
"count": 5,
"date": "2013-08-08"
},
{
"count": 2,
"date": "2013-08-10"
},
{
"count": 9,
"date": "2013-08-06"
},
{
"count": 3,
"date": "2013-08-07"
}
]
```
where dates are at the format ```yyyy-MM-dd```

- get count of all processed raw notifications created for last x days (```countProcessedRawNotificationsForLastDays.do?days=5```). The response is like : 
```JSON
[
{
"count": 4,
"date": "2013-08-09"
},
{
"count": 3,
"date": "2013-08-08"
},
{
"count": 4,
"date": "2013-08-10"
},
{
"count": 7,
"date": "2013-08-06"
},
{
"count": 3,
"date": "2013-08-07"
}
]
```
where dates are at the format ```yyyy-MM-dd```


### 3.4.3. DecoratedNotification metrics

You can get 6 different metrics for Decorated notifications : 
- get count of all decorated notifications created with the url ```countAllDecoratedNotifications.do```. It responds the following JSON : 
```JSON
{
"count": 42
}
```

- get count of all decorated notifications for a specific topic (```countAllDecoratedNotificationsForTopic.do?topic=topicName```) that responds : 
```JSON
{
"count": 3,
"topic": {
"name": "topicName"
}
}
```

- get count of not sent decorated notifications (```countNotSentDecoratedNotifications.do```). It gives this JSON : 
```JSON
{
"count": 42
}
```

- get count of all not sent decorated notifications for a topic (```countNotSentDecoratedNotificationsForTopic.do?topic=topicName```). As a response, one gets : 
```JSON
{
"count": 3,
"topic": {
"name": "topicName"
}
}
```

- get count of all decorated notifications created for last x days (```countCreatedDecoratedNotificationsForLastDays.do?days=5```). The response is like : 
```JSON
[
{
"count": 4,
"date": "2013-08-09"
},
{
"count": 3,
"date": "2013-08-08"
},
{
"count": 4,
"date": "2013-08-10"
},
{
"count": 7,
"date": "2013-08-06"
},
{
"count": 3,
"date": "2013-08-07"
}
]
```
where dates are at the format ```yyyy-MM-dd```

- get count of all sent decorated notifications created for last x days (```countSentDecoratedNotificationsForLastDays.do?days=5```). The response is like : 
```JSON
[
{
"count": 4,
"date": "2013-08-09"
},
{
"count": 3,
"date": "2013-08-08"
},
{
"count": 4,
"date": "2013-08-10"
},
{
"count": 7,
"date": "2013-08-06"
},
{
"count": 3,
"date": "2013-08-07"
}
]
```
where dates are at the format ```yyyy-MM-dd```

### 3.4.4. Deleted Decorated Notifications metrics

When a decorated notification cannot be sent 5 times, we suppose that there is an issue with the recipient's address and the code is made that the email will not be sent anymore. Instead of simply delete this email from the decorated notifications table in the database, we put it in another table named deleted decorated notifications. In that way, it is possible to know how many notifications were not sent. 

To get metrics about this, you can call 2 different URLs : 
- ```/countDeletedDecoratedNotifications.do```to get a global count of not sent notifications
- ```/countDeletedDecoratedNotificationsForLastDays.do?days=30```to get a count per day of deleted decorated notifications. 

The JSON sent in response are the same as described in previous parts. 

### 3.4.5. Decorated notifications

It is also possible to retrieve decorated notifications through the REST API, and not only metrics about it. 
You can add optional parameters to the url to call : 
- email of the recipient to retrieve only decorated notifications sent to a specific email address
- number of last decorated notifications sent

For instance, if you want to retrieve the last 10 decorated notifications sent to john@doe.com, you will have to call ```/getDecoratedNotifications.do?email=john@doe.com&number=10``` 

As a response, you will receive a JSON with the following format : 

```JSON
[
{
"_id": {
"_time": 1379577313,
"_machine": -426248641,
"_inc": -1870395218,
"_new": false
},
"recipient": {
"address": "john@doe.com",
"displayName": "John Doe"
},
"rawNotification": {
"_id": {
"_time": 1379577261,
"_machine": -426248641,
"_inc": -1870395223,
"_new": false
},
"processed": false,
"topic": {
"name": "helpdesk"
},
"createdAt": "Sep 19, 2013 9:54:21 AM",
"context": {
"subject": "Test with file",
"content": "I hope I would be able to download the file associated to this notification",
"date": "19/9/2013",
"urgent": false,
"fileIds": [
{
"_time": 1379577261,
"_machine": -426248641,
"_inc": -1870395222,
"_new": false
}
]
}
},
"sent": true,
"createdAt": "Sep 19, 2013 9:55:13 AM",
"sentAt": "Sep 19, 2013 9:55:49 AM",
"sendingAttempts": 0
},
...
]
```

### 3.4.6. Topics

Thanks to the REST API, it is possible to get a list of topics and sub topics. 

If you want to get a list of all topics, the URL to call is ```/topics.do```. 
The response looks like this JSON : 
```JSON
[
{
"name": "facturation"
},
{
"name": "helpdesk.societe1"
},
{
"name": "facturation.societe2"
},
{
"name": "helpdesk.societe2"
},
{
"name": "facturation.societe1"
}
]
```

And if you want to get all sub-topics of a topic you have to call the following URL : ```/subTopicsForTopic.do?topic=facturation```
You will receive this response : 
```JSON
[
{
"name": "facturation.societe2"
},
{
"name": "facturation"
},
{
"name": "facturation.societe1"
}
]
```

### 3.4.7. Subscriptions

The REST API also provides a way to get informations about subscriptions. 

To get the number of total subscriptions, you have to call the URL ```/countAllSubscriptions.do```. 

If you want to count all subscriptions for a particular topic, you can call ```/countAllSubscriptionsForTopic.do?topic=facturation```

In both above cases, the answer will be (for instance) : 
```JSON
{"count":6}
```

If your selector is an instance of ISelectorWriteEnabled, you also have the ability of creating and removing subscriptions. 
In order to do that, you have to call ```subscriptions.do```. 

If you want to create a subscription, you have to do an HTTP PUT on this URL and to provide a JSON in the body and to set the content-type to ```application/json```. 
The JSON to provide should be like this : 
```JSON
{
"topic": "facturation",
"recipient": "superguy@email.com",
"displayName": "Super Guy"
}
```

If you want to remove a subscription, the URL is still the same but the HTTP method is DELETE. You also have to provide two parameters : the recipient email and the topic. If the deletion is successful, you will receive an HTTP Status of OK (200) 

### 3.4.8. Physical Notifications

The notification engines's REST API has a URL to retrieve physical notifications. This URL is ```/physicalNotifications.do```. you can add the recipient's email address as a paramerter to filter physical notifications. the response format is JSON. 

```JSON
[
{
"sentAt": 1379506537482,
"id": 1180840879,
"subject": "Helpdesk notif",
"filesAttached": [],
"recipient": {
"email": "john@doe.com",
"displayName": "John Doe"
},
"notificationContent": "<p>Dear John Doe</p>\n\n<p>This mail has been sent by Helpdesk application.<p>\n\n<p>Please take into account these actions from Helpdesk :<br/>\n\n> This notification should generate a physical notification after you received it. \n</p>\n\n<p>Best regards,</p>\n\n<p>Helpdesk Team</p>"
},
{
"sentAt": 1379506877240,
"id": 1496271426,
"subject": "File attached",
"filesAttached": [
{
"id": "52399a77e69724ea1138fa97",
"fileName": "images_off.bmp"
}
],
"recipient": {
"email": "john@doe.com",
"displayName": "John Doe"
},
"notificationContent": "<p>Dear John Doe</p>\n\n<p>This mail has been sent by Facturation application.</p>\n\n> You have a file attached to this notification\n\n<p>Best regards,</p>\n\n<p>Facturation Team</p>"
},
...
]
```

### 3.4.9. Files

Since it is possible to send files with notifications, we also added a URL to retrieve those files. The URL to call is ```/files/{objectId}/{filename}.{extension}``` where : 
- objectId is the id of the file (given in physical notification)
- filename is the name of the file without the extension
- extension is the extension of the file (pdf, jgp, png etc.)

The response will be the file itself. 

# 4. Security

In order not to allow everybody to use the Notification Engine, we added some security in the application. 

## 4.1. Cors Filter

By default, a web application prevents JavaScript from making XMLHttpRequest to other domains. We created a filter that allows to do it in order to be able to build other applications that use the API of the NotificationEngine. 

But you can limit this permission to particular domain. This configuration can be done in the class ```CorsFilter``` under the package ```org.notificationengine.web.filter```. In order to do that, you have to change the following line: 

```java
response.addHeader("Access-Control-Allow-Origin", "*");
```

Instead of the star, you can list all domains that will be allowed to do requests to the NotificationEngine, separated by commas. 

This filter is only made for web application that do requests to the NotificationEngine. It is still possible to do CURL to the NotificationEngine. That's why we also added a token based security.

## 4.2. Tokens

To secure the NotificationEngine, we added a token based security. It means that each request has to have a valid token in the header. The header property that contains the token is simply called ```token```.

There are two ways to get a token: either you login with a username/password through the ```login.do``` URL and you will receive a token valid for 30 minutes (the time is reinitialized each time an action is done with the token), or you create a token valid for a very long period through the ```requestToken.do``` URL. In order to be able to request a token for a long time, you have to be already logged in. This can be simply done with the admin console.

All tokens are stored in a MongoDB collection. This can't be changed for now.

# 5. Extending the Notification Engine

The Notification Engine provides some built-in Selectors and Notificators, but there are chances that you need to create new Selectors or Notificators to fit your needs.

There are 2 ways for doing this :

#### First option
- get the code base
- add your own components
- build the war
- deploy it

#### Second option
- get the code base 
- install it in your maven repo
- create a custom project where you add your own components
- build the war of your custom project
- deploy the war of your custom project

In both cases, you need to add your components to the configuration file.

## 5.1. Configuring custom components

### 5.1.1. Configuring custom selectors

If you look at the configuration of the AdministratorSelector, you can already see how to configure a custom Selector :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

As you can see, you declare the selectorType as "customSelector", and provide the full name of the selector class in the selectorClass field.

The NotificationEngine will then instantiate your selector, using a 2 parameters constructor :
- one for the Topic
- one for the map of options (provided in the configuration file)

Here is the example with the code of the AdministratorSelector :

```JAVA
public class AdministratorSelector extends Selector {

public AdministratorSelector(Topic topic, Map<String, String> options) {
super(topic, options);
}

@Override
protected Collection<Subscription> retrieveSubscriptionsForTopic(Topic topic) {
// not shown...
}
}
```

Just do the same with your own custom selectors.

### 5.1.2. Configuring custom notificators

If you look at the configuration of the LoggerNotificator, you can already see how to configure a custom Notificator :

```JSON
{
"channels" : [
{
"id" : "facturationChannel",
"topic" : "facturation",
"selectorType" : "customSelector",
"selectorClass" : "org.notificationengine.selector.AdministratorSelector",
"notificatorType" : "customNotificator",
"notificatorClass" : "org.notificationengine.notificator.LoggerNotificator"
}
]
}
```

As you can see, you declare the notificatorType as "customNotificator", and provide the full name of the notificator class in the notificatorClass field.

The NotificationEngine will then instantiate your notificator, using a 2 parameters constructor :
- one for the Topic
- one for the map of options (provided in the configuration file)

Here is the example with the code of the LoggerNotificator :

```JAVA
public class LoggerNotificator extends Notificator {

public LoggerNotificator(Topic topic, Map<String, String> options) {
super(topic, options);
}

@Override
protected void processNotSentDecoratedNotifications(
Collection<DecoratedNotification> notSentDecoratedNotifications) {
// not shown...
}

}
```

Just do the same with your own custom notificators.

## 5.2. Creating a custom project

### 5.2.1. Purpose and principles

It is possible to extend the Notification Engine without altering the code base.

All you need is creating a new Maven project declaring in its pom.xml :

- the Notification Engine "core" as a war dependency (for applying the war overlay technique provided by maven war plugin)

- the Notification Engine "core" as a classic dependency (with "classes" classifier for the code to compile)

This is illustrated by this extract of the pom.xml of the custom project JDBC selector provided under custom/jdbcselector directory :
```XML
<dependency>
<groupId>org.notificationengine</groupId>
<artifactId>notificationengine</artifactId>
<version>0.0.1-SNAPSHOT</version>
<type>war</type>
</dependency> 

<dependency>
<groupId>org.notificationengine</groupId>
<artifactId>notificationengine</artifactId>
<version>0.0.1-SNAPSHOT</version>
<classifier>classes</classifier>
</dependency>
```

### 5.2.2. Example of the JDBC Selector

We applied this option for creating a custom project that extends the Notification Engine by providing a JDBC based Selector.

You can see in last chapter an except of the pom.xml file of this custom project.

The JDBC selector can be configurated in its localsettingsJdbcSelector.properties file (under src/main/resources) :

```
jdbc.driverClassName=org.hsqldb.jdbc.JDBCDriver
jdbc.url=jdbc:hsqldb:hsql://localhost/subscriptions
jdbc.username=SA
jdbc.password=
jdbc.sql.order=SELECT u.EMAIL FROM USER u, SUBSCRIPTION s, TOPIC t WHERE u.ID = s.USER_ID AND t.ID = s.TOPIC_ID AND t.LABEL = :topic
jdbc.sql.recipient.alias=EMAIL
jdbc.sql.topic.param=topic
``` 

Properties "jdbc.driverClassName", "jdbc.url", "jdbc.username", "jdbc.password" are self-explaining.

Property "jdbc.sql.order" is the SQL order that will be executed by the selector to get the Subscriptions for a given Topic.

Property "jdbc.sql.recipient.alias" is the name of the alias for the column containing the address of the recipient concerned by the Subscription.

Property "jdbc.sql.topic.param=topic" is the name of the parameter used for substituting the Topic at runtime.

Note that if you use a database different than hsqldb, you will have to add the dependency in the pom.xml. 

# 6. Admin console

An admin console has been created to use the REST API of the NotificationEngine. The source code is hosted on a [GitHub repo](https://github.com/matthis-d/NotificationEngine-front). 

The built of this admin console is placed in the folder ```src/main/webapp/WEB-INF/console-admin``` of the NotificationEngine. In that way, when you start the NotificationEngine, by default, it shows the admin console.

# 7. Unit tests

We tried to add some unit tests to our Notification Engine.
Some of them need a MongoDB instance running on localhost, on port 27017.
This instance should contain a database named notificationengine_test, with collections rawnotifications, decoratednotifications and subscriptions.
This is not very "state of the art", please be indulgent :)

# 8. Roadmap

We use the issues of GitHub to define the new features we plan to implement.

We also use it to list the bugs we find.

Feel free to contribute, and we would really welcome any of your suggestions for improving the Notification Engine.
