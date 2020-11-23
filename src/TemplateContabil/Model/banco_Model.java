package TemplateContabil.Model;

import Auxiliar.LctoTemplate;
import Entity.Warning;
import Entity.ErrorIgnore;
import LctoTemplate.CfgBancoTemplate;
import OFX.OFX;
import Robo.View.roboView;
import TemplateContabil.Control.ControleTemplates;
import TemplateContabil.Template;
import fileManager.Selector;
import fileManager.StringFilter;
import java.io.File;
import java.util.List;

public class banco_Model {

    protected static File fileEscrituracaoMensal;
    protected static File fileExtratos;
    protected static File fileTemplatePadrao;
    protected static int mes;
    protected static int ano;

    public static void autoSetFromControleTemplates(ControleTemplates controle) {
        System.out.println("Definindo informações estáticas para o modelo do banco");
        setMesAno(controle.getMes(), controle.getAno());
        setPathPrincipal(controle.getFilePathPrincipalArquivos());
        setFileEscrituracaoMensal(controle.getFileEscrituracaoMensal());
        setFileTemplatePadrao(controle.getFileTemplatePadrao());
    }

    public static void setMesAno(int mes, int ano) {
        banco_Model.mes = mes;
        banco_Model.ano = ano;
    }

    public static void setPathPrincipal(File fileExtratos) {
        banco_Model.fileExtratos = fileExtratos;
    }

    public static void setFileEscrituracaoMensal(File fileEscrituracaoMensal) {
        banco_Model.fileEscrituracaoMensal = fileEscrituracaoMensal;
    }

    public static void setFileTemplatePadrao(File fileTemplatePadrao) {
        banco_Model.fileTemplatePadrao = fileTemplatePadrao;
    }

    protected CfgBancoTemplate cfgBanco;
    protected StringFilter filtroBanco;
    protected File arquivoBanco;
    protected List<LctoTemplate> lctos;

    public banco_Model(CfgBancoTemplate cfgBanco) {
        this.cfgBanco = cfgBanco;

        filtroBanco = new StringFilter(cfgBanco.getFiltroNomeArquivoOriginal());

        arquivoBanco = Selector.getFileOnFolder(
                banco_Model.fileExtratos,
                filtroBanco.printMap(filtroBanco.getHas(), ";"),
                filtroBanco.printMap(filtroBanco.getHasNot(), ";")
        );
    }

    public void verificarArquivo() {
        if (arquivoBanco == null) {
            throw new ErrorIgnore("Arquivo com '" 
                    + filtroBanco.printMap(filtroBanco.getHas(), " ") 
                    + "' e sem '" 
                    + filtroBanco.printMap(filtroBanco.getHasNot(), ";")
                    + "' no nome não encontrado na pasta " + roboView.link(fileExtratos));
        }
    }

    public void setLctosFromOFX() {
        lctos = OFX.getListaLctos(arquivoBanco);
    }

    public File getArquivoBanco() {
        verificarArquivo();
        return arquivoBanco;
    }

    public void setLctosFromList(List<LctoTemplate> lctos) {
        this.lctos = lctos;
    }

    public void importarBanco() {
        importarBanco(true, true);
    }

    public void importarBanco(boolean exibirAvisoFinal, boolean filtrarMesmoMesAno) {
        verificarArquivo();

        System.out.println("Colocando lançamentos do banco " + cfgBanco.getNomeBanco() + " no template padrao");
        File arquivoSalvo = new File(fileExtratos.getAbsolutePath().concat("\\").concat(fileTemplatePadrao.getName().replaceAll(".xlsm", cfgBanco.getNomeBanco().concat(".xlsm"))));

        Template template;
        if (filtrarMesmoMesAno) {
            template = new Template(
                    mes,
                    ano,
                    fileTemplatePadrao,
                    arquivoSalvo,
                    cfgBanco.getEmpresa(),
                    0,
                    cfgBanco.getHistoricoPadraoDebito(),
                    cfgBanco.getHistoricoPadraoCredito(),
                    cfgBanco.getContaBanco(),
                    lctos
            );
        } else {
            template = new Template(
                    fileTemplatePadrao,
                    arquivoSalvo,
                    cfgBanco.getEmpresa(),
                    0,
                    cfgBanco.getHistoricoPadraoDebito(),
                    cfgBanco.getHistoricoPadraoCredito(),
                    cfgBanco.getContaBanco(),
                    lctos
            );
        }

        if (template.isResult()) {
            if (exibirAvisoFinal) {
                throw new Warning("Template do banco " + cfgBanco.getNomeBanco() + " salvo em " + roboView.link(fileExtratos));
            }
        } else {
            throw new ErrorIgnore("Erro ao salvar o template do banco '" + cfgBanco.getNomeBanco() + "' na pasta " + roboView.link(fileExtratos));
        }
    }

    public List<LctoTemplate> getLctos() {
        return lctos;
    }
}
