const amqp = require('amqplib')

const rabbit = require('./rabbit')
const events = require('./events')

console.log('Usage: npm run downstream <orgId>')
console.log(JSON.stringify(events.createMessage(rabbit.config.orgId)))

amqp.connect(rabbit.connectionString).then((conn) => {
  return conn.createChannel().then((ch) => {
    const q = `${rabbit.config.orgId}.downstream`
    const json = JSON.stringify(events.createMessage(rabbit.config.orgId))

    const ok = ch.assertQueue(q, {durable: true});
    return ok.then((qok) => {
      ch.sendToQueue(q, new Buffer(json), { contentType: 'application/json'})
      console.log('Message sent to', q)
      return ch.close()
    })
  }).finally(() => { conn.close() })
}).catch(console.warn)
