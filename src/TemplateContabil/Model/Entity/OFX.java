package TemplateContabil.Model.Entity;

import fileManager.FileManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OFX {

    private static String valorLinha = "";

    public static List<LctoTemplate> getListaLctos(File arquivo) {
        List<LctoTemplate> lctos = new ArrayList<>();
        try {
            // Ler arquivo
            String textoArquivo = FileManager.getText(arquivo.getAbsolutePath()).replaceAll("\r", "");
            String[] linhas = textoArquivo.split("\n");

            String data = "";
            String historico = "";
            String valor = "";
            String doc = "";

            // Percorre todas linhas para ir acumulando os valores
            for (String linha : linhas) {
                if (!linha.equals("")) {
                    // Adiciona lcto
                    if (linha.contains("</STMTTRN>") && !data.equals("") && !historico.equals("")
                            && !valor.equals("")) {
                        lctos.add(new LctoTemplate(data, doc, "", historico, new BigDecimal(valor)));
                        data = "";
                        historico = "";
                        valor = "";
                        doc = "";

                        // Data
                    } else if (!getValorParametro("<DTPOSTED>", linha).equals("")) {
                        data = valorLinha.substring(6, 8) + "/" + valorLinha.substring(4, 6) + "/"
                                + valorLinha.substring(0, 4);

                        // Valor
                    } else if (!getValorParametro("<TRNAMT>", linha).equals("")) {
                        valor = valorLinha;
                        // Se tiver virgula
                        if (valor.indexOf(".") < valor.indexOf(",")) {
                            valor = valor.replaceAll("\\.", "").replaceAll("\\,", ".");
                        }

                        // Historico
                    } else if (!getValorParametro("<MEMO>", linha).equals("")) {
                        // if historico nao for vazio, adiciona o valorLinha, se historico for vazio,
                        // adiciona o valorLinha
                        if (!historico.equals("")) {
                            historico += " " + valorLinha;
                        } else {
                            historico = valorLinha;
                        }
                    } else if (!getValorParametro("<CHECKNUM>", linha).equals("")) {
                        doc = valorLinha;

                    }

                }
            }

            lctos = getUniqueLancamentos(lctos);            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lctos;
    }

    private static List<LctoTemplate> getUniqueLancamentos(List<LctoTemplate> lancamentos){
        List<LctoTemplate> lancamentosUnicos = new ArrayList<>();
            for (LctoTemplate lcto : lancamentos) {
                String dataLcto = lcto.getData();
                String historicoLcto = lcto.getHistorico();
                String valorLcto = lcto.getValor().toString();
                String docLcto = lcto.getDocumento();

                boolean lctoJaExiste = false;
                for (LctoTemplate lctoSemDuplicado : lancamentosUnicos) {
                    boolean isSameDate = lctoSemDuplicado.getData().equals(dataLcto);
                    boolean isSameHistorico = lctoSemDuplicado.getHistorico().equals(historicoLcto);
                    boolean isSameValor = lctoSemDuplicado.getValor().toString().equals(valorLcto);
                    boolean isSameDoc = lctoSemDuplicado.getDocumento().equals(docLcto);

                    boolean isLancamentoIgual = isSameDate && isSameHistorico && isSameValor && isSameDoc;
                    if (isLancamentoIgual) {
                        lctoJaExiste = true;
                        break;
                    }
                }

                if (!lctoJaExiste) {
                    lancamentosUnicos.add(lcto);
                }
            }

            return lancamentosUnicos;
    }

    private static String getValorParametro(String nomeParametro, String linha) {
        if (linha.contains(nomeParametro)) {
            valorLinha = linha.replaceAll(nomeParametro, "").replaceAll(nomeParametro.replaceAll("<", "</"), "").trim();
        } else {
            valorLinha = "";
        }

        return valorLinha;
    }
}
