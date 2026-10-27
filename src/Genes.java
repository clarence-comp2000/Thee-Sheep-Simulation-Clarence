public class Genes {

    private  static double mutationChance = 0.01;
    private double perception;
    private double speed;
    private int lifeSpan;
    private double stealth;

    private int maxStamina;
    private int staminaUseRate;
    private int staminaRecoverRate;
    private double sprintMultiplier;

    private int reproduceCooldown;


    private double hungerCapacity;
    private double hungerRate = 0.0001;
    
    



    //properties
    public double GetPerception(){
        return perception;
    }

    public double GetSpeed()
    {
        return speed;
    }

    public int GetLifespan()
    {
        return lifeSpan;
    }

    public int GetMaxStamina(){
        return maxStamina;
    }

    public int GetStaminaUseRate()
    {
        return staminaUseRate;
    }

    public int GetStaminaRecoverRate()
    {
        return staminaRecoverRate;
    }

    public double GetHungerCapacity()
    {
        return hungerCapacity;
    }

    public double GetHungerRate()
    {
        return hungerRate;
    }

    public double GetSprintMultiplier()
    {
        return sprintMultiplier;
    }

    public double GetStealth()
    {
        return stealth;
    }

    public int GetReproduceCooldown()
    {
        return reproduceCooldown;
    }



    public Genes(double perception, 
        double speed, 
        int lifeSpan, 
        int maxStamina, 
        int staminaRecoverRate, 
        int staminaUseRate,
        double hungerCapacity,
        double sprintMultiplier,
        double stealth,
        int reproduceCooldown)
    {
        this.perception = perception;
        this.speed = speed;
        this.lifeSpan = lifeSpan;
        this.maxStamina = maxStamina;
        this.staminaRecoverRate = staminaRecoverRate;
        this.staminaUseRate = staminaUseRate;
        this.hungerCapacity = hungerCapacity;
        this.sprintMultiplier = sprintMultiplier;
        this.stealth = stealth;
        this.reproduceCooldown = reproduceCooldown;
    }

    public Genes(Genes ParentA, Genes ParentB)
    {
        this.perception = InheritGene(ParentA.GetPerception(), ParentB.GetPerception());
        this.speed = InheritGene(ParentA.GetSpeed(), ParentB.GetSpeed());
        this.lifeSpan = InheritGene(ParentA.GetLifespan(), ParentB.GetLifespan());
        this.maxStamina = InheritGene(ParentA.GetMaxStamina(), ParentB.GetMaxStamina());
        this.staminaUseRate = InheritGene(ParentA.GetStaminaUseRate(), ParentB.GetStaminaUseRate());
        this.staminaRecoverRate = InheritGene(ParentA.GetStaminaRecoverRate(), ParentB.GetStaminaRecoverRate());
        this.hungerCapacity = InheritGene(ParentA.GetHungerCapacity(), ParentB.GetHungerCapacity());
        this.sprintMultiplier = InheritGene(ParentA.GetSprintMultiplier(), ParentB.GetSprintMultiplier());
        this.reproduceCooldown = InheritGene(ParentA.GetReproduceCooldown(), ParentB.GetReproduceCooldown());
        this.stealth = InheritGene(ParentA.GetStealth(), ParentB.GetStealth());
         
    }
    




    private double InheritGene(double geneA, double geneB)
    {
        double roll = Math.random();
        if(roll < mutationChance)
        {
            System.out.println("MUTATION!");
            double newGene = geneA + geneB;
            return newGene * Math.random();
            ///mutate
        }else
        {
            roll = Math.random();
            if(roll > 0.5)
            {
                return geneA;
            }else{
                return geneB;
            }
        }
        
    }

    private int InheritGene(int geneA, int geneB)
    {
        return (int)InheritGene((double)geneA, (double)geneB);
        
    }

}
