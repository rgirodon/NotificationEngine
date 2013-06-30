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