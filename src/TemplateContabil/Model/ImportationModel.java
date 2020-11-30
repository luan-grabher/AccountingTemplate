/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TemplateContabil.Model;

import Entity.ErrorIgnore;
import Entity.Warning;
import Robo.View.roboView;
import TemplateContabil.ComparacaoTemplates;
import TemplateContabil.Model.Entity.CfgImportacaoLancamentos;
import TemplateContabil.Model.Entity.LctoTemplate;
import TemplateContabil.Template;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class ImportationModel {

    private String resultadoComparacao = "";
    //##################
    private String nomeBanco;

    protected String filtroFileComparar = "";

    protected banco_Model modelo = null;

    /**
     * Pega lançamentos da configuração
     *
     * @param nomeBanco Nome do banco que será exibido na comparação
     * @param cfg Configuração para importar o arquivo principal
     * @param cfgComparar Configuração do arquivo para comparar, para não
     * comparar, deixar em branco
     */
    public ImportationModel(String nomeBanco, CfgImportacaoLancamentos cfg, CfgImportacaoLancamentos cfgComparar) {
        this.nomeBanco = nomeBanco;

        List<LctoTemplate> lancamentos = getLctosFromFile(cfg);
        if (cfgComparar != null) {
            List<LctoTemplate> comparar = getLctosFromFile(cfgComparar);
            resultadoComparacao = ComparacaoTemplates.getComparacaoString(this.nomeBanco, cfgComparar.getFile().getName(), lancamentos, comparar);
        }

        //Cria template só do principal
        criarTemplateDosLancamentos(cfg);
    }

    /**
     * Cria lista de lançamentos do arquivo informado
     *
     * @param cfg Configuração para definir como o arquivo será lido, OFX ou
     * Excel com as colunas definidas
     * @return Retorna lista de lançamentos do arquivo
     */
    private List<LctoTemplate> getLctosFromFile(CfgImportacaoLancamentos cfg) {
        List<LctoTemplate> lctos = new ArrayList<>();

        if (cfg.getTIPO() == CfgImportacaoLancamentos.TIPO_OFX) {
            lctos = TemplateContabil.Model.Entity.OFX.getListaLctos(cfg.getFile());
        } else if (cfg.getTIPO() == CfgImportacaoLancamentos.TIPO_EXCEL) {
            System.out.println("Define modelo de Extratos Excel");
            ExtratoExcel modeloExtratoExcel = new ExtratoExcel(cfg.getFile());

            System.out.println("Define lctos no objeto de modelo dos bancos");
            if (cfg.getExcel_colunaValor().equals("")) {
                modeloExtratoExcel.setLctos(
                        cfg.getExcel_colunaData(),
                        cfg.getExcel_colunaDoc(),
                        cfg.getExcel_colunaPreTexto(),
                        cfg.getExcel_colunaComplementoHistorico(),
                        cfg.getExcel_colunaEntrada(),
                        cfg.getExcel_colunaSaida(),
                        cfg.getExcel_colunaValor()
                );
            }

            lctos = modeloExtratoExcel.getLctos();
        }

        return lctos;
    }

    /**
     * Criar arquivo Template dos lançamentos na mesma pasta
     *
     *
     * Ai meu parceiro, este é modelo da importacao, e o cfgimportacao vai virar
     * só importação. A classe importação vai guardar os lançamentos. A classe
     * importacao vasi usar mapa para as colunas. Esse modelo vai receber o mês.
     * A classe importacao vai guardar o id de configuracao
     *
     *
     */
    public void criarTemplateDosLancamentos(CfgImportacaoLancamentos cfg) {
        //Cria arquivo
        String nomeArquivoSalvo = cfg.getNome() + ".xlsm";

        File arquivoSalvo = new File(cfg.getFile().getParent() + nomeArquivoSalvo);

        Template template = new Template(
                mes,
                ano,
                arquivoSalvo,
                idConfig,
                cfg.getlctos);

        if (template.criarTemplateXlsm()) {
            throw new Warning("Template do banco " + cfgBanco.getNomeBanco() + " salvo em " + roboView.link(arquivo.getParentFile()));
        } else {
            throw new ErrorIgnore("Erro ao salvar o template do banco '" + cfgBanco.getNomeBanco() + "' na pasta " + roboView.link(arquivo.getParentFile()));
        }
    }
}
