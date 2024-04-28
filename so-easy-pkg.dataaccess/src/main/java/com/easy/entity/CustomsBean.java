package com.easy.entity;

import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easy.utility.LogUtil;
import com.easy.utility.SysUtility;

public class CustomsBean extends BaseBean {
	public JSONObject MessageHead = new JSONObject();
	public JSONObject MessageBody = new JSONObject();
	public JSONObject BaseTransfer = new JSONObject();
	/*********************申报**********************/
	//621
	public JSONArray InventoryHeads = new JSONArray();
	public JSONArray InventoryLists = new JSONArray();
	
	//311
	public JSONArray OrderHeads= new JSONArray();
	public JSONArray OrderLists= new JSONArray();
	
	//411
	public JSONArray Payment=new JSONArray();
	//511
	public JSONArray Logistics=new JSONArray();
	//513
	public JSONArray LogisticsStatus=new JSONArray();
	//623
    public JSONArray InvtCancel	=new JSONArray();
	//625
    public JSONArray InvtRefundHeads=new JSONArray();
    public JSONArray InvtRefundLists=new JSONArray();
    //711
    public JSONArray DeliveryHeads=new JSONArray();
    public JSONArray DeliveryLists=new JSONArray();
    
    /*********************回执**********************/
    
    
	public static CustomsBean getInstance(){
		return new CustomsBean();
	}
	
	public JSONArray getInventoryHeadsMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray Inventorys = new JSONArray();
			for (int i = 0; i < InventoryHeads.length(); i++) {
				JSONObject Inventory = new JSONObject();
				JSONObject InventoryHead = (JSONObject)InventoryHeads.get(i);
				JSONArray InventoryDetails = getChildData(InventoryHead, InventoryLists, "guid");
				Inventory.put("InventoryHead", InventoryHead);
				Inventory.put("InventoryList", InventoryDetails);
				Inventorys.put(Inventory);
			}
			MessageBody.put("Inventory", Inventorys);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getOrderMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray orders = new JSONArray();
			for (int i = 0; i < OrderHeads.length(); i++) {
				JSONObject order = new JSONObject();
				JSONObject orderHead = (JSONObject)OrderHeads.get(i);
				JSONArray orderDetails = getChildData(orderHead, OrderLists, "guid");
				order.put("orderHead", orderHead);
				order.put("OrderList", orderDetails);
				orders.put(order);
			}
			MessageBody.put("Order",orders);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getPaymentMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray msgHeads = new JSONArray();
			for (int i = 0; i < Payment.length(); i++) {
				JSONObject head = new JSONObject();
				JSONObject tempLogisticsHead = (JSONObject)Payment.get(i);
				head.put("PaymentHead", tempLogisticsHead);
				msgHeads.put(head);
			}
			MessageBody.put("Payment", msgHeads);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getLogisticsMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray msgHeads = new JSONArray();
			for (int i = 0; i < Logistics.length(); i++) {
				JSONObject head = new JSONObject();
				JSONObject tempLogisticsHead = (JSONObject)Logistics.get(i);
				head.put("LogisticsHead", tempLogisticsHead);
				msgHeads.put(head);
			}
			MessageBody.put("Logistics", msgHeads);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getLogisticsStatusMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			MessageBody.put("LogisticsStatus", LogisticsStatus);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getInvtCancelMessage(){
		JSONArray rows = new JSONArray();
		try {
			
			MessageBody.put("InvtCancel", InvtCancel);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getInvtRefundHeadMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray msgHeads = new JSONArray();
			for (int i = 0; i < InvtRefundHeads.length(); i++) {
				JSONObject head = new JSONObject();
				JSONObject tempHead = (JSONObject)InvtRefundHeads.get(i);
				JSONArray tempDetails = getChildData(tempHead, InvtRefundLists, "guid");
				head.put("InvtRefundHead", tempHead);
				head.put("InvtRefundList", tempDetails);
				msgHeads.put(head);
			}
			MessageBody.put("InvtRefund", msgHeads);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getDeliveryHeadsMessage(){
		JSONArray rows = new JSONArray();
		try {
			JSONArray msgHeads = new JSONArray();
			for (int i = 0; i < DeliveryHeads.length(); i++) {
				JSONObject head = new JSONObject();
				JSONObject tempHead = (JSONObject)DeliveryHeads.get(i);
				JSONArray tempDetails = getChildData(tempHead, DeliveryHeads, "guid");
				head.put("DeliveryHead", tempHead);
				head.put("DeliveryList", tempDetails);
				msgHeads.put(head);
			}
			MessageBody.put("Delivery", msgHeads);
			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			row.put("MessageBody", MessageBody);
			row.put("BaseTransfer", BaseTransfer);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	}
	
	public JSONArray getMessageHead(){
		JSONArray rows = new JSONArray();
		try {			
			JSONObject row = new JSONObject();
			row.put("MessageHead", MessageHead);
			rows.put(row);
		} catch (JSONException e) {
			LogUtil.printLog(e.getMessage(), Level.ERROR);
		}
		return rows;
	} 
}
