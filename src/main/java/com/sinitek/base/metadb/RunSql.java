package com.sinitek.base.metadb;

import java.io.*;
import java.sql.*;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;


/**
 * Created by IntelliJ IDEA.
 * User: dy.cao
 * Date: 2019/01/08
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunSql {

    public static void main(String[] args) {
        Options options = prepareOptions();
        if (args.length > 0){
            CommandLine cmdLine = null;
            try {
                CommandLineParser parser = new DefaultParser();
                cmdLine = parser.parse( options, args);
            } catch (ParseException e) {
                e.printStackTrace();

                printHelpInfo(options);
                return;
            }

            String dblink = cmdLine.getOptionValue("dblink");
            String sqlpath = cmdLine.getOptionValue("sqlpath");

            boolean isok = StringUtils.isNotBlank(dblink) &&
                    StringUtils.isNotBlank(sqlpath);
            if (!isok){
                printHelpInfo(options);
                return;
            }

            if (StringUtils.isBlank(dblink)){
                printHelpInfo(options);
                System.out.println("\n请输入dblink参数");
                return;
            }

            if (StringUtils.isBlank(sqlpath)){
                printHelpInfo(options);
                System.out.println("\n请输入sqlpath参数");
                return;
            }
            //先检测sql语句，然后执行
            checkSql(dblink, sqlpath);
        }
        else{
            printHelpInfo(options);
        }
    }

    private static Options prepareOptions(){
        Options options = new Options();
        options.addOption( "dblink", true, "数据库连接字符串：oracle:127.0.0.1:1521:orcl:user:password" );
        options.addOption( "sqlpath", true, "sql文件位置" );

        return options;
    }

    private static void printHelpInfo(Options options){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "java -jar runsql.jar", options );
    }

    /**
     * 检测sql代码
     */
    private static void checkSql(String dblink, String sqlpath){
        System.out.println("\n开始检测sql文件：");
        File file = new File(sqlpath);

        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs){					//遍历File[]数组
            if(!f.isDirectory())		//若非目录(即文件)，则执行
            {
                System.out.println(f);
                try {
                    InputStreamReader read = new InputStreamReader(new FileInputStream(f));
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        //判断commit
                        if (line.contains("commit")) {
                            //System.out.println(line);
                            //System.out.println("  --sql语句中包含commit");
                            throw new Exception(f + "文件中包含commit");
                        }
                    }
                    read.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        System.out.println("\n  --检测通过!");
        //执行sql代码T
        getDbype(dblink, sqlpath);
    }

    /**
     * 连接数据库并执行sql
     *
     */
    private static void executionSql(String link, String user, String password, String sqlpath){
        //连接db
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(link, user, password);
            File file = new File(sqlpath);
            File[] fs = file.listFiles();
            for(File f:fs) {
                if (!f.isDirectory())
                {
                    FileSystemResource rc = new FileSystemResource(f);
                    EncodedResource er = new EncodedResource(rc, "GBK");
                    ScriptUtils.executeSqlScript(conn, er);
                }
            }
            System.out.println("\n  --sql执行成功");
            conn.close();
        } catch (SQLException e) {
            System.out.println("\n  --数据库连接失败");
            e.printStackTrace();
        }
    }

    /**
     * 判断数据库类型
     */
    private static void getDbype(String dblink, String sqlpath){
        System.out.println("\n开始执行sql代码：");

        //拆分link字符串
        String[] items = dblink.trim().split(":");
        String dbtype = items[0];

        if ("oracle".equalsIgnoreCase(dbtype)) {
            String str1 = "jdbc:oracle:thin:@//" + items[1] + ":" + items[2] + "/" + items[3];
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                executionSql(str1, items[4], items[5], sqlpath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if ("mysql".equalsIgnoreCase(dbtype)) {
            String str2 = "jdbc:mysql://" + items[1] + ":" + items[2] + "/" + items[3];
            try {
                Class.forName("com.mysql.jdbc.Driver");
                executionSql(str2, items[4], items[5], sqlpath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}
