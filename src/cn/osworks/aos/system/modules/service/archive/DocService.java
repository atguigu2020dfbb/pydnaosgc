package cn.osworks.aos.system.modules.service.archive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.osworks.aos.core.typewrap.Dto;
import cn.osworks.aos.core.typewrap.Dtos;

/**
 * 
 * PDF文件转换
 * 
 * @author shq
 *
 * @date 2017-1-13
 */
@Service
public class DocService {
	private static final int environment = 1;// 环境1：windows,2:linux(涉及pdf2swf路径问题)
	private String fileString;
	private String outputPath = "";// 输入路径，如果不设置就输出在默认位置
	private String fileName;
	private File pdfFile;
	private File jpegFile;
	private File swfFile;
	private File docFile;

	
	  public void DocConverter(String fileString) {  
          ini(fileString);  
     }  
	/*
	 * 重新设置 file @param fileString
	 */
	public void setFile(String fileString) {
		ini(fileString);
	}

	/*
	 * 初始化 @param fileString
	 */
	private void ini(String fileString) {
		this.fileString = fileString;
		fileName = fileString.substring(0, fileString.lastIndexOf("."));
		docFile = new File(fileString);
		pdfFile = new File(fileName + ".pdf");
		swfFile = new File(fileName + ".swf");
	}

	/*
	 * 转为PDF @param file
	 */

