package com.syb.write;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.syb.pojo.Employee;
import com.syb.utils.TestFileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sun
 * @Date: 2023/11/15/17:24
 */
public class SimpleWrite {
    private List<Employee> data(int num){
        ArrayList<Employee> list = ListUtils.newArrayList();//创建一个list集合
        for (int i = 0; i < num; i++) {
            Employee employee = new Employee();
            employee.setId(i);
            employee.setName("sun"+i);
            employee.setSalary(1000.0+i);
            list.add(employee);
        }
        return list;
    }

    public void write(){
        // 写法1 JDK8+
        // since: 3.0.0-beta1
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, Employee.class)
                .sheet("模板")
                .doWrite(() -> {
                    // 分页查询数据
                    return data(10);
                });

        // 写法2
        fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, Employee.class).sheet("模板").doWrite(data(10));

        // 写法3
        fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写
        try (ExcelWriter excelWriter = EasyExcel.write(fileName, Employee.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
            excelWriter.write(data(10), writeSheet);
        }


    }
}
