package org.example;

import org.json.JSONObject;
import org.pugg.Mining;

import java.lang.reflect.Array;
import java.math.BigInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    static String rpcURL = "https://data-seed-prebsc-1-s1.binance.org:8545/"; //bsc test rpc 节点地址
    static String contractAddress = "0x5d882B62cC6C8C4d5a6C0F48e528cd74008AB93E";  //bsc test 合约地址

    public static void main( String[] args )
    {
        // 如果没有私钥 可以创建一个随机私钥， 私钥必须保存好  合约地址 由于
        String privateKey = Mining.getSingleton().generatePrivateKey();
        //1 初始化 传入  rpc 地址、 账户私钥、  合约地址
        Mining.getSingleton().Initialize(rpcURL, privateKey, contractAddress);

        //得到 你的账户 public address
        String address = Mining.getSingleton().getAddress();
        System.out.println( "publicAddress: " + address);

        JSONObject result_obj = Mining.getSingleton().ClientGetMiningSign(0);
        if (result_obj != null) {
            System.out.println(result_obj.toString());

        }

        /* 4  release 使用完毕后释放资源
        * */
        Mining.getSingleton().Shutdown();
    }
}
