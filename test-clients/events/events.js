const uuid = require('uuid')

module.exports = {
  createMessage: (orgId) => {
    return {
        corrId: uuid(),
        action: 'GET_ALL_EMPLOYEES',
        status: 'DOWNSTREAM_QUEUE',
        time: new Date().getTime(),
        orgId,
        source: "fk",
        client: "vfs",
        message: null,
        data: []
    }
  }
}
