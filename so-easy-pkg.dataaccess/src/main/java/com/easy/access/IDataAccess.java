package com.easy.access;

import java.sql.Connection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.easy.exception.LegendException;

/**
 * so-easy private
 * 
 * @author yewenhai 2020-02-16
 * 
 * @version 7.0.0
 * 
 */
public interface IDataAccess {
	
	/**
	 * GetActiveCN() 获取数据库当前连接
	 * BeginTrans() 开始一个事务
	 * ComitTrans()提交事务
	 * RoolbackTrans() 事务回滚
	 * */
	public Connection GetActiveCN() throws LegendException;
	public abstract boolean BeginTrans() throws LegendException;
	public abstract boolean ComitTrans() throws LegendException;
	public abstract boolean RoolbackTrans() throws LegendException;
	
	/**
	 * 获取数据表
	 * @param TableName 根节点名称
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @param page 起始页
	 * @param pagesize 页大小
	 * @param KeyField SQLServer根据传入的主键进行分页，默认主键为Indx
	 * @return json字符串
	 * */
	public abstract String GetTable(String SQL) throws LegendException;
	public abstract String GetTable(String TableName, String SQL) throws LegendException;
	public abstract String GetTable(String TableName, String SQL, Object parms) throws LegendException;
	public abstract String GetTable(String SQL, int page, int pagesize) throws LegendException;
	public abstract String GetTable(Connection ActiveCN, String TableName, String SQL, Object parms) throws LegendException;
	public abstract String GetTable(String TableName, String SQL, int page, int pagesize) throws LegendException;
	public abstract String GetTable(String TableName, String SQL, Object parms, int page, int pagesize) throws LegendException;
	public abstract String GetTable(String TableName, String SQL, int page, int pagesize, String KeyField) throws LegendException;
	public abstract String GetTable(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField) throws LegendException;
	/**
	 * 获取数据表
	 * @param TableName 根节点名称
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @param page 起始页
	 * @param pagesize 页大小
	 * @param KeyField SQLServer根据传入的主键进行分页，默认主键为Indx
	 * @return JSONObject对象
	 * */
	public abstract JSONObject GetTableJSON(String TableName, String SQL) throws LegendException;
	public abstract JSONObject GetTableJSON(String TableName, String SQL, Object parms) throws LegendException;
	public abstract JSONObject GetTableJSON(String TableName, String SQL, int page, int pagesize) throws LegendException;
	public abstract JSONObject GetTableJSON(String TableName, String SQL, Object parms, int page, int pagesize) throws LegendException;
	public abstract JSONObject GetTableJSON(String TableName, String SQL, int page, int pagesize, String KeyField) throws LegendException;
	public abstract JSONObject GetTableJSON(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField) throws LegendException;
	
	/**
	 * 获取数据表
	 * @param TableName 根节点名称
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @param page 起始页
	 * @param pagesize 页大小
	 * @param KeyField SQLServer根据传入的主键进行分页，默认主键为Indx
	 * @return Datas对象
	 * */
	public abstract Datas GetTableDatas(String TableName, String SQL) throws LegendException;
	public abstract Datas GetTableDatas(Connection ActiveCN, String TableName, String SQL, Object parms) throws LegendException;
	public abstract Datas GetTableDatas(String TableName, String SQL, Object parms) throws LegendException;
	public abstract Datas GetTableDatas(String TableName, String SQL, int page, int pagesize) throws LegendException;
	public abstract Datas GetTableDatas(String TableName, String SQL, Object parms, int page, int pagesize) throws LegendException;
	public abstract Datas GetTableDatas(String TableName, String SQL, int page, int pagesize, String KeyField) throws LegendException;
	public abstract Datas GetTableDatas(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField) throws LegendException;
	
	/**
	 * 获取数据表
	 * @param TableName 根节点名称
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @param page 起始页
	 * @param pagesize 页大小
	 * @param KeyField SQLServer根据传入的主键进行分页，默认主键为Indx
	 * @return JSONObject对象
	 * */
	public abstract JSONObject GetTableJSONUI(String TableName, String SQL, int page, int pagesize, String KeyField) throws LegendException;
	public abstract JSONObject GetTableJSONUI(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField) throws LegendException;
	public abstract JSONObject GetTableJSONUI(String TableName, String SQL, int page, int pagesize, String KeyField,JSONArray footer) throws LegendException;
	public abstract Datas GetTableDatasUI(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField) throws LegendException;
	public abstract Datas GetTableDatasUI(String TableName, String SQL, Object parms, int page, int pagesize, String KeyField,JSONArray footer) throws LegendException;
	public abstract Datas GetTableDatasUI(Connection conn,String TableName, String SQL, Object parms,int page, int pagesize, String KeyField) throws LegendException;
	
