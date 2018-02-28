package cn.pcorp.util;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
 
 
/**
 * @author 潘立海
 * 文档管理通用类
 */
public class DocUtil {
     
    private static Log log = LogFactory.getLog("DocUtil");
     
     
    public static boolean createPath(String pathStr)
    throws Exception
    {
        File path = new File(pathStr);
        if(!path.exists())
        {
            path.mkdirs();
            log.debug("create path:" + pathStr);
        }
        return true;        
    }
     
    public static boolean deletePath(String pathStr)
    throws Exception
    {
//      pathStr = getDoclibRoot() + "/" + pathStr;
        File path = new File(pathStr);
        if(path.exists())
        {
            delAllDir(path);
            log.debug("delete path:" + pathStr);
        }
        return true;        
    }
     
    public static boolean renamePath(String oldPathStr,String newPathStr)
    throws Exception
    {
//      oldPathStr = getDoclibRoot() + "/" + oldPathStr;
//      newPathStr = getDoclibRoot() + "/" + newPathStr;
        File oldPath = new File(oldPathStr);
        File newPath = new File(newPathStr);
        if(oldPath.exists())
        {
            oldPath.renameTo(newPath);
            log.debug("rename path:'" + oldPathStr + "' to '" +newPathStr +"'");
        }
        return true;        
    }
     
    /**
     * 
     * @TODO 保存文件
     * @param root 保存的根目录
     * @param savaPath 当前上传的文件名称 支持多个以分号分割
     * @param ins 流
     * @return
     */
    public static boolean saveFile(String root,String savaPath, InputStream ins)
    throws Exception{
        String[] path = savaPath.split(";");
        int i =   0;
        int fileLength;
        int readLength = 4096;
        int totalLength=  0;
        for (String p : path) {
            readLength = 4096;
            totalLength=  0;
            DataInputStream dis = new DataInputStream(ins);
            String[] pathsize = p.split(":");
            fileLength = Integer.parseInt(pathsize[  1]);
            File file = new File(root+DateUtils.getFulltime()+pathsize[  0].substring(pathsize[  0].indexOf(".")));
            i++;
            FileOutputStream fos = null;            
            try {               
                fos = new FileOutputStream(file);
                if(fileLength<readLength){
                    readLength=fileLength;
                }
                byte[] bufferBytes = new byte[readLength];
                int ret;
                while ((ret = dis.read(bufferBytes)) != -  1){
                    fos.write(bufferBytes,   0, ret);
                    totalLength+=readLength;//总共读完了多长记录                 
                    if(totalLength+readLength>fileLength){//如果没有数据了则退出
                        readLength = fileLength-totalLength;
                        bufferBytes = new byte[readLength];
                        if(readLength==  0){
                            break;
                        }
                    }                   
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (fos != null)
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        log.error("关闭文件流出错", ioe);
                    }
                // if(ins!=null) try{ins.close();}catch(IOException
                // ioe){log.error("关闭文件流出错",ioe);}
            }
        }
 
        return true;
    }
     
    /**
     * 
     * 保存文件
     * @param savaPath
     * @param fileBytes
     * @return
     */
    public static boolean saveFile(String savaPath, byte[] fileBytes)
    throws Exception
    {
        File file = new File(savaPath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(fileBytes);
        fos.close();
         
        return true;
    }
     
 
    /**
     * 
     * @TODO 删除文件夹下所有内容，包括此文件夹
     * @param file
     * @throws IOException
     */
    public static void delAllDir(File file) 
    throws IOException 
    {
        if(!file.exists())//文件夹不存在不存在
            return;
            //throw new IOException("指定目录不存在:"+file.getName());
 
        boolean rslt=true;  //保存中间结果
        if(!(rslt=file.delete())) //先尝试直接删除
        {
            //若文件夹非空。枚举、递归删除里面内容
            File subs[] = file.listFiles();
            for (int i =   0; i <= subs.length -   1; i++) 
            {
                if (subs[i].isDirectory())
                    delAllDir(subs[i]);//递归删除子文件夹内容
                rslt = subs[i].delete();//删除子文件夹本身
            }
            rslt = file.delete();//删除此文件夹本身
        }
 
        //if(!rslt)
        //  throw new IOException("无法删除:"+file.getName());
        return;
    }
 
 
    /**
     * 判断非目录文件是否存在？
     * @param filename
     * @return
     */
    public static boolean hasFile(String filename)
    {
        File file = new File(filename);
        if(!file.exists())
            return false;
        else if(file.isDirectory())
            return false;
        else
            return true;
    }
     
    /**
     * 拷贝文件
     * @param file1
     * @param file2
     * @return
     */
    public static boolean CopyFile(String file1,String file2)
    {
        try {
            java.io.File file_in=new java.io.File(file1);
            java.io.File file_out=new java.io.File(file2);
            FileInputStream in1=new FileInputStream(file_in);
            FileOutputStream out1=new FileOutputStream(file_out);
            byte[] bytes=new byte[4096];
            int c;
            while((c=in1.read(bytes))!=-  1)
                out1.write(bytes,  0,c);
            in1.close();
            out1.close();
            return true;
        } catch (Exception e) {
            return false;
        }
 
    }
 
}