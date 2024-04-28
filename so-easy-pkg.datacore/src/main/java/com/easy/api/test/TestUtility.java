package com.easy.api.test;

import com.easy.utility.SysUtility;

public class TestUtility {
	
	public static String getDestoryJsonData(){
		StringBuffer jsonData = new StringBuffer();
		jsonData.append("{");
		jsonData.append("  \"MessageHead\": {");
		jsonData.append("    \"MESSAGE_ID\": \"80C5FE697A291430CF468CFC2BBE5729\",");
		jsonData.append("    \"MESSAGE_TYPE\": \"EEntDeclIo\",");
		jsonData.append("    \"MESSAGE_TIME\": \"2017-01-01 13:21:43\",");
		jsonData.append("    \"MESSAGE_SOURCE\": \"pullTest\",");
		jsonData.append("    \"MESSAGE_DEST\": \"OBORExpInsp\",");
		jsonData.append("    \"MESSAGE_VERSION\": \"1.0\",");
		jsonData.append("    \"MESSAGE_CATEGORY\": \"21\",");
//		jsonData.append("    \"TECH_REG_CODE\": \"588424997\",");
		jsonData.append("    \"MESSAGE_SIGN_DATA\": \"9a678acc109503d54a1fc0ee293e47a6\"");
		jsonData.append("  },");
		jsonData.append("    \"MessageBody\": {");
		jsonData.append("    \"RESPONSE_ID\": \"2167f23af7384d1994dd0072afb9728f,0c9269d3fa7f422ab5a3769cbc650a01\"");
		jsonData.append("  }");
		jsonData.append("}");
		return jsonData.toString();
	}
	public static String getPullJsonData(){
		StringBuffer jsonData = new StringBuffer();
		jsonData.append("{");
		jsonData.append("  \"MessageHead\": {");
		jsonData.append("    \"MESSAGE_ID\": \"80C5FE697A291430CF468CFC2BBE5729\",");
		jsonData.append("    \"MESSAGE_TYPE\": \"EEntDeclIo\",");
		jsonData.append("    \"MESSAGE_TIME\": \"2017-01-01 13:21:43\",");
		jsonData.append("    \"MESSAGE_SOURCE\": \"pullTest\",");
		jsonData.append("    \"MESSAGE_DEST\": \"OBORExpInsp\",");
		jsonData.append("    \"MESSAGE_VERSION\": \"1.0\",");
		jsonData.append("    \"MESSAGE_CATEGORY\": \"21\",");
//		jsonData.append("    \"TECH_REG_CODE\": \"588424997\",");
		jsonData.append("    \"MESSAGE_SIGN_DATA\": \"9a678acc109503d54a1fc0ee293e47a6\"");
		jsonData.append("  }");
//		jsonData.append("    \"MessageBody\": {");
//		jsonData.append("    \"DECL_NO\": \"27b7c399134f4a1ebb44759b02220655\",");
//		jsonData.append("  }");
		jsonData.append("}");
		return jsonData.toString();
	}
	public static String getPullJsonData2(){
		StringBuffer jsonData = new StringBuffer();
		jsonData.append("{");
		jsonData.append("  \"MessageHead\": {");
		jsonData.append("    \"MESSAGE_ID\": \"80C5FE697A291430CF468CFC2BBE5729\",");
		jsonData.append("    \"MESSAGE_TYPE\": \"EEntDeclIo\",");
		jsonData.append("    \"MESSAGE_TIME\": \"2017-01-01 13:21:43\",");
		jsonData.append("    \"MESSAGE_SOURCE\": \"pullTest\",");
		jsonData.append("    \"MESSAGE_DEST\": \"OBORExpInsp\",");
		jsonData.append("    \"MESSAGE_VERSION\": \"1.0\",");
		jsonData.append("    \"MESSAGE_CATEGORY\": \"21\",");
//		jsonData.append("    \"TECH_REG_CODE\": \"588424997\",");
		jsonData.append("    \"MESSAGE_SIGN_DATA\": \"9a678acc109503d54a1fc0ee293e47a6\"");
		jsonData.append("  },");
		jsonData.append("    \"MessageBody\": {");
		jsonData.append("    \"DECL_NO\": \"27b7c399134f4a1ebb44759b02220655\"");
		jsonData.append("  }");
		jsonData.append("}");
		return jsonData.toString();
	}
	public static String getPushJsonData(){
		StringBuffer jsonData = new StringBuffer();
		jsonData.append("{");
		jsonData.append("    \"MessageHead\": {");
		jsonData.append("      \"MESSAGE_ID\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("      \"MESSAGE_TYPE\": \"EEntDeclIo\",");
		jsonData.append("      \"MESSAGE_TIME\": \"2017-01-01 13:21:43\",");
		jsonData.append("      \"MESSAGE_SOURCE\": \"pullTest\",");
		jsonData.append("      \"MESSAGE_DEST\": \"OBORExpInsp\",");
		jsonData.append("      \"MESSAGE_CATEGORY\": \"11\",");
		jsonData.append("      \"MESSAGE_VERSION\": \"1.0\",");
//		jsonData.append("      \"TECH_REG_CODE\": \"588424997\",");
		jsonData.append("      \"MESSAGE_SIGN_DATA\": \"9a678acc109503d54a1fc0ee293e47a6\"");
		jsonData.append("    },");
		jsonData.append("    \"MessageBody\": {");
		jsonData.append("      \"ITF_DCL_IO_DECL\": [{");
		jsonData.append("        \"EntDeclNo\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("        \"EntUuid\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("        \"DeclId\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("        \"AppCertCode\": \"0\",");
		jsonData.append("        \"ApplOri\": \"0\",");
		jsonData.append("        \"AppCertName\": \"0\",");
		jsonData.append("        \"techRegCode\": \"588424997\",");
		jsonData.append("        \"DeclRegNo\": \"3200910212\",");
		jsonData.append("        \"DeclRegName\": \"苏州宏康通关物流有限公司\",");
		jsonData.append("        \"DespCtryCode\": \"840\",");
		jsonData.append("        \"GoodsPlace\": \"港务物流\",");
		jsonData.append("        \"OrgName\": \"东渡局本部\",");
		jsonData.append("        \"DeclPersnCertNo\": \"3900703001\",");
		jsonData.append("        \"TradeModeCode\": \"11\",");
		jsonData.append("        \"VsaOrgCode\": \"320000\",");
		jsonData.append("        \"ConvynceName\": \"GODSPEED\",");
		jsonData.append("        \"AplKind\": \"I\",");
		jsonData.append("        \"DespDate\": \"20160801T00:00:00\",");
		jsonData.append("        \"Contactperson\": \"黄黎萍\",");
		jsonData.append("        \"ConsignorEname\": \"WYLAND WORLDWIDE,L.L.C.,\",");
		jsonData.append("        \"EntyPortCode\": \"399501\",");
		jsonData.append("        \"ContTel\": \"5689928\",");
		jsonData.append("        \"IqRegisterno\": \"3900910115\",");
		jsonData.append("        \"DeclDate\": \"20160906T15:40:11\",");
		jsonData.append("        \"EntyPortName\": \"厦门东渡港区\",");
		jsonData.append("        \"PurpOrgName\": \"东渡局本部\",");
		jsonData.append("        \"TradeCountryCode\": \"840\",");
		jsonData.append("        \"ConsigneeCname\": \"厦门地道赞商业有限公司\",");
		jsonData.append("        \"DeclCode\": \"13\",");
		jsonData.append("        \"PurpOrgCode\": \"320000\",");
		jsonData.append("        \"DeclPersonName\": \"黄黎萍\",");
		jsonData.append("        \"TradeCountryName\": \"美国\",");
		jsonData.append("        \"DestCode\": \"350206\",");
		jsonData.append("        \"DestName\": \"厦门市湖里区\",");
		jsonData.append("        \"BillLadNo\": \"NYKSCHIS51191200_03\",");
		jsonData.append("        \"GdsArvlDate\": \"20160824T00:00:00\",");
		jsonData.append("        \"InspOrgName\": \"东渡局本部\",");
		jsonData.append("        \"TransModeCode\": \"1\",");
		jsonData.append("        \"InvoiceNo\": \"090601地道赞\",");
		jsonData.append("        \"OrigBoxFlag\": \"0\",");
		jsonData.append("        \"DespCtryName\": \"美国\",");
		jsonData.append("        \"DeliveryOrder\": \"NYKSCHIS51191200_03\",");
		jsonData.append("        \"InspOrgCode\": \"320000\",");
		jsonData.append("        \"SoftType\": \"320000\",");
		jsonData.append("        \"AttaCollectName\": \"合同,发票,报检委托书,装箱单,其他单据,提单/运单\",");
		jsonData.append("        \"TransMeanNo\": \"263W\",");
		jsonData.append("        \"SpeclInspQuraRe\": \"ECIQ无纸化报检\",");
		jsonData.append("        \"SpecPassFlag\": \"0000\",");
		jsonData.append("        \"SpecDeclFlag\": \"0000\",");
		jsonData.append("        \"PrnSign\": \"0\",");
		jsonData.append("        \"ConsigneeEname\": \"***\",");
		jsonData.append("        \"DespPortCode\": \"840432\",");
		jsonData.append("        \"OrgCode\": \"320000\",");
		jsonData.append("        \"DespPortName\": \"波特兰（美国）\",");
		jsonData.append("        \"MarkNo\": \"10280000\",");
		jsonData.append("        \"ConsigneeCode\": \"3995615923\",");
		jsonData.append("        \"ContractNo\": \"SO0348744\",");
		jsonData.append("        \"ITF_DCL_IO_DECL_GOODS\": [{");
		jsonData.append("          \"GoodsId\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("          \"EntDeclNo\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("          \"StdWeight\": \"0\",");
		jsonData.append("          \"StdWeightUnitCode\": \"075\",");
		jsonData.append("          \"GoodsNo\": \"1\",");
		jsonData.append("          \"ProdHsCode\": \"9703000090\",");
		jsonData.append("          \"HsCodeDesc\": \"其他各种材料制的雕塑品原件\",");
		jsonData.append("          \"CiqCode\": \"9703000090999\",");
		jsonData.append("          \"CiqName\": \"其他各种材料制的雕塑品原件\",");
		jsonData.append("          \"DeclGoodsCname\": \"海豚喷泉雕塑\",");
		jsonData.append("          \"Qty\": \"2\",");
		jsonData.append("          \"QtyMeasUnit\": \"023\",");
		jsonData.append("          \"Weight\": \"350\",");
		jsonData.append("          \"WtMeasUnit\": \"035\",");
		jsonData.append("          \"StdQty\": \"2\",");
		jsonData.append("          \"GoodsTotalVal\": \"28000\",");
		jsonData.append("          \"Currency\": \"840\",");
		jsonData.append("          \"OriCtryCode\": \"840\",");
		jsonData.append("          \"Purpose\": \"99\",");
		jsonData.append("          \"By1\": \"XMSW\",");
		jsonData.append("          \"StdQtyUnitCode\": \"023\",");
		jsonData.append("          \"NoDangFlag\": \"1\",");
		jsonData.append("          \"ITF_DCL_IO_DECL_GOODS_PACK\": [{");
		jsonData.append("            \"PackId\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("            \"GoodsId\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("            \"EntDeclNo\": \"1020bae0871349afb4ad0da289caf46d\",");
		jsonData.append("            \"GoodsNo\": \"1\",");
		jsonData.append("            \"PackTypeCode\": \"4C12\",");
		jsonData.append("            \"PackCatgName\": \"中木箱\",");
		jsonData.append("            \"PackTypeShort\": \"中木箱\",");
		jsonData.append("            \"PackQty\": \"2\",");
		jsonData.append("            \"IsMainPack\": \"1\"");
		jsonData.append("          }]");
		jsonData.append("        }]");
		jsonData.append("      }]");
		jsonData.append("    }");
		jsonData.append("}");
		return jsonData.toString();
	}
	
	
	
	
	
