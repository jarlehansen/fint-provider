const orgId = (process.argv[2] === undefined) ? 'rogfk.no' : process.argv[2]

const config = {
    host: 'localhost',
    user: 'guest',
    password: 'guest',
    vhost: '',
    orgId: orgId
}

const connectionString = `amqp://${config.user}:${config.password}@${config.host}:5672/${config.vhost}`
module.exports = {
  connectionString,
  config
}
