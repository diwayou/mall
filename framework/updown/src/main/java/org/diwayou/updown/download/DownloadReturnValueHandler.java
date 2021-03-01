package org.diwayou.updown.download;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.diwayou.core.result.ResultWrapper;
import org.diwayou.core.util.WebResponseUtil;
import org.diwayou.updown.annotation.ResponseFile;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author gaopeng 2021/2/7
 */
@Slf4j
public class DownloadReturnValueHandler implements HandlerMethodReturnValueHandler {

    private static final String DATA_SHEET_NAME = "结果";

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseFile.class) ||
                returnType.hasMethodAnnotation(ResponseFile.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");

        if (returnValue == null) {
            WebResponseUtil.writeJson(response, ResultWrapper.success());

            return;
        }

        ResponseFile responseFile = returnType.getMethodAnnotation(ResponseFile.class);
        if (responseFile == null) {
            responseFile = AnnotatedElementUtils.getMergedAnnotation(returnType.getContainingClass(), ResponseFile.class);
        }
        if (responseFile == null) {
            throw new IllegalStateException("未找到ResponseFile注解" + returnType);
        }

        String name = responseFile.name();
        if (StringUtils.isBlank(name)) {
            name = UUID.randomUUID().toString();
        }
        response.setContentType(new MediaType("application", "vnd.ms-excel", StandardCharsets.UTF_8).toString());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder("attachment")
                .filename(name + ExcelTypeEnum.XLSX.getValue(), StandardCharsets.UTF_8)
                .build().toString());

        DownloadCallback<?> callback;
        Class<?> dataClass;
        if (returnValue instanceof DownloadCallback<?>) {
            dataClass = ResolvableType.forType(
                    ((ParameterizedType) returnType.getGenericParameterType()).getActualTypeArguments()[0])
                    .resolve();

            callback = (DownloadCallback<?>) returnValue;
        } else if (returnValue instanceof Collection) {
            dataClass = ResolvableType.forType(
                    ((ParameterizedType) returnType.getGenericParameterType()).getActualTypeArguments()[0])
                    .resolve();

            callback = context -> {
                context.setFinished();
                return (List<Object>) returnValue;
            };
        } else {
            throw new RuntimeException("不能处理该返回结果" + returnType);
        }

        writeExcel(dataClass, callback, response.getOutputStream());
    }

    private void writeExcel(Class<?> dataClass, DownloadCallback<?> callback, OutputStream outputStream) {
        ExcelWriter excelWriter = EasyExcel.write(outputStream, dataClass).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(DATA_SHEET_NAME).build();

        try {
            List<?> dataList;
            DownloadContext context = new DownloadContext();

            do {
                dataList = callback.execute(context);

                excelWriter.write(dataList, writeSheet);

                if (CollectionUtils.isEmpty(dataList)) {
                    break;
                }
            } while (!context.isFinished());
        } catch (Exception e) {
            log.error("写excel失败", e);
            // 失败写入空数据，要不然excel格式可能有问题
            excelWriter.write(Collections.emptyList(), writeSheet);
        } finally {
            excelWriter.finish();
        }
    }
}
