package algoritmogenetico;

import static java.lang.Math.sin;

/**
 *
 * @author matheus
 */
public class Solucao implements Comparable<Solucao> {

    public final static int DOMINIO_MENOR = -100;
    public final static int DOMINIO_MAIOR = 100;

    public int[] chromosome;
    public double fitness;

    /**
     * Construtor da classe.
     *
     * @param bits
     */
    public Solucao(int[] bits) {
        this.chromosome = bits;
        this.fitness = evaluate(chromosome);
    }

    /**
     * Método que converte de binário para double.
     *
     * @param bits
     * @return
     */
    private static double binarioToDouble(int[] bits) {
        double retorno = 0;

        //Converte para a base 10.
        for (int i = 0; i < bits.length; i++) {
            retorno = retorno + bits[i] * Math.pow(2, bits.length - i - 1);
        }

        //Obtem tamanho do espaço
        int espaco = DOMINIO_MAIOR - DOMINIO_MENOR;

        retorno = retorno * (espaco / (Math.pow(2, bits.length) - 1));

        retorno = retorno + DOMINIO_MENOR;

        return retorno;
    }

    /**
     * Função de avaliação do fitness da função.
     *
     * @param bits
     * @return
     */
    public static double evaluate(int[] bits) {
        double x = 0;
        double y = 0;
        int vetor[] = new int[bits.length / 2];

        for (int i = 0; i < bits.length / 2; i++) {
            vetor[i] = bits[i];
        }
        x = binarioToDouble(vetor);

        //Obtendo o valor dos bits de Y.
        int metade = bits.length / 2;
        for (int i = metade; i < bits.length; i++) {
            vetor[i - metade] = bits[i];
        }
        y = binarioToDouble(vetor);

        return (0.5 + ((Math.pow(sin(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))), 2) - 0.5) / (Math.pow(1 + 0.001 * (Math.pow(x, 2) + Math.pow(y, 2)), 2))));

    }

    public void evaluate() {
        this.fitness = evaluate(this.chromosome);
    }

    /**
     * Função de comparação entre dois fitness. Retorna 1 para parametro menor,
     * -1 para parametro maior e 0 para igualdade
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Solucao o) {

        if (this.fitness < o.fitness) {
            return -1;
        }
        if (this.fitness > o.fitness) {
            return 1;
        }

        return 0;
    }

}
