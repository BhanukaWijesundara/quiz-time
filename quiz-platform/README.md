# Quiz Platform

This is a quiz platform application built using Spring Boot. It allows users to create, manage, and participate in quizzes. The application includes features for user authentication, quiz management, and a user-friendly interface.

## Features

- User registration and authentication
- Create, retrieve, and submit quizzes
- Manage user profiles
- Responsive design for a better user experience

## Project Structure

```
quiz-platform
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── quizplatform
│   │   │               ├── QuizApplication.java
│   │   │               ├── controller
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── QuizController.java
│   │   │               │   └── UserController.java
│   │   │               ├── service
│   │   │               │   ├── QuizService.java
│   │   │               │   └── UserService.java
│   │   │               ├── model
│   │   │               │   ├── Quiz.java
│   │   │               │   ├── Question.java
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   ├── QuizRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── dto
│   │   │               │   ├── QuizDto.java
│   │   │               │   └── UserDto.java
│   │   │               └── config
│   │   │                   └── SecurityConfig.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── db
│   │       │   └── migration
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── quizplatform
│                       ├── controller
│                       │   └── QuizControllerTest.java
│                       └── service
│                           └── QuizServiceTest.java
├── pom.xml
├── .gitignore
└── README.md
```

## Getting Started

1. **Clone the repository:**
   ```
   git clone <repository-url>
   ```

2. **Navigate to the project directory:**
   ```
   cd quiz-platform
   ```

3. **Build the project using Maven:**
   ```
   mvn clean install
   ```

4. **Run the application:**
   ```
   mvn spring-boot:run
   ```

5. **Access the application:**
   Open your web browser and go to `http://localhost:8080`.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or features.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.