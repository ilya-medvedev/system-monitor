# System Monitor
Real-time monitoring tool

## Runbook

### Development Environment
1. Install [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. Install Java Standard Edition Development Kit  8 ([Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net/install/))
3. Install [Maven](https://maven.apache.org/download.cgi) 3

### Download
    git clone git@github.com:ilya-medvedev/system-monitor.git

### Build
    cd system-monitor
    mvn clean package

### Run
    java -jar server/target/system-monitor.jar ${properties}

### Run Docker Container

#### Development Environment
1. [Install Docker](https://docs.docker.com/engine/installation/)

#### Build Docker Image
    mvn -pl server docker:build

#### Run Docker Container
    docker run -i -t --rm \
               --name system-monitor \
               -p 8080:8080 \
               system-monitor ${properties}

##### Links:
1. [Docker run reference](https://docs.docker.com/engine/reference/run/)

### Properties
|          Property           | Default |
|:---------------------------:|:-------:|
|  --sensor.disk.device-name  |  sda    |
|  --sensor.disk.sector-size  |  512    |
| --sensor.net.interface-name |  eth0   |

#### Links:
1. [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
2. [Spring Boot Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
