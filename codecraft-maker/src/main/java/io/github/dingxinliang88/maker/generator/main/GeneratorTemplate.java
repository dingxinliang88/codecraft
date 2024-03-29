package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.generator.JarGenerator;
import io.github.dingxinliang88.maker.generator.ScriptGenerator;
import io.github.dingxinliang88.maker.generator.file.DynamicFileGenerator;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.MetaManager;
import java.io.File;
import java.io.IOException;

/**
 * 代码生成模板类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public abstract class GeneratorTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        // 读取元数据
        Meta meta = MetaManager.getMeta();

        // 0. 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath =
                projectPath + File.separator + "generated/" + File.separator + meta.getName();
        // 清空目标目录
        if (FileUtil.exist(outputPath)) {
            FileUtil.del(outputPath);
        }
        FileUtil.mkdir(outputPath);

        // 生成
        doGenerate(meta, outputPath);
    }

    public void doGenerate(Meta meta, String outputPath)
            throws TemplateException, IOException, InterruptedException {
        // 1. 复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath);

        // 3. 构建 Jar 包
        String jarPath = buildJar(meta, outputPath);

        // 4. 封装脚本
        String shellOutputFilePath = buildScript(outputPath, jarPath);

        // 5. 生成精简版的程序（产物）
        buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputFilePath);
    }

    protected String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, true);
        return sourceCopyDestPath;
    }

    protected void generateCode(Meta meta, String outputPath)
            throws IOException, TemplateException {
        String inputResourcePath = "";

        // Java 包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join(File.separator,
                StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath =
                outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath =
                inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.JsonGenerateCommand
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/cli/command/JsonGenerateCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/JsonGenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandRegistry
        inputFilePath =
                inputResourcePath + File.separator + "templates/java/cli/CommandRegistry.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/CommandRegistry.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.valid.CommandPreParser
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/cli/valid/CommandPreParser.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/valid/CommandPreParser.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath =
                inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator
                + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // TODO 可以考虑让 AI 在生成文档的最后，再额外补充一段生成的话术，比如求 star 等等
        // README.md
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // TODO 考虑 .gitignore 文件的 ftl 模板，区分项目，比如后端，前端，小程序，等等
        // .gitignore
        inputFilePath = inputResourcePath + File.separator + "templates/.gitignore.ftl";
        outputFilePath = outputPath + File.separator + ".gitignore";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }

    protected String buildJar(Meta meta, String outputPath)
            throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        return "target" + File.separator + jarName;
    }

    protected String buildScript(String outputPath, String jarPath) {
        String shellOutputPath = outputPath + File.separator + "craft";
        ScriptGenerator.doGenerate(jarPath, shellOutputPath);
        return shellOutputPath;
    }

    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath,
            String shellOutputFilePath) {
        String distOutputPath = outputPath + "-dist";
        // 拷贝 jar 包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // 拷贝脚本文件
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        // 拷贝源模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
        return distOutputPath;
    }

    protected String buildZip(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }
}
