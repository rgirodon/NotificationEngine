# Notification Engine

Notification Engine based on :
+ an HTTP Listener with REST API for listening notification demands

+ Generic Pull Daemons for detecting changes and sending notification demands :
  + JDBC Pull Daemon
  + FileSystem Pull Daemon
  + MongoDB Pull Daemon

+ Generic Selector for determining who subscribed to notifications :
  + JDBC Selector
  + MongoDB Selector
  + LDAP Selector

+ Generic Notificator for physically sending notifications

Internal persistance is done with MongoDB database.

Many configurations options are not developped yet.

## Settings 
This application needs to have a mongoDB database running and can be ran on a glassfish 3.1 server. 

The database have to be named notificationengine with 3 collections: rawnotifications, decoratednotifications and subscriptions.