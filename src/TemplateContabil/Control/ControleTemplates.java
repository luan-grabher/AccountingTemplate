package TemplateContabil.Control;

import TemplateContabil.Model.Entity.LctoTemplate;
import Entity.Executavel;
import Entity.Warning;
import LctoTemplate.CfgBancoTemplate;
import Robo.View.roboView;
import TemplateContabil.ComparacaoTemplates;
import TemplateContabil.Model.Entity.CfgImportacaoLancamentos;
import TemplateContabil.Model.ExtratoExcel;
import TemplateContabil.Model.banco_Model;
import fileManager.Selector;
import fileManager.StringFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControleTemplates {

    protected int mes;
    protected int ano;
    protected int empresa;
    protected String pathNameEmpresa;
    protected String pathEscrituracaoMensal;
    protected String pastaAnualNome;
    protected String pathPrincipalArquivos;
    protected File fileMainFolder;

    protected String pastaAnualPadraoNome = "Extratos";

    protected StringFilter filtroTemplatePadrao = new StringFilter("Template;Extratos;.xlsm");
    private final String pastaMensalNome;

    /**
     * Define um controle com a pasta indicada.\n Após a construção da classe
     * deverá ser chamada a função "setDefaultPaths" que irá construir as pastas
     * necessárias.\n Antes de chamar a função você também pode configurar as
     * pastas.\n Depois será necessário chamar a classe executavel
     * "definirFileTemplatePadrao" antes de chamar a classe executavel dos
     * bancos
     *
     * @param mes Mês das pastas
     * @param ano Ano das pastas
     * @param pastaAnual Por exemplo: Caso coloque "Extratos;Contas a Pagar" a
     * pasta acessada será "Esc Mensal/ano/Extratos/mes.ano/Contas a Pagar
     * @param empresa Codigo da empresa
     * @param pathNameEmpresa A pasta da empresa em G:/Contabil/Clientes
     */
    public ControleTemplates(int mes, int ano, int empresa, String pathNameEmpresa, String pastaAnual, String pastaMensal) {
        this.mes = mes;
        this.ano = ano;
        this.empresa = empresa;
        this.pathNameEmpresa = pathNameEmpresa;
        this.pastaAnualNome = pastaAnual.equals("") ? pastaAnualPadraoNome : pastaAnual;
        this.pastaMensalNome = pastaMensal.equals("") ? "" : "\\" + pastaMensal;

        //Define pastas do mês em string
        pathEscrituracaoMensal = "\\\\HEIMERDINGER\\DOCS\\Contábil\\Clientes\\" + pathNameEmpresa + "\\Escrituração mensal";
        pathPrincipalArquivos = pathEscrituracaoMensal + "\\#ano#\\" + pastaAnualNome + "\\#mes#.#ano#" + pastaMensalNome;
        replaceMonthAndYearOnMainFolder();
    }

    /**
     * Caso a pasta principal seja diferente da convencional
     * "PastaEmpresa\ano\PastaAnual(Extratos)\mes.ano\arquivos mensais", você
     * pode definir outro com esta função, se usar "#ano#" e "#mes#" na string,
     * irá fazer replace com o mes e ano indicados
     */
    public void setPathPrincipalArquivos(String pathPrincipalArquivos) {
        this.pathPrincipalArquivos = pathPrincipalArquivos;
        replaceMonthAndYearOnMainFolder();
    }

    /**
     * Dá replace no path em String da pasta principal nos #ano# e #mes# e
     * reseta um file da pasta principal
     */
    private void replaceMonthAndYearOnMainFolder() {
        //Dá replace dos #ano e #mes na pasta principal em string
        pathPrincipalArquivos = pathPrincipalArquivos.replaceAll("#mes#", "" + ((this.mes < 10 ? "0" : "") + this.mes));
        pathPrincipalArquivos = pathPrincipalArquivos.replaceAll("#ano#", "" + this.ano);
        //Cria variavel file da pasta principal com a string
        fileMainFolder = new File(pathPrincipalArquivos).getAbsoluteFile();
    }


    public class importacaoPadraoBancoOfx extends importacaoPadraoBanco {

        public importacaoPadraoBancoOfx(String nomeBanco, String filtroArquivo, int nroBanco) {
            super(nomeBanco, filtroArquivo + ";.ofx", nroBanco);
        }
    }

    public class importacaoBancoExtratoExcel extends importacaoPadraoBanco {

        public importacaoBancoExtratoExcel(String nomeBanco, String filtroArquivo, int nroBanco, String colunaData, String colunaDoc, String colunaPreTexto, String colunaComplementoHistorico, String colunaEntrada, String colunaSaida) {
            super(nomeBanco, filtroArquivo, nroBanco);

            cfgTipoLctos = new CfgImportacaoLancamentos();
            cfgTipoLctos.setTIPO(CfgImportacaoLancamentos.TIPO_EXCEL);

            cfgTipoLctos.setExcel_colunaData(colunaData);
            cfgTipoLctos.setExcel_colunaDoc(colunaDoc);
            cfgTipoLctos.setExcel_colunaPreTexto(colunaPreTexto);
            cfgTipoLctos.setExcel_colunaComplementoHistorico(colunaComplementoHistorico);
            cfgTipoLctos.setExcel_colunaEntrada(colunaEntrada);
            cfgTipoLctos.setExcel_colunaSaida(colunaSaida);
        }

        public importacaoBancoExtratoExcel(String nomeBanco, String filtroArquivo, int nroBanco, String colunaData, String colunaDoc, String colunaPreTexto, String colunaComplementoHistorico, String colunaValor) {
            super(nomeBanco, filtroArquivo, nroBanco);

            cfgTipoLctos = new CfgImportacaoLancamentos();
            cfgTipoLctos.setTIPO(CfgImportacaoLancamentos.TIPO_EXCEL);

            cfgTipoLctos.setExcel_colunaData(colunaData);
            cfgTipoLctos.setExcel_colunaDoc(colunaDoc);
            cfgTipoLctos.setExcel_colunaPreTexto(colunaPreTexto);
            cfgTipoLctos.setExcel_colunaComplementoHistorico(colunaComplementoHistorico);
            cfgTipoLctos.setExcel_colunaValor(colunaValor);
        }
    }
}
