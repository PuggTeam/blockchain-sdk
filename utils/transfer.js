const w = require('./utils/wallet');

export default class Transaction {
  constructor(config, networks) {
    this.config = config;
    this.networks = networks;
    this.privateKey = null;
  }

  /* EXTERNAL FUNCTIONS */

  async send(networkName, txData, privateKey = null) {
    const { promiEvent } = await this.sendTx(
      this.networks.getWeb3(networkName),
      this.addDefaults(txData, networkName),
      privateKey ? privateKey : this.privateKey
    );
    return { promiEvent };
  }

  /* HELPER FUNCTIONS */

  addDefaults(txData, networkName) {
    return {
      ...txData,
      gasPrice:
        txData.gasPrice !== undefined
          ? txData.gasPrice
          : this.networks.getConfig(networkName).gasPrice
    };
  }

  async estimateGas(web3, txData) {
    return Math.round(
      (await web3.eth.estimateGas(txData)) * this.config.gasEstimationMargin
    );
  }

  async sendTx(web3, txData, privateKey = null) {
    txData.gas = await this.estimateGas(web3, txData);

    if (
      privateKey === null ||
      txData.from === null ||
      web3.eth.accounts
        .privateKeyToAccount(privateKey)
        .address.toLowerCase() !== txData.from.toLowerCase()
    ) {
      const promiEvent = web3.eth.sendTransaction(txData);
      return { promiEvent };
    }

    const account = web3.eth.accounts.privateKeyToAccount(privateKey);
    if (txData.from.toLowerCase() !== account.address.toLowerCase()) {
      throw new Error(
        'Private key does not match from address in transaction object.'
      );
    }

    const signedTx = await account.signTransaction(txData);
    const promiEvent = web3.eth.sendSignedTransaction(signedTx.rawTransaction);
    return { promiEvent };
  }
}

module.exports = Wallet;
