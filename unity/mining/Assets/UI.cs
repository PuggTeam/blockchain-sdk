using UnityEngine.UI;
using UnityEngine;
using System.Collections.Generic;
using System.Numerics;
using System;

public class UI : MonoBehaviour
{
    public Text log;
    private static string kRpcURL = "https://data-seed-prebsc-1-s1.binance.org:8545/";      //bsc 测试网
    private static string kContractAddress = "0xC0Ff7E54393145372c75538aAE3d3558BE4C3FD7";  //合约地址
    public InputField privateKeyInput;
    public InputField signersInput;
    public InputField taskIdsInput;
    public InputField pointsInput;
    public InputField signaturesInput;
    public InputField txhashInput;
    // Start is called before the first frame update
    void Start()
    {
       
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    List<string> split_string(string str)
    {
        string[] array = str.Split(',');
        List<string> rs = new List<string>(array);
        return rs;
    }

    List<BigInteger> split_bint(string str)
    {
        string[] array = str.Split(',');
        List<BigInteger> rs = new List<BigInteger>();
        for (int i = 0; i < array.Length; i++)
        {
            rs.Add(BigInteger.Parse(array[i]));
        }
        return rs;
    }

    public void onClick_Button(string fun)
    {
        switch (fun)
        {
            case "Initialize":
            {
                string privateKey = privateKeyInput.text;
                StartCoroutine(PuggMining.Initialize(kRpcURL, privateKey, kContractAddress, (code, value) => {
                    if (code == "ok")
                    {
                        string _log = "Initialize : code = " + code + "\n";
                        _log += "value : " + value;
                        log.text = _log;
                    }
                }));
            }
            break;
            case "ClientGetMiningSign":
            {
                StartCoroutine(PuggMining.ClientGetMiningSign(0, (code, signer, taskId, points, signature, time) => {
                    if (code == "ok")
                    {
                        string _log = "ClientGetMiningSign : code = " + code + "\n";
                        _log += "signer : " + signer + "\n";
                        _log += "taskId : " + taskId + "\n";
                        _log += "points : " + points + "\n";
                        _log += "signature : " + signature + "\n";
                        _log += "time : " + time + "\n";
                        log.text = _log;
                    }
                }));
            }
            break;
            case "ServerGetCoinCountToday":
            {
                StartCoroutine(PuggMining.ServerGetCoinCountToday((code, value) => {
                    if (code == "ok")
                    {
                        string _log = "ServerGetCoinCountToday : code = " + code + "\n";
                        _log += "value : " + value;
                        log.text = _log;
                    }
                }));
            }
            break;
            case "ServerBatchUploadMiningResult":
            {
                List<string> singers = split_string(signersInput.text);
                List<BigInteger> taskIds = split_bint(taskIdsInput.text);
                List<BigInteger> points = split_bint(pointsInput.text);
                List<string> signatures = split_string(signaturesInput.text);


                StartCoroutine(PuggMining.ServerBatchUploadMiningResult(singers, taskIds, points, signatures, (code, txhash) => {
                    if (code == "ok")
                    {
                        string _log = "ServerBatchUploadMiningResult : code = " + code + "\n";
                        _log += "txhash : " + txhash;
                        log.text = _log;
                    }
                }));
            }
            break;
            case "WaitForTransactionReceipt":
            {
                string txhash = txhashInput.text;
                StartCoroutine(PuggMining.WaitForTransactionReceipt(txhash, (code, status) => {
                    if (code == "ok")
                    {
                        string _log = "WaitForTransactionReceipt : code = " + code + "\n";
                        _log += "status : " + status;
                        log.text = _log;
                    }
                }));
            }
            break;
        }
    }
}
