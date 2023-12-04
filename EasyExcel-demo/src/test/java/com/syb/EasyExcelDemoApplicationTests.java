package com.syb;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.syb.pojo.Employee;
import com.syb.utils.TestFileUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

@SpringBootTest
class EasyExcelDemoApplicationTests {

    private List<Employee> data(int num){
        ArrayList<Employee> list = ListUtils.newArrayList();//创建一个list集合
        for (int i = 0; i < num; i++) {
            Employee employee = new Employee();
            employee.setId(i);
            employee.setDate(new Date());
            employee.setName("sun"+i);
            employee.setSalary(1000.0+i);
            list.add(employee);
        }
        return list;
    }

    @Test
    public void testWrite(){
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, Employee.class).sheet("模板").doWrite(data(10));
    }

    @Test
    public void read() {
        // 写法1：JDK8+ ,不用额外写一个EmployeeListener
        // since: 3.0.0-beta1
        String fileName = TestFileUtil.getPath() + "simpleWrite1700044733223.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, Employee.class, new PageReadListener<Employee>(dataList -> {
            for (Employee Employee : dataList) {
                System.out.printf("读取到一条数据%s", JSON.toJSONString(Employee));
            }
        })).sheet().doRead();
    }

}
