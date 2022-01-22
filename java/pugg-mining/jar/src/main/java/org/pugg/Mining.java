package org.pugg;

import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.Response.Error;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

public class Mining implements PuggService{

    private Web3j           web3j;
    private Credentials     credentials;
    private String          contractAddress;
    private boolean         isInit = false;
    private final           long sleepDuration = 2000;
    private final           int attempts = 60;
    static final String     signMessage = "doneTask(address signer,uint256 taskId,uint256 points)";
    private volatile static Mining singleton;  //1:volatile修饰
    

    private Mining() {}
    public static Mining getSingleton() {
        if (singleton == null) {  //2:减少不要同步，优化性能
            synchronized (Mining.class) {  // 3：同步，线程安全
                if (singleton == null) {
                    singleton = new Mining();  //4：创建singleton 对象
                }
            }
        }
        return singleton;
    }

//    public static String generateMnemonic() {
//        byte[] initialEntropy = new byte[16];
//        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
//        return MnemonicUtils.generateMnemonic(initialEntropy);
//    }

    public String getAddress () {
        return credentials.getAddress();
    }

    public String generatePrivateKey() {
        String privateKey = null;
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair(SecureRandomUtils.secureRandom());
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();
            privateKey = privateKeyInDec.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } finally {
            return privateKey;
        }
    }

    private BigInteger _getTaskPoints (BigInteger taskId) throws IOException {
        if (!isInit) { return null; }
        BigInteger result = null;
        Function function = new Function(
                "taskmap",
                Arrays.asList(new Uint256(taskId)),
                Arrays.asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST).send();

        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if (results.size() == 4) {
            Uint8 active = (Uint8)results.get(3);
            if (active.getValue().compareTo(BigInteger.valueOf(1)) == 0) {
                Uint256 points = (Uint256)results.get(2);
                result = points.getValue();
            }
        }
        return result;
    }

    private BigInteger _getTransactionGasLimit(Transaction transaction) {
        try {
            EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
            if (ethEstimateGas.hasError()){
                throw new RuntimeException(ethEstimateGas.getError().getMessage());
            }
            return ethEstimateGas.getAmountUsed();
        } catch (IOException e) {
            throw new RuntimeException("net error");
        }


    }

    private BigInteger _getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 每次用户完成了一次踩单车，会调用该接口获取签名后的表单
     *
     * @param miningType 接口参数预留，该参数暂时不用例会
     * @return 结果表单
     */
    @Override
    public JSONObject ClientGetMiningSign(int miningType) {
        if (!isInit) { return null; }
        JSONObject result = null;
        try {
            BigInteger point = _getTaskPoints(BigInteger.valueOf(1000));
            if (point != null) {
                BigInteger taskId = BigInteger.valueOf(1000);
                result = new JSONObject();
                byte[] data =  SolidityPackEncoder.soliditySHA3(Arrays.asList(
                        new Utf8String(signMessage),
                        new Address(credentials.getAddress()),
                        new Uint256(taskId),
                        new Uint256(point)));
                Sign.SignatureData sgindata = Sign.signPrefixedMessage(data, credentials.getEcKeyPair());
                String signature = Numeric.toHexString(sgindata.getR()) + Numeric.toHexString(sgindata.getS()).substring(2) + Numeric.toHexString(sgindata.getV()).substring(2);
                result.put("code", "OK");
                result.put("signer", credentials.getAddress());
                result.put("taskId", BigInteger.valueOf(1000));
                result.put("point", point);
                result.put("signature", signature.substring(2));
                result.put("timestamp", System.currentTimeMillis());
            }
            else {
                result.put("code", "ERROR");
                result.put("error", "task does not exist or is not active");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = new JSONObject();
            result.put("code", "ERROR");
            result.put("error", e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 获取今天发放的用于 所有用户 踩单车的代币数量，这个函数会在服务器端一天中某一固定时刻调用，获取一次。
     *
     * @return 结果表单
     */
    @Override
    public JSONObject ServerGetCoinCountToday() {
        if (!isInit) { return null; }
        JSONObject result = null;
        try {
            Function function = new Function(
                    "getCoinCountPerDay",
                    Collections.emptyList(),
                    Arrays.asList(new TypeReference<Uint256>() {}));
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST).send();

            List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            if (results.size() == 1) {
                Uint256 points = (Uint256)results.get(0);
                result = new JSONObject();
                result.put("code", "OK");
                result.put("points", points.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new JSONObject();
            result.put("code", "ERROR");
            result.put("error", e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 用于服务器批量上传某一时段所有用户挖矿情况
     *
     * @param batchData 数组中 String 为 GetMiningSign 返回表单数据序列化后结果
     * @return 结果表单
     */
    @Override
    public JSONObject ServerBatchUploadMiningResult(ArrayList<String> batchData) {
        if (!isInit) { return null; }
        JSONObject result = null;
        try {
            List<Address> singers = new ArrayList<Address>();
            List<Uint256> taskIds = new ArrayList<Uint256>();
            List<Uint256> points = new ArrayList<Uint256>();
            List<Utf8String> signatures = new ArrayList<Utf8String>();
            for (int i = 0; i < batchData.size(); i++) {
                JSONObject obj = new JSONObject(batchData.get(i));
                if (obj.has("signer") && obj.has("taskId") && obj.has("point") && obj.has("signature")) {
                    singers.add(new Address(obj.getString("signer")));
                    taskIds.add(new Uint256(obj.getBigInteger("taskId")));
                    points.add(new Uint256(obj.getBigInteger("point")));
                    signatures.add(new Utf8String(obj.getString("signature")));
                }
            }
            DynamicArray singers_ = new DynamicArray(Address.class, singers);
            DynamicArray taskIds_ = new DynamicArray(Uint256.class, taskIds);
            DynamicArray points_ = new DynamicArray(Uint256.class, points);
            DynamicArray signatures_ = new DynamicArray(Utf8String.class, signatures);


            Function function = new Function(
                    "doneTasks",
                    Arrays.asList(singers_, taskIds_, points_, signatures_),
                    Collections.emptyList());

            String encodedFunction = FunctionEncoder.encode(function);
            EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            BigInteger nonce = _getNonce(credentials.getAddress());
            Transaction transaction = Transaction.createFunctionCallTransaction(credentials.getAddress(), nonce, ethGasPrice.getGasPrice(), null, contractAddress, encodedFunction);
            BigInteger gasLimit = _getTransactionGasLimit(transaction);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, ethGasPrice.getGasPrice(), gasLimit.multiply(BigInteger.valueOf(2)), contractAddress, encodedFunction);
            String signData = Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials));
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signData).send();
            Error error = ethSendTransaction.getError();
            if (error == null) {
                String hash = ethSendTransaction.getTransactionHash();
                if (hash != null) {
                    result = new JSONObject();
                    result.put("code", "OK");
                    result.put("txhash", hash);
                }
            }
            else {
                result = new JSONObject();
                result.put("code", "ERROR");
                result.put("error", error.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new JSONObject();
            result.put("code", "ERROR");
            result.put("error", e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 用于初始化SDK，相关初始化操作可放在这里
     *
     * @param rpcURL
     * @param privateKey
     * @param _contractAddress
     */
    @Override
    public void Initialize(String rpcURL, String privateKey, String _contractAddress) {
        try {
            if (!isInit) {
                web3j = Web3j.build(new HttpService(rpcURL));
                EthBlockNumber respone = web3j.ethBlockNumber().send();
                BigInteger block = respone.getBlockNumber();
                if (block != null) {
                    credentials = Credentials.create(privateKey);
                    contractAddress = _contractAddress;
                    isInit = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于等待交易哈希的返回结果
     *
     * @param hash
     */
    @Override
    public JSONObject WaitForTransactionReceipt(String hash) {
        if (!isInit) { return null; }
        JSONObject result = null;
        try {
            PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(web3j, this.sleepDuration, this.attempts);
            TransactionReceipt rec = processor.waitForTransactionReceipt(hash);
            if (rec != null) {
                result = new JSONObject();
                result.put("code", "OK");
                result.put("txhash", hash);
                result.put("status", rec.getStatus());
            }
        } catch (IOException | TransactionException e) {
            e.printStackTrace();
            result = new JSONObject();
            result.put("code", "ERROR");
            result.put("error", e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 查看SDK是否初始化, 不会有参数传入
     *
     * @return 是否初始化
     */
    @Override
    public boolean IsInitialize() {
        return isInit;
    }

    /**
     * 接口所在程序结束运行的时候会调用, 不会有参数传入
     */
    @Override
    public void Shutdown() {
        if (isInit) {
            web3j.shutdown();
            isInit=false;
        }
    }
}
