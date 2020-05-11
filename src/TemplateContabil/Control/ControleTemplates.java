package TemplateContabil.Control;

import Auxiliar.LctoTemplate;
import Entity.Aviso;
import Entity.Executavel;
import LctoTemplate.CfgBancoTemplate;
import Robo.View.roboView;
import Selector.Entity.FiltroString;
import TemplateContabil.ComparacaoTemplates;
import TemplateContabil.Model.Entity.CfgTipoLctosBancoModel;
import TemplateContabil.Model.ExtratoExcel;
import TemplateContabil.Model.banco_Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControleTemplates {

    protected int mes;
    protected int ano;
    protected int empresa;
    protected String pathNameEmpresa;
    protected String pathEscrituracaoMensal;
    protected String pathNamePrincipalArquivos;
    protected String pathPrincipalArquivos;
    protected File filePathPrincipalArquivos;
    protected File fileEscrituracaoMensal;
    protected File fileTemplatePadrao;
    
    protected String defaultMainPath = "Extratos";

    protected FiltroString filtroTemplatePadrao = new FiltroString("Template;Extratos;.xlsm");

    /**
     * Define um controle com a pasta indicada.\n
     * Após a construção da classe deverá ser chamada a função "setDefaultPaths" que irá construir as pastas necessárias.\n
     * Antes de chamar a função você também pode configurar as pastas.\n
     * Depois será necessário chamar a classe executavel "definirFileTemplatePadrao" antes de chamar a classe executavel dos bancos
     * @param pathNameEmpresa A pasta da empresa em G:/Contabil/Clientes
     */
    public ControleTemplates(int mes, int ano, int empresa, String pathNameEmpresa) {
        Constructor(mes, ano, empresa, pathNameEmpresa, defaultMainPath);
    }

    /**
     * Define um controle com a pasta indicada.\n
     * Após a construção da classe deverá ser chamada a função "setDefaultPaths" que irá construir as pastas necessárias.\n
     * Antes de chamar a função você também pode configurar as pastas.\n
     * Depois será necessário chamar a classe executavel "definirFileTemplatePadrao" antes de chamar a classe executavel dos bancos
     * @param pathNamePrincipalArquivos Por exemplo: Caso coloque "Extratos;Contas a Pagar" a pasta acessada será "Esc Mensal/ano/Extratos/mes.ano/Contas a Pagar
     * @param pathNameEmpresa A pasta da empresa em G:/Contabil/Clientes
     */
    public ControleTemplates(int mes, int ano, int empresa, String pathNameEmpresa, String pathNamePrincipalArquivos) {
        Constructor(mes, ano, empresa, pathNameEmpresa, pathNamePrincipalArquivos);
    }

    private void Constructor(int mes, int ano, int empresa, String pathNameEmpresa, String pathNamePrincipalArquivos) {
        this.mes = mes;
        this.ano = ano;
        this.empresa = empresa;
        this.pathNameEmpresa = pathNameEmpresa;
        this.pathNamePrincipalArquivos = pathNamePrincipalArquivos.equals("")?defaultMainPath:pathNamePrincipalArquivos;

        setDefaultPaths();
    }

    private void setDefaultPaths() {
        String[] pathsMensaisAnuais = pathNamePrincipalArquivos.split(";",2);
        String pathAnual = pathsMensaisAnuais[0];
        String pathMensal = pathsMensaisAnuais.length == 2?"\\" + pathsMensaisAnuais[1]:"";
        
        pathEscrituracaoMensal = "\\\\HEIMERDINGER\\DOCS\\Contábil\\Clientes\\" + pathNameEmpresa + "\\Escrituração mensal";
        pathPrincipalArquivos = pathEscrituracaoMensal + "\\#ano#\\" + pathAnual + "\\#mes#.#ano#" + pathMensal;
    }

    public void setPathPrincipalArquivos(String pathPrincipalArquivos) {
        this.pathPrincipalArquivos = pathPrincipalArquivos;
    }

    public void definirFilesAndPaths() {
        System.out.println("Definindo mes e ano das pastas");
        pathPrincipalArquivos = pathPrincipalArquivos.replaceAll("#mes#", "" + ((this.mes<10?"0":"") + this.mes));
        pathPrincipalArquivos = pathPrincipalArquivos.replaceAll("#ano#", "" + this.ano);

        System.out.println("Definindo o file da pasta Escrituração Mensal");
        fileEscrituracaoMensal = new File(pathEscrituracaoMensal).getAbsoluteFile();

        System.out.println("Definindo o file da pasta de extratos");
        filePathPrincipalArquivos = new File(pathPrincipalArquivos).getAbsoluteFile();
    }

    public void definirVariaveisEstaticasModeloBanco() {
        banco_Model.autoSetFromControleTemplates(this);
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }

    public File getFilePathPrincipalArquivos() {
        return filePathPrincipalArquivos;
    }

    public File getFileEscrituracaoMensal() {
        return fileEscrituracaoMensal;
    }

    public File getFileTemplatePadrao() {
        return fileTemplatePadrao;
    }

    public void setFiltroTemplatePadrao(FiltroString filtroTemplatePadrao) {
        this.filtroTemplatePadrao = filtroTemplatePadrao;
    }

    public class definirFileTemplatePadrao extends Executavel {

        @Override
        public void run() {
            System.out.println("Definindo o template padrão");
            fileTemplatePadrao = Selector.Pasta.procura_arquivo(
                    fileEscrituracaoMensal,
                    filtroTemplatePadrao.getListPossuiStr(";"),
                    filtroTemplatePadrao.getListNaoPossuiStr(";")
            );

            if (fileTemplatePadrao != null) {
                definirVariaveisEstaticasModeloBanco();
            } else {
                throw new Error("Template padrão (" + filtroTemplatePadrao.getListPossuiStr(" ") + ") não enconrado em " + roboView.link(fileEscrituracaoMensal));
            }
        }

        @Override
        public String getNome() {
            return "Definindo o template padrão";
        }

    }

    public class importacaoPadraoBanco extends Executavel {

        protected String nomeBanco = "";
        protected String filtroArquivo = "";
        protected int nroBanco = 0;
        protected CfgBancoTemplate cfgBanco = new CfgBancoTemplate();
        protected CfgTipoLctosBancoModel cfgTipoLctos = new CfgTipoLctosBancoModel();
        protected CfgTipoLctosBancoModel cfgTipoLctosComparar = new CfgTipoLctosBancoModel(CfgTipoLctosBancoModel.TIPO_INATIVO);
        protected String filtroFileComparar = "";
        protected String resultadoComparacao = "";
        protected banco_Model modelo = null;

        public importacaoPadraoBanco(String nomeBanco, String filtroArquivo, int nroBanco) {
            this.nomeBanco = nomeBanco;
            this.filtroArquivo = filtroArquivo;
            this.nroBanco = nroBanco;
        }

        public importacaoPadraoBanco(CfgBancoTemplate cfgBanco) {
            this.cfgBanco = cfgBanco;
        }

        public importacaoPadraoBanco(CfgBancoTemplate cfgBanco, CfgTipoLctosBancoModel cfgTipoLctos) {
            this.cfgBanco = cfgBanco;
            this.cfgTipoLctos = cfgTipoLctos;
        }

        public importacaoPadraoBanco(CfgBancoTemplate cfgBanco, CfgTipoLctosBancoModel cfgTipoLctos, String filtroFileComparar, CfgTipoLctosBancoModel cfgTipoLctosComparar) {
            this.cfgBanco = cfgBanco;
            this.cfgTipoLctos = cfgTipoLctos;
            this.cfgTipoLctosComparar = cfgTipoLctosComparar;
            this.filtroFileComparar = filtroFileComparar;
        }

        public importacaoPadraoBanco(String nomeBanco, String filtroArquivo, int nroBanco, CfgTipoLctosBancoModel cfgTipoLctos) {
            this.nomeBanco = nomeBanco;
            this.filtroArquivo = filtroArquivo;
            this.nroBanco = nroBanco;
            this.cfgTipoLctos = cfgTipoLctos;
        }

        public importacaoPadraoBanco(String nomeBanco, String filtroArquivo, int nroBanco, CfgTipoLctosBancoModel cfgTipoLctos, String filtroFileComparar, CfgTipoLctosBancoModel cfgTipoLctosComparar) {
            this.nomeBanco = nomeBanco;
            this.filtroArquivo = filtroArquivo;
            this.nroBanco = nroBanco;
            this.cfgTipoLctos = cfgTipoLctos;
            this.cfgTipoLctosComparar = cfgTipoLctosComparar;
            this.filtroFileComparar = filtroFileComparar;
        }

        @Override
        public String getNome() {
            return "Importando Banco " + this.nomeBanco;
        }

        @Override
        public void run() {
            System.out.println("Iniciando importação padrão Banco " + this.nomeBanco);

            if (nroBanco != 0 && cfgBanco.getEmpresa() == 0) {
                cfgBanco.setEmpresa(empresa);
                cfgBanco.setContaBanco(nroBanco);
                cfgBanco.setNomeBanco(this.nomeBanco);
                cfgBanco.setFiltroNomeArquivoOriginal(filtroArquivo);
            }
            chamarFuncaoModelo();
        }

        public void chamarFuncaoModelo() {
            System.out.println("Cria objeto do " + nomeBanco + " do modelo dos bancos padrão");
            modelo = new banco_Model(cfgBanco);

            System.out.println("Define Lctos Modelo");
            defineLctosModelo();

            //inicia importação
            System.out.println("Iniciando importação para o arquivo de template");
            boolean mostrarAvisoFinalImportacao = cfgTipoLctosComparar.getTIPO() == CfgTipoLctosBancoModel.TIPO_INATIVO;

            modelo.importarBanco(mostrarAvisoFinalImportacao, cfgBanco.isFiltrarMesAno());
            
            if(!mostrarAvisoFinalImportacao){
                throw new Aviso("Comparação: " + resultadoComparacao);
            }
        }

        protected void defineLctosModelo() {
            List<LctoTemplate> lctos;
            List<LctoTemplate> lctosComparados;

            lctos = getLctosFromFile(modelo.getArquivoBanco(), cfgTipoLctos);

            if (cfgTipoLctosComparar.getTIPO() != CfgTipoLctosBancoModel.TIPO_INATIVO) {
                File fileComparar = Selector.Pasta.procura_arquivo(filePathPrincipalArquivos, filtroFileComparar);
                if (fileComparar != null) {
                    lctosComparados = getLctosFromFile(modelo.getArquivoBanco(), cfgTipoLctosComparar);
                    resultadoComparacao = ComparacaoTemplates.getComparacaoString(nomeBanco, fileComparar.getName(), lctos, lctosComparados);
                } else {
                    resultadoComparacao = roboView.mensagemNaoEncontrado(filtroFileComparar, filePathPrincipalArquivos);
                }
            }

            modelo.setLctosFromList(lctos);
        }

        protected List<LctoTemplate> getLctosFromFile(File file, CfgTipoLctosBancoModel cfg) {
            List<LctoTemplate> lctos = new ArrayList<>();

            if (cfg.getTIPO() == CfgTipoLctosBancoModel.TIPO_OFX) {
                lctos = OFX.OFX.getListaLctos(file);
            } else if (cfg.getTIPO() == CfgTipoLctosBancoModel.TIPO_EXCEL) {
                System.out.println("Define modelo de Extratos Excel");
                ExtratoExcel modeloExtratoExcel = new ExtratoExcel(file);

                System.out.println("Define lctos no objeto de modelo dos bancos");
                if (cfg.getExcel_colunaValor().equals("")) {
                    modeloExtratoExcel.setLctos(
                            cfg.getExcel_colunaData(),
                            cfg.getExcel_colunaDoc(),
                            cfg.getExcel_colunaPreTexto(),
                            cfg.getExcel_colunaComplementoHistorico(),
                            cfg.getExcel_colunaEntrada(),
                            cfg.getExcel_colunaSaida()
                    );
                } else {
                    modeloExtratoExcel.setLctos(
                            cfg.getExcel_colunaData(),
                            cfg.getExcel_colunaDoc(),
                            cfg.getExcel_colunaPreTexto(),
                            cfg.getExcel_colunaComplementoHistorico(),
                            cfg.getExcel_colunaValor()
                    );
                }

                lctos = modeloExtratoExcel.getLctos();
            }

            return lctos;
        }
    }

    public class importacaoPadraoBancoOfx extends importacaoPadraoBanco {

        public importacaoPadraoBancoOfx(String nomeBanco, String filtroArquivo, int nroBanco) {
            super(nomeBanco, filtroArquivo + ";.ofx", nroBanco);
        }

    }

    public class importacaoBancoExtratoExcel extends importacaoPadraoBanco {

        public importacaoBancoExtratoExcel(String nomeBanco, String filtroArquivo, int nroBanco, String colunaData, String colunaDoc, String colunaPreTexto, String colunaComplementoHistorico, String colunaEntrada, String colunaSaida) {
            super(nomeBanco, filtroArquivo, nroBanco);

            cfgTipoLctos = new CfgTipoLctosBancoModel();
            cfgTipoLctos.setTIPO(CfgTipoLctosBancoModel.TIPO_EXCEL);

            cfgTipoLctos.setExcel_colunaData(colunaData);
            cfgTipoLctos.setExcel_colunaDoc(colunaDoc);
            cfgTipoLctos.setExcel_colunaPreTexto(colunaPreTexto);
            cfgTipoLctos.setExcel_colunaComplementoHistorico(colunaComplementoHistorico);
            cfgTipoLctos.setExcel_colunaEntrada(colunaEntrada);
            cfgTipoLctos.setExcel_colunaSaida(colunaSaida);
        }

        public importacaoBancoExtratoExcel(String nomeBanco, String filtroArquivo, int nroBanco, String colunaData, String colunaDoc, String colunaPreTexto, String colunaComplementoHistorico, String colunaValor) {
            super(nomeBanco, filtroArquivo, nroBanco);

            cfgTipoLctos = new CfgTipoLctosBancoModel();
            cfgTipoLctos.setTIPO(CfgTipoLctosBancoModel.TIPO_EXCEL);

            cfgTipoLctos.setExcel_colunaData(colunaData);
            cfgTipoLctos.setExcel_colunaDoc(colunaDoc);
            cfgTipoLctos.setExcel_colunaPreTexto(colunaPreTexto);
            cfgTipoLctos.setExcel_colunaComplementoHistorico(colunaComplementoHistorico);
            cfgTipoLctos.setExcel_colunaValor(colunaValor);
        }
    }
}
