var Web3 = require('web3');

class TokenERC20 {
  constructor(rpcURL, privateKey, tokenAbi, tokenAddress) {
    this.rpcURL = rpcURL;
    this.web3 = new Web3(this.rpcURL);
    this.privateKey = privateKey;
    this.account = this.web3.eth.accounts.privateKeyToAccount(this.privateKey);
    this.token = new this.web3.eth.Contract(tokenAbi, tokenAddress)
  }

  /* HELPER FUNCTIONS */
  async balanceOf(address) {
    let result = await this.token.methods.balanceOf(address).call();
    return result
  }

  async transfer(recipient, amount) {
    
    let _gas = await this.token.methods.transfer(recipient, this.web3.utils.toWei(amount.toString(), 'ether')).estimateGas({from:this.account.address});

    let reuslt = await this.token.methods.transfer(recipient, this.web3.utils.toWei(amount.toString(), 'ether')).send({
        from:this.account.address,
        gas:_gas
    })
    
    return reuslt
  }
}

module.exports = TokenERC20