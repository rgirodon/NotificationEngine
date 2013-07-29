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
'Subject' : 'Tevez is the new Juve striker' 
'Content' : 'Argentine player has signed a 4-years contract with the Italy last-year champion.'
'Date'    : '01/06/2013'
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

### 3.2.1. Built-in Selectors

### 3.2.2. Built-in Notificators

### 3.2.3. Built-in Components

#### 3.2.3.1. Mailer

#### 3.2.3.2. Template Engine

## 3.3. Raw Notifications Push API

# 4. Extending the Notification Engine

## 4.1. Configuring custom components

### 4.1.1. Configuring custom selectors

### 4.1.2. Configuring custom notificators

## 4.2. Creating a custom project

### 4.2.1. Purpose and principles

### 4.2.2. Example of the JDBC Selector

## 4.3. Unit tests

# 5. Roadmap


