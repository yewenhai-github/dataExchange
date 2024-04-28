//var p = 'http://localhost:8080/nabr/1024/';
var p = 'http://39.97.226.128:8080/nabr/1024/';

function Photo(INDX)
{
	Url = this.location.href.substr(0, this.location.href.lastIndexOf('/') + 1) + 'forms/flow/PhotoShow.html?INDX='+INDX;
	$('#Photo').removeData()
    $('#Photo').window({
        title: '照片',
        width: $(window).width()*0.6,
        modal: true,
        shadow: true,
        closed: true,
        height: $(window).height(),
       // content: "<img alt=\"\" src=\"flow/DownloadPhoto?INDX=" + INDX + "\" >",
        content:createFrame(Url),
        draggable: true,
        resizable: false,
        minimizable: false,
        maximizable: false,
        collapsible: false
    });
    $('#Photo').window('open');
    $('#Photo').show();    
}
function PopShipWin(ValueId, TextId,Type) {
	switch(Type)
	{
	case 'ORGANIZATION':
		PopShip('/forms/auth/sauthorgedit.html?ValueId=' + ValueId ,'信息新增',500,300,this);
		break;
	case 'ORGANIZATIONS':
		PopShip('/forms/auth/sauthorgedit.html?ValueId=' + ValueId ,'信息编辑',500,300,this);
		break;
	case 'ALLMENU':
		PopShip('/forms/auth/sauthmenuedit.html?ValueId=' + ValueId ,'菜单新增',600,300,this);

		break;
	case 'ALLMENUS':
		PopShip('/forms/auth/sauthmenuedit.html?ValueId=' + ValueId ,'菜单编辑',500,350,this);
		break;
	case 'PASSWORD':
		PopShip('/forms/auth/passwordedit.html' ,'密码修改',500,300,this);
		break;
	case 'PROJECT':
		PopShip('/forms/flow/ProjectEdit_1.html?ValueId=' + ValueId ,'项目信息',800,520,this);
		break;
	case 'RISK':
		PopShip('/forms/flow/RiskMonitoringEdit.html?INDX=' + ValueId ,'风险监控信息',800,520,this);
		break;
	case 'WORKCHANGE':
		PopShip('/forms/flow/WorkChangeEdit.html?INDX=' + ValueId ,'工作量变更信息',800,600,this);
		break;
	case 'STAKEHOLDER':
		PopShip('/forms/flow/StakeHolder_Edit.html?ValueId=' + ValueId ,'干系人信息',800,450,this);
		break;
	case 'CUSTOMER':
		PopShip('/forms/presales/CustomerEdit.html?ValueId=' + ValueId ,'客户管理',800,380,this);
		break;
	case 'BUSINESS':
		PopShip('/forms/presales/BusinessOpportunity_Edit.html?ValueId=' + ValueId ,'商业机会',800,500,this);
		break;
	case 'Milepost':
		PopShip('/forms/flow/Milepost_Edit.html?ValueId=' + ValueId ,'里程碑',800,430,this);
		break;
	case 'DYNAMICS':
		PopShip('/forms/flow/ProjectDynamics_Edit.html?INDX=' + ValueId ,'项目动态',800,500,this);
		break;
	case 'PEOPLE':
		PopShip('/forms/flow/TeamPeopleAddList.html?ValueId=' + ValueId ,'选择团队成员',800,500,this);
		break;
	case 'PEOPLEEDIT':
		PopShip('/forms/flow/TeamPeopleEdit.html?ValueId=' + ValueId ,'团队成员信息',800,500,this);
		break;
		
	case 'PROJECT_CHOISE':
		PopShip('/forms/flow/Pop_ProjectChoice.html?ValueId=' + ValueId + '&TextId='+TextId,'项目查询',800,550,this);
		break;
	case 'PROJECT_CHOISEP':
		PopShipById('/forms/flow/Pop_ProjectChoice.html?FLAG=PP&ValueId=' + ValueId + '&TextId='+TextId,'项目查询',800,550,this,'Pop_ShipP');
		break;
	case 'PROJECT_CHOISEP2':
		PopShipById('../../forms/flow/Pop_ProjectChoice.html?FLAG=PP&ValueId=' + ValueId + '&TextId='+TextId,'项目查询',800,550,this,'Pop_ShipP');
		break;
	case 'PARTNER':
		PopShip('/forms/finance/Pop_Partner.html?ValueId=' + ValueId + '&TextId='+TextId,'费用对象',900,550,this);
		break;
	case 'PARTNERP':
		PopShipById('/forms/finance/Pop_Partner.html?FLAG=PP&ValueId=' + ValueId + '&TextId='+TextId,'费用对象',900,420,this,'Pop_ShipP');
		break;
	case 'HREFCODEDICT':
		PopShip('/forms/base/Pop_CodeDictList.html?ValueId=' + ValueId ,TextId+'管理',800,600,this);
		break;
	case 'HREFCOLUMNREMARK':
		PopShip('/forms/base/Pop_ColumnremarkList.html?ValueId=' + ValueId ,TextId+'管理',800,600,this);
		break;
	case 'DAILYREPORT':
		PopShip('../../pages/pmo/DailyReport_Edit.html?ValueId=' + ValueId ,'日报详情',800,480,this);
		break;
	case 'DingTalk':
		PopShip('/forms/base/DingTalkEdit.html?ValueId=' + ValueId ,'钉钉消息详情',800,480,this);
		break;
	case 'Contract':
		PopShip('/forms/flow/ContractEdit.html?INDX=' + ValueId ,'合同管理',800,480,this);
		break;
	case 'AccountEdit':
		PopShip('/forms/flow/AccountReceivableEdit.html?INDX=' + ValueId ,'应收款管理',800,480,this);
		break;
	case 'CostTargetEdit':
		PopShip('/forms/flow/CostTargetEdit.html?INDX=' + ValueId ,'成本目标',800,480,this);
		break;
	}  
}
function PopShipWinC(TextId,LinkerId,PhoneId,Type){
	switch(Type){
	case 'PARTNER':
		PopShip('/forms/flow/Pop_Partner.html?PhoneId='+PhoneId+'&LinkerId='+LinkerId+'&TextId='+TextId,'客户查询',900,620,this);
		break;
	}
}

