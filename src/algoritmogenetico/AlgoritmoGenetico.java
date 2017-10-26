package algoritmogenetico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author matheus
 */
public class AlgoritmoGenetico {

    public Random r = new Random();

    public final int QUANTIDADE_BITS = 44;
    public final int QUANTIDADE_GERACOES = 1000;
    public final int POPULACAO_MAXIMA = 100;
    public final double TAXA_CROSSOVER = 0.65f;
    public final double TAXA_MUTACAO = 0.08f;

    public List<Solucao> populacao = new LinkedList<>();
    public List<Solucao> descendentes = new LinkedList<>();

    /**
     * Método principal da classe e que inicializa a execução do programa.
     *
     * @param args
     */
    public static void main(String[] args) {

        AlgoritmoGenetico algoritmoGenetico = new AlgoritmoGenetico();

        algoritmoGenetico.inicializaPopulacao();
        Collections.sort(algoritmoGenetico.populacao);

        int geracao = 0;
        while (geracao < algoritmoGenetico.QUANTIDADE_GERACOES) {

            algoritmoGenetico.proximaGeracao();

            algoritmoGenetico.mutacao();

            algoritmoGenetico.gerarDescendentes();

            Collections.sort(algoritmoGenetico.populacao);

            System.out.println(algoritmoGenetico.mediaGeracaoFitness());

            geracao++;
        }

        algoritmoGenetico.imprimir(algoritmoGenetico.populacao.get(0));

    }

    /**
     * Método que inicializa a população.
     *
     */
    public void inicializaPopulacao() {

        for (int i = 0; i < POPULACAO_MAXIMA; i++) {
            int[] individual = new int[QUANTIDADE_BITS];
            for (int j = 0; j < QUANTIDADE_BITS; j++) {
                individual[j] = r.nextInt(2);
            }
            populacao.add(new Solucao(individual));
        }

    }

    /**
     * Método que copia valores do List de descendentes para a população.
     * Utilizado quando irá gerar outra linhagem da árvore genealógica.
     */
    public void gerarDescendentes() {

        for (Solucao solution : descendentes) {
            this.populacao.add(solution);
        }
        // Ordenar.
        Collections.sort(this.populacao);

        // Remove os piores.
        for (int i = this.populacao.size() - 1; i >= this.POPULACAO_MAXIMA; i--) {
            this.populacao.remove(i);
        }

        this.descendentes.clear();

    }

    /**
     * Método que irá gerar a próxima linhagem.
     */
    public void proximaGeracao() {

        // Ajusta para um número par de pais.
        int size = (int) (POPULACAO_MAXIMA / 2 * TAXA_CROSSOVER) * 2;

        for (int i = 0; i < size; i += 2) {

            //List<Solucao> sons = this.combinacao(populacao.get(this.torneioBinario()), populacao.get(this.torneioBinario()));
            List<Solucao> sons = this.combinacao(populacao.get(roleta()), populacao.get(roleta()));

            this.descendentes.add(sons.get(0));
            this.descendentes.add(sons.get(1));
        }

    }

    /**
     * Método que sorteia dois pontos dentro da população e retorna o menor
     * valor para reprodução.
     *
     * @return
     */
    public int torneioBinario() {
        int x1 = r.nextInt(POPULACAO_MAXIMA);
        int x2 = r.nextInt(POPULACAO_MAXIMA);

        if (x1 < x2) {
            return x1;
        } else {
            return x2;
        }
    }

    /**
     * Método que sorteia o ponto a ser levado para reproduzir na população.
     *
     * @return
     */
    public int roleta() {
        double roleta[] = new double[POPULACAO_MAXIMA];
        double soma = 0;
        double sorteio = 0;
        int i = 0;

        for (Solucao s : populacao) {
            soma += s.fitness;
            roleta[i++] = soma - populacao.get(0).fitness;
        }

        soma = 0;
        i = 0;

        sorteio = r.nextDouble() * soma;

        while (roleta[i] > sorteio) {
            i++;
        }

        return i;

    }

    /**
     * Método que faz a reprodução dos pontos passados (combinação), gerando
     * assim mais dois filhos.
     *
     * @param parent_a
     * @param parent_b
     * @return
     */
    public ArrayList<Solucao> combinacao(Solucao parent_a, Solucao parent_b) {

        ArrayList<Solucao> sons = new ArrayList<>();

        int cut_point = QUANTIDADE_BITS / 2;
        int[] son_a = new int[QUANTIDADE_BITS];
        int[] son_b = new int[QUANTIDADE_BITS];

        for (int i = 0; i < QUANTIDADE_BITS; i++) {
            if (i <= cut_point) {
                son_a[i] = parent_a.chromosome[i];
                son_b[i] = parent_b.chromosome[i];
            } else {
                son_a[i] = parent_b.chromosome[i];
                son_b[i] = parent_a.chromosome[i];
            }
        }

        sons.add(new Solucao(son_a));
        sons.add(new Solucao(son_b));

        return sons;
    }

    /**
     * Função que irá realizar a mutação dos genes. A função utiliza do método
     * bit-flip.
     */
    public void mutacao() {
        for (Solucao solution : descendentes) {
            for (int i = 0; i < solution.chromosome.length; i++) {
                double prob = r.nextDouble();
                if (prob <= TAXA_MUTACAO) {
                    if (solution.chromosome[i] == 0) {
                        solution.chromosome[i] = 1;
                    } else {
                        solution.chromosome[i] = 0;
                    }
                }
                solution.evaluate();
            }
        }
    }

    /**
     * Função que retorna a média dos fitness da geracao.
     *
     * @return
     */
    public double mediaGeracaoFitness() {
        double avg = 0;
        for (Solucao solution : populacao) {
            avg += solution.fitness;
        }
        return avg / populacao.size();
    }

    /**
     * Método que irá imprimir a solução da função, ou seja os cromossomos mais
     * adequados.
     *
     * @param s
     */
    public void imprimir(Solucao s) {

        System.out.println("\n-----------------------------------------------------------------------------------------------");
        System.out.print("Gene: ");

        for (int gene : s.chromosome) {
            System.out.print("|" + gene);
        }

        System.out.println("\n\n-----------------------------------------------------------------------------------------------");
        System.out.println("Fitness: " + s.fitness);

        double x = 0;
        double y = 0;
        int[] bits = populacao.get(POPULACAO_MAXIMA - 1).chromosome;

        for (int i = 0; i < bits.length / 2; i++) {
            x += bits[i] * Math.pow(2, bits.length - i - 1);
        }

        int metade = bits.length / 2;
        for (int i = bits.length / 2; i < bits.length; i++) {
            y += bits[i] * Math.pow(2, bits.length - (i - metade) - 1);
        }

        System.out.println("\n-----------------------------------------------------------------------------------------------");
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("\n-----------------------------------------------------------------------------------------------");

    }

}
