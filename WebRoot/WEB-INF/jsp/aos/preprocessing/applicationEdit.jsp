<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>
<aos:html>
<aos:head title="办理任务">
	<aos:include lib="ext" />
	<aos:base href="preprocessing/application" />
	<style>
#_div_tips {
	line-height: 25px;
	margin-right: 10px;
}
</style>
</aos:head>
<aos:body>
	<div id="_div_tips" class="x-hidden">
		<ul>
			<li>a) 检测、确认电子文件及其元数据无病毒；</li>
			<li>b) 检测、确认电子文件及其组件的计算机文件格式符合要求；</li>
			<li>c) 检测、确认电子文件标识符、内容与形式特征、物理结构元数据齐全、完整，通过计算机文件
名能够准确实现元数据与电子文件的一一对应。</li>
		</ul>
	</div>
	<div id="_div_img" class="x-hidden" align="center" style="vertical-align: middle;">
		<img id="_img" style="vertical-align: middle; margin:10px;" />
	</div>
</aos:body>
<aos:onready>
	<aos:viewport layout="border">
		<aos:panel region="west" width="250" bodyBorder="0 1 0 0">
			<aos:layout type="vbox" align="stretch" />
			<aos:panel height="100">
				<aos:layout type="vbox" align="stretch" />
				<aos:button text="选择条目" icon="add2.png" scale="medium" margin="10"
					onclick="#_w_data.show();" />
				<aos:button text="保存" icon="ok.png" scale="medium"
					margin="0 10 0 10" onclick="_f_data_save" />
			</aos:panel>
			<aos:panel height="200" layout="fit" onrender="_f_data_render">
				<aos:docked forceBoder="1 0 1 0">
					<aos:dockeditem xtype="tbtext" text="申请人信息" />
				</aos:docked>
				<aos:formpanel id="_f_data" layout="anchor" labelWidth="60" header="false"  icon="user8.png">
					<aos:hiddenfield name="taskid" id="taskid" value="${taskid }"/>
					<aos:hiddenfield name="id" id="formid" value="${formid }"/>
					<aos:textfield name="xm" fieldLabel="申请人"   anchor="99%" />
					<aos:textfield name="idno_" fieldLabel="身份证号"   anchor="99%" />
					<aos:textfield name="sqrq" fieldLabel="申请日期"  anchor="99%" />
					<aos:hiddenfield name="state" value="0"/>
				</aos:formpanel>
			</aos:panel>
			<aos:panel flex="1" layout="fit" contentEl="_div_tips" autoScroll="true">
				<aos:docked forceBoder="1 0 1 0">
					<aos:dockeditem xtype="tbtext" text="注意事项" />
				</aos:docked>
			</aos:panel>
		</aos:panel>
		<aos:panel region="center" border="false">
			<%-- 垂直盒子布局，里面可以放置任意多个固定高度或者自适应高度的组件 --%>
			<aos:layout type="vbox" align="stretch" />
			<aos:gridpanel flex="1" id="_g_center" url="listGridAccounts.jhtml" onrender="_g_center_query" >
				<aos:docked forceBoder="1 1 1 0">
					<aos:dockeditem xtype="tbtext" text="目录信息" />
				</aos:docked>
				<aos:column type="rowno" />
				<aos:column dataIndex="id_" hidden="true"/>
				<c:forEach var="field" items="${fieldDtos}">
				<aos:column dataIndex="${field.fieldenname}"
					header="${field.fieldcnname }" width="${field.dislen }" rendererField="field_type_" />
			</c:forEach>
			<aos:column header="" flex="1"/>
			</aos:gridpanel>
		</aos:panel>

	</aos:viewport>
		
	<aos:window id="_w_data" title="目录信息" width="1000" height="600"
		autoScroll="true" layout="fit">
		<aos:docked>
			<aos:dockeditem text="查询" icon="query.png" onclick="_w_data_query" />
		</aos:docked>
		<aos:gridpanel id="_g_data" url="listData" onrender="_g_data_query">
			<aos:column type="rowno" />
			<aos:column dataIndex="id_" hidden="true" />
			<c:forEach var="field" items="${fieldDtos}">
				<aos:column dataIndex="${field.fieldenname}"
					header="${field.fieldcnname }" width="${field.dislen }"
					rendererField="field_type_" />
			</c:forEach>
			<aos:column header="" flex="1" />
		</aos:gridpanel>
		<aos:docked dock="bottom" ui="footer">
			<aos:dockeditem xtype="tbfill" />
			<aos:dockeditem onclick="_g_data_add" text="添加" icon="icon82.png" />
			<aos:dockeditem onclick="_f_data_alladd" text="全部添加"
				icon="icon82.png" />
			<aos:dockeditem onclick="#_w_data.hide();" text="关闭" icon="close.png" />
		</aos:docked>
	</aos:window>

	<aos:window id="_w_query_q" title="查询" width="720" autoScroll="true"
		layout="fit">
		<aos:formpanel id="_f_query" layout="column" columnWidth="1">
			<aos:hiddenfield name="cascode_id_" />
			<aos:hiddenfield name="tablename" />
			<aos:hiddenfield name="columnnum" id="columnnum" value="7" />
			<c:forEach var="fieldss" items="${fieldDtos}" end="7"
				varStatus="listSearch">
				<aos:combobox name="and${listSearch.count }" value="on"
					labelWidth="10" columnWidth="0.2">
					<aos:option value="on" display="并且" />
					<aos:option value="up" display="或者" />
				</aos:combobox>
				<aos:combobox name="filedname${listSearch.count }"
					emptyText="${fieldss.fieldcnname }" labelWidth="20"
					columnWidth="0.2" fields="['fieldenname','fieldcnname']"
					regexText="${fieldss.fieldenname }" displayField="fieldcnname"
					valueField="fieldenname" url="queryFields.jhtml?tablename=">
				</aos:combobox>
				<aos:combobox name="condition${listSearch.count }" value="like"
					labelWidth="20" columnWidth="0.2">
					<aos:option value="=" display="等于" />
					<aos:option value=">" display="大于" />
					<aos:option value=">=" display="大于等于" />
					<aos:option value="<" display=" 小于" />
					<aos:option value="<=" display="小于等于" />
					<aos:option value="<>" display="不等于" />
					<aos:option value="like" display="包含" />
					<aos:option value="left" display="左包含" />
					<aos:option value="right" display="右包含" />
					<aos:option value="null" display="空值" />
				</aos:combobox>
				<aos:textfield name="content${listSearch.count }" allowBlank="true"
					columnWidth="0.39" />
			</c:forEach>
			<aos:docked dock="bottom" ui="footer">
				<aos:dockeditem xtype="tbfill" />
				<aos:dockeditem onclick="_f_data_query" text="确定" icon="ok.png" />
				<aos:dockeditem onclick="#_w_query_q.hide();" text="关闭"
					icon="close.png" />
			</aos:docked>
		</aos:formpanel>
	</aos:window>	
	<script type="text/javascript">
		//加载center表格数据
		function _g_center_query() {
		var params={
			formid: formid.getValue()
		};
			_g_center_store.getProxy().extraParams=params;
			_g_center_store.load();
		}

		//监听窗口弹出事件
		function _w_max_onshow(){
			document.getElementById('_img').src = "${cxt}/static/image/demo/wxb.jpg";
		}
		//_path列转换
	function fn_path_render(value, metaData, record, rowIndex, colIndex,
				store) {
			if (value >= 1) {
				return '<img src="${cxt}/static/icon/picture.png" />';
			} else {
				return '<img src="${cxt}/static/icon/picture_empty.png" />';
			}
		}
		function _w_query_show(){
		 _w_query_q.show();
		
		}
		function _g_data_query(){
			_g_data.store.load();
		}
		function _w_data_query(){
			_w_query_q.show();
		}
		function _f_data_render(){
			//var taskid = taskid.getValue();
			var formid = Ext.getCmp('formid').getValue();
			AOS.ajax({
				url:'getRemote.jhtml',
				params:{
					formid:formid
				},
				ok:function(data){
					_f_data.form.setValues(data);
				}
			
			})
		}
		function _f_data_save(e){
		var outcome =e.btnEl.dom.innerText;
		AOS.ajax({
			url:'saveSubmitTask.jhtml',
			forms:_f_data,
			params:{
				outcome:outcome,
				state:2
			},
			ok:function(data){
				AOS.tip(data.appmsg);
			}
		})
	}
	function _g_data_add(){
			var record=AOS.select(_g_data);
			_g_center_store.insert(0,record);
		
	}
	function _f_data_query(){
	var params = AOS.getValue('_f_query');
	 var form = Ext.getCmp('_f_query');
	var tmp = columnnum.getValue();
	for(var i=1;i<=tmp;i++){
	var str = form.down("[name='filedname"+i+"']");
	var filedname =str.getValue();
		if(filedname==null){
			params["filedname"+i]=str.regexText;
		}
	} 
			_g_data_store.getProxy().extraParams = params;
			_g_data_store.load();
			_w_query_q.hide();
			AOS.reset(_f_query); 
			
	}
	</script>
</aos:onready>
</aos:html>