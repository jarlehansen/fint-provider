const EventSource = require('eventsource')
const http = require('http')
const sleep = require('sleep')

console.log('Usage: npm run sse <orgId>')

const orgId = process.argv[2]
const headers = {
  headers: {
    'x-org-id': orgId
  }
}
const es = new EventSource('http://localhost:8080/provider/sse', headers)

es.on('event', (e) => {
  console.log('Received event data:', e.data)
  sleep.sleep(2)

  var event = JSON.parse(e.data)
  event.status = 'PROVIDER_ACCEPTED'

  var options = {
    hostname: 'localhost',
    port: 8080,
    path: '',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'x-org-id': orgId
    }
  }

  var postResponse = function(response) {
    console.log('POST status:', response.statusCode)
    sleep.sleep(2)
    event.status = 'PROVIDER_RESPONSE'
    event.data = [{
      key1: 'value1'
    }, {
      key2: 'value2'
    }]

    options.path = '/provider/response'
    var responseRequest = http.request(options, (response) => {
      console.log('POST response:', response.statusCode)
    })

    console.log('Sending response request')
    responseRequest.write(JSON.stringify(event))
    responseRequest.end()
  }

  options.path = '/provider/status'
  var statusRequest = http.request(options, postResponse)
  console.log('Sending status request')
  statusRequest.write(JSON.stringify(event))
  statusRequest.end()
})
