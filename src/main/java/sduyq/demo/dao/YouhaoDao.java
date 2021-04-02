package sduyq.demo.dao;



import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import sduyq.demo.common.JDBCUtil;
import sduyq.demo.model.Youhao;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class YouhaoDao {
//	private static Statement stmt;
//	private static JDBCUtil jdbcutil=new JDBCUtil();
//	private static Connection conn=jdbcutil.connectKylin();
//
	public List<Youhao> findYhtable(Integer start, Integer end,String cont) throws SQLException {
		Youhao youhao;
		JDBCUtil jdbcutil = new JDBCUtil();
		Connection conn = jdbcutil.connectKylin();
		System.out.println(conn);
		List<Youhao> youhaoList = new ArrayList<Youhao>();
		String where1="";//一般筛选条件
		String where2="";//油耗滤波条件
		boolean whereb=false;
		if (cont!=null){
			String[] cons=new String[16];
			String conts[]=cont.split(";");
			System.out.println(conts.length);
			for(int i=0;i<conts.length;i++){
				cons[i]=conts[i];
				System.out.print("h"+conts[i]);
			}
//			System.out.print("\n第四个参数:"+cons[3].getClass().getName().toString()+cons[3].length()+"end");
//			System.out.print("地八个参数:"+cons[7]);
			if(cons[0]!=null&&cons[0].length()!=0){//底盘号
				whereb=true;
				where1=where1+" chassis like '%"+cons[0]+"%' ";
			}
			if(cons[1]!=null&&cons[1].length()!=0){//车牌号
				if (whereb){
					where1=where1+"and car_cph like '%"+cons[1]+"%' ";
				}else {
					where1=where1+" car_cph like '%"+cons[1]+"%' ";
					whereb=true;
				}
			}
			if(cons[2]!=null&&cons[2].length()!=0){//平台类型
				if (whereb){
					where1=where1+"and platform like '%"+cons[2]+"%' ";
				}else {
					where1=where1+" platform like '%"+cons[2]+"%' ";
					whereb=true;
				}
			}
			if(cons[3]!=null&&cons[3].length()!=0){//车辆类别
				if (whereb){
					where1=where1+"and vehicle_type like '%"+cons[3]+"%' ";
				}else {
					where1=where1+" vehicle_type like '%"+cons[3]+"%' ";
					whereb=true;
				}
			}
			if(cons[4]!=null&&cons[4].length()!=0){//发动机类型
				if (whereb){
					where1=where1+"and engine like '%"+cons[4]+"%' ";
				}else {
					where1=where1+" engine like '%"+cons[4]+"%' ";
					whereb=true;
				}
			}
			if(cons[5]!=null&&cons[5].length()!=0){//离合器
				if (whereb){
					where1=where1+"and clutch like '%"+cons[5]+"%' ";
				}else {
					where1=where1+" clutch like '%"+cons[5]+"%' ";
					whereb=true;
				}
			}
			if(cons[6]!=null&&cons[6].length()!=0){//驱动形式
				if (whereb){
					where1=where1+"and drive like '%"+cons[6]+"%' ";
				}else {
					where1=where1+" drive like '%"+cons[6]+"%' ";
					whereb=true;
				}
			}
			if(cons[7]!=null&&cons[7].length()!=0){//变速箱类型
				if (whereb){
					where1=where1+"and transmission like '%"+cons[7]+"%' ";
				}else {
					where1=where1+" transmission like '%"+cons[7]+"%' ";
					whereb=true;
				}
			}
			if(cons[8]!=null&&cons[8].length()!=0){//后桥类型
				if (whereb){
					where1=where1+"and rear_axle like '%"+cons[8]+"%' ";
				}else {
					where1=where1+" rear_axle like '%"+cons[8]+"%' ";
					whereb=true;
				}
			}
			if(cons[9]!=null&&cons[9].length()!=0){//车架类型
				if (whereb){
					where1=where1+"and frame like '%"+cons[9]+"%' ";
				}else {
					where1=where1+" frame like '%"+cons[9]+"%' ";
					whereb=true;
				}
			}
			if(cons[10]!=null&&cons[10].length()!=0){//轴距
				if (whereb){
					where1=where1+"and wheelbase like '%"+cons[10]+"%' ";
				}else {
					where1=where1+" wheelbase like '%"+cons[10]+"%' ";
					whereb=true;
				}
			}
			if(cons[11]!=null&&cons[11].length()!=0){//轮胎类型
				if (whereb){
					where1=where1+"and wheel like '%"+cons[11]+"%' ";
				}else {
					where1=where1+" wheel like '%"+cons[11]+"%' ";
					whereb=true;
				}
			}
			if(cons[14]!=null&&cons[14].length()!=0){//开始时间
				if (whereb){
					where1=where1+"and gpsdate between '"+cons[14]+"' ";
				}else {
					where1=where1+" gpsdate between  '"+cons[14]+"' ";
					whereb=true;
				}
			}
			if(cons[15]!=null&&cons[15].length()!=0){//结束时间
				where1=where1+"and '"+cons[15]+"' ";
			}
			if (whereb){
				where1=" where "+where1;
			}
			if (cons[12]!=null&&cons[12].length()!=0){//油耗最小滤波
				where2=" dayFuel>="+cons[12];
			}else{
				where2=" dayFuel>=5";
			}
			if (cons[13]!=null&&cons[13].length()!=0){//油耗最大滤波
				where2=where2+" and dayFuel<="+cons[13];
			}else {
				where2=where2+" and dayFuel<=100 ";
			}

		}else{
			where2=" dayFuel>=5 and dayFuel<=100 ";
		}

		// 查询油耗sql
		String selectAllSql = "select chassis,avg(dayFuel) avg_fuel " + "from " + "(select * "
				+ "from (select chassis,gpsdate,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel "
				+ "from fact_table inner join dim_table on fact_table.terminal_id=dim_table.terminal_id "
				+where1
				+ " group by chassis,gpsdate) "
				+ "where"
				+where2+" ) " + "group by chassis limit " + start + "," + end + ";";
		Statement stmt = conn.createStatement();
		int columnCount = 0;
		try {
			ResultSet rs = stmt.executeQuery(selectAllSql);
			System.out.println("\n查表:"+selectAllSql+"\n");
			while (rs.next()) {
				youhao = new Youhao();
				youhao.setChassis(rs.getString("CHASSIS"));
				youhao.setAvg_fuel(rs.getString("AVG_FUEL"));
				youhaoList.add(youhao);
			}
			rs.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		stmt.close();
		conn.close();
		return youhaoList;
	}
	//获取查询总数
	public static int getTotal(String cont) throws SQLException {
		int columnCount = 0;
		JDBCUtil jdbcutil = new JDBCUtil();
		Connection conn = jdbcutil.connectKylin();
//		String selectAllSql = "select chassis,avg(dayFuel) avg_fuel " + "from " + "(select * "
//				+ "from (select chassis,print,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel "
//				+ "from fact_table inner join time_table on fact_table.vin_time=time_table.vin_time "
//				+ "inner join dim_table on fact_table.vin=dim_table.vin " + "group by chassis,print) "
//				+ "where dayFuel>=5 and dayFuel<=100) " + "group by chassis ;";

		String where1="";//一般筛选条件
		String where2="";//油耗滤波条件
		boolean whereb=false;
		if (cont!=null){
			String[] cons=new String[16];
			String conts[]=cont.split(";");
			for(int i=0;i<conts.length;i++){
				cons[i]=conts[i];
				System.out.print("h"+conts[i]);
			}
//			System.out.print("第四个参数:"+cons[4]);
//			System.out.print("地八个参数:"+cons[7]);
			if(cons[0]!=null&&cons[0].length()!=0){//底盘号
				whereb=true;
				where1=where1+" chassis like '%"+cons[0]+"%' ";
			}
			if(cons[1]!=null&&cons[1].length()!=0){//车牌号
				if (whereb){
					where1=where1+"and car_cph like '%"+cons[1]+"%' ";
				}else {
					where1=where1+" car_cph like '%"+cons[1]+"%' ";
					whereb=true;
				}
			}
			if(cons[2]!=null&&cons[2].length()!=0){//平台类型
				if (whereb){
					where1=where1+"and platform like '%"+cons[2]+"%' ";
				}else {
					where1=where1+" platform like '%"+cons[2]+"%' ";
					whereb=true;
				}
			}
			if(cons[3]!=null&&cons[3].length()!=0){//车辆类别
				if (whereb){
					where1=where1+"and vehicle_type like '%"+cons[3]+"%' ";
				}else {
					where1=where1+" vehicle_type like '%"+cons[3]+"%' ";
					whereb=true;
				}
			}
			if(cons[4]!=null&&cons[4].length()!=0){//发动机类型
				if (whereb){
					where1=where1+"and engine like '%"+cons[4]+"%' ";
				}else {
					where1=where1+" engine like '%"+cons[4]+"%' ";
					whereb=true;
				}
			}
			if(cons[5]!=null&&cons[5].length()!=0){//离合器
				if (whereb){
					where1=where1+"and clutch like '%"+cons[5]+"%' ";
				}else {
					where1=where1+" clutch like '%"+cons[5]+"%' ";
					whereb=true;
				}
			}
			if(cons[6]!=null&&cons[6].length()!=0){//驱动形式
				if (whereb){
					where1=where1+"and drive like '%"+cons[6]+"%' ";
				}else {
					where1=where1+" drive like '%"+cons[6]+"%' ";
					whereb=true;
				}
			}
			if(cons[7]!=null&&cons[7].length()!=0){//变速箱类型
				if (whereb){
					where1=where1+"and transmission like '%"+cons[7]+"%' ";
				}else {
					where1=where1+" transmission like '%"+cons[7]+"%' ";
					whereb=true;
				}
			}
			if(cons[8]!=null&&cons[8].length()!=0){//后桥类型
				if (whereb){
					where1=where1+"and rear_axle like '%"+cons[8]+"%' ";
				}else {
					where1=where1+" rear_axle like '%"+cons[8]+"%' ";
					whereb=true;
				}
			}
			if(cons[9]!=null&&cons[9].length()!=0){//车架类型
				if (whereb){
					where1=where1+"and frame like '%"+cons[9]+"%' ";
				}else {
					where1=where1+" frame like '%"+cons[9]+"%' ";
					whereb=true;
				}
			}
			if(cons[10]!=null&&cons[10].length()!=0){//轴距
				if (whereb){
					where1=where1+"and wheelbase like '%"+cons[10]+"%' ";
				}else {
					where1=where1+" wheelbase like '%"+cons[10]+"%' ";
					whereb=true;
				}
			}
			if(cons[11]!=null&&cons[11].length()!=0){//轮胎类型
				if (whereb){
					where1=where1+"and wheel like '%"+cons[11]+"%' ";
				}else {
					where1=where1+" wheel like '%"+cons[11]+"%' ";
					whereb=true;
				}
			}
			if(cons[14]!=null&&cons[14].length()!=0){//开始时间
				if (whereb){
					where1=where1+"and gpsdate between '"+cons[14]+"' ";
				}else {
					where1=where1+" gpsdate between  '"+cons[14]+"' ";
					whereb=true;
				}
			}
			if(cons[15]!=null&&cons[15].length()!=0){//结束时间
				where1=where1+"and '"+cons[15]+"' ";
			}
			if (whereb){
				where1="where "+where1;
			}
			if (cons[12]!=null&&cons[12].length()!=0){//油耗最小滤波
				where2=" dayFuel>="+cons[12];
			}else{
				where2=" dayFuel>=5";
			}
			if (cons[13]!=null&&cons[13].length()!=0){//油耗最大滤波
				where2=where2+" and dayFuel<="+cons[13];
			}else {
				where2=where2+" and dayFuel<=100 ";
			}

		}else{
			where2=" dayFuel>=5 and dayFuel<=100 ";
		}
		String selectAllSql ="select chassis,avg(dayFuel) avg_fuel " + "from " + "(select * "
				+ "from (select chassis,gpsdate,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel "
				+ "from fact_table inner join dim_table on fact_table.terminal_id=dim_table.terminal_id "
				+ where1
				+ " group by chassis,gpsdate) "
				+ "where"
				+where2+" ) " + "group by chassis ;";
		System.out.println("\n查表:"+selectAllSql+"\n");
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery(selectAllSql);
		while (rs.next()) {
			columnCount++;
		}

		rs.close();
		stmt2.close();
		conn.close();
		return columnCount;

	}
	public double[][] findBar(HttpServletRequest request) throws SQLException {
		int start=0;
		int end=100;
		String cont=request.getParameter("parm");
		System.out.println(cont);
		String[] cons=new String[16];
		String conts[]=cont.split(";");
//		System.out.println(conts.length);
		for(int i=0;i<conts.length;i++){
			cons[i]=conts[i];
			System.out.print("h"+conts[i]);
		}
//		System.out.println(cons[5]);
		String where1="";//一般筛选条件
		String where2="";//油耗滤波条件
		boolean whereb=false;
		if (conts.length!=0){
			if(cons[0]!=null&&cons[0].length()!=0){//底盘号
				whereb=true;
				where1=where1+" chassis like '%"+cons[0]+"%' ";
			}
			if(cons[1]!=null&&cons[1].length()!=0){//车牌号
				if (whereb){
					where1=where1+"and car_cph like '%"+cons[1]+"%' ";
				}else {
					where1=where1+" car_cph like '%"+cons[1]+"%' ";
					whereb=true;
				}
			}
			if(cons[2]!=null&&cons[2].length()!=0){//平台类型
				if (whereb){
					where1=where1+"and platform like '%"+cons[2]+"%' ";
				}else {
					where1=where1+" platform like '%"+cons[2]+"%' ";
					whereb=true;
				}
			}
			if(cons[3]!=null&&cons[3].length()!=0){//车辆类别
				if (whereb){
					where1=where1+"and vehicle_type like '%"+cons[3]+"%' ";
				}else {
					where1=where1+" vehicle_type like '%"+cons[3]+"%' ";
					whereb=true;
				}
			}
			if(cons[4]!=null&&cons[4].length()!=0){//发动机类型
				if (whereb){
					where1=where1+"and engine like '%"+cons[4]+"%' ";
				}else {
					where1=where1+" engine like '%"+cons[4]+"%' ";
					whereb=true;
				}
			}
			if(cons[5]!=null&&cons[5].length()!=0){//离合器
				if (whereb){
					where1=where1+"and clutch like '%"+cons[5]+"%' ";
				}else {
					where1=where1+" clutch like '%"+cons[5]+"%' ";
					whereb=true;
				}
			}
			if(cons[6]!=null&&cons[6].length()!=0){//驱动形式
				if (whereb){
					where1=where1+"and drive like '%"+cons[6]+"%' ";
				}else {
					where1=where1+" drive like '%"+cons[6]+"%' ";
					whereb=true;
				}
			}
			if(cons[7]!=null&&cons[7].length()!=0){//变速箱类型
				if (whereb){
					where1=where1+"and transmission like '%"+cons[7]+"%' ";
				}else {
					where1=where1+" transmission like '%"+cons[7]+"%' ";
					whereb=true;
				}
			}
			if(cons[8]!=null&&cons[8].length()!=0){//后桥类型
				if (whereb){
					where1=where1+"and rear_axle like '%"+cons[8]+"%' ";
				}else {
					where1=where1+" rear_axle like '%"+cons[8]+"%' ";
					whereb=true;
				}
			}
			if(cons[9]!=null&&cons[9].length()!=0){//车架类型
				if (whereb){
					where1=where1+"and frame like '%"+cons[9]+"%' ";
				}else {
					where1=where1+" frame like '%"+cons[9]+"%' ";
					whereb=true;
				}
			}
			if(cons[10]!=null&&cons[10].length()!=0){//轴距
				if (whereb){
					where1=where1+"and wheelbase like '%"+cons[10]+"%' ";
				}else {
					where1=where1+" wheelbase like '%"+cons[10]+"%' ";
					whereb=true;
				}
			}
			if(cons[11]!=null&&cons[11].length()!=0){//轮胎类型
				if (whereb){
					where1=where1+"and wheel like '%"+cons[11]+"%' ";
				}else {
					where1=where1+" wheel like '%"+cons[11]+"%' ";
					whereb=true;
				}
			}
			if(cons[14]!=null&&cons[14].length()!=0){//开始时间
				if (whereb){
					where1=where1+"and gpsdate between '"+cons[14]+"' ";
				}else {
					where1=where1+" gpsdate between  '"+cons[14]+"' ";
					whereb=true;
				}
			}
			if(cons[15]!=null&&cons[15].length()!=0){//结束时间
				where1=where1+"and '"+cons[15]+"' ";
			}
			if (whereb){
				where1="where "+where1;
			}
			if (cons[12]!=null&&cons[12].length()!=0){//油耗最小滤波
				where2=" dayFuel>="+cons[12];
				start=Integer.parseInt(cons[12]);
			}else{
				where2=" dayFuel>=5";
			}
			if (cons[13]!=null&&cons[13].length()!=0){//油耗最大滤波
				where2=where2+" and dayFuel<="+cons[13];
				end=Integer.parseInt(cons[13]);
			}else {
				where2=where2+" and dayFuel<=100 ";
			}

		}else{
			where2=" dayFuel>=5 and dayFuel<=100 ";
		}

		int lens=(end-start)/10;
		System.out.println("start:"+start+"end:"+end);
		double[] y=new double[]{0,0,0,0,0,0,0,0,0,0,0};//设置11个是为了存储右边界值
		int[][] x=new int[10][2];
		int count=0;
		for(int i=0;i<10;i++){
			x[i][0]=start+i*lens;
			x[i][1]=x[i][0]+lens;
		}
		//String sql="select chassis,avg(dayFuel) avg_fuel from (select * from (select chassis,gpsdate,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel from fact_table inner join dim_table on fact_table.terminal_id=dim_table.terminal_id  group by chassis,gpsdate) where dayFuel>=5 and dayFuel<=100  ) group by chassis";
		String selectAllSql ="select avg(dayFuel) avg_fuel " + "from " + "(select * "
				+ "from (select chassis,gpsdate,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel "
				+ "from fact_table inner join dim_table on fact_table.terminal_id=dim_table.terminal_id "
				+ where1
				+ " group by chassis,gpsdate) "
				+ "where"
				+where2+" ) " + "group by chassis ;";
		System.out.println("查 bar");
		JDBCUtil jdbcutil = new JDBCUtil();
		Connection conn = jdbcutil.connectKylin();
		Statement stmt3=conn.createStatement();
		ResultSet rs=stmt3.executeQuery(selectAllSql);
		while(rs.next()){
			double ful=rs.getDouble("avg_fuel");
			y[(int)(ful-start)/lens]++;
			count++;
		}
		y[9]=y[9]+y[10];
		java.text.DecimalFormat  df  =new java.text.DecimalFormat("#.00");
		if (count!=0){
			for(int i=0;i<10;i++){
				y[i]=Double.parseDouble(df.format(y[i]/count));
			}
		}


		System.out.println(y[9]+"hellO"+count);
		double[][] data=new double[10][3];
		for(int i=0;i<10;i++){
			data[i][0]=x[i][0];
			data[i][1]=x[i][1];
			data[i][2]=y[i];
		}
		rs.close();
		stmt3.close();
		conn.close();
		return data; // 到youhao.html
	}
	public JSONObject upload(MultipartFile excelFile,HttpServletRequest request) throws IOException, SQLException {
		String cont=request.getParameter("parm");
		String filename=excelFile.getOriginalFilename();
		System.out.println(filename);
		JSONObject ob=new JSONObject();
		InputStream inputStream = excelFile.getInputStream();
		XSSFWorkbook Workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = Workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		ArrayList<String> chassises=new ArrayList<>();
		System.out.println("文件行数："+lastRowNum);
		for (int rowNum=1;rowNum<lastRowNum;rowNum++){
			XSSFRow row=sheet.getRow(rowNum);
			String chassis=row.getCell(0).getStringCellValue();
			chassises.add(chassis);
		}

		Workbook.close();
		inputStream.close();
		if (chassises.size()!=0){

			String inchassis="where chassis in ('"+chassises.get(0)+"'";
			for (int i=1;i<chassises.size();i++){
				inchassis=inchassis+",'"+chassises.get(i)+"'";
			}

			int start=0;
			int end=100;
			String[] cons=new String[16];
			String conts[]=cont.split(";");
			for(int i=0;i<conts.length;i++){
				cons[i]=conts[i];
				System.out.print("h"+conts[i]);
			}
			String where1="";//一般筛选条件
			String where2="";//油耗滤波条件
			boolean whereb=true;
			if (conts.length!=0){
				if(cons[0]!=null&&cons[0].length()!=0){//底盘号
					whereb=true;
					where1=where1+" and chassis like '%"+cons[0]+"%' ";
				}
				if(cons[1]!=null&&cons[1].length()!=0){//车牌号
					if (whereb){
						where1=where1+"and car_cph like '%"+cons[1]+"%' ";
					}else {
						where1=where1+" car_cph like '%"+cons[1]+"%' ";
						whereb=true;
					}
				}
				if(cons[2]!=null&&cons[2].length()!=0){//平台类型
					if (whereb){
						where1=where1+"and platform like '%"+cons[2]+"%' ";
					}else {
						where1=where1+" platform like '%"+cons[2]+"%' ";
						whereb=true;
					}
				}
				if(cons[3]!=null&&cons[3].length()!=0){//车辆类别
					if (whereb){
						where1=where1+"and vehicle_type like '%"+cons[3]+"%' ";
					}else {
						where1=where1+" vehicle_type like '%"+cons[3]+"%' ";
						whereb=true;
					}
				}
				if(cons[4]!=null&&cons[4].length()!=0){//发动机类型
					if (whereb){
						where1=where1+"and engine like '%"+cons[4]+"%' ";
					}else {
						where1=where1+" engine like '%"+cons[4]+"%' ";
						whereb=true;
					}
				}
				if(cons[5]!=null&&cons[5].length()!=0){//离合器
					if (whereb){
						where1=where1+"and clutch like '%"+cons[5]+"%' ";
					}else {
						where1=where1+" clutch like '%"+cons[5]+"%' ";
						whereb=true;
					}
				}
				if(cons[6]!=null&&cons[6].length()!=0){//驱动形式
					if (whereb){
						where1=where1+"and drive like '%"+cons[6]+"%' ";
					}else {
						where1=where1+" drive like '%"+cons[6]+"%' ";
						whereb=true;
					}
				}
				if(cons[7]!=null&&cons[7].length()!=0){//变速箱类型
					if (whereb){
						where1=where1+"and transmission like '%"+cons[7]+"%' ";
					}else {
						where1=where1+" transmission like '%"+cons[7]+"%' ";
						whereb=true;
					}
				}
				if(cons[8]!=null&&cons[8].length()!=0){//后桥类型
					if (whereb){
						where1=where1+"and rear_axle like '%"+cons[8]+"%' ";
					}else {
						where1=where1+" rear_axle like '%"+cons[8]+"%' ";
						whereb=true;
					}
				}
				if(cons[9]!=null&&cons[9].length()!=0){//车架类型
					if (whereb){
						where1=where1+"and frame like '%"+cons[9]+"%' ";
					}else {
						where1=where1+" frame like '%"+cons[9]+"%' ";
						whereb=true;
					}
				}
				if(cons[10]!=null&&cons[10].length()!=0){//轴距
					if (whereb){
						where1=where1+"and wheelbase like '%"+cons[10]+"%' ";
					}else {
						where1=where1+" wheelbase like '%"+cons[10]+"%' ";
						whereb=true;
					}
				}
				if(cons[11]!=null&&cons[11].length()!=0){//轮胎类型
					if (whereb){
						where1=where1+"and wheel like '%"+cons[11]+"%' ";
					}else {
						where1=where1+" wheel like '%"+cons[11]+"%' ";
						whereb=true;
					}
				}
				if(cons[14]!=null&&cons[14].length()!=0){//开始时间
					if (whereb){
						where1=where1+"and gpsdate between '"+cons[14]+"' ";
					}else {
						where1=where1+" gpsdate between  '"+cons[14]+"' ";
						whereb=true;
					}
				}
				if(cons[15]!=null&&cons[15].length()!=0){//结束时间
					where1=where1+"and '"+cons[15]+"' ";
				}
//				if (whereb){
//					where1=""+where1;
//				}
				if (cons[12]!=null&&cons[12].length()!=0){//油耗最小滤波
					where2=" dayFuel>="+cons[12];
					start=Integer.parseInt(cons[12]);
				}else{
					where2=" dayFuel>=5";
				}
				if (cons[13]!=null&&cons[13].length()!=0){//油耗最大滤波
					where2=where2+" and dayFuel<="+cons[13];
					end=Integer.parseInt(cons[13]);
				}else {
					where2=where2+" and dayFuel<=100 ";
				}

			}else{
				where2=" dayFuel>=5 and dayFuel<=100 ";
			}

//			String selectAllSql="select chassis from dim_table where chassis in ("+inchassis+")";
			String selectAllSql ="select chassis,avg(dayFuel) avg_fuel " + "from " + "(select * "
					+ "from (select chassis,gpsdate,(max(engtoltalfuel)-min(engtoltalfuel))*100/(max(gpsdistance)-min(gpsdistance)) dayFuel "
					+ "from fact_table inner join dim_table on fact_table.terminal_id=dim_table.terminal_id "
					+ inchassis+")"+where1
					+ "group by chassis,gpsdate) "
					+ "where"
					+where2+" ) " + "group by chassis ;";
			System.out.println("文件查询"+selectAllSql);

			JDBCUtil jdbcutil = new JDBCUtil();
			Connection conn = jdbcutil.connectKylin();
			Statement stmt4=conn.createStatement();
			ResultSet rs=stmt4.executeQuery(selectAllSql);

			int count=0;
			ArrayList<Youhao> youhaoList=new ArrayList<Youhao>();

			int lens=(end-start)/10;
			double[] y=new double[]{0,0,0,0,0,0,0,0,0,0,0};//设置11个是为了存储右边界值
			int[][] x=new int[10][2];
			for(int i=0;i<10;i++){
				x[i][0]=start+i*lens;
				x[i][1]=x[i][0]+lens;
			}
			while(rs.next()){
				Youhao youhao = new Youhao();
				youhao.setChassis(rs.getString("CHASSIS"));
				youhao.setAvg_fuel(rs.getString("AVG_FUEL"));
				youhaoList.add(youhao);
				double ful=rs.getDouble("avg_fuel");
				y[(int)(ful-start)/lens]++;
				count++;
			}
			y[9]=y[9]+y[10];
			java.text.DecimalFormat  df  =new java.text.DecimalFormat("#.00");
			if (count!=0){
				for(int i=0;i<10;i++){
					y[i]=Double.parseDouble(df.format(y[i]/count));
				}
			}
			System.out.println(y[9]+"hellO"+count);
			double[][] data=new double[10][3];
			for(int i=0;i<10;i++){
				data[i][0]=x[i][0];
				data[i][1]=x[i][1];
				data[i][2]=y[i];
			}
			ob.put("total", count);
			ob.put("rows", youhaoList);
			ob.put("bardata",data);
			rs.close();
			stmt4.close();
			conn.close();

		}else{
			ob.put("data","hello hi");
		}

		return ob;
	}

}
