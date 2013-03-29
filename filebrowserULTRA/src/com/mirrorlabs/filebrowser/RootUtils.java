/**
 *   920 Text Editor is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   920 Text Editor is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with 920 Text Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mirrorlabs.filebrowser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class RootUtils
{
	 private static String TEMP_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/.Ultra Explorer";
    /***
     * The number of bytes in a kilobyte.
     */
    public static final double ONE_KB = 1024.0;

    /***
     * The number of bytes in a megabyte.
     */
    public static final double ONE_MB = ONE_KB * ONE_KB;

    /***
     * The number of bytes in a gigabyte.
     */
    public static final double ONE_GB = ONE_KB * ONE_MB;

    /***
     * Returns a human-readable version of the file size, where the input
     * represents a specific number of bytes.
     * 
     * @param size
     *            the number of bytes
     * @return a human-readable display value (includes units)
     */
    public static String byteCountToDisplaySize(long size)
    {
        return byteCountToDisplaySize((double) size);
    }

    public static String byteCountToDisplaySize(double size)
    {
        String displaySize;
        double ret;

        if((ret = size / ONE_GB) > 1.0)
        {
            displaySize = " G";
        }else if((ret = size / ONE_MB) > 1.0)
        {
            displaySize = " M";
        }else if((ret = size / ONE_KB) > 1.0)
        {
            displaySize = " KB";
        }else
        {
            ret = size;
            displaySize = " B";
        }

        DecimalFormat df = new DecimalFormat("0.00");

        return df.format(ret) + displaySize;
    }

    public static String ReadFile(String filename)
    {
        return ReadFile(filename, "UTF-8");
    }

    public static String Read(String filename, String encoding)
    {
        return Read(new File(filename), encoding);
    }

    public static String Read(File file, String encoding)
    {

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file));

            // Create an array of characters the size of the file
            char[] allChars = new char[(int) file.length()];

            // Read the characters into the allChars array
            in.read(allChars, 0, (int) file.length());
            in.close();

            // Convert to a string
            String allCharsString = new String(allChars);
            return allCharsString;
        }catch (IOException ex)
        {
            throw new RuntimeException(file + ": trouble reading", ex);
        }

    }
    
    /**
     * 读�?�整个文件, android默认编�?为utf-8,如果文件编�?是gbk或其它编�?,�?是没有指定正确的编�?,就会统一当�?ut-8编�?处�?�
     * 
     * @param filename
     *            文件�??
     * @param encoding
     *            指定文件编�?,�?�则使用系统默认的编�?
     * @return
     */
    public static String ReadFile(String filename, String encoding)
    {
        return ReadFile(new File(filename), encoding);
    }
    
    public static String ReadFile(File filename, String encoding)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            return ReadFile(fis, encoding);
        }catch (FileNotFoundException e)
        {
            return "";
        }
    }
    
    public static String ReadFile(InputStream fis, String encoding)
    {
        BufferedReader br;
        StringBuilder b = new StringBuilder();
        String line;
        String sp = System.getProperty("line.separator");

        try
        {
            br = new BufferedReader(new InputStreamReader(fis, encoding));
            try
            {
                while ((line = br.readLine()) != null)
                {
                    b.append(line).append(sp);
                }
                br.close();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return b.toString();
    }
/*
    public static void writeFile(String path, String text)
    {
        writeFile(path, text, "UTF-8", true);
    }
*/
    /**
     * 写入文件, 需�?指定编�?
     * 
     * @param path
     * @param text
     * @param encoding
     * @return 
     * @return
     */
    
    public static boolean writeFile(String path, String text, String encoding, boolean isRoot)
    {
        try
        {
            File file = new File(path);
            String tempFile = TEMP_PATH + "/temp.root.file";
            String fileString = path;
            boolean root = false;
            if(!file.canWrite() && isRoot)
            {
                //需�?Root�?��?处�?�
                fileString = tempFile;
                root = true;
            }
            BufferedWriter bw = null;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileString), Charset.forName(encoding)));
            bw.write(text);
            bw.close();
            if(root)
            {
                BufferedReader ret = LinuxShell.execute("ls " 
                        + LinuxShell.getCmdPath(fileString) 
                        + " " + LinuxShell.getCmdPath(path));
                //LinuxShell.execute("rm -r " + LinuxShell.getCmdPath(tempFile));
                if(ret == null)
                    return false;
            }
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void DeleteFileRoot(String path){
    	if(LinuxShell.isRoot()){
    		if(new File(path).isDirectory()){
    		  LinuxShell.execute("rm -f -r "+path);

    	    }else{
    	      LinuxShell.execute("rm -r "+path);

    	    }
    	}
    }
    
    public static String getExt(String path)
    {
        int lastIndex = path.lastIndexOf(".");
        if(lastIndex == -1)
            return null;
        return path.substring(lastIndex + 1).trim().toLowerCase();
    }

    public static ArrayList<File> getFileList(String path, boolean runAtRoot)
    {
        ArrayList<File> fileList = new ArrayList<File>();
        ArrayList<File> folderList = new ArrayList<File>();
        if(runAtRoot == false)
        {
            File base = new File(path);
            File[] files = base.listFiles();
            if(files == null)
                return null;
            for(File file: files)
            {
                if(file.isDirectory())
                {
                    folderList.add(file);
                } else {
                    fileList.add(file);
                }
            }
        }else{
            /** 带 root */
            BufferedReader reader = null; //errReader = null;
            try
            {
                
                reader = LinuxShell.execute("IFS='\n';CURDIR='"+LinuxShell.getCmdPath(path)+"';for i in `ls $CURDIR`; do if [ -d $CURDIR/$i ]; then echo \"d $CURDIR/$i\";else echo \"f $CURDIR/$i\"; fi; done");
                if(reader == null)
                    return null;
                
                File f;
                String line;
                while ((line = reader.readLine()) != null)
                {
                    f = new File(line.substring(2));
                    if(line.startsWith("d"))
                    {
                        folderList.add(f);
                    } else {
                        fileList.add(f);
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        Comparator<File> mComparator = new Comparator<File>() {
            public int compare(File fl1, File fl2)
            {
                return fl1.getName().compareToIgnoreCase(fl2.getName());
            }
        };
        //排�?
        Collections.sort(fileList, mComparator);
        Collections.sort(folderList, mComparator);
        
        ArrayList<File> list = new ArrayList<File>();
        for(File f:folderList)
            list.add(f);
        for(File f:fileList)
            list.add(f);
        
        fileList = null;
        folderList = null;
        
        return list;
    }

}
