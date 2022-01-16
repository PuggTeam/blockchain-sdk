package org.example;

import org.json.JSONObject;
import org.pugg.Mining;

import java.math.BigInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    static String rpcURL = "HTTP://127.0.0.1:7545"; //本地测试链的 rpc 节点地址
    static String contractAddress = "0x23e6BA588381c25Ce1Cd6aeC7f204D65C85cBF7a";  //本地测试链的 合约地址

    public static void main( String[] args )
    {
        // 如果没有私钥 可以创建一个随机私钥， 私钥必须保存好  合约地址 由于
        String privateKey = Mining.generatePrivateKey();
        //1 初始化 传入  rpc 地址、 账户私钥、  合约地址
        Mining.Init(rpcURL, privateKey, contractAddress);

        //得到 你的账户 public address
        String address = Mining.getAddress();
        System.out.println( "publicAddress: " + address);

        //初始化之后可以 开始调用合约方法
        //# 1 isExist  根据 taskId 判断任务是否存在
        boolean result = Mining.isExist(BigInteger.valueOf(1));  // taskId = 1

        //# 2 getTaskPoints 根据 taskId 获取任务完成后的积分奖励 如果任务不存在则返回 null
        BigInteger points = Mining.getTaskPoints(BigInteger.valueOf(1));

        /*# 3 signDoneTask 完成任务签名
        传入参数:
            taskId  任务ID
            points  任务完成后的积分奖励
        返回: JSONObject:
                message:     签名信息
                signer：     签名人账户
                taskId：     完成的任务ID
                points：     获得的积分奖励
                signature:   生成的签名
        */
        JSONObject result_obj = Mining.signDoneTask(BigInteger.valueOf(1), points);
        if (result_obj != null) {
            System.out.println(result_obj.toString());
        }

        /* 4  release 使用完毕后释放资源
        * */
        Mining.release();
    }
}
