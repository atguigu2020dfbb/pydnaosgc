<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<aos:html>
<aos:head title="档案编研">
	<aos:include lib="ext" />
	<aos:base href="research/research" />
</aos:head>
<aos:body>
</aos:body>
<aos:onready>
	<aos:viewport layout="fit">
		<aos:gridpanel id="_g_north" region="north" url="listAccounts.jhtml" onrender="_g_north_render">
			<aos:docked>
				<aos:dockeditem xtype="tbtext" text="档案编研信息" />
				<aos:dockeditem xtype="tbseparator" />
				<aos:dockeditem text="新增"  icon="add.png" onclick="fn_add" />
				<aos:dockeditem text="发布"  icon="go.png" onclick="fnaos_rows_s2" />
				<aos:dockeditem text="取消发布" icon="stop.gif" onclick="fn_rows" />
				<aos:dockeditem text="删除" icon="del.png" onclick="fn_rows" />
			</aos:docked>
			<aos:column type="rowno" />
			<aos:selmodel type="checkbox" mode="multi" />
			<aos:column header="题名" dataIndex="r1"  />
			<aos:column header="状态"  dataIndex="r2" width="80" minWidth="80" maxWidth="80" align="center" />
			<aos:column header="编研类别" dataIndex="r3"  width="60" />
			<aos:column header="摘要" dataIndex="r4" width="140" />
			<aos:column header="创建机构" dataIndex="r5" width="80" />
			<aos:column header="创建人" dataIndex="r6"  />
			<aos:column header="创建日期" dataIndex="r7"   />
			<aos:column header="发布日期" dataIndex="r8"  align="center" width="60" />
		</aos:gridpanel>
		<aos:window id="_w_info" width="800" height="500" layout="fit">
			<aos:formpanel id="_f_info" layout="column" labelWidth="80" padding="10 25 0 0" columnWidth="1" >
				<aos:textfield fieldLabel="题名" name="name_" columnWidth="0.5"  />
				<aos:textfield fieldLabel="编研类别" name="age_"  columnWidth="0.5" />
				<aos:textfield fieldLabel="摘要" name="id_no_" columnWidth="0.5" />
				<aos:textfield fieldLabel="状态" name="org_id_" columnWidth="0.5" />
				<aos:textfield fieldLabel="创建机构" name="balance_" columnWidth="0.5" />
				<aos:textfield fieldLabel="创建人" name="credit_line_" columnWidth="0.5"  />
				<aos:textareafield fieldLabel="兴趣爱好" name="interests_" columnWidth="1" />
				<aos:textfield fieldLabel="附件上传" name="credit_line_" columnWidth="0.4"  />
				<aos:button text="选择文件" margin="0 0 0 10" icon="query.png" />
				<aos:htmleditor  name="remark_" columnWidth="1" />
				<aos:docked dock="bottom" ui="footer">
					<aos:dockeditem xtype="tbfill" />
					<aos:dockeditem xtype="button" text="保存数据" icon="ok.png" id="savedata"/>
					<aos:dockeditem xtype="button" text="删除" icon="del.png" />
					<aos:dockeditem xtype="tbfill" />
				</aos:docked>
			</aos:formpanel>
		
		
		</aos:window>
	</aos:viewport>
	<script type="text/javascript">
	function _g_north_render(){
		_g_north_store.load();
	}
	function fn_add(){
		_w_info.show();
	
	}
	</script>
</aos:onready>
<script type="text/javascript">
	//表格列按钮单击事件
	function fn_column_button_onclick(obj) {
		var record = AOS.selectone(Ext.getCmp('_g_north'));
		AOS.tip("可以获取当前行的任意数据传给后台: " + "[卡号：" + record.data.card_id_ + "]");
		console.log(record);
		//Ext.getCmp('_g_west').getStore().reload();
	}
</script>
</aos:html>