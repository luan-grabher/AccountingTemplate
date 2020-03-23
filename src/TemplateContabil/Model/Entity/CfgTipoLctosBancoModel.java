package TemplateContabil.Model.Entity;

import java.io.File;

public class CfgTipoLctosBancoModel {

    public final static int TIPO_INATIVO = -1;
    public final static int TIPO_OFX = 0;
    public final static int TIPO_EXCEL = 1;

    protected int TIPO = TIPO_OFX;

    protected File file =  new File("");
    protected String excel_colunaData = "";
    protected String excel_colunaDoc = "";
    protected String excel_colunaPreTexto = "";
    protected String excel_colunaComplementoHistorico = "";
    protected String excel_colunaEntrada = "";
    protected String excel_colunaSaida = "";
    protected String excel_colunaValor = "";

    public CfgTipoLctosBancoModel() {
    }

    public CfgTipoLctosBancoModel(int tipo) {
        TIPO = tipo;
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

    public String getExcel_colunaData() {
        return excel_colunaData;
    }

    public void setExcel_colunaData(String excel_colunaData) {
        this.excel_colunaData = excel_colunaData;
    }

    public String getExcel_colunaDoc() {
        return excel_colunaDoc;
    }

    public void setExcel_colunaDoc(String excel_colunaDoc) {
        this.excel_colunaDoc = excel_colunaDoc;
    }

    public String getExcel_colunaPreTexto() {
        return excel_colunaPreTexto;
    }

    public void setExcel_colunaPreTexto(String excel_colunaPreTexto) {
        this.excel_colunaPreTexto = excel_colunaPreTexto;
    }

    public String getExcel_colunaComplementoHistorico() {
        return excel_colunaComplementoHistorico;
    }

    public void setExcel_colunaComplementoHistorico(String excel_colunaComplementoHistorico) {
        this.excel_colunaComplementoHistorico = excel_colunaComplementoHistorico;
    }

    public String getExcel_colunaEntrada() {
        return excel_colunaEntrada;
    }

    public void setExcel_colunaEntrada(String excel_colunaEntrada) {
        this.excel_colunaEntrada = excel_colunaEntrada;
    }

    public String getExcel_colunaSaida() {
        return excel_colunaSaida;
    }

    public void setExcel_colunaSaida(String excel_colunaSaida) {
        this.excel_colunaSaida = excel_colunaSaida;
    }

    public String getExcel_colunaValor() {
        return excel_colunaValor;
    }

    public void setExcel_colunaValor(String excel_colunaValor) {
        this.excel_colunaValor = excel_colunaValor;
    }

    public CfgTipoLctosBancoModel(String excel_colunaData, String excel_colunaDoc, String excel_colunaPreTexto, String excel_colunaComplementoHistorico, String excel_colunaEntrada, String excel_colunaSaida, String excel_colunaValor) {
        this.excel_colunaData = excel_colunaData;
        this.excel_colunaDoc = excel_colunaDoc;
        this.excel_colunaPreTexto = excel_colunaPreTexto;
        this.excel_colunaComplementoHistorico = excel_colunaComplementoHistorico;
        this.excel_colunaEntrada = excel_colunaEntrada;
        this.excel_colunaSaida = excel_colunaSaida;
        this.excel_colunaValor = excel_colunaValor;
    }

}
