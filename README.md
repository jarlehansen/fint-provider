# FINT provider

[![Build Status](https://travis-ci.org/FINTprosjektet/fint-provider.svg?branch=master)](https://travis-ci.org/FINTprosjektet/fint-provider)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1b8e2b2813394091b02048f6db310547)](https://www.codacy.com/app/FINT/fint-provider?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=FINTprosjektet/fint-provider&amp;utm_campaign=Badge_Grade)

## Configuration

| Key | Default value |
|-----|---------------|
| fint.provider.eventstate.host | localhost |
| fint.provider.eventstate.port | 6379 |
| fint.provider.test-mode | false |
| fint.provider.max-number-of-emitters | 20 |

## Test clients

Simple test clients written in nodejs to help test various elements of the fint-provider application.  
To run the test clients move into the test-clients directory and run npm commands.

| Start command | Description |
|---------------|-------------|
| npm run sse *orgId* | Server-Sent-Events client that will connect to the backend, read messages and post back results |
| npm run downstream *rabbitmq-password* *orgId* | Publishes Event-objects to the downstream queue |