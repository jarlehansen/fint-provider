# FINT provider

[![Build Status](https://travis-ci.org/FINTprosjektet/fint-provider.svg?branch=master)](https://travis-ci.org/FINTprosjektet/fint-provider)
[![Coverage Status](https://coveralls.io/repos/github/FINTprosjektet/fint-provider/badge.svg?branch=master)](https://coveralls.io/github/FINTprosjektet/fint-provider?branch=master)

## Configuration

| Key | Default value | Description |
|-----|---------------|-------------|
| fint.provider.ttl-status | 2 | How long (in minutes) the provider will wait for an initial status from the adapter (that the event was received and understood) |
| fint.provider.ttl-response | 15 | How long (in minutes) the provider will wait for a response with the data from the adapter |
| fint.provider.max-number-of-emitters | 50 | The max number of emitters one orgId can have |

## Run provider locally

* [provider - http://localhost:8080/provider/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* [consumer - http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)
* [redis-commander - http://localhost:8081](http://localhost:8081)

`npm start --prefix test-clients`

Start only test-clients (useful to debug fint-provider)  
`npm run start-test-clients --prefix test-clients`