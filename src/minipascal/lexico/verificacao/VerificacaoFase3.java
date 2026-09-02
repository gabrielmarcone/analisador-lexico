package minipascal.lexico.verificacao;

import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

/**
 * Verificação manual da Fase 3 (model + tabela), sem depender de JUnit —
 * útil só neste momento inicial, antes de configurar JUnit na IDE. Depois
 * que os testes JUnit de verdade existirem (Fase 8), esta classe pode ser
 * apagada.
 */
public class VerificacaoFase3 {

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        TabelaPalavrasReservadas tabela = new TabelaPalavrasReservadas();

        // 1) Palavra reservada oficial
        checar("'program' deve ser Palavra reservada",
                tabela.buscar("program") == TipoToken.PALAVRA_RESERVADA);

        // 2) 'div' está na lista oficial -> Palavra reservada (correção feita)
        checar("'div' deve ser Palavra reservada (está no item 10)",
                tabela.buscar("div") == TipoToken.PALAVRA_RESERVADA);

        // 3) 'mod' NÃO está na lista oficial -> Operador aritmético
        checar("'mod' deve ser Operador aritmetico (não está no item 10)",
                tabela.buscar("mod") == TipoToken.OPERADOR_ARITMETICO);

        // 4) Operadores lógicos
        checar("'and' deve ser Operador logico",
                tabela.buscar("and") == TipoToken.OPERADOR_LOGICO);
        checar("'or' deve ser Operador logico",
                tabela.buscar("or") == TipoToken.OPERADOR_LOGICO);
        checar("'not' deve ser Operador logico",
                tabela.buscar("not") == TipoToken.OPERADOR_LOGICO);

        // 5) Palavra fora da tabela -> null (o lexer trataria como Identificador)
        checar("'total' (identificador comum) deve retornar null",
                tabela.buscar("total") == null);

        // 6) 'Media_das_medias' (variação de caixa) não deve casar, já que
        //    a tabela é case-sensitive e a palavra não é reservada mesmo
        checar("'Media_das_medias' deve retornar null (é identificador)",
                tabela.buscar("Media_das_medias") == null);

        // 7) Contagem: 48 palavras da lista oficial + mod + and + or + not = 52
        checar("tabela deve ter 52 entradas (48 reservadas + mod + and + or + not)",
                tabela.tamanho() == 52);

        // 8) Formatação de Token — deve bater com o formato do exemplo do professor
        Token t = new Token("program", TipoToken.PALAVRA_RESERVADA, 1);
        checar("Token.toString() deve ser 'program\\tPalavra reservada'",
                t.toString().equals("program\tPalavra reservada"));

        Token t2 = new Token("pi", TipoToken.IDENTIFICADOR, 3);
        checar("getLexema() deve retornar 'pi'", t2.getLexema().equals("pi"));
        checar("getLinha() deve retornar 3", t2.getLinha() == 3);

        System.out.println();
        System.out.println(total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void checar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("[OK]    " + descricao);
        } else {
            falhas++;
            System.out.println("[FALHA] " + descricao);
        }
    }
}
