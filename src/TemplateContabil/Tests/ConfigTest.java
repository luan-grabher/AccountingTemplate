package TemplateContabil.Tests;

import TemplateContabil.Model.Config;

public class ConfigTest {
    public static void main(String[] args) {
        getConfig();
        getPastaArquivos();
    }

    private static void getConfig(){
        System.out.println(Config.config);
    }

    private static void getPastaArquivos(){
        System.out.println(Config.getPastaArquivos("Teste", "2020", "01", "PastaAnual", "\\PastaMensal"));
    }
}
