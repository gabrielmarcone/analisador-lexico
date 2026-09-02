package minipascal.lexico.model;

/**
 * Classes léxicas da linguagem. O texto de cada valor é o que aparece na
 * coluna de token do arquivo de saída (sem código numérico).
 */
public enum TipoToken {

    PALAVRA_RESERVADA("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    NUMERO_INTEIRO("Número inteiro"),
    NUMERO_REAL("Número real"),
    OPERADOR_ARITMETICO("Operador aritmético"),
    OPERADOR_RELACIONAL("Operador relacional"),
    OPERADOR_LOGICO("Operador lógico"),
    SIMBOLO_ESPECIAL("Símbolo especial"),
    ATRIBUICAO("Atribuição"),
    FIM("Fim"),
    CONSTANTE_STRING("Constante string"),
    CONSTANTE_CHAR("Constante char"),
    ERRO_LEXICO("Erro léxico");

    private final String descricao;

    TipoToken(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
