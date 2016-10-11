System Monitor
==============

Development Environment
-----------------------
1. Install Git ([Git SCM](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git))
2. Install Java Standard Edition Development Kit  8 ([Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net/install/))
3. Install Maven 3 ([Apache Maven Project](https://maven.apache.org/download.cgi))

Download
--------
    git clone git@github.com:ilya-medvedev/system-monitor.git

Build
-----
    cd system-monitor
    mvn clean package

Run
---
    java -jar application/target/application-${version}.jar ${properties}

Properties
----------
|    Property     | Required |                                                Description                                                 |
|:---------------:|:--------:|:----------------------------------------------------------------------------------------------------------:|
|  --disk.device  |   True   | Device name (See [/proc/diskstats](https://www.kernel.org/doc/Documentation/ABI/testing/procfs-diskstats)) |
| --net.interface |   True   |                                       Interface (See /proc/net/dev)                                        |

See:
1. [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
2. [Spring Boot Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)