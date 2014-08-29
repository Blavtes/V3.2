/**
 * Copyright (C) 2014 Togic Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.togic.weather;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * @author jar @date 2014年4月16日
 */
public class FileUtil {
    public static boolean writeFile(String filePath, InputStream stream) {
        OutputStream o = null;
        try {
            o = new FileOutputStream(filePath);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            Log.i("appstore", "exception filinotfound");
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            Log.i("appstore", "exception ioexception");
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static boolean writeFile(String filePath, String content,
            boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static StringBuilder readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (fileContent.length() != 0) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            reader = null;
            line = null;
            return fileContent;
        } catch (IOException e) {
            Log.i("imagethread", "ioexception");
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e) {
                    return null;
                }
            }
        }
    }

    public static StringBuilder readFromInputStream(InputStream in) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;

    }

    public static InputStream getInputStreamFromUrl(String fileUrl,
            int readTimeOutMillis) {

        InputStream stream = null;
        try {

            URL url = new URL(fileUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (readTimeOutMillis > 0) {
                con.setReadTimeout(readTimeOutMillis);
                System.out.println("adasdsadas1");
            }
            System.out.println("adasdsadas2");
            if (con != null && con.getResponseCode() == 200) {
                System.out.println("adasdsadas3");
                stream = con.getInputStream();

            } else {
                Log.v("appstore", "!!!!http err : " + con.getResponseCode());
            }
            System.out.println("adasdsadas4");
        } catch (MalformedURLException e) {
            closeInputStream(stream);
            throw new RuntimeException("MalformedURLException occurred. ", e);
        } catch (IOException e) {

            closeInputStream(stream);

            throw new RuntimeException("IOException occurred. ", e);
        } catch (Exception e) {

        }

        return stream;
    }

    private static void closeInputStream(InputStream s) {
        if (s == null) {
            return;
        }

        try {
            s.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        }
    }

    public static void checkFolderExists(String path) {
        File file = new File(path);
        if (file.exists())
            if (!file.isDirectory()) {
                file.mkdir();
            } else {
            }
        else {
            file.mkdir();
        }
    }

    public static boolean checkFileExists(File file) {
        if (file.exists())
            if (file.isFile())
                return true;
        return false;
    }

    public static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            if (file.isFile())
                return true;
        return false;
    }

    public static boolean changeFileName(String oldFilePath, String newFileName) {
        File oldFile = new File(oldFilePath);
        checkFileExists(oldFile);
        String c = oldFile.getParent();
        File mm = new File(c + "/" + newFileName);
        if (oldFile.renameTo(mm)) {
            return true;
        } else {
            return false;
        }
    }
}
