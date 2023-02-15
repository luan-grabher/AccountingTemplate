package TemplateContabil.Model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fileManager.FileManager;

public class Config {
    public static final String configFileName = "./accountingTemplate.json";
    public static final Map<String, String> config = loadConfig();

    public static Map<String, String> loadConfig(){
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            throw new Error("Arquivo de configuração não encontrado: " + configFile.getAbsolutePath());
        }

        //read json and parse
        String json = FileManager.getText(configFile);
        Map<String, Object> map = new Gson().fromJson(json, Map.class);

        String serverFolder = (String) map.get("serverFolder");
        serverFolder = serverFolder.replace(":serverName:", (String) map.get("serverName"));
        serverFolder = serverFolder.replace(":serverPathName:", (String) map.get("serverPathName"));
        serverFolder = serverFolder.replace(":departmentName:", (String) map.get("departmentName"));
        serverFolder = serverFolder.replace(":programsFolderName:", (String) map.get("programsFolderName"));
        serverFolder = serverFolder.replace(":companyName:", (String) map.get("companyName"));

        String templatePath = (String) map.get("templatePath");
        templatePath = templatePath.replace(":serverFolder:", serverFolder);
        templatePath = templatePath.replace(":templatesFolderName:", (String) map.get("templatesFolderName"));
        templatePath = templatePath.replace(":templateFileName:", (String) map.get("templateFileName"));

        /*
        "escrituracaoFolder" : "\\\\:serverName:\\DOCS\\Contábil\\Clientes\\:companyFolder:\\Escrituração mensal",
        "pastaArquivos" : ":escrituracaoFolder:\\:ano:\\:pastaAnual:\\:mes:.:ano::pastaMensal:"
         */
        String escrituracaoFolder = (String) map.get("escrituracaoFolder");
        escrituracaoFolder = escrituracaoFolder.replace(":serverName:", (String) map.get("serverName"));

        String pastaArquivos = (String) map.get("pastaArquivos");
        pastaArquivos = pastaArquivos.replace(":escrituracaoFolder:", escrituracaoFolder);

        Map<String, String> config = new HashMap<>();
        config.put("templatePath", templatePath);
        config.put("escrituracaoFolder", escrituracaoFolder);
        config.put("pastaArquivos", pastaArquivos);

        return config;
    }

    public static String getPastaArquivos(String companyFolder, String ano, String mes, String pastaAnual, String pastaMensal){
        String pastaArquivos = config.get("pastaArquivos");
        pastaArquivos = pastaArquivos.replace(":companyFolder:", companyFolder);
        pastaArquivos = pastaArquivos.replace(":ano:", ano);
        pastaArquivos = pastaArquivos.replace(":mes:", mes);
        pastaArquivos = pastaArquivos.replace(":pastaAnual:", pastaAnual);
        pastaArquivos = pastaArquivos.replace(":pastaMensal:", pastaMensal);

        return pastaArquivos;
    }
}
