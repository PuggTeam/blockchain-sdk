package org.example;

import org.json.JSONObject;
import org.pugg.Mining;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    static String rpcURL = "https://data-seed-prebsc-1-s1.binance.org:8545/"; //bsc test rpc 节点地址
    static String contractAddress = "0xC0Ff7E54393145372c75538aAE3d3558BE4C3FD7";  //bsc test 合约地址

    public static void main( String[] args )
    {
        // 如果没有私钥 可以创建一个随机私钥， 私钥必须保存好  合约地址 由于
        String privateKey = Mining.getSingleton().generatePrivateKey();
        //1 初始化 传入  rpc 地址、 账户私钥、  合约地址
        Mining.getSingleton().Initialize(rpcURL, privateKey, contractAddress);

        //得到 你的账户 public address
        String address = Mining.getSingleton().getAddress();
        System.out.println( "publicAddress: " + address);

        //签名完成任务 签名者就是 私钥的 账户
        JSONObject result_obj = Mining.getSingleton().ClientGetMiningSign(0);
        if (result_obj != null) {
            System.out.println(result_obj.toString());

            //批量上传 签名信息到合约 只有被授权帐号才有权限上传
            ArrayList<String> batchData = new ArrayList<String>();
            batchData.add(result_obj.toString());

            JSONObject result_obj2 = Mining.getSingleton().ServerBatchUploadMiningResult(batchData);
            if (result_obj2 != null && result_obj2.getString("code").equals("OK")) {
                String txhash = result_obj2.getString("txhash");

                // 获取交易的结果
                JSONObject status = Mining.getSingleton().WaitForTransactionReceipt(txhash);
            }
        }


        /* 4  release 使用完毕后释放资源
        * */
        Mining.getSingleton().Shutdown();
    }
}
