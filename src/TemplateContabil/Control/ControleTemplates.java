package TemplateContabil.Control;

import Entity.Executavel;
import Robo.View.roboView;
import TemplateContabil.Model.Entity.Importation;
import TemplateContabil.Model.ImportationModel;
import com.aspose.pdf.Document;
import com.aspose.pdf.ExcelSaveOptions;
import fileManager.Selector;
import java.io.File;

public class ControleTemplates {

    private Integer mes;
    private Integer ano;

    protected File pastaPrincipal;
    protected File pastaEscMensal;

    /**
     * Define um controle de templates.Para que tudo funcione bem você deve
     * definir a pasta da Esc. Mensal da empresa, e depois a pasta principal.
     * Caso a pasta seja diferente da convencional você pode somente definir a
     * pasta principal com o File bruto da pasta que possui o arquivo.
     *
     * Caso a pasta não seja convencional de mes e você não queria validar os
     * lançamentos do mes e ano, deixe null o mes e ano
     *
     * @param mes Mes das pastas e mes de validação do arquivo
     * @param ano Ano das pastas e mes de validação do arquivo
     */
    public ControleTemplates(Integer mes, Integer ano) {
        this.mes = mes;
        this.ano = ano;
    }

    /**
     * Define a pasta que será utilizada
     *
     * @param pastaAnual Pasta Anual, por exemplo: Caso coloque "Extratos" a
     * pasta acessada será "Esc Mensal/ano/Extratos/mes.ano/"
     * @param pastaMensal Pasta Mensal que aparece depois do mes e ano, por
     * exemplo "Banco 5899" acessará a pasta "Esc Mensal/ ano/ Extratos/
     * mes.ano/ Banco 5899". Para não utilizar deixe em branco
     */
    public void setPasta(String pastaAnual, String pastaMensal) {
        //Coloca barra antes da pasta mensal caso exista 
        pastaMensal = pastaMensal.equals("") ? "" : "\\" + pastaMensal;

        String path = pastaEscMensal.getAbsolutePath() + "\\#ano#\\" + pastaAnual + "\\#mes#.#ano#" + pastaMensal;

        //Substitui #mes# pelo mês informado
        path = path.replaceAll("#mes#", "" + ((this.mes < 10 ? "0" : "") + this.mes));
        //Substitui #ano# pelo ano informado
        path = path.replaceAll("#ano#", "" + this.ano);

        //Cria variavel file da pasta principal com a string, utiliza getAbsoluteFile por algum motivo que nao sei, deixei ai
        pastaPrincipal = new File(path).getAbsoluteFile();
    }

    /**
     * Define a pasta que será utilizada
     *
     * @param folder File do folder principal
     */
    public void setPasta(File folder) {
        pastaPrincipal = folder;
    }

    /**
     * Define a pasta de escrituração mensal com o nome da pasta da empresa
     *
     * @param nomePastaEmpresa O nome da pasta da empresa em
     * G:/Contabil/Clientes
     */
    public void setPastaEscMensal(String nomePastaEmpresa) {
        pastaEscMensal = new File("\\\\HEIMERDINGER\\DOCS\\Contábil\\Clientes\\" + nomePastaEmpresa + "\\Escrituração mensal");
    }

    /**
     * Classe executavel que define na importacao passada o arquivo conforme o
     * filtro se existir, se nao encontrar mostra erro
     */
    public class defineArquivoNaImportacao extends Executavel {

        private final String filtroArquivo;
        private final Importation importation;

        public defineArquivoNaImportacao(String filtroArquivo, Importation importation) {
            this.filtroArquivo = filtroArquivo;
            this.importation = importation;
        }

        @Override
        public void run() {
            File file = Selector.getFileOnFolder(pastaPrincipal, filtroArquivo);

            //Se encontrar o arquivo
            if (file != null) {
                //Verifica se é PDF para converter
                if (file.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
                    //Converte em XLSX
                    file = convertPdfToExcel(file);
                }

                importation.setFile(file);
            } else {
                throw new Error("Não foi encontrado o arquivo '" + filtroArquivo + "' na pasta " + roboView.link(pastaPrincipal));
            }
        }
    }

    public File convertPdfToExcel(File pdfFile) {
        Document doc = new Document(pdfFile.getAbsolutePath());
        // Set Excel options
        ExcelSaveOptions options = new ExcelSaveOptions();
        // Set output format
        options.setFormat(ExcelSaveOptions.ExcelFormat.XLSX);
        // Set minimizing option
        options.setMinimizeTheNumberOfWorksheets(true);

        //Setr new file xlsx
        File newFile = new File(pdfFile.getAbsolutePath().replaceAll(".PDF", ".pdf").replaceAll(".pdf", ".xlsx"));

        // Convert PDF to XLSX
        doc.save(newFile.getAbsolutePath(), options);

        return newFile;
    }

    //class to get lctos from importation file
    public class getLctosFromImportationFile extends Executavel {

        private final Importation importation;

        public getLctosFromImportationFile(Importation importation) {
            this.importation = importation;
        }

        @Override
        public void run() {
            //Chama o modelo da importação que irá criar o template e gerar warning se algo der errado
            ImportationModel.getLctosFromFile(importation);
            ImportationModel modelo = new ImportationModel(importation.getNome(), mes, ano, importation, null);
        }
    }

    public class converterArquivoParaTemplate extends Executavel {

        private final Importation importation;
        private final Importation comparar;

        public converterArquivoParaTemplate(Importation importation) {
            this.importation = importation;
            this.comparar = null;
        }

        public converterArquivoParaTemplate(Importation importation, Importation comparar) {
            this.importation = importation;
            this.comparar = comparar;
        }

        @Override
        public void run() {
            //Chama o modelo da importação que irá criar o template e gerar warning se algo der errado
            ImportationModel modelo = new ImportationModel(importation.getNome(), mes, ano, importation, comparar);
            modelo.criarTemplateDosLancamentos(importation);
        }
    }
}
