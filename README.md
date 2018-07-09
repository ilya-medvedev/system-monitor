# System Monitor
Real-time monitoring tool

## Runbook
### Development Environment
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. Java Standard Edition Development Kit 8 ([Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net/install/))
3. [Maven](https://maven.apache.org/download.cgi) 3
### Download
    git clone git@github.com:imedvedko/system-monitor.git
### Change directory
    cd system-monitor
### Build
    mvn clean package
### Run
    java -jar modules/server/target/system-monitor.jar ${properties}
### Run Docker Container
#### Environment
1. [Docker](https://docs.docker.com/install/)

#### Build Docker Image
    mvn -P docker -pl modules/server docker:build

#### Run Docker Container
    docker run -i -t --rm \
               --name system-monitor \
               --publish 8080:8080 \
               --publish 5005:5005 \
               --memory 128M \
               system-monitor ${properties}

##### Links
1. [Docker run reference](https://docs.docker.com/engine/reference/run/)

#### Remote Debug
1. Open "Run/Debug Configurations" Window in [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Run -> Edit Configurations...)
2. Click "+" to Add New Configuration
3. Select "Remote"
4. Configure [Remote Debug](https://www.jetbrains.com/help/idea/run-debug-configuration-remote-debug.html) with these parameters
5. Click "OK" to save
6. Run Debug (Run -> Debug...)

|     Item      | Value  |
|:-------------:|:------:|
| Debugger mode | Attach |
|     Host      |  Port  |
|     Port      |  5005  |

### Properties
|          Property           | Default Value |
|:---------------------------:|:-------------:|
|  --sensor.disk.device-name  |     sda       |
|  --sensor.disk.sector-size  |     512       |
| --sensor.net.interface-name |     eth0      |

#### Links
1. [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
2. [Spring Boot Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
