package io.github.dingxinliang88.maker.template.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板生成器模型配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;
    private ModelGroupConfig modelGroupConfig;

    /**
     * 文件目录配置
     * <p>
     * fieldName 目前只支持 basePackage
     */
    private ModelInfoConfig fileDirPathConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {

        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        // 是否必填，不针对模型组和模型组内字段
        private Boolean required;
        // 替换文本
        private String replaceText;
    }

    @NoArgsConstructor
    @Data
    public static class ModelGroupConfig {

        private String condition;
        private String groupKey;
        private String groupName;
        private String type;
        private String description;
    }
}
