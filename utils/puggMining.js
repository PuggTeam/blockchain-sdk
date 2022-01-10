var Web3 = require('web3');

class PuggMining {
  constructor(rpcURL, privateKey, contract_abi, contract_address) {
    this.rpcURL = rpcURL;
    this.web3 = new Web3(this.rpcURL);
    this.privateKey = privateKey;
    this.account = this.web3.eth.accounts.privateKeyToAccount(this.privateKey);
    this.contract = new this.web3.eth.Contract(contract_abi, contract_address)
  }

  /* HELPER FUNCTIONS */
  signDoneTask (taskId) {
    let result = this.account.sign(this.web3.utils.soliditySha3("doneTask(uint256)", taskId.toString()));
    result.signer = this.account.address;
    result.taskId = taskId
    return result
  }

  async doneTasks (Ids, addrs, signatures) {
    let Ids_bn = []
    for (let i = 0; i < Ids.length; i++) {
        Ids_bn.push(this.web3.utils.toBN(Ids[i]));
    }

    let _gas = await this.contract.methods.doneTasks(Ids_bn, addrs, signatures).estimateGas({from:this.account.address});
    if (typeof _gas != 'undefined') {
        let reuslt = await this.contract.methods.doneTasks(Ids_bn, addrs, signatures).send({
            from:this.account.address,
            gas:_gas
        })
        return reuslt
    }
  }

  async withdrawPoints () {
    let _gas = await this.contract.methods.withdrawPoints().estimateGas({from:this.account.address});
    if (typeof _gas != 'undefined') {
        let reuslt = await this.contract.methods.withdrawPoints().send({
            from:this.account.address,
            gas:_gas
        })
        return reuslt
    }
  }
}

module.exports = PuggMining