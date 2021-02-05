package TemplateContabil.Model.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Importation {

    public final static int TIPO_INATIVO = -1;
    public final static int TIPO_OFX = 0;
    public final static int TIPO_EXCEL = 1;

    private int TIPO;

    private File file = new File("");
    private String nome = "";
    private String idTemplateConfig;
    private final Map<String, String> excelCols = new HashMap<>();
    private final Map<String, Map<String, String>> xlsxCols = new HashMap<>();
    private final List<LctoTemplate> lctos = new ArrayList<>();

    /**
     * Cria classe de configuração de lançamentos. Por padrão o tipo de
     * importação é OFX
     */
    public Importation() {
        TIPO = TIPO_OFX;
    }

    public Importation(int tipo) {
        TIPO = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getTIPO() {
        return TIPO;
    }

    public void setTIPO(int TIPO) {
        this.TIPO = TIPO;
    }

    /**
     * mapa de configuração de colunas
     */
    public Map<String, Map<String, String>> getXlsxCols() {
        return xlsxCols;
    }

    /**
     * Pega o mapa de colunas do Excel que indica qual a coluna que fica o
     * valor. Utilize os seguintes vetores: data, documento, pretexto,
     * historico, entrada, saida, valor
     *
     * @return o mapa de colunas Excel
     */
    public Map<String, String> getExcelCols() {
        return excelCols;
    }

    public List<LctoTemplate> getLctos() {
        return lctos;
    }

    public String getIdTemplateConfig() {
        return idTemplateConfig;
    }

    public void setIdTemplateConfig(String idTemplateConfig) {
        this.idTemplateConfig = idTemplateConfig;
    }
}
