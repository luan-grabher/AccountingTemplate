package TemplateContabil.Model;

import TemplateContabil.Model.Entity.LctoTemplate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import Dates.Dates;

public class ComparacaoTemplates {

    //static month to filter
    private static Integer month = null;

    //static year to filter
    private static Integer year = null;

    //public set month to filter
    public static void setMonth(Integer month) {
        ComparacaoTemplates.month = month;
    }

    //public set year to filter
    public static void setYear(Integer year) {
        ComparacaoTemplates.year = year;
    }

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
            //Pega soma do dia sem zero na frente
            BigDecimal somaDiaLctos1 = getSumFromDayLcto(dia, lctos1).setScale(2, RoundingMode.HALF_UP);
            BigDecimal somaDiaLctos2 = getSumFromDayLcto(dia, lctos2).setScale(2, RoundingMode.HALF_UP);

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

    private static BigDecimal getSumFromDayLcto(Integer day, List<LctoTemplate> lctos) {
        BigDecimal[] sum = new BigDecimal[]{new BigDecimal("0.00")};

        //for each lcto
        for (LctoTemplate lcto : lctos) {
            try {
                Calendar cal = Dates.getCalendarFromFormat(lcto.getData(), "dd/MM/yyyy");
                //if month is null or month is equal to lcto month and year is null or year is equal to lcto year
                if (month == null || (month != null && month.equals(cal.get(Calendar.MONTH) + 1))) {
                    if (year == null || (year != null && year.equals(cal.get(Calendar.YEAR)))) {
                        //if day converted to int is equal to lcto day
                        if (day == cal.get(Calendar.DAY_OF_MONTH)) {
                            BigDecimal val = lcto.getValor();
                            if (lcto.getEntrada_Saida().equals("S")) {
                                val = val.negate();
                            }
            
                            sum[0] = sum[0].add(val);
                        }
                    }
                }   
            } catch (Exception e) {
                
            }
        }

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
