package TemplateContabil.Model;

import TemplateContabil.Model.Entity.LctoTemplate;
import Auxiliar.Valor;
import Entity.ErrorIgnore;
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

    /**
     * Adiciona os lançamentos do arquivo Excel com base nas colunas passadas.
     *
     * @param colunaData
     * @param colunaDoc
     * @param colunaPreTexto
     * @param colunaValor
     * @param colunaEntrada
     * @param colunasHistorico
     * @param colunaSaida
     */
    public void setLctos(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaEntrada, String colunaSaida, String colunaValor) {
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
            throw new ErrorIgnore("Ocorreu um erro inesperado ao tentar extrair os lançamentos do arquivo " + roboView.link(arquivo));
        }
    }

    /**
     * Adiciona os lançamentos do arquivo Excel com base nas colunas passadas. A
     * sheet e workbook ja devem estar definidos
     *
     * @param colunaData
     * @param colunaDoc
     * @param colunaPreTexto Para definir um pretexto bruto ao invés de uma
     * coluna coloque "#" na frente
     * @param colunasHistorico Coloque as colunas que compoem o historico
     * separados por ";" na ordem em que aparecem
     * @param colunaEntrada coluna com valores de entrada
     * @param colunaSaida coluna com valores de saida, tem que colocar "-" na
     * frente caso no excel os valores apareçam positivos
     * @param colunaValor Coluna que possui valores de entrada e saida(com sinal
     * -)
     */
    private void setLctosFromSheet(String colunaData, String colunaDoc, String colunaPreTexto, String colunasHistorico, String colunaEntrada, String colunaSaida, String colunaValor) {
        if (colunaData != null && !colunaData.isBlank()
                && colunasHistorico != null && !colunasHistorico.isBlank()) {
            //Separa as colunas de historico
            String[] colunasComplemento = colunasHistorico.split(";");

            for (Row row : sheet) {
                try {
                    Cell celData = row.getCell(JExcel.Cell(colunaData));
                    //Se a celula da data existir
                    if (celData != null) {
                        String celDateValueString = JExcel.getStringCell(celData);
                        Valor data = new Valor(celDateValueString);
                        if (data.éUmaDataValida() || (!celDateValueString.equals("") && JExcel.isDateCell(celData))) {
                            //Converte Data se for data excel
                            if (!data.éUmaDataValida()) {
                                data.setString(JExcel.getStringDate(Integer.valueOf(data.getNumbersList().get(0))));
                            }

                            String doc = "";
                            String preTexto = "";
                            String complemento = "";
                            BigDecimal value;

                            //Define o documento se tiver
                            if (colunaDoc != null && !colunaDoc.equals("")) {
                                doc = JExcel.getStringCell(row.getCell(JExcel.Cell(colunaDoc)));
                            }

                            //Define o pretexto se tiver
                            if (colunaPreTexto != null && !colunaPreTexto.equals("")) {
                                if (colunaPreTexto.contains("#")) {
                                    preTexto = colunaPreTexto.replaceAll("#", "");
                                } else {
                                    Cell cell = row.getCell(JExcel.Cell(colunaPreTexto));
                                    if (cell != null) {
                                        preTexto = JExcel.getStringCell(cell);
                                    }
                                }
                            }

                            //Define o completemento se tiver
                            if (colunasComplemento.length > 0) {
                                StringBuilder sbComplemento = new StringBuilder();
                                for (String colunaComplemento : colunasComplemento) {
                                    if (!colunaComplemento.equals("")) {
                                        if (!sbComplemento.toString().equals("")) {
                                            sbComplemento.append(" - ");
                                        }

                                        Cell cell = row.getCell(JExcel.Cell(colunaComplemento));

                                        if (cell != null) {
                                            String celComplementoString = JExcel.getStringCell(cell);
                                            sbComplemento.append(celComplementoString);
                                        }
                                    }
                                }

                                complemento = sbComplemento.toString();
                            }

                            if (colunaValor == null || colunaValor.equals("")) {
                                //Pega celulas
                                Cell entryCell = row.getCell(JExcel.Cell(colunaEntrada));
                                Cell exitCell = row.getCell(JExcel.Cell(colunaSaida.replaceAll("-", "")));

                                //Cria variavel de valores
                                BigDecimal entryBD = getBigDecimalFromCell(entryCell, false);
                                BigDecimal exitBD = getBigDecimalFromCell(exitCell, colunaSaida.contains("-"));

                                value = entryBD.compareTo(BigDecimal.ZERO) == 0 ? exitBD : entryBD;
                            } else {
                                //Pega celula
                                Cell cell = row.getCell(JExcel.Cell(colunaValor.replaceAll("-", "")));

                                value = getBigDecimalFromCell(cell, colunaValor.contains("-"));
                            }

                            //Se valor for diferente de zero
                            if (value.compareTo(BigDecimal.ZERO) != 0) {
                                lctos.add(new LctoTemplate(data.getString(), doc, preTexto, complemento, new Valor(value)));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            throw new Error("A coluna de extração da data e historico não podem ficar em branco!");
        }
    }

    /**
     * Pega bigdecimal de uma celula do excel numerica
     * 
     * @param cell CElula que ira pegar numero
     * @param forceNegative Se deve multiplicar por -1 o numero se for positivo
     * @return celula em número BigDecimal
     */
    private BigDecimal getBigDecimalFromCell(Cell cell, boolean forceNegative) {
        //Pega texto das celulas
        String valueString = cell != null ? JExcel.getStringCell(cell) : "0.00";
        valueString = valueString.replaceAll("[^0-9\\.,-]", "");

        //Se tiver . antes da virgula remove os pontos e coloca ponto no lugar da virgula
        if (valueString.indexOf(".") < valueString.indexOf(",")) {
            valueString = valueString.replaceAll("\\.", "").replaceAll("\\,", ".");
        }

        BigDecimal valueBigDecimal = new BigDecimal(valueString.equals("") ? "0" : valueString);

        //Se a coluna tiver que multiplicar por -1 e o valor encontrado for maior que zero
        if (forceNegative && valueBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
            valueBigDecimal = valueBigDecimal.multiply(new BigDecimal("-1"));
        }
        
        return valueBigDecimal;
    }

}