function RowPopWin( rowIndx, FieldName, ValueId, DataId,Type) {
	switch(Type)
	{
	case 'SHIP':
		PopShip('/forms/flow/Pop_Ship.html?rowIndx=' + rowIndx + '&FieldName=' + FieldName + '&ValueId=' + ValueId + '&TextId=' + DataId,'船名航次查询',750,520,this);
		break;
	case 'PORT':
		PopShip('/forms/flow/Pop_Port.html?rowIndx=' + rowIndx + '&FieldName=' + FieldName + '&ValueId=' + ValueId + '&TextId=' + DataId,'港口查询',750,520,this);
		break;
	case 'PARTNER':
		PopShip('/forms/flow/Pop_Partner.html?rowIndx=' + rowIndx + '&FieldName=' + FieldName + '&ValueId=' + ValueId + '&TextId=' + DataId,'客户查询',900,620,this);
		break;
	}  
}

function ContentPopWin(ValueId,TextId,Type){
	switch(Type){
	case 'COMMENTSP':
		PopShipById('/forms/flow/Pop_Comments.html?INDX=' + ValueId + '&FLAG=' + TextId,'评论',530,300,this,'Pop_ShipP');
		break;
	
	case 'ALM':
		PopShipById('/forms/flow/Pop_ALMComments.html?INDX=' + ValueId,'注释',530,300,this,'Pop_ShipP');
		break;
	}
}


//页面左下角显示文本框注释
function ShowControlNotes(PageName){
	$.ajax({
        url:'../../auth/GetColumnRemark',
        type:"POST",
        dataType:"json",
        data:{FORMNAME:PageName},
        success:function(result){  
      	     for(var i =0;i<result.rows.length;i++)
      	     {
      	       var id =	result.rows[i].CONTRALNAME;
      	       var remark = result.rows[i].REMARK;		      	       
      	       $("#" + id).parent().attr("data-tip",remark);
      	       $("#" + id).parent().on('click',function(){
      	    	 parent.$("#remark").html($(this).attr("data-tip"));
      	       });
      	       $("#" + id).parent().on('mouseout',function(){
      	    	 parent.$("#remark").html("");
      	       });
      	     }
        }
 });
}

