package cn.osworks.aos.system.modules.controller.archive;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.dao.SqlDao;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tablenamePO;
import cn.osworks.aos.system.modules.dao.vo.UserInfoVO;
import cn.osworks.aos.system.modules.service.archive.DataService;
import cn.osworks.aos.system.modules.service.archive.SharpService;
import cn.osworks.aos.web.report.AOSReport;
import cn.osworks.aos.web.report.AOSReportModel;
/**
 * 档案利用
 * 
 * @author PX
 *
 * 2018-8-23
 */
@Controller
@RequestMapping(value="archive/sharp")
public class SharpController {
	@Autowired
	private SharpService sharpService;
	@Autowired
	private DataService dataService;
	@Autowired
	private SqlDao sysDao;
	/**
	 * 页面初始化
	 * 
	 * @author PX
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 *
	 * 2018-8-23
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("initInput")
	public String initInput(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws UnsupportedEncodingException{
		Dto dto=Dtos.newDto(request);
		String listtablename=dto.getString("listtablename");
		//listtablename="wsda";
		String tablenamedesc=dto.getString("tablenamedesc");
		tablenamedesc=URLDecoder.decode(tablenamedesc,"utf-8");
		if(listtablename==null||listtablename.equals("")||listtablename.equals("null")){
			return "aos/archive/sharp.jsp?time="+new Date().getTime();
		}
		List<Archive_tablefieldlistPO> title = sharpService.getDataFieldListTitle(listtablename);
		request.setAttribute("listtablename", listtablename);
		request.setAttribute("tablenamedesc", tablenamedesc);
		request.setAttribute("fieldDtos", title);
		return "aos/archive/sharp.jsp?time="+new Date().getTime();
	}
	/**
	 * 显示数据
	 * 
	 * @author PX
	 * @param request
	 * @param response
	 *
	 * 2018-8-23
	 */
	@RequestMapping(value="listAccounts")
	public void listAccounts(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		//标题列表
		String listtablename=inDto.getString("listtablename");
		if(listtablename.equals("null")||listtablename.equals("")||listtablename==null){
			return;
		}
		inDto.put("tablename", inDto.getString("listtablename"));
		List<Dto> fieldDtos = sharpService.getDataFieldListDisplayAll(inDto);
		int pCount = dataService.getPageTotal(inDto);
		//条目数据
		String outString = AOSJson.toGridJson(fieldDtos, pCount);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
		
	}
	/**
	 * 选择表
	 * 
	 * @author PX
	 * @param session
	 * @param request
	 * @param response
	 *
	 * 2018-8-20
	 */
	@RequestMapping(value="listTablename")
	public void listTablename(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		List<Archive_tablenamePO> list = sharpService.findTablename();
		String outString =AOSJson.toGridJson(list);
		WebCxt.write(response, outString);
	}
	/**
	 * 传递用户
	 * 
	 * @author PX
	 * @param session
	 * @param request
	 * @param response
	 *
	 * 2018-8-20
	 * @throws ParseException 
	 */
	@RequestMapping(value="jymessage")
	public void jymessage(HttpSession session,HttpServletRequest request,HttpServletResponse response) throws ParseException{
		Dto outdto = Dtos.newDto(request);
		Dto dto = sharpService.jymessage(outdto,session);
		WebCxt.write(response, AOSJson.toJson(dto));
	}
	/**
	 * 借阅保存
	 * 
	 * @author PX
	 * @param session
	 * @param request
	 * @param response
	 *
	 * 2018-8-29
	 */
	@RequestMapping("savejy")
	public void savejy(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Dto outDto=Dtos.newDto(request);
		Dto out=Dtos.newDto();
		try {
			if(outDto.getString("archivestate").equals("未借阅")){
				boolean b = sharpService.savejy(outDto);
				if(!b){
					out.setAppCode(-2);
				}else{
					out.setAppCode(1);
				}
			}else{
				sharpService.updatejy(outDto);
				out.setAppCode(-1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			out.setAppCode(1);
		}
		WebCxt.write(response, AOSJson.toJson(out));
	}
	/**
	 * 归还操作
	 * 
	 * @author PX
	 * @param session
	 * @param request
	 * @param response
	 *
	 * 2018-8-30
	 */
	@RequestMapping("returnjy")
	public void returnjy(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Dto outDto=Dtos.newDto(request);
		Dto out=Dtos.newDto();
		int i = sharpService.returnjy(outDto,session);
		if(i==1){
			out.setAppCode(1);
		}else if(i==-1){
			out.setAppCode(-1);
		}else{
			out.setAppCode(-2);
		}
		WebCxt.write(response, AOSJson.toJson(out));
	}
	/**
	 * 
	 * 下拉列表
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="listComboBoxid")
	public void listComboBoxid(HttpServletRequest request,HttpServletResponse response){
		Dto dto = Dtos.newDto(request);
		List<Dto> list = sysDao.list("Resource.listComboBoxid", dto);
		String outString = AOSJson.toGridJson(list);
		WebCxt.write(response, outString);
	}
	/**
	 * 借阅里列表
	 * 
	 * @author PX
	 * @param session
	 * @param request
	 * @param response
	 *
	 * 2018-8-30
	 */
	@RequestMapping("listjy")
	public void listjy(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Dto dto=Dtos.newDto(request);
		List<Map<String,Object>> list = sharpService.listjy(dto);
		String outString="";
		if(list!=null&&list.size()>0){
			outString = AOSJson.toGridJson(list,list.size());
		}
		WebCxt.write(response, outString);
	}
	/**
	 * 导出日志操作
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("fillReport")
	public void fillReport(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		// inDto包装了全部的请求参数哦
		Dto qDto = Dtos.newDto(request);
		//设置表头
		LinkedHashMap<String,Object> lhm=new LinkedHashMap<String, Object>();
		lhm.put("id_", "id_");
		lhm.put("users", "用户");
		lhm.put("jytime", "借阅时间");
		lhm.put("archive_id", "档案号");
		lhm.put("tablename", "操作门类");
		lhm.put("jyday", "借阅天数");
		lhm.put("archivestate", "借阅状态");
		lhm.put("gh", "是否归还");
		List<Map<String, Object>> titleDtos = sharpService.listjy(qDto);
		// 组装报表数据模型
		AOSReportModel reportModel = new AOSReportModel();
		reportModel.setFileName("借阅信息表");
		// 设置报表集合
		reportModel.setLogsList(titleDtos);
		reportModel.setLogHeader(lhm);
		// 填充报表
		// AOSPrint aosPrint = AOSReport.fillReport(reportModel);
		// aosPrint.setFileName("excel表");
		session.setAttribute(AOSReport.DEFAULT_AOSPRINT_KEY, reportModel);
		WebCxt.write(response, AOSJson.toJson(Dtos.newOutDto()));
	}
}
