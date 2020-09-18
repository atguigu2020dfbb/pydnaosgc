package cn.osworks.aos.system.modules.controller.record;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.osworks.aos.core.asset.AOSJson;
import cn.osworks.aos.core.asset.WebCxt;
import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;
import cn.osworks.aos.system.modules.service.archive.DataService;

/**
 * 
 * 数据备份与恢复
 * 
 * @author shq
 * 
 * @date 2016-9-14
 */
@Controller
@RequestMapping(value = "archive/record")
public class RecordController extends JdbcDaoSupport{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	//盘符路径
	public static String url="";
	public static final JFileChooser fc = new JFileChooser();
	@Resource
	public void setJb(JdbcTemplate jb) {
		super.setJdbcTemplate(jb);
	}
	//静态代码块读取配置文件
	static {
		try {
			Properties props = new Properties();
			InputStream in = DataService.class
					.getResourceAsStream("/jdbc.properties");
			props.load(in);
			url = props.getProperty("jdbc.url");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/**
 * 
 * 查档系统设置页面初始化
 * 
 * @param request
 * @param response
 * @return
 * @throws UnknownHostException 
 */
@RequestMapping(value = "initData")
public String initData(HttpServletRequest request,
		HttpServletResponse response, HttpSession session) throws Exception {
		//String baddr = request.getRemoteAddr();
		//String faddr = request.getLocalAddr();
		//此时如果值相同证明是服务器机器操作，可以正常向下执行，并跳转页面
		return "aos/record/record.jsp";
	}
/**
 * 数据库备份
 * @param request
 * @param response
 * @param session
 */
@RequestMapping(value = "backupData")
public void backupData11(HttpServletRequest request,HttpServletResponse response){
		Dto out=Dtos.newDto();
		String filename =  new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date().getTime());
		Properties prop=null;
		try {
			prop = PropertiesLoaderUtils.loadAllProperties("config.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String base= prop.getProperty("filePath")+"/backup";
		//System.out.println(time);
			//当前时间毫秒值
			//long filename=System.currentTimeMillis();
			String database=url.split("=")[1];
	        String selectsql = "backup database " + database + " to disk='" + base+"/"+ filename+"' with format,name='123'";
	        jdbcTemplate.execute(selectsql);
	        out.setAppCode(1);
	        out.put("filepath", base+"/"+ filename);
	        out.put("name", filename);
		
		WebCxt.write(response, AOSJson.toJson(out));
}
/**
 * 数据库备份
 * @param request
 * @param response
 * @param session
 * @throws UnsupportedEncodingException 
 * @throws IOException 
 */
@RequestMapping(value = "download")
public void backupData(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
	//String download = request.getSession().getServletContext().getRealPath("/upload/"); //获取下载路劲
	/*
	String path="D://";
	 String fileName = "1536731752257";
     String fileType = "bak";
     File file = new File(path+fileName);  //根据文件路径获得File文件
     //设置文件类型(这样设置就不止是下Excel文件了，一举多得)
     if("pdf".equals(fileType)){
        response.setContentType("application/pdf;charset=GBK");
     }else if("bak".equals(fileType)){
        response.setContentType("application/msexcel;charset=GBK");
     }else if("doc".equals(fileType)){
        response.setContentType("application/msword;charset=GBK");
     }else if("xls".equals(fileType)){
            response.setContentType("application/msexcel;charset=GBK");
         }
     //文件名
     response.setHeader("Content-Disposition", "attachment;filename=\""
         + new String(fileName.getBytes(), "ISO8859-1") + "\"");
     response.setContentLength((int) file.length());
     byte[] buffer = new byte[4096];// 缓冲区
     BufferedOutputStream output = null;
     BufferedInputStream input = null;
     try {
       output = new BufferedOutputStream(response.getOutputStream());
       input = new BufferedInputStream(new FileInputStream(file));
       int n = -1;
       //遍历，开始下载
       while ((n = input.read(buffer, 0, 4096)) > -1) {
          output.write(buffer, 0, n);
       }
       output.flush();   //不可少
       response.flushBuffer();//不可少
     } catch (Exception e) {
       //异常自己捕捉       
     } finally {
        //关闭流，不可少
        if (input != null)
             input.close();
        if (output != null)
             output.close();
     }*/
	Dto inDto = Dtos.newDto(request);
	response.setContentType("application/xls");
	response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(inDto.getString("name"), "utf-8"));
	//好了 ，现在通过IO流来传送数据
	String strpath = inDto.getString("filepath");

	File file = new File(strpath);
	FileInputStream input=null;
	try {
		input = new FileInputStream(file);
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	OutputStream outputStream = null;
	try {
		outputStream = response.getOutputStream();
		byte[]buff=new byte[1024*10];//可以自己 指定缓冲区的大小
        int len=0;
        while((len=input.read(buff))>-1)
        {
        	outputStream.write(buff,0,len);
        }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		outputStream.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		outputStream.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**
 * 数据库还原
 * @param request
 * @param response
 * @param session
 */
@RequestMapping("restoreData")
public void restoreData(HttpServletRequest request,
		HttpServletResponse response, HttpSession session){
	Dto out=Dtos.newDto();
	String baddr = request.getRemoteAddr();
	String faddr = request.getLocalAddr();
	//判断是不是服务器机器还原数据库
	if(!baddr.equals(faddr)){
		out.setAppCode(-2);
		WebCxt.write(response, AOSJson.toJson(out));
		return;
	}
	try {
		String restorefile = getFile();
		if(restorefile==null||restorefile==""){
			return;
		}
		String database=url.split("=")[1];
		String sql2 = "ALTER DATABASE " + database + "  SET OFFLINE  WITH ROLLBACK IMMEDIATE";
	    String sql = "use master restore database " + database + " from disk='" + restorefile+ "' with replace";
	    String sql3 = "ALTER DATABASE " + database + "  SET ONLINE WITH ROLLBACK IMMEDIATE";
	    jdbcTemplate.execute(sql2);
	    jdbcTemplate.execute(sql);
	    jdbcTemplate.execute(sql3);
	    out.setAppCode(1);
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		out.setAppCode(-1);
	}
	WebCxt.write(response, AOSJson.toJson(out));
}
/**
 * 选择文件夹路径
 * @param request
 * @param response
 * @param session
 * @return
 */
public String backupfilepathData(HttpServletRequest request,
		HttpServletResponse response, HttpSession session){
	JFileChooser fileChooser = new JFileChooser("D:\\"); 

	 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 

	 int returnVal = fileChooser.showOpenDialog(fileChooser); 

	 if(returnVal == JFileChooser.APPROVE_OPTION)

	{ String filePath= fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的
		return filePath;
	}
	 return null;
}
/**
 * 选择文件
 * @return
 */
public String getFile(){
	JFileChooser fileChooser = new JFileChooser("D:\\");
	int returnVal=fileChooser.showOpenDialog(fileChooser);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	       return fileChooser.getSelectedFile().getAbsolutePath();
	    }
	return null;
}
}