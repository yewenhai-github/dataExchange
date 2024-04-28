package com.easy.app.constants;

public class ExsConstants {
    public static final String XML_GB18030_ENCODING = "GB18030";
    public static final String XML_UTF8_ENCODING = "UTF-8";
    public static final String XmlDocument = "XmlDocument";
    public static final String XmlHead = "XmlHead";
    public static final String RootNames = "RootNames";
    public static final String dataSource = "dataSource";
    public static final String ServiceMode = "ServiceMode";
    public static final String ChildFolder = "CHILD_FOLDER";
    public static final String webServicesUrl = "webServicesUrl";
    public static final String webServicesMethod = "webServicesMethod";
    public static final String FileName = "FILE_NAME";
    public static final String SystemFlag = "SystemFlag";//文件名拆后的的第一个参数
    public static final String MessageType = "MessageType";//文件名拆后的的第二个参数
    public static final String MessageDest = "MessageDest";//文件名拆后的的第三个参数
    public static final String SerialNo = "SerialNo";//文件名拆后的的第四个参数
    public static final String Indx = "Indx";
    public static final String P_Indx = "P_Indx";
    public static final String SEARCH_ID = "SEARCH_ID";
    public static final String MSG_ID = "MSG_ID";
    public static final String MSG_TYPE = "MSG_TYPE";   

    public static final String MQToDB = "MQToDB";
    public static final String DBToMQ = "DBToMQ";
    public static final String ActiveMQToDB = "ActiveMQToDB";
    public static final String IBMMQToDB =  "IBMMQToDB";
    public static final String RabbitMQToDB =  "RabbitMQToDB";
    public static final String FTPToDB =  "FTPToDB";
    public static final String LocalToDB =  "LocalToDB";
    public static final String DBToActiveMQ = "DBToActiveMQ";
    public static final String DBToIBMMQ =  "DBToIBMMQ";
    public static final String DBToRabbitMQ =  "DBToRabbitMQ";
    public static final String DBToFTP =  "DBToFTP";
    public static final String DBToLocal =  "DBToLocal";
    
    public static final String UpdateDBToDB = "UpdateDBToDB";
    public static final String DBToXml = "DBToXml";
    public static final String XmlToMQ = "XmlToMQ";
    public static final String MQToXml = "MQToXml";
    public static final String XmlToDB = "XmlToDB";
    public static final String XmlToEdiDB = "XmlToEdiDB";
    public static final String ActiveMQ = "ActiveMQ";
    public static final String IBMMQ = "IBMMQ";
    public static final String RabbitMQ = "RabbitMQ";
    public static final String FTP = "FTP";
    public static final String HTTP = "FTP";
    public static final String ElecDocs = "ElecDocs";
    public static final String ComXml = "ComXml";
    public static final String AnyXml = "AnyXml";
    public static final String AnyJson = "AnyJson";
    public static final String EdiTab = "EdiTab";
    public static final String AnsiX12 = "AnsiX12";
    public static final String EdiFact = "EdiFact";
    public static final String ActivemqProducer = "ActivemqProducer";
    public static final String ActivemqConsumer = "ActivemqConsumer";
    public static final String XmlToFloderMerge = "XmlToFloderMerge";
    public static final String XmlToFloderSplit = "XmlToFloderSplit";
    /***************************Api Begin*******************************/
    public static final String ApiPollingToInvoke = "ApiPollingToInvoke";
    public static final String ApiPollingToDB = "ApiPollingToDB";
    public static final String ApiPollingToPush = "ApiPollingToPush";
    /***************************Api End*******************************/
//    public static final String XmlWsToClient = "XmlWsToClient";
//    public static final String XmlWsToServer = "XmlWsToServer";
    public static final String Local = "Local";
    public static final String Ftp = "Ftp";
    public static final String DB = "DB";
    public static final String WebService = "WebService";
    public static final String EDS = "EDS";
    public static final String EP = "EP";
    public static final String EDI = "EDI";
    public static final String RDS = "RDS";
    public static final String BDS = "BDS";
    public static final String EdiItowNet = "EdiItowNet";
    public static final String ItowNet = "ItowNet";
    public static final String Socket = "Socket";
    //文件路径
    public static final String SOURCE_PATH = "SOURCE_PATH";
    public static final String TARGET_PATH = "TARGET_PATH";
    public static final String ERROR_PATH = "ERROR_PATH";
    public static final String HIT_PATH = "HIT_PATH";
    public static final String PASS_PATH = "PASS_PATH";
    //MessageHead 
    public static final String MESSAGE_ID = "MESSAGE_ID";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String MESSAGE_TIME = "MESSAGE_TIME";
    public static final String MESSAGE_SOURCE = "MESSAGE_SOURCE";
    public static final String MESSAGE_DEST = "MESSAGE_DEST";
    public static final String MESSAGE_CATEGORY = "MESSAGE_CATEGORY";
    public static final String MESSAGE_VERSION = "MESSAGE_VERSION";
    public static final String SIGN_DATA = "SIGN_DATA";
    public static final String MESSAGE_SIGN_DATA = "MESSAGE_SIGN_DATA";
    public static final String TECH_REG_CODE = "TECH_REG_CODE";
    public static final String MESSAGE_SERIAL_NAME = "MESSAGE_SERIAL_NAME";
    public static final String SendCode = "SEND_CODE";
    public static final String ReciptCode = "RECIPT_CODE";
    /****************报文规范的固定节点*******************/
    public static final String ResponseMessage = "ResponseMessage";
    public static final String RequestMessage = "RequestMessage";
    public static final String MessageHead = "MessageHead";
    public static final String MessageBody = "MessageBody";
    
    public static final String MessageSourceDefault = "";
    public static final String MessageDestDefault = "";
    
    public static final String PART_ID = "PART_ID";
    public static final String PART_ID_SOURCE = "PART_ID_SOURCE";
    public static final String INDX = "INDX";
    public static final String P_INDX = "P_INDX";
    /****************http*******************/ 
    public static final String OpenApi = "OpenApi";
    public static final String HttpDecl = "HttpDecl";
    public static final String HttpReceived = "HttpReceived";
    
    public static final String Resquest = "Resquest";
    public static final String Response = "Response";
//    public static final String PutMessage = "PutMessage";
//    public static final String GetMessage = "GetMessage";
//    public static final String DeleteMessage = "DeleteMessage";
//    public static final String InvokeMessage = "InvokeMessage";
    public static final String push = "push";
    public static final String pull = "pull";
    public static final String destory = "destory";
    
    /****************交换文件的固定路径*******************/ 
    public static final String appLogPath = "/java/workspace/logs";
    public static final String appTempPath = "/java/workspace/temp";
    
    /******************EdiFact*************************/
    public static final String UNH = "UNH";
    public static final String BGM = "BGM";
    public static final String DMS = "DMS";
    public static final String DTM = "DTM";
    public static final String FTX = "FTX";
    public static final String UNT = "UNT";

    public static final String MQ = "MQ";
    public static final String DataBase = "DataBase";

}
