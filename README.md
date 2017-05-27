# FINT provider

[![Build Status](https://travis-ci.org/FINTprosjektet/fint-provider.svg?branch=master)](https://travis-ci.org/FINTprosjektet/fint-provider)
[![Coverage Status](https://coveralls.io/repos/github/FINTprosjektet/fint-provider/badge.svg?branch=master)](https://coveralls.io/github/FINTprosjektet/fint-provider?branch=master)

## Configuration

| Key | Default value |
|-----|---------------|
| fint.provider.eventstate.host | localhost |
| fint.provider.eventstate.port | 6379 |
| fint.provider.test-mode | false |
| fint.provider.max-number-of-emitters | 50 |

## Run provider locally

* [provider - http://localhost:8080/provider/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* [consumer - http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)
* [redis-commander - http://localhost:8081](http://localhost:8081)

`npm start --prefix test-clients`

Start only test-clients (useful to debug fint-provider)  
`npm run start-test-clients --prefix test-clients`