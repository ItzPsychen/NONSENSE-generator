# NONSENSE-generator

<img src="src/main/resources/icons/icon.png" alt="App Preview" width="300" height="300" />

## *The Application*

The *NONSENSE-generator* is an application that lets you create an **infinite amount of sentences** without any logical reason. You can use it to analyze sentences you type in, visualize the syntactic tree of any kind of phrase, or even to know the levels of toxicity and more!

The Application provides to the user a simple and user-friendly interface provided with a form. Here you will have the opportunity to type a sentence of your own and play with it. By analyzing and generating, you can create different new fancy sentences. As a user, you will also have the possibility to personalize the generating methods and strategies by selecting what kind of structures and words to use.

All this comes with *customizable* settings. Here you can choose themes, where to get your input and output files, and also expand our vocabulary of words, which we use to make better and more complicated sentences.

---

At the beginning we will show you what the settings will be. Here you have access to the path where everything is *got* and *saved*.

#### 1. **Setting the API key**

We will ask you, user, to set your personal path of the API key inside the appropriate space provided at the top of the settings. Once done, just *Apply* and get started.

#### 2. **Get Started with the Generator**

After setting all the needed information, you now have full access to the application. You can now use the generator as you wish without any problem of losing what you generated, by using the *Save* option that puts everything you do into an output file. All settings are also saved so that they don't revert to default once you close the app. 

#### 3. **Exit the Application**

The user can simply use our provided *"Exit"* button on top of the form, without any problem of losing their data about what they generated.

#### 4. **Credits**

At the top of our Generator, you can open a *Credits* section using the *"About"* button. Here you can see the team with all the names of the developers.

---

## *The All-Functions Manual*

The following section provides a detailed breakdown of the Application, so that you won't get lost using it in the future.

- **Analyze** button
  
  By entering a sentence and analyzing it, you will be provided with the structure of the sentence, along with the syntactic tree if the user wants to.

- **Generate** button
  
  This is the main button, used to create our nonsense sentences. You, user, have at your disposal a complete menu where you can choose from multiple options how to generate the sentences.

- **Menu Options** for generating
  
  The *Sentence Structure* section lets you choose if you want the application to choose it randomly, maintain the same one used from the input, or choose one yourself. From the settings, you can even make it *recursive*.  
  The *Word Generation* section has two options: the first will make our Generator use new words you did not type in, while the second will make your generated result in future tense.

- **Use Generated** button
  
  This simple button will re-use your generated sentence as a new input by writing it in the input-text section. Now the previous generation can be analyzed and used again as you wish.

- **Toxicity** bars
   
  These five bars will help you visualize better at what level of toxicity, profanity, insult, sexual and political content your generated sentence is at. The lower the level, the more green they become; the higher they get, the more red they will be.

- **Save** option
    
  This checkbox, if selected, will automatically take all your future generated sentences and one by one add them to a file that will never be deleted automatically by the Application. In this file, you will always have the possibility to check your previously created sentences.

- **Settings**
  
  The Settings option, found under *File*, will open a new form where you can change the paths of many files. At the bottom, the user can change the recursion level (previously cited), disable it, set the maximum length of an input sentence and finally change the theme (light/dark).

- **Vocabulary**
  
  This form, which you can open with the *Vocabulary* option under *Edit*, will let you add all the nouns, verbs and adjectives you want so that the Generator in the future can use the new words you provided.

- **About**
   
  The Credits section explained as before. You can find this by going to the *Help* option and then *About*.

---

## *User Manual*
Before using this program, make sure your device meets the following requirements:

- **Java Development Kit (JDK) version 21** must be installed.
- A valid **Google API Key** is required to access the application's services.
  
**Installation**

After verifying the requirements, proceed with the installation of the project on your local machine.

Once the installation is complete, you can start the program using one of the following methods:

- by launching the program from the terminal

- by launching the program from the application interface


## Design Patterns Used
This application incorporates several well-known design patterns to ensure separation of concerns, scalability, and maintainability. Below is a detailed explanation of the patterns used:

### 1. Facade Pattern
**Main class:** The Facade pattern is centrally implemented by this class which provides a simplified interface to access different complex services. This class: `AppManager`
- Aggregates the services `AnalyzeSentenceService`, `GenerateSentenceService`, and `ModerationSentenceService`
- Exposes high-level methods such as `analyzeSentence()` and `generateSentence()`
- Hides the implementation complexity of individual services
- Manages result-saving operations through `FileManager`

The class also implements an aspect of the Facade pattern, acting as an intermediary between the user interface and backend system, simplifying interactions with `AppManager`.

