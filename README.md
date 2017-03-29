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
    java -jar application/target/system-monitor.jar ${properties}

### Run Docker Container

#### Development Environment
1. [Install Docker](https://docs.docker.com/engine/installation/)

#### Build Docker Image
    mvn -pl application docker:build

#### Run Docker Container
    docker run -d \
               --name system-monitor \
               -v `pwd`/application/docker:/opt/system-monitor/config:ro \
               -p 8080:8080 \
               system-monitor ${properties}

##### Links:
1. [Docker run reference](https://docs.docker.com/engine/reference/run/)

### Properties
|        Property         |                                                Description                                                 |
|:-----------------------:|:----------------------------------------------------------------------------------------------------------:|
|  --sensors.disk.device  | Device name (See [/proc/diskstats](https://www.kernel.org/doc/Documentation/ABI/testing/procfs-diskstats)) |
| --sensors.net.interface |                                       Interface (See /proc/net/dev)                                        |

#### Links:
1. [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
2. [Spring Boot Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
