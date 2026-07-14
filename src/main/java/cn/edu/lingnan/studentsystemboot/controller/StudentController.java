package cn.edu.lingnan.studentsystemboot.controller;

import cn.edu.lingnan.studentsystemboot.common.Result;
import cn.edu.lingnan.studentsystemboot.pojo.Student;
import cn.edu.lingnan.studentsystemboot.service.StudentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 分页查询列表 GET /api/students
     */
    @GetMapping
    public Result<Page<Student>> pageQuery(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Student> pageData = studentService.pageList(q, page + 1, size);
        return Result.success(pageData);
    }

    /**
     * 根据id查询单个 GET /api/students/{id}
     */
    @GetMapping("/{id}")
    public Result<Student> getOne(@PathVariable Integer id) {
        Student student = studentService.getById(id);
        if (student == null) {
            return Result.fail("学生不存在");
        }
        // 填充头像
        try {
            String seed = URLEncoder.encode(student.getName(), java.nio.charset.StandardCharsets.UTF_8);
            student.setAvatar("https://api.dicebear.com/7.x/initials/svg?seed=" + seed);
        } catch (Exception ignore) {
        }
        return Result.success(student);
    }

    /**
     * 新增学生 POST /api/students
     */
    @PostMapping
    public Result<Integer> add(@RequestBody Student student) {
        int newId = studentService.addStudent(student);
        return Result.success(newId);
    }

    /**
     * 修改学生 PUT /api/students/{id}
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody Student student) {
        student.setId(id);
        boolean ok = studentService.updateStudent(student);
        if (ok) {
            return Result.success(null);
        } else {
            return Result.fail("学生不存在");
        }
    }

    /**
     * 删除学生 DELETE /api/students/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean ok = studentService.deleteById(id);
        if (ok) {
            return Result.success(null);
        } else {
            return Result.fail("学生不存在");
        }
    }

    /**
     * Excel导出 GET /api/students/export
     */
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String ids,
            HttpServletResponse resp
    ) throws Exception {
        List<Student> studentList;
        if (ids != null && !ids.isBlank()) {
            List<Integer> idArr = new ArrayList<>();
            Arrays.stream(ids.split(",")).forEach(str -> idArr.add(Integer.parseInt(str)));
            studentList = studentService.listByIds(idArr);
        } else {
            studentList = studentService.listAllByQ(q);
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学生列表");
        String[] headers = {"学号", "姓名", "性别", "班级", "电话", "邮箱", "身份证号", "入学日期", "状态"};

        // 表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 0; i < studentList.size(); i++) {
            Student s = studentList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(s.getStudentNo() == null ? "" : s.getStudentNo());
            row.createCell(1).setCellValue(s.getName() == null ? "" : s.getName());
            row.createCell(2).setCellValue(s.getGender() == null ? "" : s.getGender());
            row.createCell(3).setCellValue(s.getClazz() == null ? "" : s.getClazz());
            row.createCell(4).setCellValue(s.getPhone() == null ? "" : s.getPhone());
            row.createCell(5).setCellValue(s.getEmail() == null ? "" : s.getEmail());
            row.createCell(6).setCellValue(s.getIdCard() == null ? "" : s.getIdCard());
            row.createCell(7).setCellValue(s.getEnrollDate() == null ? "" : s.getEnrollDate());
            row.createCell(8).setCellValue(s.getStatus() == null ? "在读" : s.getStatus());

            for (int j = 0; j < headers.length; j++) {
                row.getCell(j).setCellStyle(dataStyle);
                sheet.autoSizeColumn(j);
            }
        }

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("学生列表_" + System.currentTimeMillis() + ".xlsx", "UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        OutputStream out = resp.getOutputStream();
        workbook.write(out);
        out.flush();
        workbook.close();
    }
}