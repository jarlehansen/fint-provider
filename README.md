# FINT provider

[![Build Status](https://travis-ci.org/FINTprosjektet/fint-provider.svg?branch=master)](https://travis-ci.org/FINTprosjektet/fint-provider)
[![Coverage Status](https://coveralls.io/repos/github/FINTprosjektet/fint-provider/badge.svg?branch=master)](https://coveralls.io/github/FINTprosjektet/fint-provider?branch=master)

## Configuration

| Key | Default value |
|-----|---------------|
| fint.provider.eventstate.host | localhost |
| fint.provider.eventstate.port | 6379 |
| fint.provider.test-mode | false |
| fint.provider.max-number-of-emitters | 20 |

## Run consumer locally

`npm start --prefix test-clients`