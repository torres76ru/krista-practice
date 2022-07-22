package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AiBot {
    private class Vector2i {
        private Vector2i(int x, int y) {
            this.x = x;
            this.y = y;
        }
        private int x;
        private int y;
    }
    //Fields
    private List<MovingGroup> movingGroups;
    private Round round;
    private int currentStep;
    private int teamIndex;
    private int enemyTeamId;
    private int homePlanetIndex;
    private int frontPlanetIndex;

    private int minePlanetIndex;
    private int[][] distanceMap;
    private List<Planet> planets;

    private List<Planet> myPlanets;

    private List<Planet> defensePlanets;
    private List<MovingGroup> allMovingGroups;

    private Map<Integer, Boolean> planetDefend;
    public AiBot(Round round)
    {
        this.round = round;
        this.currentStep = 0;
        this.homePlanetIndex = -1;
        this.frontPlanetIndex = -1;
        this.minePlanetIndex = -1;
        this.movingGroups = new ArrayList<>();
        this.planets = new ArrayList<>();
        this.myPlanets = new ArrayList<>();
        this.defensePlanets = new ArrayList<>();
        this.allMovingGroups = new ArrayList<>();
        this.planetDefend = new HashMap<>();

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

    public int getPopulationNextRound(int planetId)
    {
        /*
        * @return int
        * Возвращает кол-во населения на планете, с учетом запланированных экипажей в следующем раунде
        */
        int population = this.planets.get(planetId).getPopulation() + this.planets.get(planetId).getReproduction();
        for (MovingGroup mg : this.movingGroups.stream().filter(movingGroup -> movingGroup.getFrom() == planetId).collect(Collectors.toList()))
        {
            population -= mg.getCount();
        }
        for (MovingGroup mg : this.allMovingGroups.stream().filter(movingGroup -> movingGroup.getTo() == planetId && movingGroup.getStepsLeft() == 1 && movingGroup.getOwnerTeam() == this.teamIndex).collect(Collectors.toList()))
        {
            population += mg.getCount();
        }
        for (MovingGroup mg : this.allMovingGroups.stream().filter(movingGroup -> movingGroup.getTo() == planetId && movingGroup.getStepsLeft() == 1 && movingGroup.getOwnerTeam() != this.teamIndex).collect(Collectors.toList()))
        {
            population -= mg.getCount();
        }
        return population;
    }

    public Vector2i getPopulationChanges(int planetId, int population, int teamId, int stepsLeft)
    {
        /*
         * @return Vector<population, teamId>
         * Возвращает кол-во населения на планете
         */
        Vector2i popChanges = new Vector2i(population, teamId);
        if (teamId != -1)
        {
            popChanges.x += this.planets.get(planetId).getReproduction();
        }


        int invaders = 0;
        int invaders1 = 0;
        int teamId1 = teamIndex;
        int invaders2 = 0;
        int teamId2 = 0;
        if (teamIndex == 0)
            teamId2 = 1;


        for (MovingGroup mg : this.allMovingGroups.stream().filter(movingGroup -> movingGroup.getTo() == planetId && movingGroup.getStepsLeft() == stepsLeft && movingGroup.getOwnerTeam() == this.teamIndex).collect(Collectors.toList()))
        {
            invaders1 += mg.getCount();

        }
        for (MovingGroup mg : this.allMovingGroups.stream().filter(movingGroup -> movingGroup.getTo() == planetId && movingGroup.getStepsLeft() == stepsLeft && movingGroup.getOwnerTeam() != this.teamIndex).collect(Collectors.toList()))
        {
            invaders2 += mg.getCount();
        }

        invaders = Math.abs(invaders1 - invaders2);

        if (invaders1 > invaders2)
        {
            if (teamId == -1)
            {
                if (invaders > popChanges.x)
                {
                    popChanges.x = invaders - popChanges.x;
                    popChanges.y = teamId1;
                }
                else
                {
                    popChanges.x = 0;
                }
            }
            else {
                if (teamId1 == teamId) {
                    popChanges.x += invaders;
                } else {
                    popChanges.x -= invaders;
                    if (popChanges.x == 0) {
                        popChanges.y = -1;
                    }
                    if (popChanges.x < 0) {
                        popChanges.x = Math.abs(popChanges.x);
                        popChanges.y = teamId1;
                    }
                }
            }
        }
        else {
            if (teamId == -1)
            {
                if (invaders > popChanges.x)
                {
                    popChanges.x = invaders - popChanges.x;
                    popChanges.y = teamId2;
                }
                else
                {
                    popChanges.x = 0;
                }
            }
            else {
                if (teamId2 == teamId) {
                    popChanges.x += invaders;
                } else {
                    popChanges.x -= invaders;
                    if (popChanges.x == 0) {
                        popChanges.y = -1;
                    }
                    if (popChanges.x < 0) {
                        popChanges.x = Math.abs(popChanges.x);
                        popChanges.y = teamId2;
                    }
                }
            }
        }

        return popChanges;
    }

    public void supply()
    {
        MovingGroup mG = new MovingGroup();
        for(Planet pl : myPlanets)
        {
            if (pl.getId() != this.frontPlanetIndex)
            {
                if (!this.defensePlanets.contains(pl))
                {
                    mG = new MovingGroup();
                    mG.setFrom(pl.getId());
                    mG.setTo(this.frontPlanetIndex);
                    mG.setCount(this.getPopulationNextRound(pl.getId()) - 1);
                    this.movingGroups.add(mG);
                }
            }
        }
    }

    public void defense()
    {
        for (Planet planet : this.myPlanets.stream().filter(planet-> planet.getId() != this.frontPlanetIndex).collect(Collectors.toList()))
        {
            List<Vector2i> prognoz = this.oracule(planet, planet.getPopulation() - planet.getReproduction(),this.distanceMap[planet.getId()][this.frontPlanetIndex] + 1);
            for (Vector2i vect : prognoz)
            {
                if (vect.y != this.teamIndex && this.defensePlanets.stream().filter(defensePlanet -> planet.getId() == defensePlanet.getId()).collect(Collectors.toList()).isEmpty())
                {
                    this.defensePlanets.add(planet);

                }
            }
        }

        for (Planet planet : this.defensePlanets)
        {
            List<Vector2i> prognoz = this.oracule(planet, planet.getPopulation(),this.distanceMap[planet.getId()][this.frontPlanetIndex] + 1);
            if (prognoz.get(this.distanceMap[planet.getId()][this.frontPlanetIndex]).y != teamIndex)
            {
                MovingGroup mG = new MovingGroup();
                mG.setFrom(this.frontPlanetIndex);
                mG.setTo(planet.getId());
                mG.setCount(howMuchCanSend(this.frontPlanetIndex,prognoz.get(this.distanceMap[planet.getId()][this.frontPlanetIndex]).x + 1));
                this.movingGroups.add(mG);
            }
        }
    }

    public List<Vector2i> oracule(Planet planet, int population, int steps)
    {
        /*
        *@return List<Vector2i> (Население, Владелец)
        * Возвращает прогноз населения на планете в течении указанного кол-ва шагов
         */
        List<Vector2i> prognoz = new ArrayList<>();
        for (int i = 0; i < steps; i++)
        {
            if (i == 0)
                prognoz.add(this.getPopulationChanges(planet.getId(), population, planet.getOwnerTeam(), i + 1));
            else
                prognoz.add(this.getPopulationChanges(planet.getId(), prognoz.get(i-1).x, prognoz.get(i-1).y, i + 1));
        }
        return prognoz;
    }

    private int howMuchCanSend(int planetIndex, int tryCount)
    {
        if (this.getPopulationNextRound(this.frontPlanetIndex) - 1 < tryCount)
            return this.getPopulationNextRound(this.frontPlanetIndex) - 1;
        else
            return tryCount;
    }

    public void sense()
    {
        this.currentStep = this.round.getCurrentStep();
        this.teamIndex = this.round.getTeamId();
        this.planets = this.round.getPlanets();
        this.distanceMap = this.round.getDistanceMap();
        this.myPlanets = this.round.getOwnPlanets();
        this.allMovingGroups = this.round.getMovingGroups();
    }

    public void think()
    {
        if (this.teamIndex == 0)
        {
            this.homePlanetIndex = 0;
            this.minePlanetIndex = 4;
            this.enemyTeamId = 1;
        }
        else
        {
            this.homePlanetIndex = 9;
            this.minePlanetIndex = 5;
            this.enemyTeamId = 0;
        }


        if (this.currentStep > 0 && this.currentStep < 15)
        {
            if (this.teamIndex == 0)
                this.frontPlanetIndex = 3;
            else
                this.frontPlanetIndex = 6;
        }
        if (this.currentStep > 14)
        {
            if (this.teamIndex == 0)
                this.frontPlanetIndex = 2;
            else
                this.frontPlanetIndex = 7;
        }
    }
    public void act()
    {
        if (this.currentStep == 0 || this.currentStep == 1)
        {
            MovingGroup mG = new MovingGroup();

            //3 planets
            for (Planet pl : this.planets.stream().filter(
                    planet -> this.distanceMap[this.homePlanetIndex][planet.getId()] < 12
            ).collect(Collectors.toList()))
            {
                mG = new MovingGroup();
                mG.setFrom(this.homePlanetIndex);
                mG.setTo(pl.getId());
                mG.setCount(1);
                this.movingGroups.add(mG);
            }

            //Mine
            mG = new MovingGroup();
            mG.setCount(1);
            if (this.teamIndex == 0)
                mG.setTo(4);
            else
                mG.setTo(5);
            mG.setFrom(this.homePlanetIndex);
            this.movingGroups.add(mG);

        }

        //Supply/defend systems
        if (this.currentStep > 0) {
            this.defense();
            this.supply();
        }

        if (this.currentStep > 9 && this.currentStep < 15)
        {
            List<Vector2i> prognoz = this.oracule(this.planets.get(this.minePlanetIndex), this.planets.get(this.minePlanetIndex).getPopulation(), 8);
            boolean needSend = false;
            for (int i = 4; i < prognoz.size(); i++)
            {
                if(prognoz.get(i).y != this.teamIndex && needSend == false){
                    needSend = true;
                    MovingGroup mG = new MovingGroup();
                    mG.setFrom(this.frontPlanetIndex);
                    mG.setTo(this.minePlanetIndex);
                    mG.setCount(this.howMuchCanSend(this.frontPlanetIndex, prognoz.get(i).x + 1));
                    this.movingGroups.add(mG);
                }
            }
        }

        if (this.currentStep > 25)
        {
            MovingGroup mG = new MovingGroup();
            mG.setFrom(this.frontPlanetIndex);
            if (this.teamIndex == 0)
                mG.setTo(7);
            else
                mG.setTo(2);
            mG.setCount(15);
            this.movingGroups.add(mG);
        }
    }

    public List<MovingGroup> getMovingGroups()
    {
        this.sense();
        this.think();
        this.act();
        return this.movingGroups;
    }

}
