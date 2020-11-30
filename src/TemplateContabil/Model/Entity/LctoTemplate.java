package TemplateContabil.Model.Entity;

import Auxiliar.Valor;

public class LctoTemplate {

    private String data = "1900-01-01";
    private String documento = "";
    private String prefixoHistorico = "";
    private String complementoHistorico = "";
    private Valor valor =  new Valor(0);
    private String entrada_Saida = "E";



    public LctoTemplate(String data, String documento, String prefixoHistorico, String complementoHistorico, Valor valor) {
        this.data = data;
        this.documento = documento;
        this.prefixoHistorico = prefixoHistorico;
        this.complementoHistorico = complementoHistorico;
        this.valor = valor;
        setEntrada_Saida();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setPrefixoHistorico(String prefixoHistorico) {
        this.prefixoHistorico = prefixoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    private void setEntrada_Saida() {
        //Entrada ou Saida e Valor
        if (valor.getString().contains("-")) {
            valor.setString(valor.getString().replaceAll("-", ""));
            entrada_Saida = "S";
        } else {
            entrada_Saida = "E";
        }
    }
     
    public String getPrefixoHistorico() {
        return prefixoHistorico;
    }

    public String getData() {
        return data;
    }

    public String getDocumento() {
        return documento;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public Valor getValor() {
        return valor;
    }

    public String getEntrada_Saida() {
        return entrada_Saida;
    }

    public String getHistorico() {
        String historico = documento.equals("") ? "" : documento + " - ";
        historico += prefixoHistorico.equals("") ? "" : prefixoHistorico + " ";
        historico += complementoHistorico;
        historico = historico.replaceAll("[^a-zA-Z0-9-./ ]", " ");
        return historico;
    }

}