### 2. Singleton Pattern
**Classes implementing the pattern:**
- `ConfigManager`: Ensures a single instance for managing application configurations
- `LoggerManager`: Provides a centralized access point for loggers
- `Noun`, `Verb`, `Adjective`: Implement Singleton to ensure a single reference source for respective grammatical categories
- `SentenceStructure`: Ensures centralized management of sentence structures

All these classes maintain a private static instance of themselves and provide a `getInstance()` method to access this instance.

### 3. Strategy Pattern
**Main interfaces:**
- `StructureSentenceStrategy`: Defines the strategy for generating sentence structures
- `WordSelectionStrategy`: Defines the strategy for word selection
- `TenseStrategy`: Defines the strategy for verb conjugation

**Concrete implementations:**
- Sentence structures: `RandomStructureStrategy`, `SameAsAnalyzedStructureStrategy`, `SelectedStructureStrategy`
- Word selection: `NewWordStrategy`, `OriginalWordStrategy`
- Verb tense: `PresentTenseStrategy`, `FutureTenseStrategy`

This pattern allows for dynamically interchanging sentence generation algorithms, making the system highly flexible.

### 4. Observer Pattern
**Interfaces:**
- `ConfigObserver`: For observing configuration changes
- `FileObserver`: For observing file changes

**Classes implementing the pattern:**
- `APIClient`: Observes changes to API configuration
- `Noun`, `Verb`, `Adjective`, `SentenceStructure`: Implement `ConfigObserver` to react to configuration changes
- `FileManager`: Acts as a subject to notify file changes
- `Word` (abstract class): Implements `FileObserver` to react to file changes

### 5. Factory Pattern
**Main class:** `WordFactory`
The `WordFactory` class implements the Factory Method Pattern, providing a method to create instances of different word-handling classes based on the requested type (`WordType.NOUN`, `WordType.VERB`, `WordType.ADJECTIVE`), centralizing creation logic.



### Classes with Multiple Design Patterns
- `ConfigManager`: Implements both Singleton and Observer (as subject)
- `AppManager`: Implements Facade and uses Singleton (`ConfigManager`)
- `Verb`: Implements Singleton, Observer, and Strategy (for tense management)
- `Word`: Implements Template Method and Observer (as observer)
- `FormController`: Implements Facade and Command

This architecture rich in design patterns makes the system highly modular, extensible, and with a clear separation of responsibilities, facilitating both maintenance and software evolution.

## *Installation Guide*

This guide describes how to run the NONSENSE Generator application either by downloading the pre-built JAR file or running the project from the source code using Maven.

### 1. Prerequisites

Before running the application, make sure you have the following tools installed and configured:

#### Java Development Kit (JDK) 21 or later
- Check the version with:
  ```bash
  java -version
  ```
