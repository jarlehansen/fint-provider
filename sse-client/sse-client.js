const EventSource = require('eventsource')
const headers = {headers: {'x-org-id': 'hfk.no'}}
const es = new EventSource('http://localhost:8080/provider/sse', headers)

es.on('test', (e) => {
  console.log(e.data);
});
