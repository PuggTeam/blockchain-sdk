const dotenv = require('dotenv');
dotenv.config();
const w = require('../utils/wallet');
const Transaction =  require('../utils/transfer');
const TokenERC20 =  require('../utils/tokenERC20');
const IERC20 = require('./abi/IERC20.json');


const RPC_URL = process.env.RPC_URL;
const MNEMONIC = process.env.MNEMONIC;

console.log(`Your RPC_URL is ${RPC_URL}`);
console.log(`Your MNEMONIC is ${MNEMONIC}`);


w.importAccountFromMnemonic(MNEMONIC);
console.log(`Your PrivateKey is ${w.getSelectedAccountPrivateKey()}`);

var transaction = new Transaction(RPC_URL, w.getSelectedAccountPrivateKey());

const test_send = async () => {
    const tx = await transaction.send("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15",0.01)
    console.log(tx);
}

const test_getBalance = async () => {
    const balance = await transaction.getBalance(transaction.getAddress());
    console.log(balance);
}



test_send()
test_getBalance()


var tokenERC20 = new TokenERC20(RPC_URL, w.getSelectedAccountPrivateKey(), IERC20.abi, "0x2f01C1d3a504d1bEA1a2C19E7B49D862b8D2cA5a");

const test_BalanceOf = async (address) => {
    const balance = await tokenERC20.balanceOf(address);
    console.log(balance);
}


const test_transfer = async () => {
    const result = await tokenERC20.transfer("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15", 1);
    console.log(result);
}

test_BalanceOf("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15")
test_transfer()