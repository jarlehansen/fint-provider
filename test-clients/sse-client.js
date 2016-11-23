const EventSource = require('eventsource')

console.log('Usage: npm run sse <orgId>')

const headers = {headers: {'x-org-id': process.argv[2]}}
const es = new EventSource('http://localhost:8080/provider/sse', headers)

es.on('event', (e) => {
  console.log(e.data)
})
