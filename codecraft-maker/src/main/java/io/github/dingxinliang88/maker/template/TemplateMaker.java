package io.github.dingxinliang88.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.enums.FileGenerateTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.FileTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.ModelTypeEnum;
import io.github.dingxinliang88.maker.template.enums.FileFilterRangeEnum;
import io.github.dingxinliang88.maker.template.enums.FileFilterRuleEnum;
import io.github.dingxinliang88.maker.template.model.FileFilterConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerFileConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerModelConfig;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模板制作
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TemplateMaker {

    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath =
                new File(projectPath).getParent() + File.separator + "sample/springboot-init";
        String inputFilePath1 = "src/main/java/com/youyi/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType(ModelTypeEnum.STRING.getValue());
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType(ModelTypeEnum.STRING.getValue());
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(
                modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        // -------------------

        // 文件参数配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();

        // 文件组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("for test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        // 文件配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        long id = makeTemplate(meta, originProjectPath,
                templateMakerFileConfig, templateMakerModelConfig,
                1752657938160234496L);
        System.out.println("id = " + id);
    }

    /**
     * 生成模板文件。
     *
     * @param newMeta                  新的元信息对象
     * @param originProjectPath        原始项目路径
     * @param templateMakerFileConfig  模板制作文件配置
     * @param templateMakerModelConfig 模板制作模型配置
     * @param id                       模板文件的唯一标识
     * @return 模板文件的唯一标识
     */
    private static Long makeTemplate(Meta newMeta, String originProjectPath,
            TemplateMakerFileConfig templateMakerFileConfig,
            TemplateMakerModelConfig templateMakerModelConfig, Long id) {

        if (Objects.isNull(id)) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 0. 工作空间隔离
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = projectPath + File.separator + ".tmp";
        String templatePath = tmpDirPath + File.separator + id;

        // 首次制作，复制制作模板
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 1. 输入信息
        // 1.1 文件信息
        String sourceRootPath = templatePath + File.separator +
                FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        // 兼容 win
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        // 2. 生成文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            // 如果填的是相对路径，改成绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不存在目录）
            List<File> fileList = FileFilter.doFileFilter(inputFilePath,
                    fileInfoConfig.getFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, templateMakerModelConfig,
                        sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (Objects.nonNull(fileGroupConfig)) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setFiles(newFileInfoList); // 文件全放在一个分组下

            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        // 转换为可以接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        // 当前轮次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (Objects.nonNull(modelGroupConfig)) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();

            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setCondition(condition);

            // 模型放到一个分组下
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            newModelInfoList.addAll(inputModelInfoList);
        }

        // 3. 生成 meta.json 文件，和 template 同级
        String metaOutputPath = templatePath + File.separator + "meta.json";

        // 如果已有 meta.json 文件，说明不是第一次制作，则在 meta.json 的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // 2. 配置信息去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            // 1. 构造配置参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }

        // 2. 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);

        return id;
    }

    /**
     * 生成文件模板。
     *
     * @param inputFile                输入文件
     * @param templateMakerModelConfig 模型配置
     * @param sourceRootPath           源路径
     * @return 文件配置信息对象
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile,
            TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        // 1. 路径转换
        // 绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath()
                .replaceAll("\\\\", "/"); // 兼容 win
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 2. 使用字符串替换算法，生成 ftl 模板文件
        String fileContent;
        // 如果已有模板文件，说明不是第一次制作，需要在模板的基础上再次制作
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对同一个文件内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            if (Objects.isNull(modelGroupConfig)) {
                // 不是分组
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey,
                        modelInfoConfig.getFieldName()); // 多一个层级
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(),
                    replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 如果和源文件一致，则为静态生成
        if (newFileContent.equals(fileContent)) {
            // 输入路径 = 输出路径
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            // 生成动态模板
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }


    /**
     * 去除重复的文件信息，只保留唯一的文件路径。
     *
     * @param fileInfoList 文件信息列表
     * @return 去除重复文件信息后的列表
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(
            List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 针对分组的策略：相同分组下的文件 merge，不同分组保留

        // 1. 将所有文件配置 FileInfo 分为有分组和无分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                );

        // 2. 对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并，不同分组可同时保留
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>(); // 保存每个组对应的合并后的对象 map
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tmpFileInfoList = entry.getValue();
            // 按照 inputPath 进行去重
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                    tmpFileInfoList.stream()
                            .flatMap(fileInfo -> fileInfo.getFiles().stream())
                            .collect(
                                    Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o,
                                            (e, r) -> r)
                            ).values()
            );
            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tmpFileInfoList); // 最后一个元素是最新的
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 创建新的文件配置列表，将合并后的分组添加到列表
        List<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>(
                groupKeyMergedFileInfoMap.values());

        // 4. 将无分组的文件配置添加的列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultFileInfoList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o,
                                (e, r) -> r)
                ).values()));
        return resultFileInfoList;
    }

    /**
     * 去除重复的模型信息，只保留唯一的字段名。
     *
     * @param modelInfoList 模型信息列表
     * @return 去除重复模型信息后的列表
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(
            List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 针对分组的策略：相同分组下的模型 merge，不同分组保留

        // 1. 将所有模型配置 ModelInfo 分为有分组和无分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                );

        // 2. 对于有分组的模型配置，如果有相同的分组，同分组内的模型进行合并，不同分组可同时保留
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>(); // 保存每个组对应的合并后的对象 map
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tmpModelInfoList = entry.getValue();
            // 按照 fieldName 进行去重
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                    tmpModelInfoList.stream()
                            .flatMap(modelInfo -> modelInfo.getModels().stream())
                            .collect(
                                    Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,
                                            o -> o, (e, r) -> r)
                            ).values()
            );
            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(
                    tmpModelInfoList); // 最后一个元素是最新的
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 创建新的模型配置列表，将合并后的分组添加到列表
        List<Meta.ModelConfig.ModelInfo> resultModelInfoList = new ArrayList<>(
                groupKeyMergedModelInfoMap.values());

        // 4. 将无分组的模型配置添加的列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultModelInfoList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o,
                                (e, r) -> r)
                ).values()));
        return resultModelInfoList;
    }


}
