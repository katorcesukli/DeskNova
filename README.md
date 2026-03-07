### Desk Nova
An IT Service Desk ticketing system with workload allocation management & admin panel made with Spring Boot & React.

### Setup

#### Docker

- Ensure you have docker installed on your machine to properly create the image

- To build the project's docker image, go to the `/backend` folder and run `docker build -t desk-nova-app .`

- Next to run the image, run `docker run -e "SPRING_PROFILES_ACTIVE=dev -p 8080 desk-nova-app"`, we need to inject the `spring_profiles_active` property to dev in the command so that the project knows which environment file will be run. Note that property profile should have all the environment variables needed to run the backend (e.g. DATABASE_URL, DATABASE_USERNAME)  

#### Gradle

- Ensure you you have the latest version of gradle by downloading the installation files on their website [Gradle Releases](https://gradle.org/releases/) and the follow their installation guide here: [Gradle Installation Guide](https://docs.gradle.org/current/userguide/installation.html).

- Next, properly link the distribution url to either your local `gradle-'ver'-bin.zip` or to the default url.

- If you're going to use a local distribution url, go to `backend/gradle/wrapper/gradle-wrapper.properties` and paste the local file path to the `distributionUrl` property.

  `Example: distributionUrl=file:///C:/Users/USER/Downloads/gradle-9.3.0-bin.zip`

- Run `gradle build` to ensure the project builds successfully

 
