package org.pugg;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class App {

    static String rpcURL = "https://data-seed-prebsc-1-s1.binance.org:8545/"; //bsc test rpc 节点地址
    static String contractAddress = "0x5d882B62cC6C8C4d5a6C0F48e528cd74008AB93E";  //bsc test 合约地址

    public static void main(String[] args) {
        Mining.getSingleton().Initialize(rpcURL, "441ddb45c0fb161e00d66271fed37632e996980b801d7e3188c4a0f8ed7314a3", contractAddress);

        JSONObject obj = Mining.getSingleton().ClientGetMiningSign(0);
        if (obj != null) {
            ArrayList<String> data = new ArrayList<String>();
            data.add(obj.toString());
            Mining.getSingleton().ServerBatchUploadMiningResult(data);
        }
    }
}
