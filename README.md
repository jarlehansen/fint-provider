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

## Run provider locally

* [provider - http://localhost:8080/provider/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* [consumer - http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)
* [redis-commander - http://localhost:8081](http://localhost:8081)

Start fint-provider + test-consumer and adapter  
`npm start --prefix test-clients`

Start only test-clients (useful to debug fint-provider)  
`npm run start-test-clients --prefix test-clients`

Start fint-provider + test-consumer (useful to test adapter)  
`npm run start-test-consumer --prefix test-clients`