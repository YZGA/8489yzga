package cn.edu.lingnan.studentsystemboot.controller;

import cn.edu.lingnan.studentsystemboot.common.Result;
import cn.edu.lingnan.studentsystemboot.pojo.Course;
import cn.edu.lingnan.studentsystemboot.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 查询全部/带关键词搜索 GET /api/courses
     */
    @GetMapping
    public Result<List<Course>> listAll(@RequestParam(required = false) String query) {
        List<Course> list;
        if (query == null || query.isBlank()) {
            list = courseService.listAll();
        } else {
            list = courseService.listByKeyword(query);
        }
        return Result.success(list);
    }

    /**
     * 根据ID查询单个 GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public Result<Course> getOne(@PathVariable Integer id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return Result.fail("课程不存在");
        }
        return Result.success(course);
    }

    /**
     * 新增课程 POST /api/courses
     */
    @PostMapping
    public Result<Integer> add(@RequestBody Course course) {
        int newId = courseService.addCourse(course);
        return Result.success(newId);
    }

    /**
     * 修改课程 PUT /api/courses/{id}
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody Course course) {
        course.setId(id);
        boolean ok = courseService.updateCourse(course);
        if (ok) {
            return Result.success(null);
        } else {
            return Result.fail("课程不存在或更新失败");
        }
    }

    /**
     * 删除课程 DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean ok = courseService.deleteById(id);
        if (ok) {
            return Result.success(null);
        } else {
            return Result.fail("课程不存在");
        }
    }

    /**
     * Excel导出 GET /api/courses/export
     */
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String fields,
            HttpServletResponse resp
    ) throws Exception {
        List<Course> dataList;
        if (query == null || query.isBlank()) {
            dataList = courseService.listAll();
        } else {
            dataList = courseService.listByKeyword(query);
        }

        // 默认导出全部字段
        String[] fieldArr;
        if (fields == null || fields.isBlank()) {
            fieldArr = new String[]{"id","courseNo","courseName","teacher","credit"};
        } else {
            fieldArr = fields.split(",");
        }

        // 表头中文映射
        String[] titleMap = {"编号","课程号","课程名称","授课教师","学分"};
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("课程列表");

        // 表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fieldArr.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(titleMap[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int rowIdx = 0; rowIdx < dataList.size(); rowIdx++) {
            Course c = dataList.get(rowIdx);
            Row row = sheet.createRow(rowIdx + 1);
            int col = 0;
            for (String f : fieldArr) {
                switch (f) {
                    case "id": row.createCell(col).setCellValue(c.getId()); break;
                    case "courseNo": row.createCell(col).setCellValue(c.getCourseNo() == null ? "" : c.getCourseNo()); break;
                    case "courseName": row.createCell(col).setCellValue(c.getCourseName() == null ? "" : c.getCourseName()); break;
                    case "teacher": row.createCell(col).setCellValue(c.getTeacher() == null ? "" : c.getTeacher()); break;
                    case "credit": row.createCell(col).setCellValue(c.getCredit() == null ? 0 : c.getCredit()); break;
                }
                row.getCell(col).setCellStyle(dataStyle);
                sheet.autoSizeColumn(col);
                col++;
            }
        }

        // 响应下载头
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setCharacterEncoding("UTF-8");
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = URLEncoder.encode("课程列表_" + dateStr + ".xlsx", "UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        OutputStream out = resp.getOutputStream();
        workbook.write(out);
        out.flush();
        workbook.close();
    }
}