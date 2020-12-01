package TemplateContabil.Model;

import TemplateContabil.Model.Entity.LctoTemplate;
import java.io.File;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Template {

    public static final File templateParaCopiar = new File("\\\\heimerdinger\\docs\\Informatica\\Programas\\Moresco\\Robos\\Contabilidade\\TemplateImportacao\\Default.xlsm");
    private final File salvarTemplateComo;
    private final String idConfig;
    private final List<LctoTemplate> lctos;

    private final int mes;
    private final int ano;

    /**
     * Inicia classe que cria o template com os lançamentos a partir do template
     * para copiar, que deverá ser o padrao.Irá copiar o id da configuração,
     * quando abrir o xlsm irá se virar com o resto.
     *
     *
     * @param mes Mês para validar, os lançamentos que não forem deste mes não
     * serão colocados. Para não veriricar deixar como 0.
     * @param ano Ano para validar, os lançamentos que não forem deste mes não
     * serão colocados. Para não veriricar deixar como 0.
     * @param salvarTemplateComo File do template que será salvo, este file não
     * precisa existir.
     * @param idConfig Id da configuração na pasta do G
     * @param lctos Lista de lançamentos do template que serão colados no
     * template salvo
     *
     */
    public Template(int mes, int ano, File salvarTemplateComo, String idConfig, List<LctoTemplate> lctos) {
        this.mes = mes;
        this.ano = ano;
        this.salvarTemplateComo = salvarTemplateComo;
        this.idConfig = idConfig;
        this.lctos = lctos;
    }

    /**
     * Cria template copiando o padrão e coloca os lançamentos e id de configuração nele
     * 
     * @return Retorna se ocorreu tudo certo
     */
    public boolean criarTemplateXlsm() {
        try {
            //Abrir template
            XSSFWorkbook wk = new XSSFWorkbook(templateParaCopiar);

            colarIdConfigs(wk.getSheet("Parâmetros Gerais"));
            transferirLctos(wk.getSheet("Dados"));

            //salvar
            //wk.close();
            return JExcel.JExcel.saveWorkbookAs(salvarTemplateComo, wk);
        } catch (Exception e) {
            System.out.println("Erro: " + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Coloca o id da dconfiguração na aba de parametros
     */
    private void colarIdConfigs(XSSFSheet sheet) {
        //Cola Id
        sheet.getRow(4).getCell(1).setCellValue(idConfig);
    }

    /**
     * Coloca lançamentos na aba de dados
     */
    private void transferirLctos(XSSFSheet sheet) {
        //Limpa linhas 
        while (sheet.getLastRowNum() > 1) {
            JExcel.JExcel.removeRows(sheet, 1, sheet.getLastRowNum());
        }

        lctos.forEach((lcto) -> {
            boolean valid = mes > 0 && ano > 0 ? validateDate(lcto.getData()) : true;
            if (valid) {
                XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);

                //Cria celulas
                for (int i = 0; i < 8; i++) {
                    row.createCell(i);
                }

                row.getCell(0).setCellValue(lcto.getData());
                row.getCell(1).setCellValue(lcto.getDocumento());
                row.getCell(2).setCellValue(lcto.getHistorico());
                row.getCell(6).setCellValue(lcto.getValor().getDouble());
                row.getCell(7).setCellValue(lcto.getEntrada_Saida());
            }
        });
    }

    /**
     * Retorna se uma data é valida
     * @return Retorna se uma data é valida
     */
    private boolean validateDate(String date) {
        try {
            String mesBarra = "/" + mes + "/";
            String mesMMBarra = "/" + (mes < 10 ? "0" : "") + mes + "/";

            String anoBarra = "/" + ano;

            if (!date.contains(mesBarra) && !date.contains(mesMMBarra)) {
                return false;
            } else if (!date.endsWith(anoBarra)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
