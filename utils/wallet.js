const { generateMnemonic, EthHdWallet } = require('eth-hd-wallet');

class Wallet {
  /* EXTERNAL FUNCTIONS */

  importAccountFromMnemonic(mnemonic) {
    if (mnemonic === '') {
      throw new Error('Mnemonic can not be an empty string.');
    }

    this.wallet = EthHdWallet.fromMnemonic(mnemonic.trim());
  }

  generateMnemonic() {
    return generateMnemonic();
  }

  getSelectedAccountPrivateKey() {
    /* eslint-disable-next-line no-underscore-dangle */
    const [address] = this.wallet.generateAddresses(1);
    return `0x${this.wallet.getPrivateKey(address).toString('hex')}`;
  }
}
module.exports = new Wallet();
// const wall = new Wallet();
// wall.importAccountFromMnemonic(
//   'tunnel penalty legal property alpha agree lyrics village canal biology cross select'
// );
// console.log(wall.wallet.getPrivateKey());
// console.log(wall.getSelectedAccountPrivateKey());