	public static String getPullXmlData(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<OBORMessage>"
				+"<MessageHead>"
				+"<MESSAGE_ID>80C5FE697A291430CF468CFC2BBE5729</MESSAGE_ID>"
				+"<MESSAGE_TYPE>EEntDeclIo</MESSAGE_TYPE>"
				+"<MESSAGE_TIME>2017-01-01 13:21:43</MESSAGE_TIME>"
				+"<MESSAGE_SOURCE>pullTest</MESSAGE_SOURCE>"
				+"<MESSAGE_DEST>OBORExpInsp</MESSAGE_DEST>"
				+"<MESSAGE_VERSION>1.0</MESSAGE_VERSION>"
				+"<MESSAGE_CATEGORY>21</MESSAGE_CATEGORY>"
				+"<TECH_REG_CODE>588424997</TECH_REG_CODE>"
				+"<RECEIVED_MODE>1</RECEIVED_MODE>"
				+"<MESSAGE_SIGN_DATA>42a28f2f6f4531be25acaf5a7dc98d3d</MESSAGE_SIGN_DATA>"
				+"</MessageHead>"
//				+"<MessageBody>"
//				+"<SEARCH_TABLE>"
//				+"<DECL_NO>27b7c399134f4a1ebb44759b02220655</DECL_NO>"
//				+"</SEARCH_TABLE>"
//				+"</MessageBody>"
				+"</OBORMessage>";
	}
	public static String getPushXmlData(){
		String changeId = SysUtility.GetUUID();
		String XmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" 
				+"<OBORMessage>"
				+"  <MessageHead>"
				+"    <MESSAGE_ID>"+changeId+"</MESSAGE_ID>"
				+"    <MESSAGE_TYPE>EEntDeclIo</MESSAGE_TYPE>"
				+"    <MESSAGE_TIME>2017-01-01 13:21:43</MESSAGE_TIME>"
				+"    <MESSAGE_SOURCE>CPZ_Insp</MESSAGE_SOURCE>"
				+"    <MESSAGE_DEST>OBORExpInsp</MESSAGE_DEST>"
				+"    <MESSAGE_CATEGORY>11</MESSAGE_CATEGORY>"
				+"    <MESSAGE_VERSION>1.0</MESSAGE_VERSION>"
				+"    <TECH_REG_CODE>588424997</TECH_REG_CODE>"
				+"    <MESSAGE_SIGN_DATA>42a28f2f6f4531be25acaf5a7dc98d3d</MESSAGE_SIGN_DATA>"
				+"  </MessageHead>"
				+"  <MessageBody>"
				+" <ITF_DCL_IO_DECL>"
				+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
				+"<EntUuid>"+changeId+"</EntUuid>" 
				+"<DeclId>"+changeId+"</DeclId>" 
				+"<AppCertCode>0</AppCertCode>" 
				+"<ApplOri>0</ApplOri>" 
				+"<AppCertName>0</AppCertName>" 
				+"<techRegCode>588424997</techRegCode>" 
				+"<DeclRegNo>3200910212</DeclRegNo>" 
				+"<DeclRegName>苏州宏康通关物流有限公司</DeclRegName>" 
				+"<DespCtryCode>840</DespCtryCode>" 
				+"<GoodsPlace>港务物流</GoodsPlace>" 
				+"<OrgName>东渡局本部</OrgName>" 
				+"<DeclPersnCertNo>3900703001</DeclPersnCertNo>" 
				+"<TradeModeCode>11</TradeModeCode>" 
				+"<VsaOrgCode>320000</VsaOrgCode>" 
				+"<ConvynceName>GODSPEED</ConvynceName>" 
				+"<AplKind>I</AplKind>" 
				+"<DespDate>20160801T00:00:00</DespDate>" 
				+"<Contactperson>黄黎萍</Contactperson>" 
				+"<ConsignorEname>WYLAND WORLDWIDE,L.L.C.,</ConsignorEname>" 
				+"<EntyPortCode>399501</EntyPortCode>" 
				+"<ContTel>5689928</ContTel>" 
				+"<IqRegisterno>3900910115</IqRegisterno>" 
				+"<DeclDate>20160906T15:40:11</DeclDate>" 
				+"<EntyPortName>厦门东渡港区</EntyPortName>" 
				+"<PurpOrgName>东渡局本部</PurpOrgName>" 
				+"<TradeCountryCode>840</TradeCountryCode>" 
				+"<ConsigneeCname>厦门地道赞商业有限公司</ConsigneeCname>" 
				+"<DeclCode>13</DeclCode>" 
				+"<PurpOrgCode>320000</PurpOrgCode>" 
				+"<DeclPersonName>黄黎萍</DeclPersonName>" 
				+"<TradeCountryName>美国</TradeCountryName>" 
				+"<DestCode>350206</DestCode>" 
				+"<DestName>厦门市湖里区</DestName>" 
				+"<BillLadNo>NYKSCHIS51191200_03</BillLadNo>" 
				+"<GdsArvlDate>20160824T00:00:00</GdsArvlDate>" 
				+"<InspOrgName>东渡局本部</InspOrgName>" 
				+"<TransModeCode>1</TransModeCode>" 
				+"<InvoiceNo>090601地道赞</InvoiceNo>" 
				+"<OrigBoxFlag>0</OrigBoxFlag>" 
				+"<DespCtryName>美国</DespCtryName>" 
				+"<DeliveryOrder>NYKSCHIS51191200_03</DeliveryOrder>" 
				+"<InspOrgCode>320000</InspOrgCode>" 
				+"<SoftType>320000</SoftType>" 
				+"<AttaCollectName>合同,发票,报检委托书,装箱单,其他单据,提单/运单</AttaCollectName>" 
				+"<TransMeanNo>263W</TransMeanNo>" 
				+"<SpeclInspQuraRe>ECIQ无纸化报检</SpeclInspQuraRe>" 
				+"<SpecPassFlag>0000</SpecPassFlag>" 
				+"<SpecDeclFlag>0000</SpecDeclFlag>" 
				+"<PrnSign>0</PrnSign>" 
				+"<ConsigneeEname>***</ConsigneeEname>" 
				+"<DespPortCode>840432</DespPortCode>" 
				+"<OrgCode>320000</OrgCode>" 
				+"<DespPortName>波特兰（美国）</DespPortName>" 
				+"<MarkNo>10280000</MarkNo>" 
				+"<ConsigneeCode>3995615923</ConsigneeCode>" 
				+"<ContractNo>SO0348744</ContractNo>" 
				+"<ITF_DCL_IO_DECL_GOODS>"
				+"<GoodsId>"+changeId+"</GoodsId>" 
				+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
				+"<StdWeight>0</StdWeight>" 
				+"<StdWeightUnitCode>075</StdWeightUnitCode>" 
				+"<GoodsNo>1</GoodsNo>" 
				+"<ProdHsCode>9703000090</ProdHsCode>" 
				+"<HsCodeDesc>其他各种材料制的雕塑品原件</HsCodeDesc>" 
				+"<CiqCode>9703000090999</CiqCode>" 
				+"<CiqName>其他各种材料制的雕塑品原件</CiqName>" 
				+"<DeclGoodsCname>海豚喷泉雕塑</DeclGoodsCname>" 
				+"<Qty>2</Qty>" 
				+"<QtyMeasUnit>023</QtyMeasUnit>" 
				+"<Weight>350</Weight>" 
				+"<WtMeasUnit>035</WtMeasUnit>" 
				+"<StdQty>2</StdQty>" 
				+"<GoodsTotalVal>28000</GoodsTotalVal>" 
				+"<Currency>840</Currency>" 
				+"<OriCtryCode>840</OriCtryCode>" 
				+"<Purpose>99</Purpose>" 
				+"<By1>XMSW</By1>" 
				+"<StdQtyUnitCode>023</StdQtyUnitCode>" 
				+"<NoDangFlag>1</NoDangFlag>"
				+"<ITF_DCL_IO_DECL_GOODS_PACK>"
					+"<PackId>"+changeId+"</PackId>" 
					+"<GoodsId>"+changeId+"</GoodsId>" 
					+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
					+"<GoodsNo>1</GoodsNo>" 
					+"<PackTypeCode>4C12</PackTypeCode>" 
					+"<PackCatgName>中木箱</PackCatgName>" 
					+"<PackTypeShort>中木箱</PackTypeShort>" 
					+"<PackQty>2</PackQty>" 
					+"<IsMainPack>1</IsMainPack>" 
				+"</ITF_DCL_IO_DECL_GOODS_PACK>"
			+"</ITF_DCL_IO_DECL_GOODS>"
//				+"<ITF_DCL_IO_DECL_ATT>"
	//				+"<AttId>"+changeId+"</AttId>" 
	//				+"<AttDocTypeCode>101039</AttDocTypeCode>" 
	//				+"<AttDocName>报检委托书</AttDocName>" 
	//				+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
//				+"</ITF_DCL_IO_DECL_ATT>"
//				+"<ITF_DCL_IO_DECL_CONT>"
	//				+"<ContId>"+changeId+"</ContId>" 
	//				+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
	//				+"<CntnrModeCode>111</CntnrModeCode>" 
	//				+"<ContainerQty>1</ContainerQty>" 
	//				+"<LclFlag>1</LclFlag>" 
	//				+"<ContNo>NYKU0832273</ContNo>" 
	//				+"<SeqNo>1</SeqNo>" 
	//				+"<ITF_DCL_IO_DECL_CONT_DETAIL>"
		//				+"<ContDtId>"+changeId+"</ContDtId>" 
		//				+"<ContId>"+changeId+"</ContId>" 
		//				+"<EntDeclNo>"+changeId+"</EntDeclNo>" 
		//				+"<CntnrModeCode>211</CntnrModeCode>" 
		//				+"<ContNo>NYKU0832273</ContNo>" 
		//				+"<LclFlag>1</LclFlag>" 
		//				+"<SeqNo>1</SeqNo>" 
	//				+"</ITF_DCL_IO_DECL_CONT_DETAIL>"
//				+"</ITF_DCL_IO_DECL_CONT>"
				+"</ITF_DCL_IO_DECL>"
				+"</MessageBody>"
				+"</OBORMessage>";
		return XmlData;
	}
	
}
