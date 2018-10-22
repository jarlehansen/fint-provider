# FINT provider

[![Build Status](https://travis-ci.org/FINTprosjektet/fint-provider.svg?branch=master)](https://travis-ci.org/FINTprosjektet/fint-provider)
[![Coverage Status](https://coveralls.io/repos/github/FINTprosjektet/fint-provider/badge.svg?branch=master)](https://coveralls.io/github/FINTprosjektet/fint-provider?branch=master)

## Configuration

| Key | Description | Default value |
|-----|---------------|-------------|
| fint.provider.ttl-status | How long (in minutes) the provider will wait for an initial status from the adapter (that the event was received and understood) | 2 |
| fint.provider.ttl-response |  How long (in minutes) the provider will wait for a response with the data from the adapter | 15 |
| fint.provider.max-number-of-emitters | The max number of emitters one orgId can have | 50 |
| fint.provider.event-state.list-name | The name of the list used to store event states stored in redisson | current-corrids |
| fint.provider.swagger-https | Force https in the swagger api-docs request | true (disabled in the test profile) |
| fint.provider.test-mode | Runs the provider in test-mode, enabling a test consumer and adapter. | false |

## Log payload

To log the event payload add the configuration to `application.properties` or as an environment variable.

```
logging.level.no.fint.provider.events.status.StatusController: DEBUG
logging.level.no.fint.provider.events.response.ResponseController: DEBUG
```