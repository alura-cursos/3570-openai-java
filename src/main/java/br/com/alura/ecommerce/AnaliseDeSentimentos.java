package br.com.alura.ecommerce;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AnaliseDeSentimentos {

    public static void main(String[] args) {
        try {
            var promptSistema = """
                    Você é um analisador de sentimentos de avaliações de produtos.
                    Escreva um parágrafo com até 50 palavras resumindo as avaliações e depois atribua qual o sentimento geral para o produto.
                    Identifique também 3 pontos fortes e 3 pontos fracos identificados a partir das avaliações.
                                    
                    #### Formato de saída
                    Nome do produto:
                    Resumo das avaliações: [resuma em até 50 palavras]
                    Sentimento geral: [deve ser: POSITIVO, NEUTRO ou NEGATIVO]
                    Pontos fortes: [3 bullets points]
                    Pontos fracos: [3 bullets points]
                    """;

            var diretorioAvaliacoes = Path.of("src/main/resources/avaliacoes");
            var arquivosDeAvaliacoes = Files
                    .walk(diretorioAvaliacoes, 1)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .collect(Collectors.toList());

            for (Path arquivo : arquivosDeAvaliacoes) {
                System.out.println("Iniciando analise do produto: " +arquivo.getFileName());
                var promptUsuario = carregarArquivo(arquivo);

                var request = ChatCompletionRequest
                        .builder()
                        .model("gpt-4-1106-preview")
                        .messages(Arrays.asList(
                                new ChatMessage(
                                        ChatMessageRole.SYSTEM.value(),
                                        promptSistema),
                                new ChatMessage(
                                        ChatMessageRole.USER.value(),
                                        promptUsuario)))
                        .build();

                var chave = System.getenv("OPENAI_API_KEY");
                var service = new OpenAiService(chave, Duration.ofSeconds(60));

                var resposta = service
                        .createChatCompletion(request)
                        .getChoices().get(0).getMessage().getContent();

                salvarAnalise(arquivo.getFileName().toString().replace(".txt", ""), resposta);
                System.out.println("Analise finalizada");
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao realizar as analises de sentimentos!");
        }
    }

    private static String carregarArquivo(Path arquivo) {
        try {
            return Files.readAllLines(arquivo).toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar o arquivo!", e);
        }
    }

    private static void salvarAnalise(String arquivo, String analise) {
        try {
            var path = Path.of("src/main/resources/analises/analise-sentimentos-" +arquivo +".txt");
            Files.writeString(path, analise, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o arquivo!", e);
        }
    }

}
