package com.lan.accountbook.sys.controller;

import com.lan.accountbook.common.ResultObj;
import com.lan.accountbook.sys.domain.Bills;
import com.lan.accountbook.sys.domain.Billtype;
import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.service.AccountBillService;
import com.lan.accountbook.sys.service.BilltypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private AccountBillService billService;

    @Autowired
    private BilltypeService billTypeService;

    private static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/page")
    public String statisticsPage() {
        return "statistics";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResultObj getStatistics(@RequestParam String viewType,
                                   @RequestParam String target,
                                   @RequestParam(defaultValue = "0") Integer type,  // 0支出，1收入，2全部
                                   HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return new ResultObj(401, "请先登录");
        }

        // 查询用户所有账单
        List<Bills> allBills = billService.lambdaQuery()
                .eq(Bills::getUserId, loginUser.getId())
                .list();

        // 根据收支类型过滤账单（利用 billtype 表的 is_income 字段）
        List<Bills> filteredBills = new ArrayList<>();
        for (Bills bill : allBills) {
            Integer isIncome = getIncomeStatusByTypeId(bill.getTypeid());
            if (type == 2) { // 全部
                filteredBills.add(bill);
            } else if (type == 0 && isIncome == 0) {
                filteredBills.add(bill);
            } else if (type == 1 && isIncome == 1) {
                filteredBills.add(bill);
            }
        }

        if ("month".equals(viewType)) {
            return filterByMonth(filteredBills, target);
        } else {
            return filterByYear(filteredBills, target);
        }
    }

    private Integer getIncomeStatusByTypeId(Integer typeid) {
        if (typeid == null) return 0;
        Billtype billtype = billTypeService.getById(typeid);
        return billtype != null && billtype.getIsIncome() != null ? billtype.getIsIncome() : 0;
    }

    private ResultObj filterByMonth(List<Bills> bills, String yearMonth) {
        List<Bills> monthBills = bills.stream()
                .filter(b -> b.getBilltime() != null)
                .filter(b -> {
                    String ym = YEAR_MONTH_FORMAT.format(b.getBilltime());
                    return ym.equals(yearMonth);
                })
                .collect(Collectors.toList());

        // 分类汇总（饼图）
        Map<String, BigDecimal> categoryMap = new HashMap<>();
        for (Bills bill : monthBills) {
            String typeName = getTypeName(bill);
            BigDecimal amount = BigDecimal.valueOf(bill.getPrice());
            categoryMap.merge(typeName, amount, BigDecimal::add);
        }

        // 每日汇总（折线图）
        Map<String, BigDecimal> dailyMap = new TreeMap<>();
        for (Bills bill : monthBills) {
            String day = DAY_FORMAT.format(bill.getBilltime());
            BigDecimal amount = BigDecimal.valueOf(bill.getPrice());
            dailyMap.merge(day, amount, BigDecimal::add);
        }

        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieData.add(item);
        }

        List<Map<String, Object>> lineData = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : dailyMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("amount", entry.getValue());
            lineData.add(item);
        }

        double total = monthBills.stream().mapToDouble(Bills::getPrice).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("pie", pieData);
        result.put("line", lineData);
        result.put("total", total);
        return new ResultObj(200, "success", result);
    }

    private ResultObj filterByYear(List<Bills> bills, String year) {
        List<Bills> yearBills = bills.stream()
                .filter(b -> b.getBilltime() != null)
                .filter(b -> {
                    String y = YEAR_FORMAT.format(b.getBilltime());
                    return y.equals(year);
                })
                .collect(Collectors.toList());

        // 月度汇总（柱状图）
        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyMap.put(String.format("%02d", i), BigDecimal.ZERO);
        }
        for (Bills bill : yearBills) {
            String month = new SimpleDateFormat("MM").format(bill.getBilltime());
            BigDecimal amount = BigDecimal.valueOf(bill.getPrice());
            monthlyMap.merge(month, amount, BigDecimal::add);
        }

        List<Map<String, Object>> barData = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : monthlyMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("amount", entry.getValue());
            barData.add(item);
        }

        // 年度分类汇总（饼图）
        Map<String, BigDecimal> categoryYearMap = new HashMap<>();
        for (Bills bill : yearBills) {
            String typeName = getTypeName(bill);
            BigDecimal amount = BigDecimal.valueOf(bill.getPrice());
            categoryYearMap.merge(typeName, amount, BigDecimal::add);
        }
        List<Map<String, Object>> pieYearData = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryYearMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieYearData.add(item);
        }

        double total = yearBills.stream().mapToDouble(Bills::getPrice).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("bar", barData);
        result.put("pie", pieYearData);
        result.put("total", total);
        return new ResultObj(200, "success", result);
    }

    private String getTypeName(Bills bill) {
        if (bill.getTypeName() != null && !bill.getTypeName().isEmpty()) {
            return bill.getTypeName();
        }
        if (bill.getTypeid() != null) {
            Billtype type = billTypeService.getById(bill.getTypeid());
            if (type != null) return type.getName();
        }
        return "未分类";
    }
}