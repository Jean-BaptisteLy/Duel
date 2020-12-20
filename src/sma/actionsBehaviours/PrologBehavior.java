package sma.actionsBehaviours;

import org.jpl7.Query;
import org.lwjgl.Sys;

import com.jme3.math.Vector3f;

import dataStructures.tuple.Tuple2;
import env.jme.NewEnv;
import env.jme.Situation;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import sma.AbstractAgent;
import sma.InterestPoint;
import sma.agents.FinalAgent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PrologBehavior extends TickerBehaviour {


	private static final long serialVersionUID = 5739600674796316846L;

	public static FinalAgent agent;
	public static Class nextBehavior;

	public static Situation sit;

	//public static int last_life; // si on ne veut pas modifier NewEnv.java...

	public PrologBehavior(Agent a, long period) {
		super(a, period);
		agent = (FinalAgent)((AbstractAgent)a);
		//last_life = AbstractAgent.MAX_LIFE; // si on ne veut pas modifier NewEnv.java...
	}



	@Override
	protected void onTick() {
		try {
			String prolog = "consult('./ressources/prolog/duel/requete.pl')";
			// si on ne veut pas modifier NewEnv.java...
			/*
			System.out.println("last_life = "+last_life);
			System.out.println("agent_life = "+agent.life);
			if(last_life > agent.life) {
				//System.out.println("---------- Je suis touché !!! ----------");
				saveEstToucheCSV();
				last_life = agent.life;
			}
			*/
			if (!Query.hasSolution(prolog)) {
				System.out.println("Cannot open file " + prolog);
			}
			else {
				sit = Situation.getCurrentSituation(agent);
				List<String> behavior = Arrays.asList("explore", "hunt", "attack");
				ArrayList<Object> terms = new ArrayList<Object>();

				for (String b : behavior) {
					terms.clear();
					// Get parameters 
					if (b.equals("explore")) {
						terms.add(sit.timeSinceLastShot);
						//terms.add(((ExploreBehavior.prlNextOffend)?sit.offSize:sit.defSize ));
						terms.add(((HighestExploreBehavior.prlNextOffend)?sit.offSize:sit.defSize ));
						terms.add(InterestPoint.INFLUENCE_ZONE);
						terms.add(NewEnv.MAX_DISTANCE);
					}
					else if (b.equals("hunt")) {
						terms.add(sit.life);
						terms.add(sit.timeSinceLastShot);
						terms.add(sit.offSize);
						terms.add(sit.defSize);
						terms.add(InterestPoint.INFLUENCE_ZONE);
						terms.add(NewEnv.MAX_DISTANCE);
						terms.add(sit.enemyInSight);
					}else if(b.equals("attack")){
						//terms.add(sit.life);
						terms.add(sit.enemyInSight);
						//terms.add(sit.impactProba);
					}
					else { // RETREAT
						terms.add(sit.life);
						terms.add(sit.timeSinceLastShot);
					}

					String query = prologQuery(b, terms);
					if (Query.hasSolution(query)) {
						//System.out.println("has solution");
						setNextBehavior();
						// on ajoute des behaviors ici
					}
				}
			}
		}catch(Exception e) {
			System.err.println("Behaviour file for Prolog agent not found");
			System.exit(0);
		}
	}



	public void setNextBehavior(){

		if(agent.currentBehavior != null && nextBehavior == agent.currentBehavior.getClass()){
			// si on ne change pas de behavior, on s'arrête maintenant et on continue dans le for
			// donc ne passe pas par un constructeur, donc pas de print du constructeur
			return;
		}
		if (agent.currentBehavior != null){
			// si on change de behavior, alors on supprime l'ancien behavior
			agent.removeBehaviour(agent.currentBehavior);
		}

		//if (nextBehavior == ExploreBehavior.class){
		if (nextBehavior == HighestExploreBehavior.class){
			//ExploreBehavior ex = new ExploreBehavior(agent, FinalAgent.PERIOD);
			HighestExploreBehavior ex = new HighestExploreBehavior(agent, FinalAgent.PERIOD);
			agent.addBehaviour(ex);
			agent.currentBehavior = ex;

		}else if(nextBehavior == HuntBehavior.class){
			HuntBehavior h = new HuntBehavior(agent, FinalAgent.PERIOD);
			agent.currentBehavior = h;
			agent.addBehaviour(h);

		}else if(nextBehavior == Attack.class){
			Attack a = new Attack(agent, FinalAgent.PERIOD, sit.enemy);
			agent.currentBehavior = a;
			agent.addBehaviour(a);

		}


	}


	public String prologQuery(String behavior, ArrayList<Object> terms) {
		String query = behavior + "(";
		for (Object t: terms) {
			query += t + ",";
		}
		return query.substring(0,query.length() - 1) + ")";
	}

	public static void executeExplore() {
		//System.out.println("explore");
		//nextBehavior = ExploreBehavior.class;
		nextBehavior = HighestExploreBehavior.class;
	}


	public static void executeHunt() {
		//System.out.println("hunt");
		nextBehavior = HuntBehavior.class;
	}

	public static void executeAttack() {
		//System.out.println("attack");
		nextBehavior = Attack.class;
	}


	public static void executeRetreat() {
		//System.out.println("retreat");
		//nextBehavior = RetreatBehavior.class;
	}
	
	// si on ne veut pas modifier NewEnv.java...
	/*
	public static void saveEstToucheCSV(){ // TP 2 Question 3)c
		
		String res = sit.toCSVFile();
		int id = new Random().nextInt(10000);
		System.out.println(res);
		try{
		    PrintWriter writer = new PrintWriter(System.getProperty("user.dir")+"/ressources/learningBase/est_touche/Mosimu_"+id+".csv", "UTF-8");
		    writer.println(res);
		    writer.close();
		    System.out.println("Execution result saved in /ressources/learningBase/est_touche/");
		} catch (IOException e) {
		  System.out.println(e);
		  System.out.println("Experiment saving failed");
		}		
	}
	*/
}