	/*
	 * 转换成swf
	 */
	public Integer pdf2swf11(String file) throws Exception {
		Runtime r = Runtime.getRuntime();
		int num=0;
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String swfexe = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"static/exe/pdf2swf.exe";
//		String outPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/Paper%";
		String outPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/page%";
		String filedir=strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"temp";
		pdfFile = new File(file);
		swfFile = new File(outPath + ".swf");
		delAllFile(filedir);
		if (!swfFile.exists()) {
			if (pdfFile.exists()) {
				if (environment == 1)// windows环境处理
				{
					try {
						// 这里根据SWFTools安装路径需要进行相应更改
						Process p=null;
						 List<String> command=new ArrayList();  
						 command.add(swfexe);
		                 command.add("\""+ pdfFile.getPath()+"\"");  
		                 command.add("\""+swfFile.getPath()+"\"");  
		                 command.add("-f");
		                 command.add("-T");  
		                 command.add("9");
		                // command.add("-t");
		                // command.add("-p 4");
		                // command.add("-I"); 
		                 p=new ProcessBuilder(command).start(); 
						System.out.print(loadStream(p.getInputStream()));
						System.err.print(loadStream(p.getErrorStream()));
						System.out.print(loadStream(p.getInputStream()));
						System.err.println("****swf转换成功，文件输出：" + swfFile.getPath() + "****");
						File dir = new File(filedir);
						File[] files = dir.listFiles();
						 num = files.length-1;
						//num=200;
						System.out.print("****swf文件个数**** "+num);
						if (pdfFile.exists()) {
//							pdfFile.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			} else {
				System.out.println("****pdf不存在，无法转换****");
			}
		} else {
			System.out.println("****swf已存在不需要转换****");
		}
		return num;
	}

	public String pdf2swf(String file) throws Exception {
		Dto qDto=Dtos.newDto();
		int num=0;
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String swfexe = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"static/exe/pdf2swf.exe";
		String outPath =file.substring(0,file.lastIndexOf("/"))+"/mark/";
		String swfname = file.substring(file.lastIndexOf("/")+1,file.lastIndexOf("."));
		swfFile = new File(outPath +"/"+swfname+ ".swf");
		String strswfFile=swfFile.getPath();
		String strswf=strswfFile.substring(strswfFile.lastIndexOf("dataaos")+7);
		pdfFile = new File(outPath+"/"+swfname+".pdf");
		//delAllFile(filedir);
		if (!swfFile.exists()) {
			if (pdfFile.exists()) {
				if (environment == 1)// windows环境处理
				{
					try {
						// 这里根据SWFTools安装路径需要进行相应更改
							 List<String> command=new ArrayList();  
							 command.add(swfexe);
			                 command.add("\""+ pdfFile.getPath()+"\"");  
			                 command.add("\""+swfFile.getPath()+"\"");  
			                 command.add("-f");
			                 command.add("-T");  
			                 command.add("9");
							Process p=new ProcessBuilder(command).start(); 
							System.out.print(loadStream(p.getInputStream()));
							System.err.print(loadStream(p.getErrorStream()));
							System.out.print(loadStream(p.getInputStream()));
							System.err.println("****swf转换成功，文件输出：" + swfFile.getPath() + "****");
						System.out.print("****swf文件个数**** "+num);
						if (pdfFile.exists()) {
//							pdfFile.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			} else {
				System.out.println("****pdf不存在，无法转换****");
			}
		} else {
			System.out.println("****swf已存在不需要转换****");
		}
		return strswf;
	}
	
	/**
	 * 
	 * jpg转swf
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Integer jpeg2swf(String file) throws Exception {
		Runtime r = Runtime.getRuntime();
		int num=0;
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String swfexe = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"static/exe/jpeg2swf.exe";
		String outPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/page1";
		String filedir=strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"temp";
		jpegFile = new File(file);
		swfFile = new File(outPath + ".swf");
		delAllFile(filedir);
		if (!swfFile.exists()) {
			if (jpegFile.exists()) {
				if (environment == 1)// windows环境处理
				{
					try {
						// 这里根据SWFTools安装路径需要进行相应更改
						Process p=null;
						 List<String> command=new ArrayList();  
						 command.add(swfexe);
		                 command.add("\""+ jpegFile.getPath()+"\""); 
		                 command.add("-o");
		                 command.add("\""+swfFile.getPath()+"\"");  
		                 command.add("-T");  
		                 command.add("9");
		                 p=new ProcessBuilder(command).start(); 
						System.out.print(loadStream(p.getInputStream()));
						System.err.print(loadStream(p.getErrorStream()));
						System.out.print(loadStream(p.getInputStream()));
						System.err.println("****swf转换成功，文件输出：" + swfFile.getPath() + "****");
						if (jpegFile.exists()) {
//							pdfFile.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			} else {
				System.out.println("****pdf不存在，无法转换****");
			}
		} else {
			System.out.println("****swf已存在不需要转换****");
		}
		return num;
	}
	
	

	
	/**
	 * 
	 * png转swf
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Integer png2swf(String file) throws Exception {
		Runtime r = Runtime.getRuntime();
		int num=0;
		String strdir = DocService.class.getResource("/").getFile().replace("%20", " ");
		String swfexe = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"static/exe/png2swf.exe";
		String outPath = strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"/temp/page1";
		String filedir=strdir.substring(1,strdir.lastIndexOf("WEB-INF"))+"temp";
		File pngFile = new File(file);
		swfFile = new File(outPath + ".swf");
		delAllFile(filedir);
		if (!swfFile.exists()) {
			if (pngFile.exists()) {
				if (environment == 1)// windows环境处理
				{
					try {
						// 这里根据SWFTools安装路径需要进行相应更改
						Process p=null;
						 List<String> command=new ArrayList();  
						 command.add(swfexe);
		                 command.add("\""+ pngFile.getPath()+"\""); 
		                 command.add("-o");
		                 command.add("\""+swfFile.getPath()+"\"");  
		                 command.add("-T");  
		                 command.add("9");
		                 p=new ProcessBuilder(command).start(); 
						System.out.print(loadStream(p.getInputStream()));
						System.err.print(loadStream(p.getErrorStream()));
						System.out.print(loadStream(p.getInputStream()));
						System.err.println("****swf转换成功，文件输出：" + swfFile.getPath() + "****");
						File dir = new File(filedir);
						File[] files = dir.listFiles();
						 num = files.length;
						System.out.print("****swf文件个数**** "+num);
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			} else {
				System.out.println("****pdf不存在，无法转换****");
			}
		} else {
			System.out.println("****swf已存在不需要转换****");
		}
		return num;
	}

	static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		//把InputStream字节流 替换为BufferedReader字符流 2013-07-17修改
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder buffer = new StringBuilder();
		while ((ptr = reader.read()) != -1) {
			buffer.append((char) ptr);
		}
		return buffer.toString();
	}
	
	
	/*
	 * 转换主方法
	 */
	public boolean conver() {
		if (swfFile.exists()) {
			System.out.println("****swf转换器开始工作，该文件已经转换为swf****");
			return true;
		}

		if (environment == 1) {
			System.out.println("****swf转换器开始工作，当前设置运行环境windows****");
		} else {
			System.out.println("****swf转换器开始工作，当前设置运行环境linux****");
		}

		try {
//			doc2pdf();
//			pdf2swf();
		} catch (Exception e) {
			// TODO: Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		if (swfFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 返回文件路径 @param s
	 */
	public String getswfPath() {
		if (swfFile.exists()) {
			String tempString = swfFile.getPath();
			tempString = tempString.replaceAll("\\\\", "/");
			return tempString;
		} else {
			return "";
		}
	}

	/*
	 * 设置输出路径
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
		if (!outputPath.equals("")) {
			String realName = fileName.substring(fileName.lastIndexOf("/"), fileName.lastIndexOf("."));
			if (outputPath.charAt(outputPath.length()) == '/') {
				swfFile = new File(outputPath + realName + ".swf");
			} else {
				swfFile = new File(outputPath + realName + ".swf");
			}
		}
	}
	 public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             flag = true;
	          }
	       }
	       return flag;
	     }
}