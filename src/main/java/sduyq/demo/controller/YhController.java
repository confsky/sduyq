package sduyq.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sduyq.demo.dao.YouhaoDao;
import sduyq.demo.model.Youhao;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("system/YH")
public class YhController {
    private YouhaoDao yhdao=new YouhaoDao();
    @RequestMapping("YHpage")
    public String getPage(){
        return "youhao/yh";
    }
    @RequestMapping("yhtable")
    @ResponseBody
    public JSONObject Yhtable(Integer offset, Integer limit,String searchcondition, ModelMap map) throws SQLException {
        System.out.println("搜索参数："+searchcondition);
//
//        if (searchcondition!=null){
//            String conts[]=searchcondition.split(";");
//            System.out.println(conts[2]);
//        }

        JSONObject ob=new JSONObject();
//        YouhaoDao yhdao=new YouhaoDao();
        List<Youhao> youhaoList = yhdao.findYhtable(offset, limit,searchcondition);
        int totle=YouhaoDao.getTotal(searchcondition);
        ob.put("total", totle);
        ob.put("rows", youhaoList);
        return ob;
    }
    @RequestMapping("yhbar")
    @ResponseBody
    public double[][] YHbar(HttpServletRequest request) throws SQLException {
        double[][] backtest=new double[10][3];
//        YouhaoDao yhdao=new YouhaoDao();
        backtest=yhdao.findBar(request);
//        yhdao.closeconn();
        return backtest;
    }
    @RequestMapping("yhexcel")
    @ResponseBody
    public JSONObject YHexcel(@RequestParam(value = "upfile", required = false) MultipartFile excelFile, HttpServletRequest request) throws IOException, SQLException {
        System.out.println("接收");
        String cont=request.getParameter("parm");
        System.out.println(cont);
        if(excelFile!=null&&!excelFile.isEmpty()){
            String filename=excelFile.getOriginalFilename();
            System.out.println(filename);
        }else{
            System.out.println("no file");
        }

        JSONObject ob= yhdao.upload(excelFile,request);
//        ob.put("data","hello hi");
        return ob;
    }
}
