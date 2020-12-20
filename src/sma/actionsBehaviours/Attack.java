package sma.actionsBehaviours;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import org.jpl7.Query;

import com.jme3.math.Vector3f;

import env.jme.Situation;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import sma.AbstractAgent;
import sma.agents.FinalAgent;
import sma.actionsBehaviours.PrologBehavior;

public class Attack extends TickerBehaviour{
	
	private static final long serialVersionUID = 4340498260100499547L;
	
	public static long FORGET_TIME = 35;;
	
	FinalAgent agent;
	
	String enemy;
	long lastTimeSeen;
	Vector3f lastPosition;
	
	public static boolean openFire = false;

	public Attack(Agent a, long period, String enemy) {
		super(a, period);
		this.enemy = enemy;
		agent = (FinalAgent)((AbstractAgent)a);
		lastPosition = agent.getEnemyLocation(enemy);
		lastTimeSeen = System.currentTimeMillis();
		openFire = false;
		//System.out.println("Player Attack");
	}

	

	@Override
	protected void onTick() {
		//System.out.println("ATTAAAAAAAAAAAAAAAAAAAAAAAAAAACK");
		askForFirePermission();
	
		agent.goTo(lastPosition);
		
		if(agent.isVisible(enemy, AbstractAgent.VISION_DISTANCE)){
			lastTimeSeen = System.currentTimeMillis();
			lastPosition = agent.getEnemyLocation(enemy);
			agent.lookAt(lastPosition);
			
			// si on ne veut pas modifier NewEnv.java...
			//savePercoitSonAdversaireCSV(); // TP 2 Question 3)c
			
			if (openFire){
				System.out.println("Enemy visible, FIRE !");				
				agent.lastAction = Situation.SHOOT;
				agent.shoot(enemy);
				
				// si on ne veut pas modifier NewEnv.java...
				//savePercoitSonAdversaireCSV(); // TP 2 Question 3)c
			}
			
		}else{
			
			if (System.currentTimeMillis() - lastTimeSeen > FORGET_TIME * getPeriod()){
				System.out.println("The enemy ran away");
				agent.removeBehaviour(this);
				agent.currentBehavior = null;
			}
			agent.lastAction = Situation.FOLLOW;
			
		}
	}
	
	public static void askForFirePermission(){
		String query = "toOpenFire("
					+PrologBehavior.sit.enemyInSight +","
					+PrologBehavior.sit.impactProba+")";
		
		openFire = Query.hasSolution(query);
	}
	// si on ne veut pas modifier NewEnv.java...
	/*
	public static void savePercoitSonAdversaireCSV(){ // TP 2 Question 3)c
		
		String res = PrologBehavior.sit.toCSVFile();
		int id = new Random().nextInt(10000);
		System.out.println(res);
		try{
		    PrintWriter writer = new PrintWriter(System.getProperty("user.dir")+"/ressources/learningBase/percoit_son_adversaire/Mosimu_"+id+".csv", "UTF-8");
		    writer.println(res);
		    writer.close();
		    System.out.println("Execution result saved in /ressources/learningBase/percoit_son_adversaire/");
		} catch (IOException e) {
		  System.out.println(e);
		  System.out.println("Experiment saving failed");
		}		
	}
	*/
}














