<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<%@ taglib uri="/WEB-INF/tld/cll.tld" prefix="c"%>
<aos:html>
<aos:head title="数据信息">
	<aos:include lib="ext,swfupload" />
	<aos:base href="archive/data" />
</aos:head>
<aos:body>

</aos:body>
<aos:onready>
	<aos:viewport layout="fit">
		<aos:gridpanel id="_g_data" url="listAccounts.jhtml"
			onrender="_g_data_query" pageSize="20" >
			<aos:docked>
				<aos:dockeditem xtype="tbtext" text="打印目录信息" />
				<aos:dockeditem text="新增" id="add" icon="add2.png" onclick="_w_data_show" />
				<aos:dockeditem text="修改" icon="edit2.png" onclick="_w_data_u_show" />
				<aos:dockeditem text="删除" icon="del2.png" onclick="_g_data_del" />
				<aos:dockeditem text="电子文件" icon="picture.png" onclick="_w_picture_show" />
				<aos:dockeditem text="设置录入" icon="picture.png" onclick="_w_input_show" />
				<aos:dockeditem xtype="tbfill" />
				<aos:triggerfield id="tablename" width="200" value="${tablename }" style="display:'none'"/>
				<aos:triggerfield emptyText="内容" id="nrzy"
					onenterkey="_g_data_query" trigger1Cls="x-form-search-trigger"
					onTrigger1Click="_g_data_query" width="200" />
					
			</aos:docked>
			<aos:column dataIndex="id_" header="流水号" hidden="true"/>
			<aos:column dataIndex="_path" header="电子文件" rendererFn="fn_path_render" />
			 <c:forEach var="field" items="${fieldDtos}"> 
		     <aos:column dataIndex="${field.fieldenname }" header="${field.fieldcnname }"  rendererField="field_type_"/>
		   </c:forEach>
			 <aos:column header="" flex="1" />
		</aos:gridpanel>
	</aos:viewport>

	<aos:window id="_w_data" title="新增条目" maxHeight="-10" width="720"
		autoScroll="true">
		<aos:formpanel id="_f_data" width="700" layout="column">
		<c:forEach var="field" items="${fieldDtos}"> 
		     <aos:textfield name="${field.fieldenname }" fieldLabel="${field.fieldcnname }" />
		   </c:forEach>
		</aos:formpanel>
		<aos:docked dock="bottom" ui="footer">
			<aos:dockeditem xtype="tbfill" />
			<aos:dockeditem onclick="_f_data_save" text="保存" icon="ok.png" />
			<aos:dockeditem onclick="#_w_data.hide();" text="关闭"
				icon="close.png" />
		</aos:docked>
	</aos:window>

	<aos:window id="_w_print_u" title="修改目录" onshow="_w_user_u_onshow"
		maxHeight="-10" width="720" autoScroll="true">
		<aos:formpanel id="_f_print_u" width="700" layout="column">
			<aos:hiddenfield fieldLabel="用户流水号" name="id_" />
			<aos:fieldset title="基本信息" labelWidth="75">
				<aos:textfield name="nrzy" fieldLabel="内容摘要" allowBlank="false"
					maxLength="50" columnWidth="0.98" />
				<aos:combobox name="zzdx" fieldLabel="纸张尺寸" emptyText="请选择..."
					value="3" columnWidth="0.49">
					<aos:option value="1" display="A4" />
					<aos:option value="2" display="A3" />
				</aos:combobox>
				<aos:combobox name="fx" fieldLabel="方向" emptyText="请选择..." value="3"
					columnWidth="0.49">
					<aos:option value="1" display="横向" />
					<aos:option value="2" display="纵向" />
				</aos:combobox>
				<aos:combobox name="dsm" fieldLabel="单双面" emptyText="请选择..."
					value="3" columnWidth="0.49">
					<aos:option value="1" display="单面打印" />
					<aos:option value="2" display="双面打印" />
				</aos:combobox>
				<aos:textfield name="ys" fieldLabel="页数" allowBlank="false"
					maxLength="50" columnWidth="0.49" />
				<aos:textfield name="fs" fieldLabel="份数" allowBlank="false"
					maxLength="50" columnWidth="0.49" />
				<aos:combobox name="yans" fieldLabel="颜色" emptyText="请选择..."
					value="3" columnWidth="0.49">
					<aos:option value="1" display="黑白打印" />
					<aos:option value="2" display="彩色打印" />
				</aos:combobox>
				<aos:textareafield name="bz" fieldLabel="备注" maxLength="4000"
					columnWidth="0.99" />
			</aos:fieldset>
		</aos:formpanel>
		<aos:docked dock="bottom" ui="footer">
			<aos:dockeditem xtype="tbfill" />
			<aos:dockeditem onclick="_f_print_u_save" text="保存" icon="ok.png" />
			<aos:dockeditem onclick="#_w_print_u.hide();" text="关闭"
				icon="close.png" />
		</aos:docked>
	</aos:window>
	<script type="text/javascript">
		//加载表格数据
		function _g_data_query() {
			var params = {
				tablename : tablename.getValue()
			};
			//这个Store的命名规则为：表格ID+"_store"。
			_g_data_store.getProxy().extraParams = params;
			_g_data_store.load();
		}


		//获取表格当前行数的API
		function getCount() {
			var count = _g_print_store.getCount();
			console.log(count);
		}

		_g_data.on("cellclick", function(pGrid, rowIndex, columnIndex, e) {
			var record = AOS.selectone(_g_data);
		});

		//弹出新增用户窗口
        function _w_data_show() {
                AOS.reset(_f_data);
               /* var record = AOS.selectone(_t_org);
                if (!AOS.empty(record)) {
                    _f_user.down('[name=org_id_]').setValue(record.raw.id);
                    _f_user.down('[name=org_name_]').setValue(record.raw.text);
                }*/
                _w_data.show();
            }
            
           function _w_input_show() {
           window.parent.fnaddtab('','设置录入','/dbtable/input/initInput.jhtml?tablename='+tablename.getValue());
             
             }
          
         //新增目录保存
			function _f_data_save(){
 				AOS.ajax({
					forms : _f_data,
					url : 'savePrint.jhtml',
					ok : function(data) {
						if (data.appcode === -1) {
                            AOS.err(data.appmsg);
                        } else {
                            _w_data.hide();
                            _g_data_store.reload();
                            AOS.tip(data.appmsg);
                        }	
					}
				});
			}
		
		//zzdx列转换
		function fn_zzdx_render(value, metaData, record, rowIndex, colIndex,
				store) {
			if (value === '1') {
				return 'A4';
			} else if (value === '2') {
				return 'A3';
			} 
		}
		//删除信息
            function _g_data_del() {
                var selection = AOS.selection(_g_data, 'id_');
                if (AOS.empty(selection)) {
                    AOS.tip('删除前请先选中数据。');
                    return;
                }
                var msg = AOS.merge('确认要删除选中的[{0}]个用户数据吗？', AOS.rows(_g_data));
                AOS.confirm(msg, function (btn) {
                    if (btn === 'cancel') {
                        AOS.tip('删除操作被取消。');
                        return;
                    }
                    AOS.ajax({
                        url: 'deleteData.jhtml',
                        params: {
                            aos_rows_: selection,
                            tablename: tablename.getValue()
                        },
                        ok: function (data) {
                            AOS.tip(data.appmsg);
                            _g_data_store.reload();
                        }
                    });
                });
            }
            //监听弹出修改目录窗口事件
            function _w_user_u_onshow() {
                var record = AOS.selectone(_g_print);
                AOS.ajax({
                    params: {id_: record.data.id_},
                    url: 'getPrint.jhtml',
                    ok: function (data) {
                        _f_print_u.form.setValues(data);
                    }
                });
            }
            //弹出修改用户窗口
            function _w_print_u_show() {
                AOS.reset(_f_print_u);
    			if(AOS.selectone(_g_print)){
    				_w_print_u.show();
    	     	}
            }

            //修改目录保存
            function _f_print_u_save() {
                AOS.ajax({
                    forms: _f_print_u,
                    url: 'updatePrint.jhtml',
                    ok: function (data) {
                        if (data.appcode === -1) {
                            AOS.err(data.appmsg);
                            return;
                        }
                        _w_print_u.hide();
                        _g_print_store.reload();
                        AOS.tip(data.appmsg);
                    }
                });
            }
            function _w_data_u_show(){
            var record = AOS.selectone(_g_data);
		}	
		
		//显示上传面板
		function _w_picture_show() {
		var record = AOS.selectone(_g_data);
		var uploadPanel= new Ext.ux.uploadPanel.UploadPanel({
		 addFileBtnText : '选择文件...',
		 uploadBtnText : '上传',
		 deleteBtnText : '移除',
		 removeBtnText : '移除所有',
		 cancelBtnText : '取消上传',
         use_query_string : true, 
         listeners:{  
		//双击  
		itemdblclick : function(grid,row){ 
		parent.fnaddtab(row.data.id, '电子文件',
							'archive/data/openFile.jhtml?id='+row.data.pid+'&tablename='+tablename.getValue());
       }
       },
       onUpload : function(){
       var me=Ext.getCmp("uploadpanel");
		if (this.swfupload&&this.store.getCount()>0) {
            if (this.swfupload.getStats().files_queued > 0) {
                this.showBtn(this,false);
                this.swfupload.uploadStopped = false;	
                this.swfupload.startUpload();
            }
        }
       // this.swfupload.destroy();
        
	},
	deletePath:	function(grid, rowIndex, colIndex) {
	            var me=Ext.getCmp("uploadpanel").getSelectionModel().getSelection();
				var id = me[0].get('pid');
				var tid = me[0].get('tid');
				
				var rowIndex = Ext.getCmp("uploadpanel").getStore().indexOf(me[0]);
                    AOS.ajax({
                    params: {id_:id,
                    tablename:tablename.getValue(),
                    tid:tid
                    }, // 提交参数,
                    url: 'deletePath.jhtml',
                    ok: function (data) {
                    var me=Ext.getCmp("uploadpanel");
                    //me.store.reload();
                    me.store.remove(me.store.getAt(rowIndex));
                    me.store.load();
                    AOS.tip(data.appmsg);
                    }
                });
	},
       onRemoveAll: function (){
        var selection = AOS.selection(_g_data, 'id_');
        AOS.ajax({
                    params: {aos_rows_: selection,
                            tablename: tablename.getValue()
                            },
                    url: 'deletePathAll.jhtml',
                    ok: function (data) {
                    var me=Ext.getCmp("uploadpanel");
                    me.removeAll();
                        AOS.tip(data.appmsg);
                    }
                });
       },
       upload_complete_handler : function(file){
       var me =Ext.getCmp("uploadpanel");
        AOS.ajax({
		params: {tid: record.data.id_,tablename:tablename.getValue()},
		url: 'getPath.jhtml',
		ok: function (data) {
		for(i in data){
		me.store.getAt(file.index).set({"pid":data[i]._pid,"tid":data[i].tid});
		}
		}
		});
    },
       
         post_params:{tid:record.data.id_,tablename:tablename.getValue()},
		 file_size_limit : 10000,//MB
		 flash_url : "${cxt}/static/swfupload/swfupload.swf",
		 flash9_url : "${cxt}/static/swfupload/swfupload_f9.swf",
		 upload_url : "${cxt}/archive/upload/archiveUpload.jhtml"
		 });
	
		var w_data_path = new Ext.Window({
			title : '电子文件',
			width : 700,
			modal:true,
			closeAction : 'destroy',
			items:[uploadPanel]
		});
		w_data_path.on("show",w_data_path_onshow);
		w_data_path.on("close",w_data_path_onclose);
		w_data_path.show();
	}
	function w_data_path_onshow() {
	//var me = this.settings.custom_settings.scope_handler;
	
		var record = AOS.selectone(Ext.getCmp('_g_data'));
		var me=Ext.getCmp("uploadpanel");
		me.store.removeAll();
		AOS.ajax({
		params: {tid: record.data.id_,tablename:tablename.getValue()},
		url: 'getPath.jhtml',
		ok: function (data) {
		for(i in data){
        me.store.add({
        pid:data[i]._pid,
        tid:data[i].tid,
        name:data[i]._path,
        fileName:data[i].filename,
        type:data[i].filetype,
        percent:100,
        status:-4,
        });
		}
		}
		});
	}
	
	function w_data_path_onclose(){
	_g_data_store.load();
	
	}
	
	//_path列转换
	function fn_path_render(value, metaData, record, rowIndex, colIndex,
				store) {
			//metaData.tdAttr = 'data-qtip="'+value+'"';
			if (value >= 1) {
			
				return '<img src="${cxt}/static/icon/picture.png" />';
			} else {
				return '<img src="${cxt}/static/icon/picture_empty.png" />';
			}
		}
	
	</script>
</aos:onready>
</aos:html>