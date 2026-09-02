package minipascal.lexico.tabela;

import minipascal.lexico.model.TipoToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela de palavras reservadas / operadores "de palavra" da linguagem.
 *
 * Decisão de projeto (c) do enunciado: opção (a) — uma estrutura de dados
 * dedicada, construída uma única vez no início da execução, separada da
 * tabela de símbolos (que só faz sentido na Unidade II).
 *
 * Estrutura escolhida: HashMap<String, TipoToken> — busca O(1), atende à
 * exigência de "busca eficiente" do enunciado.
 *
 * Esta tabela cobre TRÊS coisas diferentes, todas reconhecidas pelo mesmo
 * caminho no autômato (sequência de letras — estado q1 do AFND/AFD):
 *   1) As palavras reservadas oficiais (item 10 do enunciado);
 *   2) "mod" (item 2.6) — que a linguagem exige como operador aritmético,
 *      mas que NÃO está na lista oficial de palavras reservadas do item 10;
 *   3) "and", "or", "not" (item 2.5) — operadores lógicos, também ausentes
 *      da lista oficial do item 10.
 *
 * Atenção (correção importante): "div" JÁ está na lista oficial de palavras
 * reservadas do item 10 — por isso ele é classificado como Palavra
 * Reservada, e não como Operador aritmético. Só "mod" (que não está na
 * lista oficial) recebe o tipo Operador aritmético explicitamente.
 */
public class TabelaPalavrasReservadas {

    private final Map<String, TipoToken> tabela;

    public TabelaPalavrasReservadas() {
        this.tabela = new HashMap<>();
        construirTabela();
    }

    /**
     * Executada uma única vez, no construtor. Todas as chaves ficam em
     * minúsculo — assumimos que a linguagem é case-sensitive e que as
     * palavras reservadas aparecem em minúsculo no código-fonte, igual ao
     * exemplo do professor ("program", "begin", "if"...). Se o professor
     * confirmar que a linguagem deve ser case-insensitive, é só normalizar
     * o lexema para minúsculo antes de chamar buscar().
     */
    private void construirTabela() {
        String[] palavrasReservadas = {
            "absolute", "array", "begin", "case", "char", "const", "div", "do",
            "downto", "else", "end", "external", "file", "for", "forward", "func",
            "function", "goto", "if", "implementation", "integer", "interface",
            "interrupt", "label", "main", "nil", "nit", "of", "packed", "proc",
            "program", "real", "record", "repeat", "set", "shl", "shr", "string",
            "then", "to", "type", "unit", "until", "uses", "var", "while", "with",
            "xor"
        };
        for (String palavra : palavrasReservadas) {
            tabela.put(palavra, TipoToken.PALAVRA_RESERVADA);
        }

        // "mod" não está na lista oficial do item 10, mas é exigido pelo item 2.6
        tabela.put("mod", TipoToken.OPERADOR_ARITMETICO);

        // Operadores lógicos do item 2.5 — também ausentes da lista oficial
        tabela.put("and", TipoToken.OPERADOR_LOGICO);
        tabela.put("or", TipoToken.OPERADOR_LOGICO);
        tabela.put("not", TipoToken.OPERADOR_LOGICO);
    }

    /**
     * Busca o tipo de token associado a um lexema em formato de identificador.
     *
     * @param lexema a cadeia já reconhecida pelo autômato (estado final q1)
     * @return o TipoToken correspondente, ou null se o lexema não está na
     *         tabela — nesse caso o chamador (o lexer) deve classificá-lo
     *         como Identificador comum.
     */
    public TipoToken buscar(String lexema) {
        return tabela.get(lexema);
    }

    public boolean contem(String lexema) {
        return tabela.containsKey(lexema);
    }

    public int tamanho() {
        return tabela.size();
    }
}
