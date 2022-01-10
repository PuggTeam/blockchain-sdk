const dotenv = require('dotenv');
dotenv.config();
const w = require('../utils/wallet');
var Web3 = require('web3');
const Transaction =  require('../utils/transfer');
const TokenERC20 =  require('../utils/tokenERC20');
const PuggMining =  require('../utils/PuggMining');
const IERC20 = require('../abi/IERC20.json');
const IPuggMining = require('../abi/PuggMining.json');


const RPC_URL = process.env.RPC_URL;
const MNEMONIC = process.env.MNEMONIC;

console.log(`Your RPC_URL is ${RPC_URL}`);
console.log(`Your MNEMONIC is ${MNEMONIC}`);


w.importAccountFromMnemonic(MNEMONIC);
console.log(`Your PrivateKey is ${w.getSelectedAccountPrivateKey()}`);

//var transaction = new Transaction(RPC_URL, w.getSelectedAccountPrivateKey());

// const test_send = async () => {
//     const tx = await transaction.send("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15",0.01)
//     console.log(tx);
// }

// const test_getBalance = async () => {
//     const balance = await transaction.getBalance(transaction.getAddress());
//     console.log(balance);
// }



// test_send()
// test_getBalance()


// var tokenERC20 = new TokenERC20(RPC_URL, w.getSelectedAccountPrivateKey(), IERC20.abi, "0x2f01C1d3a504d1bEA1a2C19E7B49D862b8D2cA5a");

// const test_BalanceOf = async (address) => {
//     const balance = await tokenERC20.balanceOf(address);
//     console.log(balance);
// }


// const test_transfer = async () => {
//     const result = await tokenERC20.transfer("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15", 1);
//     console.log(result);
// }

// test_BalanceOf("0xd0c17D06dBC69c43D752EAeeFd1b3EDCA5a21d15")
// test_transfer()


var puggMining = new PuggMining(RPC_URL, w.getSelectedAccountPrivateKey(), IPuggMining.abi, "0x16AC14FDEcB713026A5F1d83da87602048FACd10");

const test_signDoneTask = () => {
    const result = puggMining.signDoneTask(1);
    console.log(result);
}

test_signDoneTask()


const test_doneTasks = async () => {
    const result = await puggMining.doneTasks([1], ['0xCacD51d5422D1b916F6f1217fa0c12bf6a69938d'], ['0xadade125259d63304b76c646190cdfb5f9c8076c5c4cae0488af72eb8fe0a6235382dde37c411d9e158cb231a45e2e4a85d188e77fb3361dc7efb05a3edc70671b']);
    console.log(result);
}

test_doneTasks()