const fs = require('fs')
const uuid = require('uuid')
const YAML = require('yamljs')
const amqp = require('amqplib')

console.log('Usage: npm run downstream <rabbitmq-password> <orgId>')

const ymlConfig = fs.readFileSync('../src/main/resources/application.yml', 'utf-8')
const springConfig = YAML.parse(ymlConfig)

const config = {
  host: springConfig.spring.rabbitmq.host,
  user: springConfig.spring.rabbitmq.username,
  vhost: springConfig.spring.rabbitmq['virtual-host'],
  password: process.argv[2],
  orgId: process.argv[3]
}

const message = {
    "corrId": uuid(),
    "verb": "GET",
    "status": "NEW",
    "time": 1479663045761,
    "orgId": config.orgId,
    "source": "fk",
    "client": "client",
    "message": null,
    "data": []
  }

const connectionString = `amqp://${config.user}:${config.password}@${config.host}:5672/${config.vhost}`
amqp.connect(connectionString).then(function(conn) {
  return conn.createChannel().then(function(ch) {
    var q = `${config.orgId}.downstream`
    var json = JSON.stringify(message)

    var ok = ch.assertQueue(q, {durable: true});
    return ok.then(function(qok) {
      ch.sendToQueue(q, new Buffer(json))
      console.log('Message sent to', q)
      return ch.close()
    })
  }).finally(() => { conn.close() })
}).catch(console.warn)
