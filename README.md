# FINT provider

## Test clients

Simple test clients written in nodejs to help test various elements of the fint-provider application.  
To run the test clients move into the test-clients directory and run npm commands.

| Start command | Description |
|---------------|-------------|
| npm run sse *orgId* | Server-Sent-Events client that will connect to the backend, read messages and post back results |
| npm run downstream *rabbitmq-password* *orgId* | Publishes Event-objects to the downstream queue |