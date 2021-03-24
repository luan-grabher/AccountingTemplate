package TemplateContabil.Model.Entity;

import java.math.BigDecimal;
import java.text.Normalizer;

public class LctoTemplate {

    private String data = "1900-01-01";
    private String documento = "";
    private String prefixoHistorico = "";
    private String complementoHistorico = "";
    private BigDecimal valor =  new BigDecimal("0.00");
    private String entrada_Saida = "E";



    public LctoTemplate(String data, String documento, String prefixoHistorico, String complementoHistorico, BigDecimal valor) {
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
        if (valor.compareTo(BigDecimal.ZERO) == 1) {
            /*Transforma em positivo pois só usamos positivos*/
            valor = valor.negate();           
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

    public BigDecimal getValor() {
        return valor;
    }

    public String getEntrada_Saida() {
        return entrada_Saida;
    }

    public String getHistorico() {
        String historico = "";
        if (documento != null) historico += documento.equals("") ? "" : documento + " - ";        
        if (prefixoHistorico != null) historico += prefixoHistorico.equals("") ? "" : prefixoHistorico + " ";
        if (complementoHistorico != null) historico += complementoHistorico;
        historico = removerAcentos(historico);
        historico = historico.replaceAll("[^a-zA-Z0-9-./ ]", " ");
        return historico;
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
