package sduyq.demo.common;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public class JDBCUtil {
	 private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	 public static String host="10.102.0.195";//10.102.0.206
//	public static String host="10.69.199.201";//10.102.0.206
	 public static String port="7070";//7070
	 public static Connection conn;
	 public static String project="yiqi_table2_test";//kylin model
	 public static Connection connectKylin() {
		 
		 try {
				Driver driver = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();  
		   
				// 配置登录Kylin的用户名和密码  
				Properties info= new Properties();  
				info.put("user","ADMIN");
				info.put("password","KYLIN");  
				String connectStr = "jdbc:kylin://"+host+":"+port+"/"+project;

				// 连接Kylin服务    
				conn=driver.connect(connectStr, info);
				System.out.println(conn);
			} catch(Exception es) {
				System.out.println(es);
			}
		return conn;
		
	}

}
