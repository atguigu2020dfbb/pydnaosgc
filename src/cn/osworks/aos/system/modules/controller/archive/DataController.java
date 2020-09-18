package cn.osworks.aos.system.modules.controller.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.PageData;

import net.sf.jasperreports.engine.JRException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.DocumentException;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.dao.SqlDao;
import cn.osworks.aos.core.id.AOSId;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.demo.dao.mapper.Demo_accountMapper;
import cn.osworks.aos.system.dao.LogUtils;
import cn.osworks.aos.system.dao.mapper.Aos_sys_dic_indexMapper;
import cn.osworks.aos.system.dao.mapper.Archive_tableinputMapper;
import cn.osworks.aos.system.dao.mapper.LogMapper;
import cn.osworks.aos.system.dao.po.Aos_sys_dicPO;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.dao.po.Archive_tableinputPO;
import cn.osworks.aos.system.dao.po.LogPO;
import cn.osworks.aos.system.modules.dao.vo.UserInfoVO;
import cn.osworks.aos.system.modules.service.archive.DataService;
import cn.osworks.aos.system.modules.service.archive.DocService;
import cn.osworks.aos.system.modules.service.archive.UploadService;
import cn.osworks.aos.system.modules.service.dbtable.InputService;
import cn.osworks.aos.web.report.AOSReport;
import cn.osworks.aos.web.report.AOSReportModel;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * 数据页面控制器
 * 
 * @author shq
 *
 * @date 2016-9-14
 */
@Controller
@RequestMapping(value="archive/data")
public class DataController {
	@Autowired
	private DataService dataService;
	@Autowired
	private LogMapper logMapper;

	@Autowired
	private DocService docService;
	
	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private InputService inputService;
	@Autowired
	private Archive_tableinputMapper archive_tableinputMapper;
	
