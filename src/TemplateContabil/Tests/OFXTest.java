package TemplateContabil.Tests;

import java.io.File;
import java.util.List;

import TemplateContabil.Model.Entity.LctoTemplate;
import TemplateContabil.Model.Entity.OFX;

public class OFXTest {
    public static void main(String[] args) {
        File file = new File("test.ofx");
        if (!file.exists()) {
            System.out.println("Arquivo 'test.ofx' não encontrado");
            return;
        }

        List<LctoTemplate> lctos = OFX.getListaLctos(file);
    }
}
