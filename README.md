# PS2 PES 2014 Option File Editor

[![Java CI](https://github.com/nthachus/pes-editor/actions/workflows/maven.yml/badge.svg)](https://github.com/nthachus/pes-editor/actions/workflows/maven.yml)

PlayStation 2 - Pro Evolution Soccer 2014 Option File Editor

- Fix almost bugs of original version.
- Multilingual support

## CONTRIBUTING

### Development using [Docker](https://docs.docker.com/)

Build this project with the command:

```batch
docker run --rm -it -v "%PWD%/.mvn:/root/.m2" -v "%PWD%:/usr/src/app" -w /usr/src/app openjdk:7-jdk-alpine ./mvnw -B clean verify
```

**Note** that `%PWD%` is the project working directory in `Unix` format, such as: `/c/Users/source/repos/pes-editor`

### Analyze source code with [SonarQube](https://www.sonarqube.org/)

Download [SonarQube Docker image](https://hub.docker.com/_/sonarqube/) and start the server:

```batch
docker pull sonarqube:community
docker run --rm -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube -Dsonar.telemetry.enable=false
```

Login to http://localhost:9000/ using `Administrator` account (admin/admin) and configure the project to analyze.
For more details, see: https://docs.sonarqube.org/latest/setup/get-started-2-minutes/

Then run Maven goal `sonar:sonar` to analyze the project:

```batch
docker run --rm -it --link sonarqube -v "%PWD%/.mvn:/root/.m2" -v "%PWD%:/usr/src/app" -w /usr/src/app openjdk:11-jre-slim ^
  ./mvnw -B sonar:sonar "-Dsonar.host.url=http://sonarqube:9000" -Dsonar.projectKey=pes-editor -Dsonar.login=<projectToken>
```

## DONATIONS

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=46LYJ44VJXAB6)
