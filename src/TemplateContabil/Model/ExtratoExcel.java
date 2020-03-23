package TemplateContabil.Model;

import Auxiliar.LctoTemplate;
import Auxiliar.Valor;
import Entity.ErroIgnorar;
import JExcel.JExcel;
import Robo.View.roboView;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExtratoExcel {

    File arquivo;
    List<LctoTemplate> lctos = new ArrayList<>();
    XSSFWorkbook wk;
    XSSFSheet sheet;

    public ExtratoExcel(File arquivo) {
        this.arquivo = arquivo;
    }

    public List<LctoTemplate> getLctos() {
        return lctos;
    }

    public void setLctos(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaEntrada, String colunaSaida){
        setLctos(colunaData, colunaDoc, colunaPreTexto, colunasHistorico, colunaEntrada, colunaSaida, "");
    }
    
    public void setLctos(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaValor){
        setLctos(colunaData, colunaDoc, colunaPreTexto, colunasHistorico, "", "", colunaValor);
    }
    
    private void setLctos(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaEntrada, String colunaSaida, String colunaValor) {
        try {
            System.out.println("Definindo workbook de " + arquivo.getName());
            wk = new XSSFWorkbook(arquivo);
            System.out.println("Definindo Sheet de " + arquivo.getName());
            sheet = wk.getSheetAt(0);

            System.out.println("Iniciando extração em " + arquivo.getName());
            setLctosFromSheet(colunaData, colunaDoc, colunaPreTexto, colunasHistorico, colunaEntrada, colunaSaida, colunaValor);
            wk.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErroIgnorar("Ocorreu um erro inesperado ao tentar extrair os lançamentos do arquivo " + roboView.link(arquivo));
        }
    }

    private void setLctosFromSheet(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaEntrada, String colunaSaida, String colunaValor) {
        String[] colunasComplemento = colunasHistorico.split(";");
        for (Row row : sheet) {
            try {
                Cell celData =  row.getCell(JExcel.Cell(colunaData));
                String celDateValueString = JExcel.getStringCell(celData);
                Valor data = new Valor(celDateValueString);
                if (data.éUmaDataValida() || (!celDateValueString.equals("") && JExcel.isDateCell(celData))) {
                    //Converte Data
                    if(!data.éUmaDataValida()){
                        data.setString(JExcel.getStringDate(Integer.valueOf(data.getNumbersList().get(0))));
                    }
                    
                    String doc = "";
                    String preTexto = "";
                    String complemento = "";
                    Valor valor;

                    if (!colunaDoc.equals("")) {
                        doc = JExcel.getStringCell(row.getCell(JExcel.Cell(colunaDoc)));
                    }

                    if (!colunaPreTexto.equals("")) {
                        if(colunaPreTexto.contains("#")){
                            preTexto = colunaPreTexto.replaceAll("#", "");
                        }else{
                            preTexto = JExcel.getStringCell(row.getCell(JExcel.Cell(colunaPreTexto)));
                        }
                    }

                    if (!colunasComplemento.equals("")) {
                        StringBuilder sbComplemento = new StringBuilder();
                        for (String colunaComplemento : colunasComplemento) {
                            if (!colunaComplemento.equals("")) {
                                if (!sbComplemento.toString().equals("")) {
                                    sbComplemento.append(" - ");
                                }

                                String celComplementoString = JExcel.getStringCell(row.getCell(JExcel.Cell(colunaComplemento))); 
                                sbComplemento.append(celComplementoString);
                            }
                        }
                        
                        complemento = sbComplemento.toString();
                    }

                    if (colunaValor.equals("")) {
                        Valor entrada = new Valor(row.getCell(JExcel.Cell(colunaEntrada)).getNumericCellValue());
                        Valor saida = new Valor(row.getCell(JExcel.Cell(colunaSaida.replaceAll("-", ""))).getNumericCellValue());
                        if(colunaSaida.contains("-")){
                            saida = new Valor(saida.getBigDecimal().multiply(new BigDecimal("-1")));
                        }
                        
                        valor = entrada.getBigDecimal().compareTo(BigDecimal.ZERO) == 0 ? saida : entrada;
                    }else{
                        valor = new Valor(row.getCell(JExcel.Cell(colunaValor.replaceAll("-", ""))).getNumericCellValue());
                        if(colunaValor.contains("-")){
                            valor = new Valor(valor.getBigDecimal().multiply(new BigDecimal("-1")));
                        }
                    }

                    lctos.add(new LctoTemplate(data.getString(), doc, preTexto, complemento, valor));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
