# System Monitor
[![Build Status](https://travis-ci.org/imedvedko/system-monitor.svg?branch=master)](https://travis-ci.org/imedvedko/system-monitor)
[![Coverage Status](https://coveralls.io/repos/github/imedvedko/system-monitor/badge.svg?branch=master)](https://coveralls.io/github/imedvedko/system-monitor?branch=master)

Real-time monitoring tool

## Runbook
### Development Environment
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. [Docker](https://docs.docker.com/install/)
3. [Docker Compose](https://docs.docker.com/compose/install/)
### Download
    git clone git@github.com:imedvedko/system-monitor.git
### Change Directory
    cd system-monitor
### Build
    docker-compose up build 
### Run Docker Containers
    docker-compose up server web
### Open Monitoring Page in your favorite browser
    http://localhost:8080/
### Remote Debug
1. Open "Run/Debug Configurations" Window in [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Run -> Edit Configurations...)
2. Click "+" to Add New Configuration
3. Select "Remote"
4. Configure [Remote Debug](https://www.jetbrains.com/help/idea/run-debug-configuration-remote-debug.html) with these parameters
5. Click "OK" to save
6. Run Debug (Run -> Debug...)

|     Item      |   Value   |
|:-------------:|:---------:|
| Debugger mode |  Attach   |
|     Host      | localhost |
|     Port      |   5005    |

### Server Properties
|          Property           | Default Value |
|:---------------------------:|:-------------:|
|  --sensor.disk.device-name  |     sda       |
|  --sensor.disk.sector-size  |     512       |
| --sensor.net.interface-name |     eth0      |

### Web Properties
|   Property   |      Description       |          Example           |
|:------------:|:----------------------:|:--------------------------:|
| --sensor.url | URL of Server Endpoint | http://server:8080/sensors |

### Links
1. [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
2. [Spring Boot Common application properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
