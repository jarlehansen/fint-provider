const fs = require('fs')
const YAML = require('yamljs')
const amqp = require('amqplib')

const events = require('./downstream-events/events')

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

const connectionString = `amqp://${config.user}:${config.password}@${config.host}:5672/${config.vhost}`
amqp.connect(connectionString).then(function(conn) {
  return conn.createChannel().then(function(ch) {
    var q = `${config.orgId}.downstream`
    var msg = JSON.stringify(events(config.orgId))

    var ok = ch.assertQueue(q, {durable: true});
    return ok.then(function(qok) {
      ch.sendToQueue(q, new Buffer(msg))
      console.log('Message sent to', q)
      return ch.close()
    })
  }).finally(() => { conn.close() })
}).catch(console.warn)
