package com.easy.app.biz;

import java.util.HashMap;

public class CustMapping {
	
	static HashMap<String,String[]> map =new HashMap<String,String[]>();

	static{
		map.put("DecHead", new String[]{"SeqNo","IEFlag","Type","AgentCode","AgentName","ApprNo","BillNo","ContrNo","CustomMaster","CutMode","DistinatePort","DistrictCode","FeeCurr","FeeMark","FeeRate","GrossWet","IEDate","IEPort","InRatio","InsurCurr","InsurMark","InsurRate","LicenseNo","ManualNo","NetWt","NoteS","OtherCurr","OtherMark","OtherRate","OwnerCode","OwnerName","PackNo","PayWay","PaymentMark","TradeCode","TradeCountry","TradeMode","TradeName","TrafMode","TrafName","TransMode","WrapType","EntryId","PreEntryId","EdiId","Risk","CopName","CopCode","EntryType","PDate","TypistNo","InputerName","PartenerID","TgdNo","DataSource","DeclTrnRel","ChkSurety","BillType","AgentCodeScc","OwnerCodeScc","TradeCodeScc","CopCodeScc","PromiseItmes","TradeAreaCode"});
		map.put("DecList", new String[]{"ClassMark","CodeTS","ContrItem","DeclPrice","DutyMode","Factor","GModel","GName","GNo","OriginCountry","TradeCurr","DeclTotal","GQty","FirstQty","SecondQty","GUnit","FirstUnit","SecondUnit","UseTo","WorkUsd","ExgNo","ExgVersion","DestinationCountry"});//DecLists
		map.put("Container", new String[]{"ContainerId","ContainerMd","ContainerWt"});//DecContainers
		map.put("LicenseDocu", new String[]{"LicenseDocu","DocuCode","CertCode","LicenseDocu"});//DecLicenseDocus
		map.put("DecFreeTxt", new String[]{"RelId","RelManNo","BonNo","VoyNo","DecBpNo","CusFie","DecNo"});
		map.put("DecSign", new String[]{"OperType","ICCode","CopCode","OperName","ClientSeqNo","Sign","SignDate","Certificate","HostId"});
		map.put("TrnHead", new String[]{"TrnPreId","TransNo","TurnNo","NativeTrafMode","TrafCustomsNo","NativeShipName","NativeVoyageNo","ContractorName","ContractorCode","TransFlag","ValidTime","ESealFlag","Notes","TrnType","ApplCodeScc"});
		map.put("TrnList", new String[]{"TrafMode","ShipId","ShipNameEn","VoyageNo","BillNo","IEDate"});
		map.put("TrnContainer", new String[]{"ContaNo","ContaSn","ContaModel","SealNo","TransName","TransWeight"});//TrnContainers
		map.put("TrnContaGoods", new String[]{"GNo","ContaNo","ContaGoodsCount","ContaGoodsWeight"});//TrnContaGoodsList
		map.put("EdocRealation", new String[]{"EdocID","EdocCode","EdocFomatType","OpNote","EdocCopId","EdocOwnerCode","SignUnit","SignTime","EdocOwnerName","EdocSize"});
		
		map.put("InventoryHead", new String[]{	
				"guid","appType","appTime","appStatus","orderNo","ebpCode","ebpName",
				"ebcCode","ebcName","logisticsNo","logisticsCode","logisticsName","copNo",
				"preNo","assureCode","emsNo","invtNo","ieFlag","declTime","customsCode",
				"portCode","ieDate","buyerIdType","buyerIdNumber","buyerName","buyerTelephone",
				"consigneeAddress","agentCode","agentName","areaCode","areaName",
				"tradeMode","trafMode","trafNo","voyageNo","billNo","loctNo","licenseNo","country",
				"freight","insuredFee","currency","wrapType","packNo","grossWeight","netWeight",
				"note"
		});
		map.put("InventoryList", new String[]{"gnum","itemRecordNo","itemNo","itemName" ,
				"gcode","gname","gmodel","barCode","countr","currency","qty","unit",
				"qty1","unit1","qty2","unit2","price","totalPrice","note"
        });
		
		map.put("OrderHead", new String[]{
				"guid","appType","appTime","appStatus","orderType",
				"orderNo","ebpCode","ebpName","ebcCode","ebcName",
				"goodsValue","freight","discount","taxTotal","acturalPaid",
				"currency","buyerRegNo","buyerName","buyerIdType","buyerIdNumber",
				"consignee","consigneeTelephone","consigneeAddress"
		});
		map.put("OrderList", new String[]{
				"gnum","itemNo","itemName","barCode","unit","qty",
				"price","totalPrice","currency","country"				
		});
		
		map.put("Logistics",new String[]{
				"guid","appType","appTime","appStatus","logisticsCode",
				"logisticsName","logisticsNo","billNo","freight","insuredFee",
				"currency","weight","packNo","goodsInfo","consignee","consigneeAddress",
				"consigneeTelephone","note"
		});

		map.put("Payment",new String[]{
				"guid","appType","appTime","appStatus","payCode","payName","payTransactionId",
				"orderNo","ebpCode","ebpName","payerIdType","payerIdNumber","payerName","telephone",
				"amountPaid","currency","payTime","note"
		});
		
		map.put("LogisticsStatus", new String[]{
				"guid","appType","appTime","appStatus","logisticsCode","logisticsName",
				"logisticsNo","logisticsStatus","logisticsTime","note"				
		});
		
		map.put("InvtCancel", new String[]{
				"guid","appType","appTime","appStatus","customsCode","orderNo",
				"ebpCode","ebpName","ebcCode","ebcName","logisticsNo","logisticsCode",
				"logisticsName","copNo","preNo","invtNo","buyerIdType","buyerIdNumber",
				"buyerName","buyerTelephone","agentCode","agentName","reason","note"				
		});
		
		map.put("InvtRefundHead", new String[]{
				"guid","appType","appTime","appStatus","customsCode","orderNo",
				"ebpCode","ebpName","ebcCode","ebcName","logisticsNo","logisticsCode",
				"logisticsName","copNo","invtNo","buyerIdType","buyerIdNumber","buyerName",
				"buyerTelephone","agentCode","agentName","reason"	
		});
		map.put("InvtRefundList", new String[]{
				"gnum","gcode","gname","qty","unit"
		});
		
		map.put("DeliveryHead", new String[]{
				"guid","appType","appTime","appStatus",
				"customsCode","copNo","preNo","rkdNo",
				"operatorCode","operatorName","ieFlag",
				"trafMode","trafNo","voyageNo","billNo",
				"logisticsCode","logisticsName","unloadLocation","note"
		});
		map.put("DeliveryList", new String[]{
				"gnum","logisticsNo","note"
		});
		
		map.put("BaseTransfer", new String[]{
				"copCode","copName","dxpMode","dxpId","note"
		});
	}
}
