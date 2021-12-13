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
    if (this.selectedWallet) {
      /* eslint-disable-next-line no-underscore-dangle */
      return `0x${this.selectedWallet._children[0].wallet
        .getPrivateKey()
        .toString('hex')}`;
    }
    return null;
  }
}
module.exports = Wallet;
// const wall = new Wallet();
// wall.importAccountFromMnemonic(
//   'tunnel penalty legal property alpha agree lyrics village canal biology cross select'
// );
// console.log(wall.wallet);
// console.log(wall.generateMnemonic());
