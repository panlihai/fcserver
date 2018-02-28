package cn.pcorp.util;

import java.io.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

/**
 * 文件目录及文件操作工具类
 * 
 * @author zhangfengtao
 *
 */
public class FileOperUtil {

	/**
	 * 新建文件目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			File myFilePath = new File(folderPath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {
		try {
			File myFilePath = new File(filePathAndName);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
//			FileWriter resultFile = new FileWriter(myFilePath);
//			PrintWriter myFile = new PrintWriter(resultFile);
//			String strContent = fileContent;
//			myFile.println(strContent);
//			resultFile.close();
			FileUtils.writeStringToFile(myFilePath, fileContent, "UTF-8");
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
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
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			java.io.File myFilePath = new java.io.File(folderPath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}

	}

	// JAVA 将字符串内容写入文本文件
	public static void writeTxtFile(String fileName, String content)
			throws Exception {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileWriter fw = new FileWriter(fileName);
		fw.write(content);
		fw.close();
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param path
	 * @return
	 */
	public static String getExtension(String path) {
		if (path == null) {
			return null;
		}
		int len = path.length();
		for (int i = len - 1; i >= 0; i--) {
			char ch = path.charAt(i);
			if (ch == '.') {
				if (i == len - 1)
					return "";
				return path.substring(i);
			}
			if (ch == java.io.File.separatorChar)
				return "";
		}
		return "";
	}

	/**
	 * 下载文件.
	 *
	 * @param response
	 * @param path
	 *            文件全路径
	 * @param fileName
	 *            文件名称
	 * @throws java.io.IOException
	 */
	public static void downloadFile(final HttpServletResponse response,
			final String path, final String fileName) throws IOException {
		response.reset();
		response.setContentType("application/x-msdownload;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ new File(fileName).getName());
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(path));
		OutputStream outputStream = response.getOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = bufferedInputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, len);
		}
		bufferedInputStream.close();
		outputStream.close();
		outputStream.flush();
	}
	public static String upload(ServletInputStream sis, String path,
			String fileName) {

		// 如果没有该目录，则创建
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}

		File file = new File(path, fileName);
		for (int imgnum = 0; file.exists(); imgnum++) {
			file = new File(path, fileName);
		}
		// 缓冲区
		byte buffer[] = new byte[1024];
		FileOutputStream fos = null;
		int len;
		try {
			fos = new FileOutputStream(file);
			len = sis.read(buffer, 0, 1024);
			// 把流里的信息循环读入到文件中
			while (len != -1) {
				fos.write(buffer, 0, len);
				len = sis.readLine(buffer, 0, 1024);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				sis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}
	public static String imgUploadPc(InputStream inputStream, String path,
			String imgName) {
		
		// 如果没有该目录，则创建
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		
		File file = new File(path, imgName);
		for (int imgnum = 0; file.exists(); imgnum++) {
			file = new File(path, imgName);
		}
		// 缓冲区
		byte buffer[] = new byte[1024];
		FileOutputStream fos = null;
		int len;
		try {
			fos = new FileOutputStream(file);
			len = inputStream.read(buffer, 0, 1024);
			// 把流里的信息循环读入到文件中
			while (len != -1) {
				fos.write(buffer, 0, len);
				len = inputStream.read(buffer, 0, 1024);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}
	public static void main(String[] args) {
		newFile("F:/word.doc", "好好学习，天天向上");
	}
}
