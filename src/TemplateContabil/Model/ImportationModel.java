/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TemplateContabil.Model;

import Auxiliar.Valor;
import Entity.ErrorIgnore;
import Entity.Warning;
import Robo.View.roboView;
import TemplateContabil.Model.Entity.Importation;
import TemplateContabil.Model.Entity.LctoTemplate;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public final class ImportationModel {

    private String resultadoComparacao = "";

    private final String nomeBanco;
    private final Integer month;
    private final Integer year;

    /**
     * Pega lançamentos da configuração e compara se tiver que comparar. Depois
     * tem que chamar a função para criar o template dos lançamentos que pegar
     * conforme a configuração.
     *
     * @param nomeBanco Nome do banco que será exibido na comparação
     * @param month Mês para validação, caso não queira validar deixe nulo
     * @param year Ano para validação, caso não queira validar deixe nulo
     * @param cfg Configuração para importar o arquivo principal
     * @param cfgComparar Configuração do arquivo para comparar, para não
     * comparar, deixar null
     */
    public ImportationModel(String nomeBanco, Integer month, Integer year, Importation cfg, Importation cfgComparar) {
        this.nomeBanco = nomeBanco;
        this.month = month;
        this.year = year;

        List<LctoTemplate> lancamentos = getLctosFromFile(cfg);
        if (cfgComparar != null) {
            List<LctoTemplate> comparar = getLctosFromFile(cfgComparar);
            resultadoComparacao = ComparacaoTemplates.getComparacaoString(this.nomeBanco, cfgComparar.getFile().getName(), lancamentos, comparar);
        }
    }

    public String getResultadoComparacao() {
        return resultadoComparacao;
    }

    /**
     * Cria lista de lançamentos do arquivo informado e coloca os lançamentos na
     * lista de lançamentos da configuração passada
     *
     * @param cfg Configuração da importação, OFX/Excel, arquivo
     * @return Retorna lista de lançamentos do arquivo da importação
     */
    private List<LctoTemplate> getLctosFromFile(Importation cfg) {
        List<LctoTemplate> lctos = new ArrayList<>();

        if (cfg.getTIPO() == Importation.TIPO_OFX) {
            lctos = TemplateContabil.Model.Entity.OFX.getListaLctos(cfg.getFile());
        } else if (cfg.getTIPO() == Importation.TIPO_EXCEL) {
            //Se não for do novo modelo
            if (cfg.getXlsxCols().isEmpty()) {
                System.out.println("Define modelo de Extratos Excel");
                ExtratoExcel modeloExtratoExcel = new ExtratoExcel(cfg.getFile());

                System.out.println("Define lctos no objeto de modelo dos bancos");
                modeloExtratoExcel.setLctos(
                        cfg.getExcelCols().get("data"),
                        cfg.getExcelCols().get("documento"),
                        cfg.getExcelCols().get("pretexto"),
                        cfg.getExcelCols().get("historico"),
                        cfg.getExcelCols().getOrDefault("entrada", ""),
                        cfg.getExcelCols().getOrDefault("saida", ""),
                        cfg.getExcelCols().getOrDefault("valor", "")
                );

                lctos = modeloExtratoExcel.getLctos();
            } else {
                //Pega Lctos
                List<Map<String, Object>> rows = JExcel.XLSX.get(cfg.getFile(), cfg.getXlsxCols());

                //Transforma em Lctos
                rows.forEach((row) -> {
                    //Define o valor
                    Valor valor = new Valor(new BigDecimal("0.00"));
                    if (row.get("entrada") != null) {
                        valor = new Valor((BigDecimal) row.get("entrada"));
                    }else if (row.get("saida") != null) {
                        valor = new Valor((BigDecimal) row.get("saida"));
                    }else if (row.get("valor") != null) {
                        valor = new Valor((BigDecimal) row.get("valor"));
                    }

                    LctoTemplate lcto = new LctoTemplate(
                            Dates.Dates.getCalendarInThisStringFormat((Calendar) row.get("data"), "dd/MM/yyyy"),
                            (String) row.get("documento"),
                            (String) row.get("prefixo"),
                            (String) row.get("historico"),
                            valor
                    );
                }
                );
            }
        }

        cfg.getLctos().addAll(lctos);
        return lctos;
    }

    /**
     * Criar arquivo Template dos lançamentos na mesma pasta do arquivo dos
     * lançamentos
     *
     * @param cfg Configuração da importação com os lançamentos
     */
    public void criarTemplateDosLancamentos(Importation cfg) {
        //Cria arquivo
        String nomeArquivoSalvo = cfg.getNome() + ".xlsm";

        File arquivoSalvo = new File(cfg.getFile().getParent() + "\\" + nomeArquivoSalvo);

        Template template = new Template(
                month,
                year,
                arquivoSalvo,
                cfg.getIdTemplateConfig(),
                cfg.getLctos());

        if (template.criarTemplateXlsm()) {
            throw new Warning("Template do banco " + nomeBanco + " salvo em " + roboView.link(arquivoSalvo.getParentFile()));
        } else {
            throw new ErrorIgnore("Erro ao salvar o template do banco '" + nomeBanco + "' na pasta " + roboView.link(arquivoSalvo.getParentFile()));
        }
    }
}
