const uuid = require('uuid')
module.exports = function(orgId) {
  return {
    "corrId": uuid(),
    "verb": "GET",
    "status": "NEW",
    "time": 1479663045761,
    "orgId": orgId,
    "source": "fk",
    "client": "client",
    "message": null,
    "data": []
  }
}
