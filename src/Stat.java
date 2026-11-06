public class Stat {
    public int numSheep;
    public int numWolves;
    public int numGrass;
    public int numFlowers;
    public int tic;

    public Stat(int numSheep, int numWolves, int numGrass, int numFlowers, int tic) {
        this.numSheep = numSheep;
        this.numWolves = numWolves;
        this.numGrass = numGrass;
        this.numFlowers = numFlowers;
        this.tic = tic;
    }


    public String toString() {
        return "[" + numSheep + ", " + numWolves + ", " + numGrass + ", " + numFlowers + ", " + tic + "]";
    }
}
