using Nethereum.ABI;
using Nethereum.ABI.FunctionEncoding.Attributes;
using Nethereum.Contracts;
using Nethereum.Hex.HexTypes;
using Nethereum.JsonRpc.UnityClient;
using Nethereum.RPC.Eth.DTOs;
using Nethereum.Signer;
using Nethereum.Util;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Numerics;
using UnityEngine;


public class PuggMining
{
    // Start is called before the first frame update
    private const string                    signMessage = "doneTask(address signer,uint256 taskId,uint256 points)";
    private const string                    ABI = @"[{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""uint256"",""name"":""Id"",""type"":""uint256""},{""indexed"":false,""internalType"":""string"",""name"":""name"",""type"":""string""},{""indexed"":false,""internalType"":""uint256"",""name"":""points"",""type"":""uint256""}],""name"":""AddTask"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""address"",""name"":""operator"",""type"":""address""},{""indexed"":false,""internalType"":""bool"",""name"":""hasApproval"",""type"":""bool""}],""name"":""DefaultApproval"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""uint256"",""name"":""Id"",""type"":""uint256""}],""name"":""DelTask"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""address"",""name"":""operator"",""type"":""address""},{""indexed"":false,""internalType"":""address[]"",""name"":""signers"",""type"":""address[]""},{""indexed"":false,""internalType"":""uint256[]"",""name"":""taskIds"",""type"":""uint256[]""},{""indexed"":false,""internalType"":""uint256[]"",""name"":""points"",""type"":""uint256[]""}],""name"":""DoneTasks"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":true,""internalType"":""address"",""name"":""previousOwner"",""type"":""address""},{""indexed"":true,""internalType"":""address"",""name"":""newOwner"",""type"":""address""}],""name"":""OwnershipTransferred"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""address"",""name"":""account"",""type"":""address""}],""name"":""Paused"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""address"",""name"":""account"",""type"":""address""}],""name"":""Unpaused"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""uint256"",""name"":""amount"",""type"":""uint256""}],""name"":""WithdrawBaseToken"",""type"":""event""},{""anonymous"":false,""inputs"":[{""indexed"":false,""internalType"":""uint256"",""name"":""amount"",""type"":""uint256""}],""name"":""WithdrawPoints"",""type"":""event""},{""inputs"":[{""internalType"":""address"",""name"":""_token"",""type"":""address""}],""name"":""__initialize"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":""taskId"",""type"":""uint256""},{""internalType"":""string"",""name"":""name"",""type"":""string""},{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""}],""name"":""addTask"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":"""",""type"":""uint256""}],""name"":""allIds"",""outputs"":[{""internalType"":""uint256"",""name"":"""",""type"":""uint256""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":"""",""type"":""address""}],""name"":""allcontributor"",""outputs"":[{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""},{""internalType"":""uint256"",""name"":""balance"",""type"":""uint256""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":""taskId"",""type"":""uint256""}],""name"":""delTask"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""address[]"",""name"":""signers"",""type"":""address[]""},{""internalType"":""uint256[]"",""name"":""taskIds"",""type"":""uint256[]""},{""internalType"":""uint256[]"",""name"":""points"",""type"":""uint256[]""},{""internalType"":""string[]"",""name"":""signatures"",""type"":""string[]""}],""name"":""doneTasks"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[],""name"":""getAllIds"",""outputs"":[{""internalType"":""uint256[]"",""name"":"""",""type"":""uint256[]""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[],""name"":""getCoinCountPerDay"",""outputs"":[{""internalType"":""uint256"",""name"":"""",""type"":""uint256""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""bytes32"",""name"":""_messageHash"",""type"":""bytes32""}],""name"":""getEthSignedMessageHash"",""outputs"":[{""internalType"":""bytes32"",""name"":"""",""type"":""bytes32""}],""stateMutability"":""pure"",""type"":""function""},{""inputs"":[{""internalType"":""string"",""name"":""message"",""type"":""string""},{""internalType"":""address"",""name"":""signer"",""type"":""address""},{""internalType"":""uint256"",""name"":""taskId"",""type"":""uint256""},{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""}],""name"":""getMessageHash"",""outputs"":[{""internalType"":""bytes32"",""name"":"""",""type"":""bytes32""}],""stateMutability"":""pure"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":""operator"",""type"":""address""}],""name"":""isApprovedForAll"",""outputs"":[{""internalType"":""bool"",""name"":"""",""type"":""bool""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":""taskId"",""type"":""uint256""}],""name"":""isExist"",""outputs"":[{""internalType"":""bool"",""name"":"""",""type"":""bool""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[],""name"":""owner"",""outputs"":[{""internalType"":""address"",""name"":"""",""type"":""address""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[],""name"":""paused"",""outputs"":[{""internalType"":""bool"",""name"":"""",""type"":""bool""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[],""name"":""pointsPerDay"",""outputs"":[{""internalType"":""uint256"",""name"":"""",""type"":""uint256""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""bytes32"",""name"":""_ethSignedMessageHash"",""type"":""bytes32""},{""internalType"":""bytes"",""name"":""_signature"",""type"":""bytes""}],""name"":""recoverSigner"",""outputs"":[{""internalType"":""address"",""name"":"""",""type"":""address""}],""stateMutability"":""pure"",""type"":""function""},{""inputs"":[],""name"":""renounceOwnership"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""}],""name"":""setCoinCountPerDay"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":""operator"",""type"":""address""},{""internalType"":""bool"",""name"":""hasApproval"",""type"":""bool""}],""name"":""setDefaultApproval"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":""_token"",""type"":""address""}],""name"":""setToken"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""bytes"",""name"":""sig"",""type"":""bytes""}],""name"":""splitSignature"",""outputs"":[{""internalType"":""bytes32"",""name"":""r"",""type"":""bytes32""},{""internalType"":""bytes32"",""name"":""s"",""type"":""bytes32""},{""internalType"":""uint8"",""name"":""v"",""type"":""uint8""}],""stateMutability"":""pure"",""type"":""function""},{""inputs"":[{""internalType"":""uint256"",""name"":"""",""type"":""uint256""}],""name"":""taskmap"",""outputs"":[{""internalType"":""uint256"",""name"":""Id"",""type"":""uint256""},{""internalType"":""string"",""name"":""name"",""type"":""string""},{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""},{""internalType"":""uint8"",""name"":""active"",""type"":""uint8""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[],""name"":""tokenAddr"",""outputs"":[{""internalType"":""address"",""name"":"""",""type"":""address""}],""stateMutability"":""view"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":""newOwner"",""type"":""address""}],""name"":""transferOwnership"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[{""internalType"":""address"",""name"":""signer"",""type"":""address""},{""internalType"":""uint256"",""name"":""taskId"",""type"":""uint256""},{""internalType"":""uint256"",""name"":""points"",""type"":""uint256""},{""internalType"":""string"",""name"":""signature"",""type"":""string""}],""name"":""verify"",""outputs"":[{""internalType"":""bool"",""name"":"""",""type"":""bool""}],""stateMutability"":""pure"",""type"":""function""},{""inputs"":[],""name"":""withdrawPoints"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""},{""inputs"":[],""name"":""withdrawToken"",""outputs"":[],""stateMutability"":""nonpayable"",""type"":""function""}]";
    private static string                   rpcURL;
    private static string                   privateKey;
    private static string                   contractAddress;
    private static Contract                 contract;
    private static AddressUtil              addressUtil;
    private static ABIEncode                abiEncode;
    private static EthereumMessageSigner    ethereumMessageSigner;
    private const float                     sleepDuration = 2f;
    private const int                       attempts = 60;
    private static bool                     isInit = false;

    [FunctionOutput]
    private class TaskDTO
    {
        [Parameter("uint256", "Id", 1)] public BigInteger Id { get; set; }
        [Parameter("string", "name", 2)] public string name { get; set; }
        [Parameter("uint256", "points", 3)] public BigInteger points { get; set; }
        [Parameter("uint8", "active", 4)] public int active { get; set; }
    }


    private static long _getTime()
    {
        TimeSpan ts = DateTime.Now - new DateTime(1970, 1, 1, 0, 0, 0, 0);
        return Convert.ToInt64(ts.TotalMilliseconds);
    }

    /*  对外接口   */

    public static string getPublicAddress()
    {
        if (isInit)
        {
            var address = EthECKey.GetPublicAddress(privateKey);
            return address;
        }
        else
        {
            throw new InvalidOperationException("PuggMining：not yet initialized");
        }
    }

    public static bool IsInitialized()
    {
        return isInit;
    }

    /*
     * 初始化函数 
     */
    public static IEnumerator Initialize(string _rpcURL, string _privateKey, string _contractAddress, Action<string, bool> callback)
    {
        if (!isInit)
        {
            rpcURL = _rpcURL;
            privateKey = _privateKey;
            contractAddress = _contractAddress;

            var getBalanceRequest = new EthBlockNumberUnityRequest(rpcURL);
            yield return getBalanceRequest.SendRequest();
            if (getBalanceRequest.Exception == null)
            {
                contract = new Contract(null, ABI, contractAddress);
                addressUtil = new AddressUtil();
                abiEncode = new ABIEncode();
                ethereumMessageSigner = new EthereumMessageSigner();
                isInit = true;
                callback("ok",isInit);
            }
            else
            {
                callback("error", false);
                throw new InvalidOperationException("PuggMining：Initialize failed");
            }

        }
        else
        {
            callback("error", false);
            throw new InvalidOperationException("PuggMining：has been initialized!");
        }
    }

    /**
     * 每次用户完成了一次踩单车，会调用该接口获取签名后的表单
     *
     * @param miningType 接口参数预留，该参数暂时不用例会
     * @return 结果表单
     */
    public static IEnumerator ClientGetMiningSign(int miningType, Action<string, string, BigInteger, BigInteger, string, long> callback)
    {
        
        if (!isInit) { callback("error", null, BigInteger.Zero, BigInteger.Zero, null, 0); throw new InvalidOperationException("PuggMining：not initialized"); }
        var taskmap = contract.GetFunction("taskmap");
        var callRequest = new EthCallUnityRequest(rpcURL);
        var taskId = new BigInteger(1000);
        yield return callRequest.SendRequest(taskmap.CreateCallInput(taskId), BlockParameter.CreateLatest());
        if (callRequest.Exception == null)
        {
            TaskDTO taskdto = taskmap.DecodeDTOTypeOutput(new TaskDTO(), callRequest.Result);
            BigInteger points = taskdto.points;
            string signer = addressUtil.ConvertToChecksumAddress(getPublicAddress());
            var abiEncode = new ABIEncode();
            var result = abiEncode.GetSha3ABIEncodedPacked(
                new ABIValue("string", signMessage),
                new ABIValue("address", signer),
                new ABIValue("uint256", taskId),
                new ABIValue("uint256", points));
            var signature = ethereumMessageSigner.Sign(result, privateKey).Substring(2);
            callback("ok", signer, taskId, points, signature, _getTime());
        }
        else
        {
            callback("error", null, BigInteger.Zero, BigInteger.Zero, null, 0);
            throw new InvalidOperationException("PuggMining：getTaskPoints failed!");
        }
    }


    /**
     * 获取今天发放的用于 所有用户 踩单车的代币数量，这个函数会在服务器端一天中某一固定时刻调用，获取一次。
     *
     * @return 结果表单
     */
    public static IEnumerator ServerGetCoinCountToday(Action<string, BigInteger> callback)
    {
        if (!isInit) { callback("error", BigInteger.Zero); throw new InvalidOperationException("PuggMining：not initialized"); }
        var callRequest = new EthCallUnityRequest(rpcURL);
        var getCoinCountPerDay = contract.GetFunction("getCoinCountPerDay");
        yield return callRequest.SendRequest(getCoinCountPerDay.CreateCallInput(), BlockParameter.CreateLatest());
        if (callRequest.Exception == null)
        {
            BigInteger value = getCoinCountPerDay.DecodeTypeOutput<BigInteger>(callRequest.Result);
            callback("ok", value);
        }
        else
        {
            // if we had an error in the UnityRequest we just display the Exception error
            callback("error", BigInteger.Zero);
            throw new InvalidOperationException("Error submitting ServerGetCoinCountToday tx: " + callRequest.Exception.Message);
        }
    }

    /**
     * 用于服务器批量上传某一时段所有用户挖矿情况
     *
     * @param batchData 数组中 String 为 GetMiningSign 返回表单数据序列化后结果
     * @return 结果表单
     */
    public static IEnumerator ServerBatchUploadMiningResult(List<string> signers, List<BigInteger> taskIds, List<BigInteger> points, List<string> signatures, Action<string, string> callback)
    {
        if (!isInit) { callback("error", null); throw new InvalidOperationException("PuggMining：not initialized"); }
        var _ethGasPriceUnityRequest = new EthGasPriceUnityRequest(rpcURL);
        var _ethEstimateGasUnityRequest = new EthEstimateGasUnityRequest(rpcURL);
        var doneTasks = contract.GetFunction("doneTasks");
        var transactionInput =  doneTasks.CreateTransactionInput(getPublicAddress(), signers, taskIds, points, signatures);
        yield return _ethGasPriceUnityRequest.SendRequest();
        yield return _ethEstimateGasUnityRequest.SendRequest(transactionInput);
        if (_ethGasPriceUnityRequest.Exception != null)
        {
            callback("error", null);
            throw new InvalidOperationException("Error submitting ServerBatchUploadMiningResult tx: " + _ethGasPriceUnityRequest.Exception.Message);
        }
        if (_ethEstimateGasUnityRequest.Exception != null)
        {
            callback("error", null);
            throw new InvalidOperationException("Error submitting ServerBatchUploadMiningResult tx: " + _ethEstimateGasUnityRequest.Exception.Message);
        }

        var gasLimit = _ethEstimateGasUnityRequest.Result.Value * 2;
        var gasPrice = _ethGasPriceUnityRequest.Result;
        transactionInput = doneTasks.CreateTransactionInput(getPublicAddress(), 
            new HexBigInteger(gasLimit),
            new HexBigInteger(gasPrice),
            new HexBigInteger(0), signers, taskIds, points, signatures);

        var transactionSignedRequest = new TransactionSignedUnityRequest(rpcURL, privateKey);
        yield return transactionSignedRequest.SignAndSendTransaction(transactionInput);
        if (transactionSignedRequest.Exception == null)
        {
            // If we don't have exceptions we just display the raw result and the
            // result decode it with our function (decodePings) from the service, congrats!
            callback("ok", transactionSignedRequest.Result);
        }
        else
        {
            // if we had an error in the UnityRequest we just display the Exception error
            callback("error", null);
            throw new InvalidOperationException("Error submitting ServerBatchUploadMiningResult tx: " + transactionSignedRequest.Exception.Message);
        }
    }

    /**
     * 通过区块链交易哈希 txhash 的到交易状态信息
     */
    public static IEnumerator WaitForTransactionReceipt(string txhash, Action<string, BigInteger> callback)
    {
        
        if (!isInit) { callback("error", BigInteger.Zero); throw new InvalidOperationException("PuggMining：not initialized"); }
        var _ethGetTransactionReceipt = new EthGetTransactionReceiptUnityRequest(rpcURL);
        TransactionReceipt receipt = null;
        int _attempts = attempts;
        Exception _exception = null;
        while (receipt == null && _attempts > 0)
        {
            yield return _ethGetTransactionReceipt.SendRequest(txhash);

            if (_ethGetTransactionReceipt.Exception == null)
            {
                receipt = _ethGetTransactionReceipt.Result;
                if (receipt != null) break;
            }
            else
            {
                _exception = _ethGetTransactionReceipt.Exception;
                break;
            }

            yield return new WaitForSeconds(sleepDuration);
            _attempts--;
        }

        if (_exception != null)
        {
            callback("error", BigInteger.Zero);
            throw new InvalidOperationException("Error submitting WaitForTransactionReceipt tx: " + _exception.Message);
        }
        else if (receipt == null)
        {
            callback("error", BigInteger.Zero);
            throw new InvalidOperationException("Error submitting WaitForTransactionReceipt tx: " + "WaitForTransactionReceipt failed for 120 seconds");
        }
        else
        {
            callback("ok", receipt.Status.Value);
        }
    }

    /**
     * 接口所在程序结束运行的时候会调用, 不会有参数传入
     */
    public static void Shutdown()
    {
        if (isInit)
        {
            contract = null;
            addressUtil = null;
            abiEncode = null;
            ethereumMessageSigner = null;
            isInit = false;
        }
    }
}
