package game;

import java.util.ArrayList;
import java.util.List;

public class AiBot {
    //Fields
    private List<MovingGroup> movingGroups = new ArrayList<>();
    private Round round;
    private int currentStep;
    private int teamIndex;
    private int homePlanetIndex;
    private int[][] distanceMap;
    private List<Planet> planets;

    private List<Planet> myPlanets;

    public AiBot(Round round)
    {
        this.round = round;
        this.currentStep = 0;
        this.planets = new ArrayList<>();
        this.myPlanets = new ArrayList<>();
        this.homePlanetIndex = -1;
    }

    //Functions
    private void logDistanceMap()
    {
        for(int i=0; i < this.distanceMap.length; i++){
            for(int j=0; j < this.distanceMap.length; j++){
                System.out.print(this.distanceMap[i][j]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
    }
    private int getClosestPlanetId(int from)
    {
        int closestPlanetId = -1;
        int minDistance = 0;
        //this.planets.stream().filter(planet -> this.round.getDistanceMap());
        for(int i=0; i < this.distanceMap.length; i++){
            if (i != from)
            {
                if (minDistance == 0)
                {
                    minDistance = this.distanceMap[from][i];
                    closestPlanetId = i;
                }
                else
                {
                    if (this.distanceMap[from][i] <  minDistance)
                    {
                        minDistance = this.distanceMap[from][i];
                        closestPlanetId = i;
                    }
                }

            }
        }
        //System.out.println(this.round.getDistanceMap());
        return closestPlanetId;
    }

    private int getClosestFrPlNotAttacked(int from)
    {
        int closestPlanetId = -1;
        int minDistance = 10000;
        //this.planets.stream().filter(planet -> this.round.getDistanceMap());
        for(int i=0; i < this.distanceMap.length; i++){
            if (i != from)
            {

                boolean armySended = false;
                for (MovingGroup mg : movingGroups)
                {
                    if (mg.getTo() == i)
                        armySended = true;
                }
               if (armySended == false)
               {
                   if (this.distanceMap[from][i] <  minDistance && this.distanceMap[from][i] <  12)
                   {
                       minDistance = this.distanceMap[from][i];
                       closestPlanetId = i;
                   }
               }

            }
        }
        //System.out.println(this.round.getDistanceMap());
        return closestPlanetId;
    }

    public void sense()
    {
        this.currentStep = this.round.getCurrentStep();
        this.teamIndex = this.round.getTeamId();
        this.planets = this.round.getPlanets();
        this.distanceMap = this.round.getDistanceMap();
        this.homePlanetIndex = round.getOwnPlanets().get(0).getId();
        this.myPlanets = this.round.getOwnPlanets();
    }
    public void think()
    {

    }
    public void act()
    {
        if (this.currentStep == 0 && this.homePlanetIndex != -1)
        {
            boolean cantSend = false;
            while (this.getClosestFrPlNotAttacked(this.homePlanetIndex) != -1 && cantSend == false)
            {
                MovingGroup mG = new MovingGroup();
                mG.setFrom(this.homePlanetIndex);
                int closesPlanet = this.getClosestFrPlNotAttacked(this.homePlanetIndex);
                mG.setTo(closesPlanet);
                if (this.planets.get(closesPlanet).getPopulation() > this.planets.get(this.homePlanetIndex).getPopulation())
                    cantSend = true;
                else
                {
                    mG.setCount(this.planets.get(closesPlanet).getPopulation() + 1);
                    this.movingGroups.add(mG);
                }
            }

        }
//        if (this.currentStep == 1)
//        {
//            MovingGroup mG = new MovingGroup();
//            mG.setFrom(this.homePlanetIndex);
//            mG.setTo(4);
//            mG.setCount(6);
//            this.movingGroups.add(mG);
//        }
    }

    public List<MovingGroup> getMovingGroups()
    {
        this.sense();
        this.think();
        this.act();
        return this.movingGroups;
    }

}