	/**
	 * 获取数据表总行数
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @return 总行数
	 * */
	public abstract int GetRowCount(String SQL) throws LegendException;
	public abstract int GetRowCount(String SQL, Object parms) throws LegendException;
	public abstract int GetRowCount(Connection conn,String SQL, Object parms) throws LegendException;
	
	/**
	 * 判断数据存在性
	 * @param SQL 用于获取数据表的SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @return 存在性
	 * */
	public abstract boolean Exists(String SQL) throws LegendException;
	public abstract boolean Exists(String SQL, Object parms) throws LegendException;
	
	/**
	 * 批量新增
	 * @param TableName 用于新增纪录的数据库表名
	 * @param Datas 数据集合
	 * @param KeyField 主键字段名称
	 * @return 成功失败标识
	 * */
	public abstract boolean Insert(String TableName, Object objs) throws LegendException;
	public abstract boolean Insert(String TableName, Object objs, String KeyField) throws LegendException;
	public abstract boolean Insert(String TableName, Object objs, String KeyField,Connection conn) throws LegendException;
	
	/**
	 * 批量修改和新增：有主键的做修改，没有主键的做新增操作。
	 * @param TableName 用于新增、修改纪录的数据库表名
	 * @param Datas 数据集合
	 * @param KeyField 主键字段名称
	 * @return 成功失败标识
	 * */
	public abstract boolean Update(String TableName, Object objs) throws LegendException;
	public abstract boolean Update(String TableName, Object objs, String KeyField) throws LegendException;
	public abstract boolean Update(String TableName, Object objs, String KeyField,Connection conn) throws LegendException;
	
	/**
	 * 批量删除
	 * @param TableName 用于删除纪录的数据库表名
	 * @param Datas 数据集合
	 * @param KeyField 主键字段名称
	 * @return 成功失败标识
	 * */
	public abstract boolean Delete(String TableName, Object objs) throws LegendException;
	public abstract boolean Delete(String TableName, Object objs,String KeyField) throws LegendException;
	public abstract boolean Delete(String TableName, Object objs,String KeyField,Connection conn) throws LegendException;
	/**
	 * 执行SQL语句
	 * @param parms 参数集合:支持Integer、String、Long、Integer[]、String[]、Long[]、Object[]
	 * @return 成功失败标识
	 * */
	public abstract boolean ExecSQL(String SQL) throws LegendException;
	public abstract boolean ExecSQL(String SQL, Object parms) throws LegendException;
	public abstract boolean ExecSQL(String SQL, Object parms, String KeyField) throws LegendException;
	public abstract boolean ExecSQL(String SQL, Object parms, String KeyField, StringBuffer KeyValue) throws LegendException;
	public abstract boolean ExecSQL(String SQL, Object parms, String KeyField, StringBuffer KeyValue, Connection conn) throws LegendException;
	/**
	 * 调用存储过程
	 * @param sql参数如：DEMO_PKG.GET_ORDER(TORDER,?,?)或者DEMO_PKG.GET_ORDER(?,?,TORDER)，其中TORDER为输出到Datas.dataJSON的根节点
	 * @return Datas 返回参数
	 * **/
	public abstract Datas Exec(String SQL,Object[] params) throws LegendException;
	
	/**
	 * 关闭数据库连接
	 * @return void
	 * */
	public abstract void Despose() throws LegendException;
	
	/**
	 * 当前登录人信息
	 * */
	public void setCurrentUser(JSONObject currentUser) throws LegendException;
	
	/**
	 * 开启日志记录
	 * */
	public void LogCopHistory(boolean flag) throws LegendException;
	
	/**
	 * 获取当前操作的第一行记录Id
	 * */
	public String GetCurrentKey() throws LegendException;
	public void SetCurrentKey(String CurrentKey) throws LegendException;
	
	public JSONArray ToTree(JSONArray data, String idfield,String pidfield) throws LegendException;
	public JSONArray ToTree(JSONArray data, String idfield,String pidfield1,String pidfield2) throws LegendException;
}