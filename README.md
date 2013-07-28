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

### 1.2.2. Raw Notifications

### 1.2.3. Subscriptions

### 1.2.4. Decorated Notifications

## 1.3. Notification Engine Components

### 1.3.1. Selectors

### 1.3.2. Notificators

### 1.3.3. Channels

# 2. Installing the Notification Engine

## 2.1. JavaEE Web application

### 2.1.1 JDK version

### 2.1.2 Maven as a build tool

## 2.2. Application Server

## 2.3. Notification Engine Database

# 3. Using the Notification Engine

## 3.1. Local settings

## 3.2. Configuration

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


