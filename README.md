### Desk Nova
An IT Service Desk ticketing system with workload allocation management & admin panel made with Spring Boot & React.

### Setup

#### Gradle

- Ensure you you have the latest version of gradle by downloading the installation files on their website [Gradle Releases](https://gradle.org/releases/) and the follow their installation guide here: [Gradle Installation Guide](https://docs.gradle.org/current/userguide/installation.html).

- Next, properly link the distribution url to either your local `gradle-'ver'-bin.zip` or to the default url.

- If you're going to use a local distribution url, go to `backend/gradle/wrapper/gradle-wrapper.properties` and paste the local file path to the `distributionUrl` property.

  `Example: distributionUrl=file:///C:/Users/USER/Downloads/gradle-9.3.0-bin.zip`

- Run `gradle build` to ensure the project builds successfully

 