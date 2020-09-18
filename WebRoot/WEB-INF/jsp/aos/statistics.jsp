<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<aos:html>
<aos:head title="利用统计">
	<aos:include lib="ext" />
	<aos:base href="archive/statistics" />
</aos:head>
<aos:body>
</aos:body>
<aos:onready>
	<aos:viewport layout="fit">
		<aos:gridpanel id="_g_statistics" url="listmessagestatistics.jhtml"
			onrender="_g_statistics_query">
			<aos:docked>
				<aos:dockeditem xtype="tbtext" text="服务监控分析" />
				<aos:dockeditem xtype="tbseparator" />
					
			</aos:docked>
			<aos:column type="rowno" />
			<aos:column header="流水号" dataIndex="id" hidden="true" align="center" />
			<aos:column header="利用人" dataIndex="lyr" width="60"
				align="center" />
			<aos:column header="利用目的" dataIndex="lymd" width="60"
				align="center" />
			<aos:column header="档案件" dataIndex="daj" width="60"
				align="center" />
			<aos:column header="时间" dataIndex="sj" width="60"
				align="center" />
			<aos:column header="用户评价" dataIndex="yhpj" width="60"
				align="center" />
		</aos:gridpanel>
			
	</aos:viewport>
	
	<script type="text/javascript">
		function _g_statistics_query(){
			_g_statistics_store.load();
		}
	</script>
</aos:onready>
</aos:html>