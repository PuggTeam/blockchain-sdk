var Web3 = require('web3');

class Transaction {
  constructor(rpcURL, privateKey) {
    this.rpcURL = rpcURL;
    this.web3 = new Web3(this.rpcURL);
    this.privateKey = privateKey;
    this.account = this.web3.eth.accounts.privateKeyToAccount(this.privateKey);
    this.tokens = {}
  }

  addTokenContract(jsonInterface, address) {
    let token = new web3.eth.Contract(jsonInterface, address)
    this.tokens[address] = token
  }

  getAddress() {
    return this.account.address
  }

  /* HELPER FUNCTIONS */
  async _estimateGasLimit(txData) {
    return Math.round((await this.web3.eth.estimateGas(txData)) * 1.5);
  }

  async send(_to, value) {
    let txData = {
      from: this.account.address,  // accounts[0]
      to: _to,    // accounts[1]
      // Please pass numbers as strings or BN objects to avoid precision errors.
      value: this.web3.utils.toHex(this.web3.utils.toWei(value.toString(), 'ether'))
      // data: '0x'
    }
    txData.gasPrice = await this.web3.eth.getGasPrice();
    txData.gasLimit = await this._estimateGasLimit(txData);

    const signedTx = await this.account.signTransaction(txData);
    const result = await this.web3.eth.sendSignedTransaction(signedTx.rawTransaction);
    return result
  }

  async getBalance(address) {
    let balance = await this.web3.eth.getBalance(address);
    return balance
  }
}

module.exports = Transaction