package cn.osworks.aos.system.modules.controller.introduce;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSCons;
import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.dao.SqlDao;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.core.typewrap.TypeConvertUtil;
import cn.osworks.aos.system.dao.po.Archive_tablefieldlistPO;
import cn.osworks.aos.system.modules.service.archive.DocService;
import cn.osworks.aos.system.modules.service.introduce.ImportService;


/**
 * 
 * 导入cotroller
 * 
 * @author Sun
 *
 * @date 2018-8-3
 */
@Controller
@RequestMapping(value ="archive/import")
public class ImportController {

	@Autowired
	private ImportService importService;
	
	/**
	 * 加载EXCEL标题到grid
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping("loadExcelGrid")
	
	public String loadExcelGrid(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String excelPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/excel";
		List<Dto> titleDtos =importService.readExcelTitle(excelPath+"/"+qDto.getString("localFilename"));
		request.setAttribute("fieldDtos", titleDtos);
		request.setAttribute("localFilename", qDto.getString("localFilename"));
		return "aos/archive/import.jsp";
	}
	/**
	 * 
	 * 查询excel数据信息
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getExcel")
	public void getExcel(HttpServletRequest request,HttpServletResponse response){
		Dto qDto=Dtos.newDto(request);
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String excelPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/excel";
		String name = excelPath+"/"+qDto.getString("localFilename");
		if(qDto.getString("localFilename").equals("")){
			return;
		}
		List<Dto> list =importService.readXls(name);
		String outString = AOSJson.toGridJson(list);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	
	/**
	 * 
	 * 源字段和目标字段查询
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getSourceField")
	public void getSourceField(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String excelPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/excel";
		String name = excelPath+"/"+qDto.getString("localFilename");
		List<Dto> titleDtos =importService.getTargetField(name,qDto);
		String outString = AOSJson.toGridJson(titleDtos);
		// 就这样返回转换为Json后返回界面就可以了。
		WebCxt.write(response, outString);
	}
	
	/**
	 * 
	 * 下拉列表
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="listComboBox")
	public void listComboBox(HttpServletRequest request,HttpServletResponse response){
		Dto qDto = Dtos.newDto(request);
		List<Archive_tablefieldlistPO> list = importService.listComboBox(qDto.getString("tablename"));
		String outString =AOSJson.toGridJson(list);
		WebCxt.write(response, outString);
	}
	
	/**
	 * 
	 * excel数据导入
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="insertData")
	public void insertData(HttpServletRequest request,HttpServletResponse response){
		Dto inDto = Dtos.newDto(request);
		Dto outDto = importService.InsertDb(inDto);
		String outString = AOSJson.toJson(outDto);
		WebCxt.write(response, outString);
	}
}