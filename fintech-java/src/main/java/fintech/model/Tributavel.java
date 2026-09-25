package fintech.model;

/**
 * Contrato implementado por tudo que gera tributo (IOF/taxas) no sistema.
 * Permite tratar Conta e Cartao pelo mesmo tipo -> polimorfismo por interface.
 */
public interface Tributavel {

    /** Valor do tributo devido no periodo. */
    double calcularTributo();

    /** Identificacao do tributo, com implementacao padrao (default method). */
    default String getNomeTributo() {
        return "IOF";
    }
}
