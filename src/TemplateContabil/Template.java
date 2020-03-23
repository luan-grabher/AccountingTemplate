package TemplateContabil;

import Auxiliar.LctoTemplate;
import java.io.File;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Template {

    private File arquivoTemplate;
    private File salvarEm;
    private int nroEmpresa;
    private int nroFilial;
    private int HPDeb;
    private int HPCred;
    private int nroBanco;
    private List<LctoTemplate> lctos;

    private int mes;
    private int ano;
    
    private boolean result = true;

    public Template(File arquivoTemplate, File salvarEm, int nroEmpresa, int nroFilial, int HPDeb, int HPCred, int nroBanco, List<LctoTemplate> lctos) {
        constructor(0, 0, arquivoTemplate, salvarEm, nroEmpresa, nroFilial, HPDeb, HPCred, nroBanco, lctos);
    }

    public Template(int mes, int ano, File arquivoTemplate, File salvarEm, int nroEmpresa, int nroFilial, int HPDeb, int HPCred, int nroBanco, List<LctoTemplate> lctos) {
        constructor(mes, ano, arquivoTemplate, salvarEm, nroEmpresa, nroFilial, HPDeb, HPCred, nroBanco, lctos);
    }

    public boolean isResult() {
        return result;
    }
    
    private void constructor(int mes, int ano, File arquivoTemplate, File salvarEm, int nroEmpresa, int nroFilial, int HPDeb, int HPCred, int nroBanco, List<LctoTemplate> lctos) {
        this.mes = mes;
        this.ano = ano;
        this.arquivoTemplate = arquivoTemplate;
        this.salvarEm = salvarEm;
        this.nroEmpresa = nroEmpresa;
        this.nroFilial = nroFilial;
        this.HPDeb = HPDeb;
        this.HPCred = HPCred;
        this.nroBanco = nroBanco;
        this.lctos = lctos;

        colocarLctosNoTemplate();
    }

    private void colocarLctosNoTemplate() {
        try {
            //Abrir template
            XSSFWorkbook wk = new XSSFWorkbook(arquivoTemplate);

            definirParametrosGerais(wk.getSheet("Parâmetros Gerais"));
            transferirLctos(wk.getSheet("Dados"));

            //salvar
            //wk.close();
            result = JExcel.JExcel.saveWorkbookAs(salvarEm, wk);
        } catch (Exception e) {
            System.out.println("Erro: " + e);
            e.printStackTrace();
            result = false;
        }

    }

    private void definirParametrosGerais(XSSFSheet sheet) {
        //Empresa
        sheet.getRow(1).getCell(1).setCellValue(nroEmpresa);
        sheet.getRow(2).getCell(3).setCellValue(salvarEm.getParentFile().getAbsolutePath());

        sheet.getRow(5).getCell(1).setCellValue(HPDeb);
        sheet.getRow(5).getCell(2).setCellValue(HPCred);

        sheet.getRow(7).getCell(3).setCellValue(nroBanco);
    }

    private void transferirLctos(XSSFSheet sheet) {
        //Limpa linhas 
        while (sheet.getLastRowNum() > 1) {
            JExcel.JExcel.removeRows(sheet, 1, sheet.getLastRowNum());
        }

        for (LctoTemplate lcto : lctos) {
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
                if (nroFilial != 0) {
                    row.getCell(3).setCellValue(nroFilial);
                }
                row.getCell(6).setCellValue(lcto.getValor().getDouble());
                row.getCell(7).setCellValue(lcto.getEntrada_Saida());
            }
        }
    }

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
