package com.syb.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sun
 * @Date: 2023/11/15/17:21
 * 与excel对应的实体类
 * 1、编写模型类并加入注解
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @ExcelProperty(value = "员工编号",index = 0)
    private int id;
    @ExcelProperty(value = "员工姓名",index = 1)
    private String name;
    @ExcelProperty(value = "入职日期",index = 2)
    private Date date;
    @ExcelProperty(value = "员工工资",index = 3)
    private double salary;
}
