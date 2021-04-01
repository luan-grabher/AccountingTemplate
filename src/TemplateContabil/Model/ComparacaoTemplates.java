package TemplateContabil.Model;

import TemplateContabil.Model.Entity.LctoTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComparacaoTemplates {

    /**
     * Retorna a diferença por dia de duas listas de lctos
     *
     * @param lctos1 Lista de lctos 1
     * @param lctos2 Lista de lctos 2
     * @param nome1 Nome dos lançamentos 1
     * @param nome2 Nome dos lançamentos 2
     *
     */
    public static String getComparacaoString(String nome1, String nome2, List<LctoTemplate> lctos1, List<LctoTemplate> lctos2) {
        StringBuilder r = new StringBuilder();

        Map<Integer, Map<String, BigDecimal>> diferencaDias = new LinkedHashMap<>();
        BigDecimal totalLctos1 = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLctos2 = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);

        //Faz soma total por cada dia se tiver diferenca adiciona diferença
        for (int dia = 1; dia <= 31; dia++) {

            String diaBarra = dia + "/";
            String diaMMBarra = (dia < 10 ? "0" : "") + diaBarra;

            //Pega soma do dia sem zero na frente
            BigDecimal somaDiaLctos1 = getSumFromDayLcto(diaBarra, lctos1).setScale(2, RoundingMode.HALF_UP);
            BigDecimal somaDiaLctos2 = getSumFromDayLcto(diaBarra, lctos2).setScale(2, RoundingMode.HALF_UP);

            //Pega soma do dia com zero na frente
            if (!diaBarra.equals(diaMMBarra)) {
                somaDiaLctos1 = somaDiaLctos1.add(getSumFromDayLcto(diaMMBarra, lctos1)).setScale(2, RoundingMode.HALF_UP);
                somaDiaLctos2 = somaDiaLctos2.add(getSumFromDayLcto(diaMMBarra, lctos2)).setScale(2, RoundingMode.HALF_UP);
            }

            //Soma aos totais
            totalLctos1 = totalLctos1.add(somaDiaLctos1).setScale(2, RoundingMode.HALF_UP);
            totalLctos2 = totalLctos2.add(somaDiaLctos2).setScale(2, RoundingMode.HALF_UP);

            //Adiciona diferença
            if (somaDiaLctos1.compareTo(somaDiaLctos2) != 0) {
                Map<String, BigDecimal> diff = new HashMap<>();
                diff.put("lctos1", somaDiaLctos1);
                diff.put("lctos2", somaDiaLctos2);
                diff.put("diferenca", somaDiaLctos1.subtract(somaDiaLctos2));

                diferencaDias.put(dia, diff);
            }
        }

        if (!diferencaDias.isEmpty()) {
            r.append("Houve diferença na comparação entre ");
            r.append(nome1);
            r.append(" e ");
            r.append(nome2);
            r.append(br());
            r.append(nome1);
            r.append(": ");
            r.append(showBigDecimal(totalLctos1));
            r.append(br());
            r.append(nome2);
            r.append(": ");
            r.append(showBigDecimal(totalLctos2));
            r.append(br());
            r.append("Diferença: ");
            r.append(showBigDecimal(totalLctos1.subtract(totalLctos2)));
            r.append(br());
            r.append(br());

            StringBuilder linhasTable = new StringBuilder();
            linhasTable.append(tr(td("Dia") + td(nome1) + td(nome2) + td("Diferença")));

            diferencaDias.forEach((dia, values) -> {
                linhasTable.append(
                        tr(
                                td(dia.toString())
                                + td(showBigDecimal(values.get("lctos1")))
                                + td(showBigDecimal(values.get("lctos2")))
                                + td(showBigDecimal(values.get("diferenca")))
                        )
                );
            });

            r.append(table(linhasTable.toString()));
            r.append(br());
        } else {
            r.append("Nenhuma diferença encontrada entre os arquivos");
        }

        //System.out.println(r.toString().replaceAll("<br>", "\n").replaceAll("</tr>", "\n"));
        return r.toString();
    }

    private static BigDecimal getSumFromDayLcto(String day, List<LctoTemplate> lctos) {
        BigDecimal[] sum = new BigDecimal[]{new BigDecimal("0.00")};

        lctos.forEach((l) -> {
            //Se for o dia
            if (l.getData().startsWith(day)) {
                BigDecimal val = l.getValor();
                if (l.getEntrada_Saida().equals("S")) {
                    val = val.negate();
                }

                sum[0] = sum[0].add(val);
            }
        });

        return sum[0];
    }

    private static String showBigDecimal(BigDecimal big) {
        return NumberFormat.getCurrencyInstance().format(big.setScale(2, RoundingMode.HALF_UP));
    }

    private static String table(String trs) {
        return "<table border='1'>" + trs + "</table>";
    }

    private static String tr(String td) {
        return "<tr>" + td + "</tr>";
    }

    private static String td(String html) {
        return "<td>" + html + "</td>";
    }

    private static String br() {
        return "<br>";
    }
}
