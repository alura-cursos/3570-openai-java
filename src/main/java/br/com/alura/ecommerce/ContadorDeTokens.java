package br.com.alura.ecommerce;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.ModelType;

import java.math.BigDecimal;

public class ContadorDeTokens {

    public static void main(String[] args) {
        var registry = Encodings.newDefaultEncodingRegistry();
        var enc = registry.getEncodingForModel(ModelType.GPT_3_5_TURBO);
        var qtd = enc.countTokens("Identifique o perfil de compra de cada cliente");

        System.out.println("QTD de Tokens: " +qtd);
        var custo = new BigDecimal(qtd)
                .divide(new BigDecimal("1000"))
                .multiply(new BigDecimal("0.0010"));
        System.out.println("Custo da requisição: R$" +custo);
    }

}
