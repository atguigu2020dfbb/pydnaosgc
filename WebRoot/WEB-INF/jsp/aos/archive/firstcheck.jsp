<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/aos.tld" prefix="aos"%>
<%@ taglib uri="/WEB-INF/tld/cll.tld" prefix="c"%>
<%
	String path = request.getContextPath();
%>
<aos:html>
<aos:head title="初检">
	<aos:include lib="ext,swfupload,flexpaper" />
	<aos:base href="archive/data" />
	<aos:include js="${cxt}/static/pdfObject/pdfobject.js" />
</aos:head>
<aos:body>
	<!--html代码  -->
	<div id="documentViewer" style="height: 800px;"></div>
</aos:body>
<aos:onready>
	<aos:viewport layout="border" id="viewport">
		<aos:panel region="west" id="westpanel" bodyBorder="0 1 0 0">
			<aos:hiddenfield id="id_" />
			<aos:hiddenfield id="index" />
			<aos:hiddenfield id="limit" />
			<aos:hiddenfield id="page" />
			<aos:hiddenfield id="query" />
			<aos:hiddenfield id="cascode_id_" />
			<aos:docked forceBoder="1 0 1 0">
				<aos:dockeditem xtype="tbtext" text="功能" />
				<aos:dockeditem text="首条" onclick="firstselect" icon="add.png" />
				<aos:dockeditem text="上条" onclick="lastselect" icon="add.png" />
				<aos:dockeditem text="下条" onclick="nextselect" icon="add.png" />
				<aos:dockeditem text="上页" onclick="lastpage" icon="add.png" />
				<aos:dockeditem text="下页" onclick="nextpage" icon="add.png" />
				<aos:dockeditem text="末条" onclick="endselect" icon="add.png" />
				<aos:dockeditem text="删除" onclick="deletedata" icon="add.png" />
				<aos:dockeditem text="加存" onclick="_f_data_i_save" icon="add.png" />
				<aos:dockeditem text="保存" onclick="_f_data_edit" icon="add.png" />
				<aos:dockeditem text="初检保存" onclick="firstcheck_save" icon="add.png" />
				<aos:dockeditem text="刷新" onclick="refresh" icon="add.png" />
			</aos:docked>
			<aos:tabpanel id="_tabpanel" activeTab="0" bodyBorder="0 0 0 0"
				tabBarHeight="30" plain="false" autoScroll="true">

				<aos:tab title="电子文件" id="_tab_org" contentEl="documentViewer"
					onshow="load_path" autoScroll="true">

				</aos:tab>

				<aos:tab title="数据列表" id="_tab_param">
					<aos:gridpanel id="_g_org" hidePagebar="true" region="center"
						enableLocking="true" onitemclick="fn_g_org" url="listOrgs.jhtml"
						onrender="_g_org_query" pageSize="${pagesize}" forceFit="false">
						
						<aos:column dataIndex="id_" id="idd" header="流水号" hidden="true" />
						<c:forEach var="field" items="${fieldDtos}">
							<aos:column dataIndex="${field.fieldenname}"
								header="${field.fieldcnname }" rendererField="field_type_" />
						</c:forEach>
						<aos:triggerfield id="cascode_id_" width="200"
							value="${cascode_id_ }" style="display:'none'" />
					</aos:gridpanel>
				</aos:tab>
			</aos:tabpanel>
		</aos:panel>
		<aos:panel region="center" border="false" autoScroll="true"
			onrender="_w_data_u_render">
			<aos:formpanel id="_f_data_u" width="700" layout="absolute">
				<aos:docked forceBoder="1 0 1 0">
					<aos:dockeditem xtype="tbtext" text="数据信息" />
				</aos:docked>
			</aos:formpanel>

		</aos:panel>


	</aos:viewport>
	<script type="text/javascript">
		
		window.onload = function() {
			Ext.getCmp("westpanel").setWidth(Ext.getCmp('viewport').getWidth() / 2);			
			//Ext.getCmp("messagepanel").setWidth(Ext.getCmp('westpanel').getHeight()/4);
			//var pdfpath="http://127.0.0.1/Data/dataaos/wsda/2e9275c32e6849afab038892747249c2/0d6d031de7e848ac93feb19c2fb0c4d2.pdf";
			//new PDFObject({ url:pdfpath}).embed("documentViewer");
			//PDFObject.embed(pdfpath, '#documentViewer');
			//alert("<%=request.getAttribute("id_")%>"+"1");
			Ext.getCmp("id_").setValue("<%=request.getAttribute("id_")%>");
			Ext.getCmp("index").setValue("<%=request.getAttribute("index")%>");
			Ext.getCmp("limit").setValue("<%=request.getAttribute("limit")%>");
			Ext.getCmp("page").setValue("<%=request.getAttribute("page")%>");
			Ext.getCmp("query").setValue("<%=request.getAttribute("query")%>");
			Ext.getCmp("cascode_id_").setValue("<%=request.getAttribute("cascode_id_")%>");			
}
		//组件渲染后触发
		function _w_data_u_render(){
			var pdfpath=null;
				_w_data_input('_f_data_u');
				//得到id_值
				var id_ = "<%=request.getAttribute("id_")%>";
				var tablename = "<%=session.getAttribute("tablename")%>";
				//得到角标值
				var index = "<%=request.getAttribute("index")%>";
				setTimeout(function(){
					_w_data_u_show(index,id_);
				},"100");
				}
		//生成录入界面 
		function _w_data_input(formid){
				var tablename = "<%=session.getAttribute("tablename")%>";
				var _panel = Ext.getCmp(formid);
				//每一次生成之前，移除之前有的录入界面
				_panel.removeAll();
					 AOS.ajax({
		                    params: {tablename: tablename},
		                    url: 'getInput.jhtml',
		                    ok: function (data) {
		                    	//data所有文本框和标签的数据
		                     var _panel = Ext.getCmp(formid);
		                    	//迭代文本框和标签
		                    for(var i in data){
		                    var items;
		                    //取最后一位，判断是不是等于L（L代表标签，D代表文本框）
		                   		if(data[i].fieldname.charAt(data[i].fieldname.length - 1)=='L'){
		                   		items=[{   
		                   				//带文本标签
						                xtype : 'label', 
						                //value:data[i].displayname,
						                //标签内容
						                text:data[i].displayname,
						                //取高度和宽度
						                width : parseInt(data[i].cwidth),
										height : parseInt(data[i].cheight),
										//x和y轴坐标，
						                x:parseInt(data[i].cleft)-200,
						                y:parseInt(data[i].ctop)-50,
						            }]
		                   				}else{
		                   	//文本框		
				                  if(data[i].yndic=='1'){
				                 items=[{
				                 name:data[i].fieldname.substring(0,data[i].fieldname.length-1),
				                // id:'yndic',
				                 //fieldLabel: 'ieldLabel',
				                 //下拉选择框
				                 xtype: "combo",  
				                 mode:'local',
				                 //fieldLabel:'数据字典',
				                 x:parseInt(data[i].cleft)-200,
				                 y:parseInt(data[i].ctop)-50,
				                 maxWidth:parseInt(data[i].cwidth),
				                 height:parseInt(data[i].cheight),
				                 //labelWidth:80,
				                 //储存一下数据(数据存储器)
				                 store: new Ext.data.SimpleStore({
				                fields: ["code_", "desc_"],  
					        		proxy: {  
						            type: "ajax",  
						            //params:{"tablename":"3333"},
						            url: "load_dic_index.jhtml?key_name_="+data[i].dic,  
							            actionMethods: {  
							                read: "POST"  //解决传中文参数乱码问题，默认为“GET”提交  
							            },
							            reader: {  
							                type: "json",  //返回数据类型为json格式  
							                root: "root"  //数据  
							            }  
					       		 },  
		        autoLoad: false  //自动加载数据  
		         }),
		                 emptyText:'请选择...',
		                 displayField: 'desc_',
		                 valueField :'desc_',
		                 //hiddenName: 'fieldenname',
		                 }]
		                   }else{
		                	   //如果yndic不为1
		                     items=[{
		                    	 //类型文本框
			                xtype : 'textfield', 
			                //value:data[i].displayname,
			                //style:'border-right: 0px solid;border-top: 0px solid;border-left: 0px solid;border-bottom: #000000 1px solid;',
			                //width : parseInt(data[i].cwidth),
			                //名称扣除最后一位
			                name:data[i].fieldname.substring(0,data[i].fieldname.length-1),
			                maxWidth : parseInt(data[i].cwidth),
							height : parseInt(data[i].cheight),
			                x:parseInt(data[i].cleft)-200,
			                y:parseInt(data[i].ctop)-50,
		            }]
		            }}
		            _panel.add(items);
		                    }
		                    }
		                });
				}
		//组件被显示后触发。			
		function _w_data_u_show(index,id_){
			var tablename = "<%=session.getAttribute("tablename")%>";
			path(id_,tablename,index);
			 AOS.ajax({
                 params: {id_:id_,
                 tablename:tablename
                 },
                 url: 'getData.jhtml',
                 ok: function (data) {
                 	_f_data_u.form.setValues(data);
                 }
             });	
		}
		//获取初检列表条目数据信息
		function _g_org_query(){
			var tablename="<%=session.getAttribute("tablename")%>";
			var cascode_id_="<%=session.getAttribute("cascode_id_")%>";
			var id_=Ext.getCmp("id_").getValue();
			var index=Ext.getCmp("index").getValue();
			var limit=Ext.getCmp("limit").getValue();
			var page=Ext.getCmp("page").getValue();
			var query=Ext.getCmp("query").getValue();
			var cascode_id_=Ext.getCmp("cascode_id_").getValue();
			var params = {
					tablename : tablename,
					cascode_id_:cascode_id_,
					id_:id_,
					index:index,
					limit:limit,
					page:page,
					query:query
				};
				//这个Store的命名规则为：表格ID+"_store"。
				_g_org_store.getProxy().extraParams = params;				
				//_g_org_store.load();
				_g_org_store.load(function(records, operation, success) {
					Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
					});
				//此时让指定数据坐标选中
				//Ext.getCmp("_g_org").getSelectionModel().selectNext();
				//Ext.getCmp("_g_org").getSelectionModel().selectAll(false);
				//alert(this.grid.getSelectionModel().getSelection());
				//Ext.getCmp("_g_org").getSelectionModel().Rows[2].Selected=true;							
		}
		//首条
		function firstselect(){
			//获取当前页面第一行记录，赋给右侧表单
			//并赋给制定表单中
			var tablename="<%=session.getAttribute("tablename")%>";
		        AOS.ajax({
		                    params: {id_: _g_org_store.getAt(0).get("id_"),
		                    tablename:tablename
		                    },
		                    url: 'getData.jhtml',
		                    ok: function (data) {
		                    	_f_data_u.form.setValues(data);
		                    	//为隐藏域id_赋值
		        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(0).get("id_"));
		                    }
		                });
		        //设置grid光标也选择第一行
		        Ext.getCmp("_g_org").getSelectionModel().select(0,true);
		      //加载图片,加载首条
		       path(_g_org_store.getAt(0).get("id_"),tablename,0);
		}
		//末条
		function endselect(){
			//获取当前页面最后一行记录，赋给右侧表单
			//并赋给制定表单中
			var limit=_g_org_store.getCount();
			var tablename="<%=session.getAttribute("tablename")%>";
	        AOS.ajax({
	                    params: {id_: _g_org_store.getAt(limit-1).get("id_"),
	                    tablename:tablename
	                    },
	                    url: 'getData.jhtml',
	                    ok: function (data) {
	                    	_f_data_u.form.setValues(data);
	                    	//为隐藏域id_赋值
	        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(limit-1).get("id_"));
	                    }
	                });
	        //设置grid光标也选择第一行
	        Ext.getCmp("_g_org").getSelectionModel().select(Number(limit)-1,true);
	        //加载图片,加载末条
	        path(_g_org_store.getAt(limit-1).get("id_"),tablename,Number(limit)-1);
		}
		//上一条
		function lastselect(){
			if(typeof(AOS.selectone(_g_org))!='undefined'){
				//此时选中了。
				//判断是不是第一条
				var selectone=AOS.selectone(_g_org);
				var index=_g_org_store.indexOf(selectone);
				//此时是第一行
				if(index==0){
					//上一条不用操作了
					AOS.tip("已经是第一条了！");
				}else{
					var tablename="<%=session.getAttribute("tablename")%>";
			        AOS.ajax({
			                    params: {id_: _g_org_store.getAt(index-1).get("id_"),
			                    tablename:tablename
			                    },
			                    url: 'getData.jhtml',
			                    ok: function (data) {
			                    	_f_data_u.form.setValues(data);
			                    	//为隐藏域id_赋值
			        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(index-1).get("id_"));
			                    }
			                });
			      //选中当前行的上一行
					Ext.getCmp("_g_org").getSelectionModel().selectPrevious();
					var id_=_g_org_store.getAt(index-1).get("id_");
					//此时坐标减一加载图片
					path(id_,tablename,Number(index)-1);
				}
			}
		}
		//下一条
		function nextselect(){
			if(typeof(AOS.selectone(_g_org))!='undefined'){
				//此时选中了。
				//判断是不是最后一条
				var selectone=AOS.selectone(_g_org);
				var index=_g_org_store.indexOf(selectone);
				//此时是最后一行
				if(Number(index)+1>=_g_org_store.getCount()){
					//下一条不用操作了
					AOS.tip("已经是最后一条了！");
				}else{
					var tablename="<%=session.getAttribute("tablename")%>";
			        AOS.ajax({
			                    params: {id_: _g_org_store.getAt(index+1).get("id_"),
			                    tablename:tablename
			                    },
			                    url: 'getData.jhtml',
			                    ok: function (data) {
			                    	_f_data_u.form.setValues(data);
			                    	//为隐藏域id_赋值
			        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(index+1).get("id_"));
			                    }
			                });
			      //选中当前行的上一行
					Ext.getCmp("_g_org").getSelectionModel().selectNext();
					var id_=_g_org_store.getAt(index+1).get("id_");
					//此时坐标加一加载图片
					path(id_,tablename,Number(index)+1);
				}
			}
		}
		//上一页
		function lastpage(){
			//得到当前页码
			var page=Ext.getCmp("page").getValue();
			if(page==1){
				AOS.tip("当前第一页");
			}else{
				var tablename="<%=session.getAttribute("tablename")%>";
				var cascode_id_="<%=session.getAttribute("cascode_id_")%>";
				var id_=Ext.getCmp("id_").getValue();
				var index=Ext.getCmp("index").getValue();
				var limit=Ext.getCmp("limit").getValue();
				var page=Ext.getCmp("page").getValue()-1;
				var query=Ext.getCmp("query").getValue();
				var cascode_id_=Ext.getCmp("cascode_id_").getValue();
				var params = {
						tablename : tablename,
						cascode_id_:cascode_id_,
						id_:id_,
						index:index,
						limit:limit,
						page:page,
						query:query
					};
					//这个Store的命名规则为：表格ID+"_store"。
					_g_org_store.getProxy().extraParams = params;				
					_g_org_store.load(function(records, operation, success) {
						Ext.getCmp("_g_org").getSelectionModel().select(0,true);
						//右侧表单刷新
				        AOS.ajax({
				                    params: {id_: _g_org_store.getAt(0).get("id_"),
				                    tablename:tablename
				                    },
				                    url: 'getData.jhtml',
				                    ok: function (data) {
				                    	_f_data_u.form.setValues(data);
				                    	//为隐藏域id_赋值
				        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(0).get("id_"));
				                    }
				                });
				      //并且电子文件刷新
						path(_g_org_store.getAt(0).get("id_"),tablename,0);
						});
					//设置页赋予新值
					Ext.getCmp("page").setValue(Number(Ext.getCmp("page").getValue())-1);
			}
		}
		//下一页
		function nextpage(){
			//当前页
			var page=Ext.getCmp("page").getValue();
			//总记录数
			var count=_g_org_store.getTotalCount();
			//每页数量
			var pagetotal=_g_org_store.pageSize;
			//计算一共多少页
			var pages=0;
			if(parseInt(count%pagetotal)==0){
				//此时没有余数
				pages=parseInt(count/pagetotal);
			}else{
				//证明有余数
				pages=parseInt(count/pagetotal)+1;
			}
			if(parseInt(page)>=pages){
				AOS.tip("当前最后一页");
			}else{
				var tablename="<%=session.getAttribute("tablename")%>";
				var cascode_id_="<%=session.getAttribute("cascode_id_")%>";
				var id_=Ext.getCmp("id_").getValue();
				var index=Ext.getCmp("index").getValue();
				var limit=Ext.getCmp("limit").getValue();
				var page=Number(Ext.getCmp("page").getValue())+1;
				var query=Ext.getCmp("query").getValue();
				var cascode_id_=Ext.getCmp("cascode_id_").getValue();
				var params = {
						tablename : tablename,
						cascode_id_:cascode_id_,
						id_:id_,
						index:index,
						limit:limit,
						page:page,
						query:query
					};
					//这个Store的命名规则为：表格ID+"_store"。
					_g_org_store.getProxy().extraParams = params;				
					_g_org_store.load(function(records, operation, success) {
						Ext.getCmp("_g_org").getSelectionModel().select(0,true);
						//右侧表单刷新
				        AOS.ajax({
				                    params: {id_: _g_org_store.getAt(0).get("id_"),
				                    tablename:tablename
				                    },
				                    url: 'getData.jhtml',
				                    ok: function (data) {
				                    	_f_data_u.form.setValues(data);
				                    	//为隐藏域id_赋值
				        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(0).get("id_"));
				                    }
				                });
				      //并且电子文件刷新
						path(_g_org_store.getAt(0).get("id_"),tablename,0);
						});
					//设置页赋予新值
					Ext.getCmp("page").setValue(Number(Ext.getCmp("page").getValue())+1);
					
			}
		}
		//删除条目数据
		function deletedata(){
			var tablename="<%=session.getAttribute("tablename")%>";
			var index=Ext.getCmp("index").getValue();
			 var selection = AOS.selection(_g_org, 'id_');
			 var tm = AOS.selection(_g_org, 'tm');
             if (AOS.empty(selection)) {
                 AOS.tip('删除前请先选中数据。');
                 return;
             }
             var msg = AOS.merge('确认要删除选中的[{0}]个用户数据吗？', AOS.rows(_g_org));
             AOS.confirm(msg, function (btn) {
                 if (btn === 'cancel') {
                     AOS.tip('删除操作被取消。');
                     return;
                 }
                 AOS.ajax({
                     url: 'deleteData.jhtml',
                     params: {
                         aos_rows_: selection,
                         tm:tm,
                         tablename: tablename
                     },
                     ok: function (data) {
                         AOS.tip(data.appmsg);
                         //_g_org_store.load();
                         //此时删除后要重新回到页面
                         //获取被选中的坐标                        
                         _g_org_store.load(function(records, operation, success) {
                        	 //此时因为是
     						Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
     						//将右侧表单赋值	
     						//右侧表单刷新
    				        AOS.ajax({
    				                    params: {id_: _g_org_store.getAt(Number(index)).get("id_"),
    				                    tablename:tablename
    				                    },
    				                    url: 'getData.jhtml',
    				                    ok: function (data) {
    				                    	_f_data_u.form.setValues(data);
    				                    	//为隐藏域id_赋值
    				        		        Ext.getCmp("id_").setValue(_g_org_store.getAt(Number(index)).get("id_"));
    				                    }
    				                });
    				      //并且电子文件刷新
    						path(_g_org_store.getAt(Number(index)).get("id_"),tablename,index);
                         });
                         //删除后将右侧表单置为空
                         //_f_data_u.form.reset();
                         //此时把隐藏域id_，置为空
                        //Ext.getCmp("id_").setValue(null);
                     }
                 });
             });
		}
		//修改目录保存
		function _f_data_edit(){
				var id_=Ext.getCmp("id_").getValue();
				var index=Ext.getCmp("index").getValue();
				if(id_==""||id_==null){
					AOS.tip("数据已被删除，不能完成保存操作");
					return;
				}
			    var tablename="<%=session.getAttribute("tablename")%>";
				AOS.ajax({
				forms : _f_data_u,
				url : 'updateData.jhtml',
				params:{
			    tablename : tablename,
			    id:id_
		},
				ok : function(data) {
					if (data.appcode === -1) {
                        AOS.err(data.appmsg);
                    } else {
                        //_w_data_i.hide();
                        //_g_org_store.reload();
                        _g_org_store.load(function(records, operation, success) {
     						Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
     						});
                        AOS.tip(data.appmsg);                      
                    }	
				}
			});
		}
		//卡片新增目录加存
		function _f_data_i_save(){
			var tablename="<%=session.getAttribute("tablename")%>";
			var index=Ext.getCmp("index").getValue();
			var _classtree=Ext.getCmp("cascode_id_").getValue();
				AOS.ajax({
				forms : _f_data_u,
				url : 'saveData.jhtml',
				params:{
					tablename : tablename,
					_classtree:_classtree
				},
				ok : function(data) {
					if (data.appcode === -1) {
                        AOS.err(data.appmsg);
                    } else {
                        //_w_data_i.hide();
                        //_w_data_input('_f_data_u');
                        AOS.tip(data.appmsg);
                       // _g_org_store.reload(); 
                        _g_org_store.load(function(records, operation, success) {
     						Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
     						});
                        
                    }	
				}
			});
		}
		//响应函数单击事件
		//表格单击事件
		function fn_g_org(obj, record) {
			_f_data_u.loadRecord(record);
			//此时把id_重新赋值
			Ext.getCmp("id_").setValue(AOS.selection(_g_org, 'id_').split(",")[0]);
		}
		//初检保存
		function firstcheck_save(){
			var id_=Ext.getCmp("id_").getValue();
			if(id_==""||id_==null){
				AOS.tip("数据已被删除，不能完成初检保存操作");
				return;
			}
			var tablename="<%=session.getAttribute("tablename")%>";
			var _classtree=Ext.getCmp("cascode_id_").getValue();
				AOS.ajax({
				forms : _f_data_u,
				url : 'firstchecksaveData.jhtml',
				params:{
					tablename : tablename,
					_classtree:_classtree,
					id:id_
				},
				ok : function(data) {
					if (data.appcode === -1) {
                        AOS.tip("_cjr不存在或_cjrq不存在");
                    } else {
                        //_w_data_i.hide();
                        //_w_data_input('_f_data_u');
                        AOS.tip(data.appmsg);
                        //_g_org_store.reload(); 
                        var index=Ext.getCmp("index").getValue();
                        _g_org_store.load(function(records, operation, success) {
     						Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
     						});
                        
                    }	
				}
			});
		}
		//刷新
		function refresh(){
			//重新加载
			    var page=Ext.getCmp("page").getValue();
				var tablename="<%=session.getAttribute("tablename")%>";
				var cascode_id_="<%=session.getAttribute("cascode_id_")%>";
				var id_=Ext.getCmp("id_").getValue();
				var index=Ext.getCmp("index").getValue();
				var limit=Ext.getCmp("limit").getValue();
				var query=Ext.getCmp("query").getValue();
				var cascode_id_=Ext.getCmp("cascode_id_").getValue();
				var params = {
						tablename : tablename,
						cascode_id_:cascode_id_,
						id_:id_,
						index:index,
						limit:limit,
						page:page,
						query:query
					};
					//这个Store的命名规则为：表格ID+"_store"。
					_g_org_store.getProxy().extraParams = params;				
					 _g_org_store.load(function(records, operation, success) {
  						Ext.getCmp("_g_org").getSelectionModel().select(Number(index),true);
  						AOS.ajax({
		                    params: {id_:id_,
		                    tablename:tablename
		                    },
		                    url: 'getData.jhtml',
		                    ok: function (data) {
		                    	_f_data_u.form.setValues(data);
		                    }
		                });
		        //电子文件页面重新加载
		        path(id_,tablename,index);
  						});
			        
		}
		//电子文件
		function load_path(){
			var tablename="<%=session.getAttribute("tablename")%>";
			var id_ = Ext.getCmp("id_").getValue();
			var index = Ext.getCmp("index").getValue();
			path(id_, tablename, index);
		}
		//电子文件加载方法
		function path(id_, tablename, index) {
			AOS.ajax({
						params : {
							id_ : id_,
							tablename : tablename,
							index : index
						},
						url : 'getfirstcheckData.jhtml',
						ok : function(data) {
							//将id值赋给hiddlen中
							Ext.getCmp("id_").setValue(id_);
							Ext.getCmp("index").setValue(index);
							if (data.pdfpath == "" || data.pdfpath == null) {
								document.getElementById('documentViewer').innerHTML = "";
							} else {
								//new PDFObject({ url:data.pdfpath}).embed("documentViewer");
								PDFObject.embed(data.pdfpath, '#documentViewer');
							}
						}
					});
		}
	</script>
</aos:onready>
<script type="text/javascript">
	
</script>
</aos:html>