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
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.List;

public class Mining {

    static Web3j        web3j;
    static Credentials  credentials;
    static String       contractAddress;
    static final String signMessage = "doneTask(address signer,uint256 taskId,uint256 points)";

    private Mining() {}

    public static void Init (String rpcURL, String privateKey, String _contractAddress) {
        web3j = Web3j.build(new HttpService(rpcURL));
        credentials = Credentials.create(privateKey);
        contractAddress = _contractAddress;
    }

//    public static String generateMnemonic() {
//        byte[] initialEntropy = new byte[16];
//        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
//        return MnemonicUtils.generateMnemonic(initialEntropy);
//    }

    public static String getAddress () {
        return credentials.getAddress();
    }

    public static String generatePrivateKey() {
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

    public static Boolean isExist (BigInteger taskId) {
        Boolean result = false;
        try {
            Function function = new Function(
                    "isExist",
                    Arrays.asList(new Uint256(taskId)),
                    Arrays.asList(new TypeReference<Bool>(){}));
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            Bool preValue = (Bool)results.get(0);
            result = preValue.getValue();
        } finally {
            return result;
        }
    }

    public static BigInteger getTaskPoints (BigInteger taskId) {
        BigInteger result = null;
        try {
            Function function = new Function(
                    "taskmap",
                    Arrays.asList(new Uint256(taskId)),
                    Arrays.asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall response = web3j.ethCall(
                    Transaction.createEthCallTransaction(null, contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST).send();

            List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
            Uint256 points = (Uint256)results.get(2);
            result = points.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public static JSONObject signDoneTask (BigInteger taskId, BigInteger points) {
        JSONObject result = new JSONObject();
        byte[] data =  SolidityPackEncoder.soliditySHA3(Arrays.asList(
                new Utf8String(signMessage),
                new Address(credentials.getAddress()),
                new Uint256(taskId),
                new Uint256(points)));
        Sign.SignatureData sgindata = Sign.signPrefixedMessage(data, credentials.getEcKeyPair());
        String signature = Numeric.toHexString(sgindata.getR()) + Numeric.toHexString(sgindata.getS()).substring(2) + Numeric.toHexString(sgindata.getV()).substring(2);
        result.put("message", signMessage);
        result.put("signer", credentials.getAddress());
        result.put("taskId", taskId);
        result.put("points", points);
        result.put("signature", signature);
        return result;
    }

    public static void release () {
        web3j.shutdown();
    }


    private static BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();
        return ethGetTransactionCount.getTransactionCount();
    }
}
