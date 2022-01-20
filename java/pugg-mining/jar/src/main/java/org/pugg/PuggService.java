package org.pugg;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Pugg service interface...
 */
public interface PuggService {

    /*
      所有接口返回的表单请包含以下两个键值对
      1. result，数据类型为 boolean，用于描述请求是否成功
      2. result_message, 数据类型为 String，用于描述附加信息，如失败原因等。内容可以为空
     */

    /**
     * 每次用户完成了一次踩单车，会调用该接口获取签名后的表单
     * @param miningType 接口参数预留，该参数暂时不用例会
     * @return 结果表单
     */
    JSONObject ClientGetMiningSign(int miningType /* 参数列表其余自订 */);

    /**
     * 获取今天发放的用于 所有用户 踩单车的代币数量，这个函数会在服务器端一天中某一固定时刻调用，获取一次。
     * @return 结果表单
     */
    JSONObject ServerGetCoinCountToday(/* 参数列表其余自订 */);

    /**
     * 用于服务器批量上传某一时段所有用户挖矿情况
     * @param batchData 数组中 String 为 GetMiningSign 返回表单数据序列化后结果
     * @return 结果表单
     */
    JSONObject ServerBatchUploadMiningResult(ArrayList<String> batchData /* 参数列表其余自订 */);

    /**
     * 用于初始化SDK，相关初始化操作可放在这里
     */
    void Initialize(String rpcURL, String privateKey, String _contractAddress);

    /**
     * 查看SDK是否初始化, 不会有参数传入
     * @return 是否初始化
     */
    boolean IsInitialize();

    /**
     * 接口所在程序结束运行的时候会调用, 不会有参数传入
     */
    void Shutdown();
}