	@Autowired
	private Aos_sys_dic_indexMapper aos_sys_dic_indexMapper;		
	@Autowired
	private SqlDao sysDao;
	@Resource(name = "redisUtils")
	private RedisUtils redisUtils;
	@Autowired
	private Demo_accountMapper demo_accountMapper;
	public static String filePath="";
	public static String firstAddress = "";
	//通过静态代码块读取配置文件
			static {
				try {
					Properties props = new Properties();
					InputStream in = DataService.class
							.getResourceAsStream("/config.properties");
					props.load(in);
					filePath = props.getProperty("filePath");
					firstAddress = props.getProperty("firstAddress");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	/**
	 * 
	 * 页面初始化
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="initData")
	public String initData(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		UserInfoVO userInfoVO =WebCxt.getUserInfo(session);
		
		String pagesize = userInfoVO.getPagesize()+"";
		if(pagesize==null){
			pagesize="20";
		}
		String queryclass = dataService.isAll(qDto);
		request.setAttribute("cascode_id_", qDto.getString("cascode_id_"));
		request.setAttribute("tablename", qDto.getString("tablename"));
		session.setAttribute("tablename", qDto.getString("tablename"));
		session.setAttribute("queryclass", queryclass);
		request.setAttribute("fieldDtos", titleDtos);
		request.setAttribute("pagesize", pagesize);
		return "aos/archive/data.jsp";
	}
	
	
	
	/**
	 * 
	 * 页面初始化
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="initZpData")
	public String initZpData(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		UserInfoVO userInfoVO =WebCxt.getUserInfo(session);
		
		String pagesize = userInfoVO.getPagesize()+"";
		if(pagesize==null){
			pagesize="20";
		}
		request.setAttribute("cascode_id_", qDto.getString("cascode_id_"));
		request.setAttribute("tablename", qDto.getString("tablename"));
		request.setAttribute("fieldDtos", titleDtos);
		request.setAttribute("pagesize", pagesize);
		return "aos/archive/zpdata.jsp";
	}
	
	/**
	 * 
	 * 页面初始化
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	@RequestMapping(value="aa")
	public String aaa(HttpServletRequest request,HttpServletResponse response) throws DocumentException, IOException{
		
		
	//	uploadService.addWaterMark();
//		Dto qDto = Dtos.newDto(request);
//		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
//		request.setAttribute("cascode_id_", qDto.getString("cascode_id_"));
//		request.setAttribute("tablename", qDto.getString("tablename"));
//		request.setAttribute("fieldDtos", titleDtos);
		return "aos/archive/aa.jsp";
	}
	
	@RequestMapping(value="bb")
	public String bb(HttpServletRequest request,HttpServletResponse response) throws DocumentException, IOException{
		
		
	//	uploadService.addWaterMark();
//		Dto qDto = Dtos.newDto(request);
//		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
//		request.setAttribute("cascode_id_", qDto.getString("cascode_id_"));
//		request.setAttribute("tablename", qDto.getString("tablename"));
//		request.setAttribute("fieldDtos", titleDtos);
		return "aos/archive/bb.jsp";
	}
	
	/**
	 * 
	 * 查询数据信息(原始)
	 * 
	 * @param request
	 * @param response
	 */
	/*@RequestMapping(value="listAccounts")
	public void listAccounts(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		inDto.put("tablename", inDto.getString("tablename"));
		String queryclass = dataService.isAll(inDto);
		inDto.put("queryclass", queryclass);
		request.getSession().setAttribute("querySession", dataService.queryConditions(inDto));
		List<Dto> fieldDtos = dataService.getDataFieldListDisplayAll(inDto);
		int pCount = dataService.getPageTotal(inDto);
		String outString = AOSJson.toGridJson(fieldDtos, pCount);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}*/
	/**
	 * 
	 * 查询数据信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="listAccounts")
	public void listAccounts(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		Dto inDto = Dtos.newDto(request);
		if(inDto.getString("appraisal").equals("1")){
			List<Map<String,Object>> sj = dataService.findSj(inDto);
			// 存入查询到的个数
			int count = sj.size();
			String outString = AOSJson.toGridJson(sj, count);
			//session域中存入鉴定标记
			session.setAttribute("appraisal", "1");
			//添加日志
			UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
			String tm=inDto.getString("tm");
			LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "鉴定档案["+inDto.getString("tablename")+"]", tm, "鉴定", request.getRemoteAddr(), new Date());
			logMapper.insert(logPO);
			// 就这样返回转换为Json后返回界面就可以了。
			WebCxt.write(response, outString);

		}else{
			inDto.put("tablename", inDto.getString("tablename"));
			String queryclass = dataService.isAll(inDto);
			inDto.put("queryclass", queryclass);
			request.getSession().setAttribute("querySession", dataService.queryConditions(inDto));
			List<Dto> fieldDtos = dataService.getDataFieldListDisplayAll(inDto);
			int pCount = dataService.getPageTotal(inDto);
			String outString = AOSJson.toGridJson(fieldDtos, pCount);
			//将分类坐标存放到session中
			session.setAttribute("queryclass", queryclass);
			// 就这样返回转换为Json后返回界面就可以了。
			WebCxt.write(response, outString);
		}
		
	}
	
	
	/**
	 * 
	 * 查询元数据信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="listExifs")
	public void listExifs(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		inDto.put("tablename", inDto.getString("tablename"));
		//String queryclass = dataService.isAll(inDto);
		//inDto.put("queryclass", queryclass);
		//request.getSession().setAttribute("querySession", dataService.queryConditions(inDto));
		List<Dto> fieldDtos = dataService.getDataExifs(inDto);
		int pCount = dataService.getPageTotal(inDto);
		String outString = AOSJson.toGridJson(fieldDtos, pCount);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	
	
	/**
	 * 
	 * 查询文书档案元数据信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="listWsdaExifs")
	public void listWsdaExifs(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		inDto.put("tablename", inDto.getString("tablename"));
		//String queryclass = dataService.isAll(inDto);
		//inDto.put("queryclass", queryclass);
		//request.getSession().setAttribute("querySession", dataService.queryConditions(inDto));
		List<Dto> fieldDtos = dataService.getDataWsdaExifs(inDto);
		int pCount = dataService.getPageTotal(inDto);
		String outString = AOSJson.toGridJson(fieldDtos, pCount);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	
	
	
	/**
	 * 
	 * 获得单条信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getData")
	public void getData(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		Object outDto = dataService.getData(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	/**
	 * 
	 * 获得电子文件信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getPath")
	public void getPath(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		List<Dto> pathDtos=dataService.getPath(inDto);
		
//		request.setAttribute("pathDtos", pathDtos);
		
		String outString =AOSJson.toGridJson(pathDtos);
//		Dto outDto = printService.getPath(inDto);
		WebCxt.write(response, outString);
	}
	
	
	/**
	 * 
	 * 打开电子文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="openSwfFile")
	public String openSwfFile(HttpServletRequest request,HttpServletResponse response) throws Exception{
			String temp="";
			Dto inDto = Dtos.newDto(request);
			//Dto outDto = Dtos.newDto();
			String strswf="";
			int pageCount=1;
			Properties prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
			String linkAddress=prop.getProperty("linkAddress");
			String base= prop.getProperty("filePath")+request.getParameter("tablename");//获取username key对应的值 
		       inDto.put("base", base);
			String  path1=dataService.getDocumentPath(inDto);
			//D://dataaos/wsda/4bcd26dbc6fa409ab1d1e6f182d8241a/a0d6df862dbc4abab16d511d0978e02f.jpg
			if(inDto.getString("type").indexOf("jpg")>-1||inDto.getString("type").indexOf("JPG")>-1){
				//String filepath=prop.getProperty("filePath");
				
				pageCount = docService.jpeg2swf(base+"/"+path1);
			}if(inDto.getString("type").indexOf("pdf")>-1){
				strswf = docService.pdf2swf(path1);
			}if(inDto.getString("type").indexOf("png")>-1){
				pageCount = docService.png2swf(path1);
			}
			List<Archive_tablefieldlistPO> titleDtos = dataService.getInfoFieldListTitle(inDto.getString("tablename")+"_info");
			//String count=String.valueOf(pageCount);
			request.setAttribute("tablename", inDto.getString("tablename"));
			request.setAttribute("id", inDto.getString("id"));
			request.setAttribute("strswf", (linkAddress+strswf).replaceAll("\\\\", "/"));
			request.setAttribute("tid", inDto.getString("tid"));
			request.setAttribute("fieldDtos", titleDtos);
			//String aa =linkAddress+strswf;
			//String bb=aa.replaceAll("\\\\", "/");
			//String bb=aa;
			return "common/zpdadocumentView.jsp";
		
	}
	
	/**
	 * 
	 * 打开电子文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="openPdfFile")
	public String openPdfFile(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
			String temp="";
			Dto inDto = Dtos.newDto(request);
			//Dto outDto = Dtos.newDto();
			Properties prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
			String linkAddress=prop.getProperty("linkAddress");
			String  path1=dataService.getDocumentPath(inDto);
		       String path = linkAddress+inDto.getString("tablename")+"/"+path1;
			//D://dataaos/wsda/4bcd26dbc6fa409ab1d1e6f182d8241a/a0d6df862dbc4abab16d511d0978e02f.jpg
			/*if(inDto.getString("type").indexOf("jpg")>-1){
				pageCount = docService.jpeg2swf(path1);
			}if(inDto.getString("type").indexOf("pdf")>-1){
				strswf = docService.pdf2swf(path1);
			}if(inDto.getString("type").indexOf("png")>-1){
				pageCount = docService.png2swf(path1);
			}*/
			List<Archive_tablefieldlistPO> titleDtos = dataService.getInfoFieldListTitle(inDto.getString("tablename")+"_info");
			//String count=String.valueOf(pageCount);
			request.setAttribute("tablename", inDto.getString("tablename"));
			request.setAttribute("id", inDto.getString("id"));
			request.setAttribute("strswf", path);
			request.setAttribute("tid", inDto.getString("tid"));
			request.setAttribute("fieldDtos", titleDtos);
			//根据id查询档号
			String tm = dataService.getTm(inDto.getString("tablename"),inDto.getString("tid"));
			//存入日志
			UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
			LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "电子文件["+inDto.getString("tablename")+"]",tm,"查看电子文件", request.getRemoteAddr(), new Date());
			logMapper.insert(logPO);
			return "common/documentView.jsp";
		
	}
	
	
	@RequestMapping(value="openFileDbclick")
	public void openFileDbclick(HttpServletRequest request,HttpServletResponse response){
		String temp="";
		Dto inDto = Dtos.newDto(request);
		int pageCount=1;
		String strswf="";
		Properties prop;
		try {
			prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
			String base= prop.getProperty("filePath")+inDto.getString("tablename");//获取username key对应的值 
		       inDto.put("base", base);
			String  path1=dataService.getDocumentPath(inDto);
			//D://dataaos/wsda/4bcd26dbc6fa409ab1d1e6f182d8241a/a0d6df862dbc4abab16d511d0978e02f.jpg
			if(inDto.getString("type").indexOf("jpg")>-1){
				pageCount = docService.jpeg2swf(path1);
			}if(inDto.getString("type").indexOf("pdf")>-1){
				strswf = docService.pdf2swf(path1);
			}if(inDto.getString("type").indexOf("png")>-1){
				pageCount = docService.png2swf(path1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//String aa="[{'pageCount':'"+pageCount+"'}]";
		String outString="[{'pageCount':'"+pageCount+"'}]";
		WebCxt.write(response, outString);
		 
	}
	
	/**
	 * 
	 * 打开电子文件
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="pdf")
	public void pdf(HttpServletRequest request,HttpServletResponse response){
		
		
		WebCxt.write(response, "33333");
		
		System.out.print("666666");
	}
	
	
	
	
	/**
	 * 
	 * 删除条目
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "deleteData")
	public void deletePrint(HttpServletRequest request,
			HttpServletResponse response,HttpSession session) {
		Dto dto = Dtos.newDto(request);
		Dto outDto = dataService.deleteData(dto);
		//添加日志
		String tm="";
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		if(dto.getString("tm").equals(",")){
			tm="";
		}else{
			tm=dto.getString("tm").split(",")[0];
		}
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "删除条目["+dto.getString("tablename")+"]",tm,"删除条目", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	/**
	 * 
	 * 删除电子文件
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="deletePath")
	public void deletePath(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		Dto dto = Dtos.newDto(request);
		Dto outDto =dataService.deletePath(dto);
		dataService.refreshPath(dto);
		//日志
		//加入日志
		String tm=dto.getString("tm");
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "删除电子文件["+dto.getString("tablename")+"]", tm, "删除", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);

		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	
	/**
	 * 
	 * 删除全部电子文件
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="deletePathAll")
	public void deletePathAll(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		Dto dto=Dtos.newDto(request);
		Dto outDto = dataService.deletePathAll(dto);
		//加入日志
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		String tm=dto.getString("tm");
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "移除电子文件["+dto.getString("tablename")+"]", tm, "移除", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	/**
	 * 
	 * 获得表单录入项
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getInput")
	public void getInput(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		List<Archive_tableinputPO> list = archive_tableinputMapper.list(inDto);
		WebCxt.write(response, AOSJson.toGridJson(list));
		
	}
	
	
	/**
	 * 
	 * 加载录入界面字典
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="load_dic_index")
	public void load_dic_index(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		qDto.setOrder("id_ DESC");
		qDto.put("catalog_id_","520591d0ad114faf8a1a7e8191f35636");
		List<Dto> list = sysDao.list("MasterData.listDicindexInfos", qDto);
		qDto.put("dic_index_id_", list.get(0).getString("id_"));
		List<Aos_sys_dicPO> listdic = sysDao.list("MasterData.listDicInfos", qDto);
		System.out.print(AOSJson.toJson(listdic));
		WebCxt.write(response, AOSJson.toGridJson(listdic));
		
	}
	
	
	/**
	 * 加存信息
	 */
	@RequestMapping(value = "saveData")
	public void saveData(HttpServletRequest request,HttpServletResponse response,HttpSession session) {
		Dto dto = Dtos.newDto(request);
		Dto outDto =dataService.saveData(dto);
		String xdfields = inputService.listYnxd(dto);
		outDto.put("xdfields", xdfields);
		//添加日志
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		//得到题名
		String tm=dto.getString("tm");
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), dto.getString("tablename"), tm, "添加条目", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	
	/**
	 * 
	 * 保存信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "updateData")
	public void updateData(HttpServletRequest request,HttpServletResponse response,HttpSession session) {
		Dto dto = Dtos.newDto(request);
		Dto outDto =dataService.updateData(dto);
		//添加日志
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		//得到题名
		String tm=dto.getString("tm");
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), dto.getString("tablename"), tm, "修改条目", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	
	/**
	 * 
	 * 查询字段下拉框
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="queryFields")
	public void queryFields(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		WebCxt.write(response, AOSJson.toGridJson(titleDtos));
	}
	
	
	/**
	 * 
	 * 记录更新
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="updateRecord")
	public void updateRecord(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		Dto outDto = dataService.updateRecord(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	/**
	 * 
	 * 记录替换
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="replaceRecord")
	public void replaceRecord(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		Dto outDto = dataService.replaceRecord(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	/**
	 * 
	 * 前后辍
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="suffixRecord")
	public void suffixRecord(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		Dto outDto = dataService.suffixRecord(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}
	
	/**
	 * 
	 * 补位
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="repairRecord")
	public void repairRecord(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		Dto outDto = dataService.repairRecord(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
		
	}
	
	/**
	 * 填充报表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws JRException
	 */
	@RequestMapping(value = "fillReport")
	public void fillReport(HttpServletRequest request, HttpServletResponse response,HttpSession session){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		List<Dto> list = dataService.exportData(qDto);
		
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		//组装报表数据模型
		AOSReportModel reportModel = new AOSReportModel();
		reportModel.setFileName("导出信息表");
		//设置报表集合
		reportModel.setFieldsList(list);
		reportModel.setExcelHeader(titleDtos);
		//填充报表
//		AOSPrint aosPrint = AOSReport.fillReport(reportModel);
//		aosPrint.setFileName("excel表");
		session.setAttribute(AOSReport.DEFAULT_AOSPRINT_KEY, reportModel);
	    WebCxt.write(response, AOSJson.toJson(Dtos.newOutDto()));
	 }
	
	/**
	 * 填充报表
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws JRException
	 */
	@RequestMapping(value = "fillReportgd")
	public void fillReportgd(HttpServletRequest request, HttpServletResponse response,HttpSession session){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		String enfield="";
		if(qDto.getString("tablename").equals("wsda")){
			enfield="jh,zrz,wjbh,tm,cwsj,ys,dh,qzh,nd,jgwtdm,bgqxdm,mj,rw,fjtm";
		}
		if(qDto.getString("tablename").equals("ctda")){
			enfield="";
		}
		qDto.put("enfield", enfield);
		List<Dto> list = dataService.exportDatagd(qDto);
		
		List<Archive_tablefieldlistPO> titleDtos = dataService.getDataFieldListTitle(qDto.getString("tablename"));
		//组装报表数据模型
		AOSReportModel reportModel = new AOSReportModel();
		reportModel.setFileName("导出信息表");
		//设置报表集合
		reportModel.setFieldsList(list);
		reportModel.setExcelHeader(titleDtos);
		//填充报表
//		AOSPrint aosPrint = AOSReport.fillReport(reportModel);
//		aosPrint.setFileName("excel表");
		session.setAttribute(AOSReport.DEFAULT_AOSPRINT_KEY, reportModel);
	    WebCxt.write(response, AOSJson.toJson(Dtos.newOutDto()));
	 }
	
	
	/**
	 * 
	 * 数据导入
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="initImport")
	public String initImport(HttpServletRequest request, HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		request.setAttribute("tablename", qDto.get("tablename"));
		return "aos/archive/import.jsp";
		
	}
	
	
	/**
	 * 
	 * 设置pagesize
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="setPagesize")
	public void setPagesize(HttpSession session, HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		UserInfoVO userInfoVO = WebCxt.getUserInfo(session);
		qDto.put("id_", userInfoVO.getId_());
		Dto outDto = dataService.setPagesize(qDto);
		//request.setAttribute("tablename", qDto.get("tablename"));
		WebCxt.write(response, AOSJson.toJson(outDto));
		
	}
	
	
	/**
	 * 
	 * 重新加载SWFfile
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="loadswfFile")
	public String loadswfFile(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String temp="";
		Dto inDto = Dtos.newDto(request);
		int pageCount=1;
		Properties prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
		String base= prop.getProperty("filePath")+request.getParameter("tablename");//获取username key对应的值 
	       inDto.put("base", base);
		String  path1=dataService.getDocumentPath(inDto);
		String count=String.valueOf(pageCount);
			try {
				if(inDto.getString("type").indexOf("jpg")>-1){
				pageCount = docService.jpeg2swf(path1);
				}if(inDto.getString("type").indexOf("pdf")>-1){
					//pageCount = docService.pdf2swf(path1);
				}if(inDto.getString("type").indexOf("png")>-1){
					pageCount = docService.png2swf(path1);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		request.setAttribute("count", count);
		if(inDto.getString("tablename").equals("zpda")){
			temp="common/sxdaView.jsp";
		}else{
			temp="common/wsdaView.jsp";
		}
	return temp;
		
	}
	/**
	 * Excel预览初始跳转页面
	 * 
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping(value = "inialExcel")
	public void inialExcel(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Dto dto = Dtos.newDto(request);
		String tablename = dto.getString("tablename");
		List<Archive_tablefieldlistPO> titleDtos = dataService
				.getDataFieldListTitle(tablename);
		request.setAttribute("fieldExcels", titleDtos);
	}
	/**
	 * 修改过期销毁档案标记
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("removeappraisal")
	public void removeappraisal(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		Dto outDto = Dtos.newDto(request);
		Dto out = Dtos.newDto();
		//修改鉴定标记值
		boolean b = dataService.updateappraisal(outDto);
		if(b){
			out.setAppCode(1);
			//添加日志
			UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
			String tm=outDto.getString("tm").split(",")[0];
			LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "移动档案["+outDto.getString("tablename")+"]", tm, "移动", request.getRemoteAddr(), new Date());
			logMapper.insert(logPO);
		}else{
			out.setAppCode(-1);
		}
		WebCxt.write(response, AOSJson.toJson(out));
	}
	/**
	 * 按条件删除
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("deleteTermData")
	public void deleteTermData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		Dto outDto = Dtos.newDto(request);
		Dto deleteTermData = dataService.deleteTermData(outDto);
		//添加日志
		UserInfoVO userInfoVO=(UserInfoVO) session.getAttribute("_userInfoVO");
		LogPO logPO = LogUtils.InsertLog(AOSId.uuid(), userInfoVO.getAccount_(), "删除条目["+outDto.getString("tablename")+"]", "", "批量删除", request.getRemoteAddr(), new Date());
		logMapper.insert(logPO);
		WebCxt.write(response, AOSJson.toJson(deleteTermData));
	}

	/**
	 * 移交档案
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception 
	 */
	@RequestMapping("transferData")
	public void transferData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception{		
		//此时进行文件夹的路径选择
		String path=dataService.transferfilepathData(request,response,session);
		//对路径进行判断，是否为空，是否选择了文件路径
		if(path==null||path==""){
			return;
		}
		Dto outDto = Dtos.newDto(request);
		String flag=outDto.getString("flag");		
		if(flag.equals("1")){
			//电子文件
			Dto dto = dataService.transferfile(outDto,path,filePath,request,response,session);
			WebCxt.write(response, AOSJson.toJson(dto));	
		}else if(flag.equals("2")){
			//条目
			dataService.transferReport(outDto,path,request,response,session);
			Dto dto = Dtos.newOutDto();
			dto.setAppCode(2);
			//设计电子条目路径
			dto.setAppMsg(path+File.separator+outDto.getString("tablename"));
		    WebCxt.write(response, AOSJson.toJson(dto));			
		}else{
			//全部
			//电子文件
			dataService.transferfile(outDto,path,filePath, request,response,session);
			dataService.transferReport(outDto,path,request,response,session);
			Dto out=Dtos.newOutDto();
			out.setAppCode(3);
			//设计电子条目路径
			out.setAppMsg(path+File.separator+outDto.getString("tablename"));
			WebCxt.write(response, AOSJson.toJson(out));
		}					
	}	
	@RequestMapping(value="deleteAllData")
	public void deleteAllData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session){
		Dto qDto = Dtos.newDto(request);
		qDto.put("query", request.getSession().getAttribute("querySession"));
		Dto outDto = dataService.deleteAllData(qDto);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}	
	@RequestMapping(value="scan")
	public void scan(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		Runtime runtime = Runtime.getRuntime();
		Process pr = runtime.exec("D:/001/bin/APP.exe"); 
		
		//runtime.exec("start D:/001/bin/APP.exe");
	}
	
	/**
	 * 档案初检
	 * 
	 * @author PX
	 * @param request
	 * @param response
	 * @param session
	 * 
	 *            2019-1-25
	 * @return
	 */
	@RequestMapping("firstcheckOpen")
	public String firstcheckOpen(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Dto out = Dtos.newDto(request);
		// 根据当前信息查询指定的条目数据信息
		Map<String, Object> map = dataService.firstcheckOpen(out);
		WebCxt.write(response, AOSJson.toJson(map));
		List<Archive_tablefieldlistPO> titleDtos = dataService
				.getDataFieldListTitle(out.getString("tablename"));
		UserInfoVO userInfoVO = WebCxt.getUserInfo(session);
		String pagesize = userInfoVO.getPagesize() + "";
		if (pagesize == null) {
			pagesize = "20";
		}
		String queryclass = dataService.isAll(out);
		request.setAttribute("tablename", out.getString("tablename"));
		request.setAttribute("fieldDtos", titleDtos);
		request.setAttribute("pagesize", pagesize);
		session.setAttribute("tablename", out.getString("tablename"));
		request.setAttribute("id_", out.getString("id_").split(",")[0]);
		request.setAttribute("index", out.getString("index"));
		request.setAttribute("cascode_id_", out.getString("cascode_id_"));
		request.setAttribute("limit", out.getString("limit"));
		request.setAttribute("page", out.getString("page"));
		request.setAttribute("query", out.getString("query"));
		request.setAttribute("cascode_id_", out.getString("cascode_id_"));
		return "aos/archive/firstcheck.jsp";
	}
	/**
	 * 初检数据查询
	 * 
	 * @author PX
	 * @param request
	 * @param response
	 * 
	 *            2019-1-31
	 */
	@RequestMapping("listOrgs")
	public void listOrgs(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Dto inDto = Dtos.newDto(request);
		inDto.put("tablename", inDto.getString("tablename"));
		// 判断是不是全部
		String queryclass = dataService.isAll(inDto);
		inDto.put("queryclass", queryclass);
		UserInfoVO userInfoVO = (UserInfoVO) session
				.getAttribute("_userInfoVO");
		List<Dto> fieldDtos = dataService.getDataFieldListDisplayAll2(inDto,
				userInfoVO.getAccount_());
		int pCount = dataService.getPageTotal(inDto);
		String outString = AOSJson.toGridJson(fieldDtos, pCount);
		// 将分类坐标存放到session中
		session.setAttribute("queryclass", queryclass);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	
	/**
	 * 
	 * 获得质检信息
	 * 
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "getfirstcheckData", produces = "text/html;charset=UTF-8")
	public void getfirstcheckData(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		Dto qDto = Dtos.newDto(request);
		String filepath = "";
		String id_ = qDto.getString("id_");
		String tablename = qDto.getString("tablename");
		id_ = id_.split(",")[0];
		request.setAttribute("id_", id_);
		request.setAttribute("tablename", tablename);
		String fileid = id_;
		// 1.根据fid和tablename查询_path2表得到pdf信息
		List<Map<String, Object>> list = dataService.find_path2pdf(fileid,
				tablename);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String path = (String) map.get("_s_path");
				if (path.split("\\.")[1].equals("pdf")) {
					String filename = (String) map.get("dirname");
					// 组合路径
					filepath = firstAddress + tablename + File.separator
							+ filename + path;
					// 获取当前ip地址
					// 路径批量替换
					// wsda/2e9275c32e6849afab038892747249c2/数字档案馆系统测试办法（档办发[2014]6号).pdf
					filepath = filepath.replace("\\", "/");
					break;
				} else {
					continue;
				}
			}
		} else {
			filepath = "";
		}
		Dto outDto = dataService.getData1(qDto);
		filepath = URLDecoder.decode(filepath, "UTF-8");
		outDto.put("pdfpath", filepath);
		// 选中行索引
		Integer index = Integer.valueOf(qDto.getString("index"));
		outDto.put("index", index);
		WebCxt.write(response, AOSJson.toJson(outDto));
	}

	/**
	 * 初检保存操作
	 * 
	 * @author PX
	 * @param request
	 * @param response
	 * @param session
	 * 
	 *            2019-2-13
	 */
	@RequestMapping(value = "firstchecksaveData")
	public void firstchecksaveData(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Dto dto = Dtos.newDto(request);
		UserInfoVO userInfoVO = (UserInfoVO) session
				.getAttribute("_userInfoVO");
		Dto outDto = dataService.firstchecksaveData(dto, userInfoVO);
		if (outDto.getAppCode() == 1) {
			// 得到题名
			// 添加日志
			String tm = dto.getString("tm");
			LogPO logPO = LogUtils.InsertLog(AOSId.uuid(),
					userInfoVO.getAccount_(), dto.getString("tablename"), tm,
					"初检保存", request.getRemoteAddr(), new Date());
			logMapper.insert(logPO);
		}
		WebCxt.write(response, AOSJson.toJson(outDto));
	}


	/**
	 * 请讲接口 http://127.0.0.1:8080/项目名称/appRedisDemo/redisDemo.do
	 * demo展示的在redis存储读取数据的方式，本系统暂时用不到redis，此redis接口可根据实际业务需求选择使用
	 * 具体redis的应用场景->百度下即可
	 */
	@RequestMapping(value="/redisDemo")
	@ResponseBody
	public Object redis(){

		Map<String,Object> map = new HashMap<String,Object>();
		String result = "";

		redisUtils.delete("fh0");											//删除
		redisUtils.delete("fh");											//删除
		redisUtils.delete("fh1");											//删除
		redisUtils.delete("fh2");											//删除

		System.out.println(redisUtils.addString("fh0","opopopo"));		//存储字符串
		System.out.println("获取字符串:"+redisUtils.get("fh0"));			//获取字符串

		result += "获取字符串:"+redisUtils.get("fh0")+",";

		Map<String, String> jmap = new HashMap<String, String>();
		jmap.put("name", "fhadmin");
		jmap.put("age", "22");
		jmap.put("qq", "313596790");
		System.out.println(redisUtils.addMap("fh", jmap));				//存储Map
		System.out.println("获取Map:"+redisUtils.getMap("fh"));			//获取Map

		result += "获取Map:"+redisUtils.getMap("fh")+",";

		List<String> list = new ArrayList<String>();
		list.add("ssss");
		list.add("bbbb");
		list.add("cccc");
		redisUtils.addList("fh1", list);									//存储List
		System.out.println("获取List:"+redisUtils.getList("fh1"));			//获取List

		result += "获取List:"+redisUtils.getList("fh1")+",";

		Set<String> set = new HashSet<String>();
		set.add("wwww");
		set.add("eeee");
		set.add("rrrr");
		redisUtils.addSet("fh2", set);									//存储Set
		System.out.println("获取Set:"+redisUtils.getSet("fh2"));			//获取Set

		result += "获取Set:"+redisUtils.getSet("fh2")+",";

		map.put("result", result);
		HashMap pd=new HashMap();
		if(pd.containsKey("callback")){
			String callback = pd.get("callback").toString();
			return new JSONPObject(callback, map);
		}else{
			return map;
		}
	}
}