- If not installed, download it from [Adoptium](https://adoptium.net/) or another trusted provider.

#### Apache Maven (Minimum version: 3.8.0)
- Verify the Maven installation with:
  ```bash
  mvn -version
  ```
- If Maven is not installed, download it at [Maven Official Website](https://maven.apache.org/download.cgi).

#### JavaFX Runtime (Version 21.0.7 or later)
- Download the JavaFX SDK from [OpenJFX Download Page](https://gluonhq.com/products/javafx/), and extract it to a directory (e.g., `/opt/javafx-sdk-21.0.7`).

### 2. Running the Application

#### Option 1: Using the Pre-Built JAR

**Download the JAR File**

- Navigate to our [latest release](https://github.com/ItzPsychen/NONSENSE-generator/releases/tag/V1.0) and download the latest
  pre-built JAR file (e.g., `NONSENSE-generator.jar`).

---

#### ⚠️ IMPORTANT
Please copy the JAR file into the main project directory, alongside the src/ folder.  This is necessary because the application currently loads resource files using relative paths from the execution directory. We apologize for the inconvenience — this is a known limitation and we're actively working to improve the resource handling in a future version, so that the application can properly load resources from within the JAR itself.

Thank you for your understanding!

---

**Run the Application**
- Use the following command to launch the application, making sure to specify the JavaFX SDK path. For example if your JavaFX SDK is into `/opt/javafx-sdk-21.0.7`, the command would look like:
  ```bash
  java --module-path /opt/javafx-sdk-21.0.7/lib \
      --add-modules javafx.controls,javafx.fxml \
      -jar NONSENSE-generator-1.0-SNAPSHOT.jar
  ```

#### Option 2: Running from Source with Maven

If you'd like to run the project directly from the source code, follow these steps:

**Clone the Repository**
- Open a terminal and execute:
  ```bash
  git clone https://github.com/your-repo-link.git
  cd nonsense-generator
  ```

**Run the Application via Maven**
- Use the following Maven command to launch the application:
  ```bash
  mvn javafx:run
  ```
- Maven will automatically download dependencies and start the application.

#### Option 3: Running from IDE (e.g. Intellij)

You can easily run the NONSENSE Generator application directly from your IDE, such as IntelliJ IDEA. Follow these steps to set up and execute the project:

1. Open Edit Configurations…
2. Add New Configuration > maven
3. In the run option write `javafx:run`




## *Execution Environment Requirements*

### Project Requirements
- **Java Development Kit (JDK) 21**: The source and target compatibility are both set to Java 21 as specified in the pom.xml file:
  ```xml
  <properties>
      <maven.compiler.source>21</maven.compiler.source>
      <maven.compiler.target>21</maven.compiler.target>
  </properties>
  ```
  This ensures compatibility with features introduced in Java 21.

- **JavaFX (version 21.0.7)**: Required for the development of the graphical user interface (GUI). The required JavaFX modules are:
  - javafx-controls
  - javafx-fxml

  To run the project with JavaFX, ensure that the Java environment is configured to support JavaFX.

### Minimum System Requirements
- **RAM**: A minimum of 2GB, as large linguistic models may consume significant memory.
- **Processor**: Any modern processor supporting Java 21.
- **Disk Space**: Approximately 500MB for runtime libraries, dependencies, and application files.
- **Operating System Compatibility**:
  The project can run on systems supporting Java 21 and JavaFX, including:
  - Windows
  - macOS
  - Linux

### Environment Variables
The application is designed to handle configuration in a secure and automated manner using predefined files and environment variables. Specifically, it relies on the `.env` file to dynamically manage key configuration paths and settings.

**Key Variables Managed by the Application**:
- **CONFIG_FILE_PATH**: Specifies the path to the primary configuration file used to store and load application settings.
- **DEFAULT_CONFIG_FILE_PATH**: Indicates the fallback configuration file path.
- **LOG_LEVEL**: Defines the verbosity level of logs generated by the application.

## *Project Dependencies*

The project uses Apache Maven for dependency management and builds. Maven handles the downloading and organization of the required third-party libraries, ensuring they are integrated seamlessly into the build process. Below is a list of critical dependencies and a brief explanation of their role in the project:

### JavaFX Modules
Used for building the graphical interface with tools like javafx-controls and javafx-fxml to ensure a responsive and interactive UI.

### Log4J (Apache Logging)
Provides robust logging capabilities to track application actions, debug behavior, and handle runtime errors efficiently.

### Stanford CoreNLP
Offers advanced linguistic analysis, such as parsing the syntactic structure of sentences and recognizing parts of speech (e.g., nouns, verbs). The StanfordCoreNLP pipeline is primarily used to generate the syntax tree


### Google Cloud APIs
Facilitates integration with Google's Natural Language Processing services, supporting text analysis, toxicity evaluation, and content moderation. The project uses Google Cloud's Natural Language API for content classification and text moderation.



### Apache Commons (Lang3)
Contains utility functions for string manipulation, randomization, and validating complex input data.

### Dotenv
Manages environmental configurations securely through an external file, ensuring flexibility for sensitive variables like API keys and file paths (.env).

### JUnit 5 (JUnit Jupiter)
Enables unit and integration testing, ensuring the application's codebase remains robust, reliable, and maintainable.


## Deliverables

The following deliverables have been produced for this project:

- [**User Manual**](documentation/Manual%20of%20usage%20-%20NONSENSE%20generator.pdf)
- [**User Stories**](https://randomgenerator.atlassian.net/jira/software/projects/SCRUM/boards/1) 
- [**Design Document**](documentation/Design%20Document.pdf) - [Individual graphs and charts can be viewed here](documentation/graphs)
- [**Source Code**](https://github.com/ItzPsychen/NONSENSE-generator)
- [**System Test Document**](documentation/System%20Test%20Report.pdf)
- [**Unit Test Report**](https://itzpsychen.github.io/NONSENSE-generator/surefire-reports/surefire-report.html)

## Documentation

### User Manual

A comprehensive user manual has been created to help you understand and use all the features of the NONSENSE-generator application. The manual includes step-by-step instructions, screenshots, and troubleshooting tips to ensure a smooth user experience.

[**Download User Manual (PDF)**](documentation/Manual%20of%20usage%20-%20NONSENSE%20generator.pdf)

### Code Documentation

Detailed documentation of the codebase is available online. This documentation includes:

- Class hierarchies and relationships
- Design patterns implementation details
- API documentation
- Package structure
- Sequence diagrams
- Developer guidelines

The documentation is generated automatically from code comments and is regularly updated to reflect the current state of the codebase.

[**View Code Documentation**](https://itzpsychen.github.io/NONSENSE-generator/)