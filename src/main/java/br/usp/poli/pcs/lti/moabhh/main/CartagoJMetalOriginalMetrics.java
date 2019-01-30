package br.usp.poli.pcs.lti.moabhh.main;

import br.usp.poli.pcs.lti.moabhh.agents.AlgorithmAgent;
import br.usp.poli.pcs.lti.moabhh.agents.CopelandHyperHeuristicAgent;
import br.usp.poli.pcs.lti.moabhh.agents.HyperHeuristicAgent;
import br.usp.poli.pcs.lti.moabhh.agents.IndicatorVoter;
import br.usp.poli.pcs.lti.moabhh.agents.ProblemManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.IndicatorFactory;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.ProblemFactory;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.HypervolumeCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.RniCalculator;
import br.usp.poli.pcs.lti.moabhh.core.votingmethods.Copeland;
import br.usp.poli.pcs.lti.moabhh.core.votingmethods.VotingMethod;

import cartago.CartagoService;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * The type Cartago j metal.
 *
 * @param <S> the type parameter
 */
@SuppressWarnings("serial")
public class CartagoJMetalOriginalMetrics<S extends Solution<?>> {

    /**
     * The entry point of application.
     *
     * @param args the command line arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        String problemName, path;
        int populationSize, numGenerations, l, k, m, qtdAgAlg;
        int delta, epsilon, beta;
        String votingMethodName;
        int executionCounter = 1;
        if (args.length == 9) {
            problemName = args[0];
            m = Integer.parseInt(args[1]);
            beta = Integer.parseInt(args[2]);
            delta = Integer.parseInt(args[3]);
            epsilon = Integer.parseInt(args[4]);
            k = Integer.parseInt(args[5]);
            l = Integer.parseInt(args[6]);
            votingMethodName = args[7];
            executionCounter = Integer.parseInt(args[8]);
        } else {
            problemName = "WFG1";
            m = 2; //M Number of objective functions
            beta = 32;
            epsilon = 40;//12
            delta = 40;//112
            k = 2; //Number of position parameters
            l = 20; //Number of distance parameters
            votingMethodName = "Copeland";
        }
        //System.out.println(beta+" "+delta+" "+epsilon+" in "+problemName+" "+m);

        VotingMethod votingmethod= new Copeland();
        populationSize = 100;
        numGenerations = 1000;//HARDCODED, in future change to 6250 750
        path = "result/";
        qtdAgAlg = 1;
        SecureRandom rnd = new SecureRandom();
        try {
            Thread.sleep(rnd.nextInt(10000));                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        long uId = System.currentTimeMillis();
        Problem[] problems = ProblemFactory.getProblems(problemName, k, l, m);
        CartagoService.startNode();
        //CartagoService.registerLogger("default", new BasicLoggerOnFile("log.txt"));
        //CartagoService.addArtifactFactory("", new DefaultArtifactFactory());

        /*create manager and artifacts with no agent reference*/
        ProblemManager manager = new ProblemManager(problems[0], uId, populationSize, numGenerations, beta,
                delta, epsilon, votingmethod, executionCounter);
        manager.setFileNameAppendix(votingMethodName);
        manager.initArtifacts();
        //System.out.println(votingmethod.getClass().getCanonicalName());
        //System.out.println(problems[0].getName());
        /*create others agents*/
        ArrayList<IndicatorVoter> voters=new ArrayList<>();
        voters.add(new IndicatorVoter(IndicatorFactory.Hypervolume, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.RNI, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.NR, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.HR, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.UD, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.AlgorithmEffort, uId));
        
        /*
        voters.add(new IndicatorVoter(IndicatorFactory.R, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.Spread, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.AlgorithmEffort, uId));
        voters.add(new IndicatorVoter(IndicatorFactory.Spacing, uId));
        */

        AlgorithmAgent ag1 = new AlgorithmAgent("GDE3", uId, "GDE3.default", "DE.Poly.default");
        AlgorithmAgent ag2 = new AlgorithmAgent("IBEA", uId, "IBEA.default", "SBX.Poly.default");
        AlgorithmAgent ag3 = new AlgorithmAgent("NSGAII", uId, "NSGAII.default", "SBX.Poly.default");
        AlgorithmAgent ag4 = new AlgorithmAgent("SPEA2", uId, "SPEA2.default", "SBX.Poly.default");
        
        

        //HHAgent hh = new RandomHHAgent("HH");
        HyperHeuristicAgent hh = new CopelandHyperHeuristicAgent("HH", uId);


        /*create artifacts with agents reference*/
        manager.initArtifactsSecondPart();

        /*init other agents*/
        ag1.init();
        ag2.init();
        ag3.init();
        ag4.init();
        manager.init();

        voters.forEach((v) -> {
            v.init();
        });
        
        hh.init();

        /*START ALL*/
        manager.start();
        ag1.start();
        ag2.start();
        ag3.start();
        ag4.start();

        voters.forEach((v) -> {
            v.start();
        });

        hh.start();
    }

    /**
     * Print results string.
     *
     * @param currentPopulation the current population
     * @param problem the problem
     * @return the string
     * @throws FileNotFoundException the file not found exception
     */
    public static String printResults(List<? extends Solution<?>> currentPopulation,
            Problem problem) throws FileNotFoundException {
        List<? extends Solution<?>> archive = generateNonDominated(currentPopulation);
        String pf
                = "pareto_fronts/" + problem.getName() + "." + problem.getNumberOfObjectives() + "D.pf";
        HypervolumeCalculator hyp = new HypervolumeCalculator(problem.getNumberOfObjectives(), pf);
        double hypValue = hyp.execute(archive);
        RniCalculator rni = new RniCalculator(problem.getNumberOfObjectives(), currentPopulation.size(),
                pf);
        double rniValue = rni.execute(archive);
        IgdCalculator igd = new IgdCalculator(problem.getNumberOfObjectives(), pf);
        double igdValue = igd.execute(archive);
        return hypValue + ";" + igdValue + ";" + rniValue;
    }

    /**
     * Generate non dominated list.
     *
     * @param population the population
     * @return the list
     */
    public static List<? extends Solution<?>> generateNonDominated(
            List<? extends Solution<?>> population) {
        return SolutionListUtils.getNondominatedSolutions(population);
    }

}
