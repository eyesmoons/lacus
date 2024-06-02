package com.lacus.utils.poi;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.lacus.common.annotation.ExcelColumn;
import com.lacus.common.annotation.ExcelSheet;
import com.lacus.common.exception.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.lacus.common.exception.error.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 自定义Excel 导入导出工具
 */
public class CustomExcelUtil {


    public static void writeToResponse(List<?> list, Class<?> clazz, HttpServletResponse response) {
        try {
            writeToOutputStream(list, clazz, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR);
        }
    }

    public static List<?> readFromRequest(Class<?> clazz,  MultipartFile file) {
        try {
            return readFromInputStream(clazz, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.Internal.UNKNOWN_ERROR);
        }
    }

    public static void writeToOutputStream(List<?> list, Class<?> clazz, OutputStream outputStream) {

        // 通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();

        ExcelSheet sheetAnno = clazz.getAnnotation(ExcelSheet.class);

        if (sheetAnno != null) {
            // 默认的sheetName是 sheet1
            writer.renameSheet(sheetAnno.name());
        }

        Field[] fields = clazz.getDeclaredFields();

        //自定义标题别名
        for (Field field : fields) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
                writer.addHeaderAlias(field.getName(), annotation.name());
            }
        }

        // 默认的，未添加alias的属性也会写出，如果想只写出加了别名的字段，可以调用此方法排除之
        writer.setOnlyAlias(true);

        // 合并单元格后的标题行，使用默认标题样式
        // writer.merge(4, "一班成绩单"); 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        writer.flush(outputStream, true);
    }



    public static List<?> readFromInputStream(Class<?> clazz,  InputStream inputStream) {
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 去除掉excel中的html标签语言  避免xss攻击
        reader.setCellEditor(new TrimXssEditor());

        Field[] fields = clazz.getDeclaredFields();

        //自定义标题别名
        for (Field field : fields) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
                reader.addHeaderAlias(annotation.name(), field.getName());
            }
        }

        return reader.read(0, 1, clazz);
    }



}
