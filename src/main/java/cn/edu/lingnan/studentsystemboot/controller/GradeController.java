package cn.edu.lingnan.studentsystemboot.controller;

import cn.edu.lingnan.studentsystemboot.common.Result;
import cn.edu.lingnan.studentsystemboot.pojo.Grade;
import cn.edu.lingnan.studentsystemboot.service.GradeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 分页查询 / 搜索全部成绩
     * GET /api/grades
     */
    @GetMapping
    public Result<Page<Grade>> page(
            @RequestParam(required = false) String q,
            // 默认值改为1，移除 +1 偏移
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        // 删掉 page + 1
        Page<Grade> pageData = gradeService.pageList(q, page, size);
        return Result.success(pageData);
    }

    /**
     * 根据ID单条查询 GET /api/grades/{id}
     */
    @GetMapping("/{id}")
    public Result<Grade> getOne(@PathVariable Integer id) {
        Grade g = gradeService.getById(id);
        if (g == null) return Result.fail("成绩不存在");
        return Result.success(g);
    }

    /**
     * 根据课程号查询 GET /api/grades/listByCourseNo
     */
    @GetMapping("/listByCourseNo")
    public Result<Map<String, Object>> listByCourseNo(@RequestParam String courseNo) {
        List<Grade> list = gradeService.listByCourseNo(courseNo);
        return Result.success(Map.of("records", list, "total", list.size()));
    }

    /**
     * 根据课程ID查询 GET /api/grades/listByCourse
     */
    @GetMapping("/listByCourse")
    public Result<Map<String, Object>> listByCourse(@RequestParam Integer course_id) {
        List<Grade> list = gradeService.listByCourseId(course_id);
        return Result.success(Map.of("records", list, "total", list.size()));
    }

    /**
     * 根据学生学号路径传参 GET /api/grades/student_no/{studentNo}
     */
    @GetMapping("/student_no/{studentNo}")
    public Result<Map<String, Object>> listByStuNoPath(@PathVariable String studentNo) {
        List<Grade> list = gradeService.listByStudentNo(studentNo);
        return Result.success(Map.of("records", list, "total", list.size()));
    }

    /**
     * 参数传学号查询
     */
    @GetMapping(params = "student_no")
    public Result<Map<String, Object>> listByStuNoParam(@RequestParam String student_no) {
        List<Grade> list = gradeService.listByStudentNo(student_no);
        return Result.success(Map.of("records", list, "total", list.size()));
    }

    /**
     * 大屏统计接口 GET /api/grades/stats
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(gradeService.getStatistics());
    }

    /**
     * 新增成绩 POST /api/grades
     */
    @PostMapping
    public Result<Integer> add(@RequestBody Grade grade) {
        // 空值预处理
        if (grade.getStudentNo() == null) grade.setStudentNo("");
        if (grade.getStudentName() == null) grade.setStudentName("");
        if (grade.getCourseNo() == null) grade.setCourseNo("");
        if (grade.getCourseName() == null) grade.setCourseName("");
        if (grade.getScore() == null) grade.setScore(0.0);
        if (grade.getGpa() == null) grade.setGpa(0.0);
        if (grade.getTerm() == null) grade.setTerm("");
        if (grade.getExamDate() == null) grade.setExamDate("");
        int newId = gradeService.add(grade);
        return Result.success(newId);
    }

    /**
     * 修改成绩 PUT /api/grades/{id}
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody Grade grade) {
        grade.setId(id);
        if (grade.getStudentNo() == null) grade.setStudentNo("");
        if (grade.getStudentName() == null) grade.setStudentName("");
        if (grade.getCourseNo() == null) grade.setCourseNo("");
        if (grade.getCourseName() == null) grade.setCourseName("");
        if (grade.getScore() == null) grade.setScore(0.0);
        if (grade.getGpa() == null) grade.setGpa(0.0);
        if (grade.getTerm() == null) grade.setTerm("");
        if (grade.getExamDate() == null) grade.setExamDate("");
        boolean ok = gradeService.update(grade);
        return ok ? Result.success(null) : Result.fail("成绩不存在或更新失败");
    }

    /**
     * 删除 DELETE /api/grades/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean ok = gradeService.deleteById(id);
        return ok ? Result.success(null) : Result.fail("成绩不存在");
    }

    /**
     * Excel导出 GET /api/grades/export
     */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String courseNo,
            @RequestParam(required = false) String scoreType,
            @RequestParam(required = false) Double scoreValue,
            HttpServletResponse resp
    ) throws Exception {
        List<Grade> dataList;
        if (courseNo != null && !courseNo.isBlank()) {
            if (scoreType != null && scoreValue != null) {
                dataList = gradeService.listByCourseNoFilter(courseNo, scoreType, scoreValue);
            } else {
                dataList = gradeService.listByCourseNo(courseNo);
            }
        } else {
            dataList = gradeService.listAll();
        }

        String[] headers = {"编号","学号","姓名","课程号","课程名","分数","绩点","学期","考试日期"};
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("成绩列表");

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < dataList.size(); i++) {
            Grade g = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(g.getId() == null ? "" : String.valueOf(g.getId()));
            row.createCell(1).setCellValue(g.getStudentNo() == null ? "" : g.getStudentNo());
            row.createCell(2).setCellValue(g.getStudentName() == null ? "" : g.getStudentName());
            row.createCell(3).setCellValue(g.getCourseNo() == null ? "" : g.getCourseNo());
            row.createCell(4).setCellValue(g.getCourseName() == null ? "" : g.getCourseName());
            row.createCell(5).setCellValue(g.getScore() == null ? 0D : g.getScore());
            row.createCell(6).setCellValue(g.getGpa() == null ? 0D : g.getGpa());
            row.createCell(7).setCellValue(g.getTerm() == null ? "" : g.getTerm());
            row.createCell(8).setCellValue(g.getExamDate() == null ? "" : g.getExamDate());

            for (int j = 0; j < headers.length; j++) {
                row.getCell(j).setCellStyle(dataStyle);
                sheet.autoSizeColumn(j);
            }
        }

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("成绩列表.xlsx", "UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        OutputStream out = resp.getOutputStream();
        wb.write(out);
        out.flush();
        wb.close();
    }
}