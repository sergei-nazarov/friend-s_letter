**Description**

Friend's Letter is a web service that allows users to create and exchange letters in a convenient environment. The
service is created using modern development technologies and tools, including Spring Boot, Spring Web, Spring Data,
Thymeleaf, OpenAPI (Swagger), Lombok, Actuator, Spring Security, Liquibase, HTML, and JavaScript. Docker Compose is used
for deployment and application management to simplify container management.

**Accessibility:**

Friend's Letter is available at http://sergeiprojects.ru/

API Documentation - http://sergeiprojects.ru/swagger-ui/index.html#/

**Application Schema:**

<img alt="The scheme of application" src="https://raw.githubusercontent.com/sergei-nazarov/friend-s_letter/main/scheme.jpg" width="600" height="400">

**Integration with External Services:**

Google Drive: Used for message storage.
PostgreSQL: As the main database for storing information.
Redis: For caching messages and improving performance.

**Project Features:**

1. **Message Creation and Exchange**: The core functionality of the project allows creating messages with additional
   parameters, such as specifying a date when the message will become unavailable, making a message public, or setting a
   limit on the number of views. When the recipient opens the link, they can see the sent message, ensuring
   confidentiality and convenience in communication.

2. **Asynchronous** Interaction with Google Drive: Messages in Google Drive are saved asynchronously. The ID for saving
   future messages is requested in advance. This ensures instant responses and eliminates the need to wait for the
   message to be saved on the server.

3. **Full Localization**: The service fully supports three languages: Russian, French, and English. The language is
   automatically detected based on the request header and is maintained within the user's session.

4. **API with Documentation**: For developers' convenience, an API is provided that allows viewing the list of messages,
   reading messages, and getting the most popular messages. This API is documented using OpenAPI, providing transparent
   and comprehensible access to the functionality.

[README also available in russian](https://github.com/sergei-nazarov/friend-s_letter/blob/main/README_RU.md)
