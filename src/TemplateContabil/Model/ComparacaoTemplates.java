package TemplateContabil.Model;

import TemplateContabil.Model.Entity.LctoTemplate;
import Auxiliar.Valor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ComparacaoTemplates {

    public static String getComparacaoString(String nome1, String nome2, List<LctoTemplate> lctos1, List<LctoTemplate> lctos2) {
        StringBuilder r = new StringBuilder();

        List<List<Valor>> diferencaDias = new ArrayList<>();
        BigDecimal totalLctos1 = new BigDecimal("0").setScale(2, RoundingMode.CEILING);
        BigDecimal totalLctos2 = new BigDecimal("0").setScale(2, RoundingMode.CEILING);

        //Faz soma total por cada dia se tiver diferenca adiciona diferença
        for (int dia = 1; dia <= 31; dia++) {

            String diaBarra = dia + "/";
            String diaMMBarra = (dia < 10 ? "0" : "") + diaBarra;

            //Pega soma do dia sem zero na frente
            BigDecimal somaDiaLctos1 = getSumFromDayLcto(diaBarra, lctos1).setScale(2, RoundingMode.CEILING);
            BigDecimal somaDiaLctos2 = getSumFromDayLcto(diaBarra, lctos2).setScale(2, RoundingMode.CEILING);

            //Pega soma do dia com zero na frente
            if (!diaBarra.equals(diaMMBarra)) {
                somaDiaLctos1 = somaDiaLctos1.add(getSumFromDayLcto(diaMMBarra, lctos1)).setScale(2, RoundingMode.CEILING);
                somaDiaLctos2 = somaDiaLctos2.add(getSumFromDayLcto(diaMMBarra, lctos2)).setScale(2, RoundingMode.CEILING);
            }

            //Soma aos totais
            totalLctos1 = totalLctos1.add(somaDiaLctos1).setScale(2, RoundingMode.CEILING);
            totalLctos2 = totalLctos2.add(somaDiaLctos2).setScale(2, RoundingMode.CEILING);

            //Adiciona diferença
            if (somaDiaLctos1.compareTo(somaDiaLctos2) != 0) {
                List<Valor> diferenca = new ArrayList<>();
                diferenca.add(new Valor(String.valueOf(dia), "dia"));
                diferenca.add(new Valor(somaDiaLctos1.toString(), nome1));
                diferenca.add(new Valor(somaDiaLctos2.toString(), nome2));
                diferenca.add(new Valor(somaDiaLctos1.subtract(somaDiaLctos2).toString(), "diferença"));

                diferencaDias.add(diferenca);
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

            diferencaDias.forEach((diferencaDia) -> {
                if(!diferencaDia.get(1).getString().equals(diferencaDia.get(2).getString())){
                    linhasTable.append(
                            tr(
                                    td(diferencaDia.get(0).getString())
                                    + td(showBigDecimal(diferencaDia.get(1).getBigDecimal()))
                                    + td(showBigDecimal(diferencaDia.get(2).getBigDecimal()))
                                    + td(showBigDecimal(diferencaDia.get(3).getBigDecimal()))
                            )
                    );
                }
            });

            r.append(table(linhasTable.toString()));
            r.append(br());
        }

        //System.out.println(r.toString().replaceAll("<br>", "\n").replaceAll("</tr>", "\n"));

        return r.toString();
    }

    private static BigDecimal getSumFromDayLcto(String day, List<LctoTemplate> lctos) {
        return new BigDecimal(String.valueOf(lctos.stream().filter(l -> l.getData().substring(0, day.length()).equals(day)).mapToDouble(l -> l.getValor().getDouble()).sum()));
    }

    private static String showBigDecimal(BigDecimal big) {
        return NumberFormat.getCurrencyInstance().format(big.setScale(2, RoundingMode.CEILING));
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
