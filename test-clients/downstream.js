const uuid = require('uuid')
const amqp = require('amqplib')

console.log('Usage: npm run downstream <rabbitmq-password> <orgId>')

const config = {
    host: 'localhost',
    user: 'guest',
    password: 'guest',
    vhost: '',
    orgId: process.argv[2]
}

const message = {
    "corrId": uuid(),
    "action": "GET_ALL_EMPLOYEES",
    "status": "DOWNSTREAM_QUEUE",
    "time": new Date().getTime(),
    "orgId": config.orgId,
    "source": "fk",
    "client": "vfs",
    "message": null,
    "data": []
}

const connectionString = `amqp://${config.user}:${config.password}@${config.host}:5672/${config.vhost}`
amqp.connect(connectionString).then(function(conn) {
  return conn.createChannel().then(function(ch) {
    const q = `${config.orgId}.downstream`
    const json = JSON.stringify(message)

    const ok = ch.assertQueue(q, {durable: true});
    return ok.then(function(qok) {
      ch.sendToQueue(q, new Buffer(json))
      console.log('Message sent to', q)
      return ch.close()
    })
  }).finally(() => { conn.close() })
}).catch(console.warn)
