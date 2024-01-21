package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.JarGenerator;
import io.github.dingxinliang88.maker.generator.ScriptGenerator;
import io.github.dingxinliang88.maker.generator.file.DynamicFileGenerator;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.MetaManager;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainGenerator {

    public static void main(String[] args)
            throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();

        String projectPath = System.getProperty("user.dir");
        String destPath =
                projectPath + File.separator + "generated" + File.separator + meta.getName();

        if (FileUtil.exist(destPath)) {
            FileUtil.del(destPath);
        }
        FileUtil.mkdir(destPath);

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String srcPath = classPathResource.getAbsolutePath();

        // Java 包基础路径
        String basePackagePath = meta.getBasePackage();
        String destBasePackagePath = StrUtil.join(File.separator,
                StrUtil.split(basePackagePath, "."));
        String destBaseJavaPackagePath =
                destPath + File.separator + "src" + File.separator + "main" + File.separator
                        + "java" + File.separator
                        + destBasePackagePath;

        // model.DataModel
        String src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "model" + File.separator + "DataModel.java.ftl";
        String dest = destBaseJavaPackagePath + File.separator + "model" + File.separator
                + "DataModel.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ConfigCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ConfigCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ConfigCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.GenerateCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "GenerateCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "GenerateCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ListCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ListCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ListCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.CommandExecutor
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "CommandExecutor.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator
                + "CommandExecutor.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // Main
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "Main.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.DynamicGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "DynamicGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.StaticGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "StaticGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "StaticGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.MainGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator + "MainGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "MainGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // pom.xml
        src = srcPath + File.separator + "templates" + File.separator
                + "pom.xml.ftl";
        dest = destPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // 构建 Jar 包
        JarGenerator.doGenerate(destPath);

        // 封装脚本
        String shellDest = destPath + File.separator + "bin" + File.separator + "craft";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        String jarPath = destPath + File.separator + "target" + File.separator + jarName;
        ScriptGenerator.doGenerate(jarPath, shellDest);
    }

}