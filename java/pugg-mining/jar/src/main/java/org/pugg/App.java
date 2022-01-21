package org.pugg;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class App {

    static String rpcURL = "HTTP://127.0.0.1:7545"; //bsc test rpc 节点地址
    static String contractAddress = "0x9247bcC813e540B4c50e73ba922978Edd14beFAE";  //bsc test 合约地址

    public static void main(String[] args) {
        Mining.getSingleton().Initialize(rpcURL, "441ddb45c0fb161e00d66271fed37632e996980b801d7e3188c4a0f8ed7314a3", contractAddress);

        JSONObject obj = Mining.getSingleton().ClientGetMiningSign(0);
        if (obj != null) {
            obj.put("signature", "441ddb45c0fb161e00d66271fed37632e996980b801d7e3188c4a0f8ed7314a3");
            ArrayList<String> data = new ArrayList<String>();
            data.add(obj.toString());
            Mining.getSingleton().ServerBatchUploadMiningResult(data);
        }
    }
}
