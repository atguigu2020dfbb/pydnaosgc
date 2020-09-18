<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<aos:html>
<aos:head title="档案统计">
	<aos:include lib="ext" />
	<aos:base href="archive/statistics" />
</aos:head>
<aos:body>
</aos:body>
<aos:onready>
	<aos:viewport layout="fit">
		<aos:gridpanel id="_g_statistics" url="liststatistics.jhtml"
			onrender="_g_statistics_query">
			<aos:docked>
				<aos:dockeditem xtype="tbtext" text="档案统计分析" />
				<aos:dockeditem xtype="tbseparator" />
				<aos:dockeditem text="导出Excel" icon="more/document-export-4.png"
					onclick="fn_export_statistics" />
					<aos:dockeditem text="详情" icon="query.png"
					onclick="select" />
			</aos:docked>
			<aos:column type="rowno" />
			<aos:column header="流水号" dataIndex="id" hidden="true" align="center" />
			<aos:column header="档案门类" dataIndex="category" width="60"
				align="center" />
			<aos:column header="案卷数" dataIndex="ajs" width="60"
				align="center" />
			<aos:column header="文件数" dataIndex="wjs" width="60"
				align="center" />
			<aos:column header="案卷页数" dataIndex="ajys" width="60"
				align="center" />
		</aos:gridpanel>
	</aos:viewport>
	<aos:window id="_w_statistics" title="详情" width="800"
			autoScroll="true">
			<aos:formpanel id="_f_statistics" width="780" layout="column"
				labelWidth="75">
				<aos:textfield fieldLabel="档案门类" name="category" columnWidth="1.0"
					readOnly="true" />
				<aos:textfield fieldLabel="案卷数" name="ajs" columnWidth="1.0"
					readOnly="true" />
				<aos:textfield fieldLabel="文件数" name="wjs" readOnly="true"
					columnWidth="1.0" />
				<aos:textfield fieldLabel="案卷页数" name="ajys" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="10年" name="bgqx10" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="30年" name="bgqx30" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="永久" name="bgqxy" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="形成单位" name="xcdw" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="形成时间" name="xcsj" columnWidth="1.0" readOnly="true"/>
				<aos:textfield fieldLabel="类型" name="lx" columnWidth="1.0" readOnly="true"/>
			</aos:formpanel>
			<aos:docked dock="bottom" ui="footer">
				<aos:dockeditem xtype="tbfill" />
				<aos:dockeditem onclick="#_w_statistics.hide();" text="关闭"
					icon="close.png" />
			</aos:docked>
		</aos:window>	
	<script type="text/javascript">
		function _g_statistics_query(){
			_g_statistics_store.load();
		}
		//导出日志
		function fn_export_statistics(){
			AOS.ajax({
				url : 'fillReport.jhtml',
				ok : function(data) {
					AOS.file('${cxt}/report/xls2.jhtml');
				}
			});
		}
		//查看详情
		function select(){
			var selection=AOS.selection(_g_statistics, 'category');
			 if (AOS.empty(selection)) {
		            AOS.tip('选中数据');
		            return;
		        }
			AOS.selection(_g_statistics, 'category').split(",")[0];
			 var form = Ext.getCmp('_f_statistics');
				
				form.down("[name='category']").setValue(AOS.selection(_g_statistics, 'category').split(",")[0]);
				form.down("[name='ajs']").setValue(AOS.selection(_g_statistics, 'ajs').split(",")[0]);
				form.down("[name='wjs']").setValue(AOS.selection(_g_statistics, 'wjs').split(",")[0]);
				form.down("[name='ajys']").setValue(AOS.selection(_g_statistics, 'ajys').split(",")[0]);
				form.down("[name='bgqx10']").setValue(10);
				form.down("[name='bgqx30']").setValue(30);
				form.down("[name='bgqxy']").setValue(80);
				form.down("[name='lx']").setValue("电子档案");
				form.down("[name='xcdw']").setValue("档案局");
				form.down("[name='xcsj']").setValue("2019");
				_w_statistics.show();
			
		}
	</script>
</aos:onready>
</aos:html>