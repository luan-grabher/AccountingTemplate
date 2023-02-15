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

        Map<String, String> config = new HashMap<>();
        config.put("templatePath", templatePath);

        return config;
    }
}
