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
"recipient" : "xxxx@yyyy.com"
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

### 3.2.2. Built-in Notificators

All notificators implement the INotificator interface, that just declares a process method.

```JAVA
public interface INotificator {

	public void process();
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

### 3.2.2.1 LoggerNotificator

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

### 3.2.2.2 MultipleMailByRecipientNotificator

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
				  "mailTemplate" : "facturationMailTemplate"
				}
			 ]
}
```

As you can see, this notificator needs an option "mailTemplate".
In our example, the built-in Template Engine will look for a template file named facturationMailTemplate.template in the template directory specified in localsettings.properties.

For building the mail content, this notificator will merge this template with a Context containing :
- all the entries of the Raw Notification Context
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{recipient}}

This mail has been sent by Facturation application.

{{message}}

Best regards,

Facturation Team
```

### 3.2.2.3 SingleMailByRecipientNotificator

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

For building the mail content, this notificator will merge this template with a Context containing :
- an entry named "notificationsByRecipient" which is the list of the Contexts of the grouped RawNotifications
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{recipient}}

This mail has been sent by Helpdesk application.

Please take into account these actions from Helpdesk :

{{#notificationsByRecipient}}
> {{message}}
{{/notificationsByRecipient}}

Best regards,

Helpdesk Team
```

### 3.2.2.4 SingleMultiTopicMailByRecipientNotificator

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

For building the mail content, this notificator will merge this template with a Context containing :
- an entry named "topics" which is a list of Contexts containing :
  - an entry "topic" with the name of the Topic
  - an entry "notificationsForTopic" which is the list of the Contexts of the grouped RawNotifications of this Topic for this recipient
- an additionnal entry named "recipient" containing the recipient address.

The template syntax is the one of Mustache framework.

Here is an example of template for this notificator :
```
Dear {{recipient}}

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

is sent to URL http://host:port/notificationengine/rawNotification.do with method PUT and header Content-Type set to application/json, then a Raw Notification with such Topic and Context will be persisted.

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

You can retrieve this example in client directory.

# 4. Extending the Notification Engine

The Notification Engine provides some built-in Selectors and Notificators, but there are chances that you need to create new Selectors or Notificators to fit your needs.

There are 2 ways for doing this :

1. First option
- get the code base
- add your own components
- build the war
- deploy it

2. Second option
- get the code base 
- install it in your maven repo
- create a custom project where you add your own components
- build the war of your custom project
- deploy the war of your custom project

In both cases, you need to add your components to the configuration file.

## 4.1. Configuring custom components

### 4.1.1. Configuring custom selectors

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

### 4.1.2. Configuring custom notificators

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

## 4.2. Creating a custom project

### 4.2.1. Purpose and principles

### 4.2.2. Example of the JDBC Selector

## 4.3. Unit tests

# 5. Roadmap


