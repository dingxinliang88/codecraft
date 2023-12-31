package com.juzi.model;

/**
 * 动态模板配置
 *
 * @author codejuzi
 */
public class MainTemplateModel {

    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者注释
     */
    private String author = "codejuzi";

    /**
     * 输出信息
     */
    private String outputText = "Sum = ";


    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public boolean isLoop() {
        return loop;
    }

    public String getAuthor() {
        return author;
    }

    public String getOutputText() {
        return outputText;
    }

    @Override
    public String toString() {
        return "MainTemplateModel{" +
                "loop=" + loop +
                ", author='" + author + '\'' +
                ", outputText='" + outputText + '\'' +
                '}';
    }
}
